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

import act.app.App;
import act.db.DbPlugin;
import act.db.DbService;
import act.db.sql.tx.TxError;
import act.db.sql.tx.TxStart;
import act.db.sql.tx.TxStop;
import act.event.ActEventListenerBase;
import org.beetl.sql.core.DSTransactionManager;

import java.util.EventObject;
import java.util.Map;

/**
 * Responsible for init BeetlSql DB service
 */
public class BeetlSqlPlugin extends DbPlugin {

    @Override
    protected void applyTo(App app) {
        super.applyTo(app);
        app.eventBus().bind(TxStart.class, new ActEventListenerBase<TxStart>() {
            @Override
            public void on(TxStart eventObject) {
                DSTransactionManager.start();
            }
        }).bind(TxStop.class, new ActEventListenerBase() {
            @Override
            public void on(EventObject eventObject) throws Exception {
                DSTransactionManager.commit();
                DSTransactionManager.clear();
            }
        }).bind(TxError.class, new ActEventListenerBase<TxError>() {
            @Override
            public void on(TxError eventObject) throws Exception {
                DSTransactionManager.rollback();
                DSTransactionManager.clear();
            }
        });
    }

    @Override
    public DbService initDbService(String id, App app, Map<String, String> conf) {
        return new BeetlSqlService(id, app, conf);
    }

}
