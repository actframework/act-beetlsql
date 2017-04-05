package act.db.beetlsql;

import org.beetl.sql.core.DSTransactionManager;
import org.osgl.exception.UnexpectedException;
import org.osgl.mvc.annotation.After;
import org.osgl.mvc.annotation.Before;
import org.osgl.mvc.annotation.Catch;
import org.osgl.mvc.annotation.Finally;

import javax.inject.Singleton;
import java.sql.SQLException;

/**
 * An injector support Transaction
 */
@Singleton
public class BeetlSqlTransactional {

    @Before
    public void start() {
        DSTransactionManager.start();
    }

    @After
    public void commit() {
        try {
            DSTransactionManager.commit();
        } catch (SQLException e) {
            rollback();
        }
    }

    @Catch(Exception.class)
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
