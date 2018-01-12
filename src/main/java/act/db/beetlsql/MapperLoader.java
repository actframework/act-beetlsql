package act.db.beetlsql;

/*-
 * #%L
 * ACT Beetlsql
 * %%
 * Copyright (C) 2017 - 2018 ActFramework
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

import act.Act;
import act.app.DbServiceManager;
import act.db.DbService;
import org.beetl.sql.core.mapper.BaseMapper;
import org.osgl.$;
import org.osgl.inject.BeanSpec;
import org.osgl.inject.GenericTypedBeanLoader;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Responsible for inject BaseMapper instance
 */
public class MapperLoader implements GenericTypedBeanLoader<BaseMapper> {

    @Override
    public BaseMapper load(BeanSpec beanSpec) {
        List<Type> typeList = beanSpec.typeParams();
        int sz = typeList.size();
        if (sz > 0) {
            Class<?> modelType = BeanSpec.rawTypeOf(typeList.get(0));
            String dbId = DbServiceManager.dbId(modelType);
            DbService service = Act.app().dbServiceManager().dbService(dbId);
            if (service instanceof BeetlSqlService) {
                BeetlSqlService beetl = $.cast(service);
                return beetl.mapper(modelType);
            }
        }
        return null;
    }
}
