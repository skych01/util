package sql;


import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;


/**
 * 使用sql 语句作查询时 需要考虑 数据库是否会切换！
 */
@Repository
public class SqlUtilRepository {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 分页查询
     * @param sql 执行的sql语句
     * @param pageNo    当前页
     * @param pageSize  每页记录
     * @param totalCount   总记录数
     * @param values    参数列表
     * @return
     */
    public Page pageSQLQuery(String sql, int pageNo, int pageSize, long totalCount, Object... values) {
        Session session = entityManager.unwrap(Session.class);
        sql = " from ( " + sql + ") target";

        if (totalCount < 0) {
            totalCount = getTotalCount(sql, session, values);
        }

        if (totalCount < 1) {
            return new Page();
        } else {
            return getResult(sql, pageNo, pageSize, totalCount, session, values);
        }
    }
    /**
     * sql查询,以List<map>类型返回
     * @param sql 执行的sql语句
     * @param parameter 需要的参数
     */
    public List<Map<String,Object>> query(String sql,Object ...parameter){
        Session session = entityManager.unwrap(Session.class);
        SQLQuery query = session.createSQLQuery(sql);
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        for (int i = 0; i < parameter.length; i++) {
            query.setParameter(i, parameter[i]);
        }
        return (List<Map<String,Object>>)query.list();
    }
    /**
     * sql查询,以List<map>类型返回
     * @param sql 执行的sql语句
     * @param parameter 需要的参数
     */
    public List<List> query4List(String sql,Object ...parameter){
        Session session = entityManager.unwrap(Session.class);
        SQLQuery query = session.createSQLQuery(sql);
        query.setResultTransformer(Transformers.TO_LIST);
        for (int i = 0; i < parameter.length; i++) {
            query.setParameter(i, parameter[i]);
        }
        return (List<List>)query.list();
    }

    /**
     * 分页查询
     */
    private Page getResult(String sql, int pageNo, int pageSize, long totalCount, Session session, Object[] params) {
        int startIndex = (pageNo - 1) * pageSize;
        SQLQuery query = session.createSQLQuery("select *  " + sql);

        for (int i = 0; i < params.length; i++) {
            query.setParameter(i, params[i]);
        }

        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map<String, Object>> list = query.setFirstResult(startIndex).setMaxResults(pageSize).list();

        if (totalCount < 1)
            return new Page();
        return new Page(pageNo-1, pageSize, totalCount, list);
    }

    /**
     * 统计数量
     */
    private long getTotalCount(String sql, Session session, Object[] params) {
        long totalCount;SQLQuery query = session.createSQLQuery("select count(*) " + sql);

        for (int i = 0; i < params.length; i++) {
            query.setParameter(i, params[i]);
        }

        Object obj = query.uniqueResult();
        totalCount = Long.parseLong(obj.toString());
        return totalCount;
    }
}
