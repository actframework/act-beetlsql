package act.db.beetlsql.inject;

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

import act.Act;
import act.app.App;
import act.db.DB;
import act.db.beetlsql.BeetlSqlService;
import act.inject.DependencyInjector;
import org.beetl.sql.core.SQLManager;
import org.osgl.inject.NamedProvider;

import javax.inject.Provider;

public class BeetlSqlProviders {

    private static Provider<BeetlSqlService> DEF_SVC = new Provider<BeetlSqlService>() {
        @Override
        public BeetlSqlService get() {
            return NAMED_SVC.get(DB.DEFAULT);
        }
    };

    private static NamedProvider<BeetlSqlService> NAMED_SVC = new NamedProvider<BeetlSqlService>() {
        @Override
        public BeetlSqlService get(String name) {
            return Act.app().dbServiceManager().dbService(name);
        }
    };

    private static Provider<SQLManager> DEF_SQL_MGR = new Provider<SQLManager>() {
        @Override
        public SQLManager get() {
            return NAMED_SQL_MGR.get(DB.DEFAULT);
        }
    };

    private static NamedProvider<SQLManager> NAMED_SQL_MGR = new NamedProvider<SQLManager>() {
        @Override
        public SQLManager get(String name) {
            return NAMED_SVC.get(name).beetlSql();
        }
    };

    public static void classInit(App app) {
        DependencyInjector injector = app.injector();
        injector.registerProvider(BeetlSqlService.class, DEF_SVC);
        injector.registerProvider(SQLManager.class, DEF_SQL_MGR);
        injector.registerNamedProvider(BeetlSqlService.class, NAMED_SVC);
        injector.registerNamedProvider(SQLManager.class, NAMED_SQL_MGR);
    }

}

