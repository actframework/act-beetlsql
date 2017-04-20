package act.db.beetlsql;

import act.Act;
import act.app.App;
import act.db.Dao;
import act.db.sql.SqlDbService;
import act.db.sql.util.NamingConvention;
import org.beetl.sql.core.*;
import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.db.*;
import org.beetl.sql.core.mapper.BaseMapper;
import org.beetl.sql.core.mapper.DefaultMapperBuilder;
import org.beetl.sql.core.mapper.MapperJavaProxy;
import org.beetl.sql.ext.DebugInterceptor;
import org.osgl.$;
import org.osgl.inject.Genie;
import org.osgl.util.E;
import org.osgl.util.S;

import javax.inject.Provider;
import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Implement `act.db.DbService` using BeetlSql
 */
public class BeetlSqlService extends SqlDbService {

    public static final String DEF_LOADER_PATH = "/sql";

    private SQLManager beetlSql;
    private ConcurrentMap<Class, BaseMapper> mapperMap = new ConcurrentHashMap<>();

    public BeetlSqlService(String dbId, App app, Map<String, String> config) {
        super(dbId, app, config);
    }

    public SQLManager beetlSql() {
        return beetlSql;
    }

    @Override
    protected void dataSourceProvided(DataSource dataSource) {
        ConnectionSource conn = ConnectionSourceHelper.getSingle(dataSource);
        DBStyle style = configureDbStyle();
        SQLLoader loader = configureLoader();
        NameConversion nm = configureNamingConvention();
        Interceptor[] ins = configureInterceptor();
        beetlSql = new SQLManager(style, loader, conn, nm, ins);
    }

    @Override
    protected DataSource createDataSource() {
        throw E.unsupport("External datasource solution must be provided. E.g. hikaricp");
    }

    @Override
    protected boolean supportDdl() {
        return false;
    }

    @Override
    public <DAO extends Dao> DAO defaultDao(Class<?> aClass) {
        throw E.unsupport("BeetlSql does not support DAO. Please use mapper instead");
    }

    @Override
    public <DAO extends Dao> DAO newDaoInstance(Class<DAO> aClass) {
        throw E.unsupport("BeetlSql does not support DAO. Please use mapper instead");
    }

    @Override
    public Class<? extends Annotation> entityAnnotationType() {
        return Table.class;
    }

    BaseMapper mapper(Class modelClass) {
        return mapperMap.get(modelClass);
    }

    public void prepareMapperClass(Class<? extends BaseMapper> mapperClass, Class<?> modelClass) {
        Object o = Proxy.newProxyInstance(mapperClass.getClassLoader(),
                new Class<?>[]{mapperClass},
                new MapperJavaProxy(new DefaultMapperBuilder(beetlSql), beetlSql, mapperClass));
        final BaseMapper mapper = $.cast(o);
        mapperMap.put(mapperClass, mapper);
        mapperMap.put(modelClass, mapper);
        Genie genie = Act.getInstance(Genie.class);
        genie.registerProvider(mapperClass, new Provider() {
            @Override
            public Object get() {
                return mapper;
            }
        });
    }

    private NameConversion configureNamingConvention() {
        String s = this.config.rawConf.get("beetlsql.nc");
        if (null != s) {
            return Act.getInstance(s);
        }

        if (NamingConvention.Default.UNDERSCORE == this.config.tableNamingConvention) {
            return new UnderlinedNameConversion();
        }
        return new DefaultNameConversion();
    }

    private SQLLoader configureLoader() {
        String loaderPath = this.config.rawConf.get("loader.path");
        if (null == loaderPath) {
            loaderPath = DEF_LOADER_PATH;
        }
        return new ClasspathLoader(loaderPath);
    }

    private Interceptor[] configureInterceptor() {
        String debug = this.config.rawConf.get("interceptor.debug");
        if (null == debug) {
            return new Interceptor[0];
        }
        boolean isDebug = Boolean.parseBoolean(debug);
        return isDebug ? new Interceptor[]{new DebugInterceptor()} : new Interceptor[0];
    }

    private DBStyle configureDbStyle() {
        Map<String, String> conf = this.config.rawConf;
        String style = conf.get("platform");
        if (null == style) {
            style = conf.get("style");
        }
        if (null == style) {
            style = this.config.dataSourceConfig.url;
        }
        if (S.notBlank(style)) {
            style = style.trim().toLowerCase();
            if (style.contains("oracle")) {
                return new OracleStyle();
            } else if (style.contains("mysql") || style.contains("maria")) {
                return new MySqlStyle();
            } else if (style.contains("postgres") || style.contains("pgsql")) {
                return new PostgresStyle();
            } else if (style.contains("h2")) {
                return new H2Style();
            } else if (style.contains("sqlserver")) {
                return new SqlServerStyle();
            } else if (style.contains("db2")) {
                return new DB2SqlStyle();
            } else if (style.contains("sqlite")) {
                return new SQLiteStyle();
            }
        }
        throw new UnsupportedOperationException("Unknown database style: " + style);
    }

}
