package act.db.beetlsql;

/*-
 * #%L
 * ACT Beetlsql
 * %%
 * Copyright (C) 2017 - 2019 ActFramework
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import act.db.Dao;
import org.beetl.sql.core.query.Query;

import java.util.List;
import java.util.StringTokenizer;

/**
 * 有状态的，操作完毕后，需要重新创建,而不是复用此类
 *
 * @param <MODEL_TYPE>
 */
public class BeetlSqlQuery<MODEL_TYPE> implements Dao.Query<MODEL_TYPE, BeetlSqlQuery<MODEL_TYPE>> {

    private BeetlSqlDao dao;
    private Class<MODEL_TYPE> entityClass;
    private String fields;
    private Object[] values;
    Query<MODEL_TYPE> query;
    private int pos = -1;
    private int limit = -1;


    protected BeetlSqlQuery(BeetlSqlDao dao, Class<MODEL_TYPE> entityClass) {
        this.dao = dao;
        this.entityClass = entityClass;
        createQuery(null, null);
    }

    protected BeetlSqlQuery(BeetlSqlDao dao, Class<MODEL_TYPE> entityClass, String fields, Object[] values) {
        this.dao = dao;
        this.entityClass = entityClass;
        this.fields = fields;
        this.values = values;
        createQuery(fields, values);
    }

    @Override
    public BeetlSqlQuery<MODEL_TYPE> offset(int pos) {
        this.pos = pos;
        return this;
    }

    @Override
    public BeetlSqlQuery<MODEL_TYPE> limit(int limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public BeetlSqlQuery<MODEL_TYPE> orderBy(String... fieldList) {
        for (String orderBy : fieldList) {
            query.orderBy(orderBy);
        }
        return this;
    }

    @Override
    public MODEL_TYPE first() {

        List<MODEL_TYPE> list = query.limit(1, 1).select();
        if (list.size() == 0) {
            return null;
        }
        return list.get(0);

    }

    @Override
    public Iterable<MODEL_TYPE> fetch() {
        if (pos != -1) {
            return query.limit(pos, limit).select();
        } else {
            return query.select();
        }

    }

    @Override
    public long count() {
        return query.count();
    }

    private Query createQuery() {
      this.query = dao.sqlManager.query(entityClass);
      return this.query;
    }

    private Query createQuery(String fields, Object[] values) {
        query = dao.sqlManager.query(entityClass);
        if (fields == null) {
            return query;
        }
        StringTokenizer st = new StringTokenizer(fields, ":,; ");
        int index = 0;
        while (st.hasMoreTokens()) {
            query.andEq(st.nextToken(), values[index++]);
        }

        return query;
    }

}
