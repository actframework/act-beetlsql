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

import org.beetl.sql.core.DSTransactionManager;
import org.osgl.exception.UnexpectedException;
import org.osgl.mvc.annotation.After;
import org.osgl.mvc.annotation.Before;
import org.osgl.mvc.annotation.Catch;
import org.osgl.mvc.annotation.Finally;

import java.sql.SQLException;
import javax.inject.Singleton;

/**
 * An injector support Transaction
 */
@Singleton
public class BeetlSqlTransactional {

    public static final int INTERCEPTOR_PRIORITY = -99;

    @Before(priority = INTERCEPTOR_PRIORITY)
    public void start() {
        DSTransactionManager.start();
    }

    @After(priority = -1 * INTERCEPTOR_PRIORITY)
    public void commit() {
        try {
            DSTransactionManager.commit();
        } catch (SQLException e) {
            rollback();
        }
    }

    @Catch(value = Exception.class, priority = INTERCEPTOR_PRIORITY)
    public void rollback() {
        try {
            DSTransactionManager.rollback();
        } catch (SQLException e) {
            throw new UnexpectedException(e);
        }
    }

    @Finally
    public void clear() {
        DSTransactionManager.clear();
    }

}
