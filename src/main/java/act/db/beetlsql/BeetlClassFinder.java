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

import static act.app.DbServiceManager.dbId;

import act.app.App;
import act.app.DbServiceManager;
import act.app.event.SysEventId;
import act.db.DbService;
import act.db.EntityClassRepository;
import act.util.AnnotatedClassFinder;
import act.util.SubClassFinder;
import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.mapper.BaseMapper;
import org.osgl.$;
import org.osgl.exception.UnexpectedException;
import org.osgl.logging.LogManager;
import org.osgl.logging.Logger;
import org.osgl.util.Generics;

import java.lang.reflect.Type;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.Entity;

/**
 * Find classes with annotation `Table`
 */
@Singleton
public class BeetlClassFinder {

    private static final Logger LOGGER = LogManager.get(BeetlClassFinder.class);

    private final EntityClassRepository repo;
    private final App app;

    @Inject
    public BeetlClassFinder(EntityClassRepository repo, App app) {
        this.repo = $.requireNotNull(repo);
        this.app = $.requireNotNull(app);
    }

    @AnnotatedClassFinder(Table.class)
    public void foundEntity(Class<?> modelClass) {
        repo.registerModelClass(modelClass);
    }

    @AnnotatedClassFinder(Entity.class)
    public void foundEntity2(Class<?> modelClass) {
        repo.registerModelClass(modelClass);
    }

    @SubClassFinder(noAbstract = false, callOn = SysEventId.PRE_START)
    public void foundMapper(Class<? extends BaseMapper> mapperClass) {
        if (mapperClass.getName().startsWith("org.beetl.sql.test.")) {
            // beetlsql-2.12.9.RELEASE package test classes into the release jar; let's get rid it
            return;
        }
        DbServiceManager dbServiceManager = app.dbServiceManager();
        try {
            Class<?> modelClass = modelClass(mapperClass);
            DbService dbService = dbServiceManager.dbService(dbId(modelClass));
            if (dbService instanceof BeetlSqlService) {
                ((BeetlSqlService) dbService).prepareMapperClass(mapperClass, modelClass);
            } else {
                throw new UnexpectedException("mapper class cannot be landed to a BeetlSqlService");
            }
        } catch (RuntimeException e) {
            LOGGER.warn(e, "Error registering mapping class: %s", mapperClass);
        }
    }

    static Class<?> modelClass(Class<? extends BaseMapper> mapperClass) {
        List<Type> paramTypes = Generics.typeParamImplementations(mapperClass, BaseMapper.class);
        if (paramTypes.size() != 1) {
            throw new UnexpectedException("Cannot determine parameter type of %s", mapperClass);
        }
        Type type = paramTypes.get(0);
        if (!(type instanceof Class)) {
            throw new UnexpectedException("Cannot determine parameter type of %s", mapperClass);
        }
        return $.cast(type);
    }

}
