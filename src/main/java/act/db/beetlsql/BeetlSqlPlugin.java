package act.db.beetlsql;

import act.app.App;
import act.db.DbPlugin;
import act.db.DbService;

import java.util.Map;

/**
 * Responsible for init BeetlSql DB service
 */
public class BeetlSqlPlugin extends DbPlugin {

    @Override
    public DbService initDbService(String id, App app, Map<String, String> conf) {
        return new BeetlSqlService(id, app, conf);
    }

}
