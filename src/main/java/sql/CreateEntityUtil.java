package sql;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.util.List;

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Repository
@Transactional
public class CreateEntityUtil {

    @Autowired
    private SqlUtilRepository sqlUtilRepository;

    @Value("${spring.datasource.url}")
    private String dataBaseUrl;

    /**
     * 生成entity时需要在这里配置src的路径以便于放在
     */
    private String srcPath = "src/main/java/";
    private String packPath = "com.chenhong.movie_capacity";

    //     ;
    @Test
    public void contextLoads() {
        List<List> list = sqlUtilRepository.query4List("show tables");
        for (List o : list) {
            String table = o.get(0).toString();
            File entityFile = new File(srcPath + packPath.replace(".", "/") +
                    "/entity/" + toClassName(table) + ".java");
            File repositoryFile = new File(srcPath + packPath.replace(".", "/") +
                    "/repository/" + toClassName(table) + "Repository.java");
            try {
                if (!entityFile.exists()) {
                    String path = entityFile.getPath();
                    path = path.substring(0, path.lastIndexOf("\\"));
                    File m = new File(path);
                    m.mkdirs();
                    entityFile.createNewFile();
                }
                if (!repositoryFile.exists()) {
                    String path = repositoryFile.getPath();
                    path = path.substring(0, path.lastIndexOf("\\"));
                    File m = new File(path);
                    m.mkdirs();
                    repositoryFile.createNewFile();
                }
                FileWriter fileWriter = new FileWriter(entityFile);
                FileWriter fileWriter1 = new FileWriter(repositoryFile);

                fileWriter1.write("package " + packPath + ".repository" + ";\n");
                fileWriter1.write("import org.springframework.data.jpa.repository.JpaRepository;");
                fileWriter1.write("import " + packPath + ".entity." + toClassName(table) + ";\n");
                fileWriter1.write("public interface " + toClassName(table) + "Repository extends JpaRepository<" + toClassName(table) + ", Integer> {}");
                fileWriter1.close();

                fileWriter.write("package " + packPath + ".entity" + ";\n");
                fileWriter.write("import javax.persistence.Column;");
                fileWriter.write("\n");
                fileWriter.write("import javax.persistence.Entity;");
                fileWriter.write("\n");
                fileWriter.write("import javax.persistence.GeneratedValue;");
                fileWriter.write("import java.util.Date;");
                fileWriter.write("\n");
                fileWriter.write("import javax.persistence.GenerationType;");
                fileWriter.write("\n");
                fileWriter.write("import javax.persistence.Id;");
                fileWriter.write("\n");
                fileWriter.write("import javax.persistence.Table;");
                fileWriter.write("\n");
                fileWriter.write("@Entity\n");
                fileWriter.write("@Table(name=\"" + table.toUpperCase() + "\")\n");
                fileWriter.write("public class " + toClassName(table) + "{\n");
                fileWriter.write("  @Id");
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                Connection conn = DriverManager.getConnection(dataBaseUrl, "root", "123456");
                Statement stat = conn.createStatement();
                ResultSet rs = stat.executeQuery("select * from " + table + " where 1=2");
                ResultSetMetaData meta = rs.getMetaData();
                StringBuilder sbVar = new StringBuilder();
                StringBuilder sbProp = new StringBuilder();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    String _type;
                    switch (meta.getColumnType(i)) {
                        case Types.CHAR:
                        case Types.VARCHAR:
                        case Types.NVARCHAR:
                        case Types.LONGVARCHAR:
                            _type = "String";
                            break;
                        case Types.DATE:
                        case Types.TIME:
                        case Types.TIMESTAMP:
                            _type = "Date";
                            break;
                        case Types.REAL:
                            _type = "real";
                            break;
                        case Types.DOUBLE:
                        case Types.DECIMAL:
                        case Types.FLOAT:
                            _type = "double";
                            break;
                        case Types.SMALLINT:
                        case Types.INTEGER:
                            _type = "int";
                            break;
                        case Types.NUMERIC:
                        case Types.BIGINT:
                            _type = "long";
                            break;
                        case Types.BOOLEAN:
                        case Types.BIT:
                            _type = "boolean";
                            break;
                        case Types.BLOB:
                        case Types.CLOB:
                        case Types.NCLOB:
                        case Types.LONGVARBINARY:
                            _type = "byte[]";
                            break;
                        default:
                            _type = "type" + meta.getColumnType(i) + "?";
                    }
                    String pname = toParamName(meta.getColumnName(i));
                    sbVar.append("@Column(name = \"" + meta.getColumnName(i) + "\")\r\n");
                    sbVar.append("private " + _type + " " + pname + ";\r\n\r\n");

                    String pname1 = pname.substring(0, 1).toUpperCase() + pname.substring(1);
                    sbProp.append("public " + _type + " get" + pname1 + "() {\r\n");
                    sbProp.append("\t" + "return " + pname + ";\r\n");
                    sbProp.append("}\r\n\r\n");
                    sbProp.append("public void set" + pname1 + "(" + _type + " value) {\r\n");
                    sbProp.append("\t" + pname + " = value;\r\n");
                    sbProp.append("}\r\n\r\n");


                }
                fileWriter.write(sbVar.toString());
                fileWriter.write(sbProp.toString());
                fileWriter.write("}");
                fileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String toParamName(String field) {
        String[] values = field.toLowerCase().split("_");
        String result = values[0];
        for (int i = 1; i < values.length; i++) {
            result += values[i].substring(0, 1).toUpperCase() + (values[i].length() > 1 ? values[i].substring(1) : "");
        }

        return result;
    }

    public static String toClassName(String field) {
        String[] values = field.toLowerCase().split("_");
        String value = values[0];
        String result = value.substring(0, 1).toUpperCase() + (value.length() > 1 ? value.substring(1) : "");
        for (int i = 1; i < values.length; i++) {
            value = values[i];
            result += value.substring(0, 1).toUpperCase() + (value.length() > 1 ? value.substring(1) : "");
        }

        return result;
    }

    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }
}