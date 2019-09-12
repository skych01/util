package sql;

import java.sql.*;
import java.util.Scanner;

/**
 * 生成建表SQL和实体类代码
 */


public class EntityUtil {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {


        outputEntityCode();
    }

    public static void outputTableSql(String text) {
        String[] lines = text.split("\\|");
        for (String line : lines) {
            String[] params = line.split(",");
            if (params.length >= 4) {
                String name = params[1];
                String remark = params[2];
                String type = params[3];
                String length = params.length > 4 ? params[4] : "";
                if (type.startsWith("字符")) {
                    System.out.println(String.format("[%1$s] [varchar](%2$s) NULL,", name, length));
                } else if (type.startsWith("数值")) {
                    System.out.println(String.format("[%1$s] [int] NULL,", name));
                } else if (type.startsWith("日期")) {
                    System.out.println(String.format("[%1$s] [date] NULL,", name));
                } else {
                    System.out.println(String.format("[%1$s] [?] NULL,", name));
                }
            } else {
                System.out.println(line);
            }
        }
    }

    public static void outputEntityCode() throws SQLException, ClassNotFoundException {
        System.out.println("Please input table:");
        Scanner scanner = new Scanner(System.in);
        String table = scanner.nextLine();
        if (table.length() > 0) {
            System.out.println("import javax.persistence.Column;");
            System.out.println("import javax.persistence.Entity;");
            System.out.println("import javax.persistence.GeneratedValue;");
            System.out.println("import javax.persistence.GenerationType;");
            System.out.println("import javax.persistence.Id;");
            System.out.println("import javax.persistence.Table;");
            System.out.println();
           // System.out.println("import org.codehaus.jackson.annotate.JsonIgnoreProperties;");
            System.out.println("");
            System.out.println("@Entity");
            System.out.println("@Table(name=\"" + table.toUpperCase() + "\")");
           // System.out.println("@JsonIgnoreProperties(value={\"hibernateLazyInitializer\",\"handler\"})");
            System.out.println("public class " + toClassName(table) + "{");
            System.out.println();
            System.out.println("	@Id");
            System.out.println("	@GeneratedValue(strategy=GenerationType.AUTO)");


            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/assessment1";
            Connection conn = DriverManager.getConnection(url, "root", "123456");
            try {
                Statement stat = conn.createStatement();
                try {
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

                    System.out.println(sbVar.toString());
                    System.out.println(sbProp.toString());
                } finally {
                    stat.close();
                }
            } finally {
                conn.close();
            }
            System.out.println("}");
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
}
