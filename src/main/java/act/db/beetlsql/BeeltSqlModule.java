package act.db.beetlsql;

import org.beetl.sql.core.mapper.BaseMapper;
import org.osgl.inject.Module;

/**
 * Configure BaseMapper injection
 */
public class BeeltSqlModule extends Module {
    @Override
    protected void configure() {
        registerGenericTypedBeanLoader(BaseMapper.class, new MapperLoader());
    }
}
