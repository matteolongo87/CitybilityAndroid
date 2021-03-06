package net.citybility.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import net.citybility.dao.Call;
import net.citybility.dao.Config;

import net.citybility.dao.CallDao;
import net.citybility.dao.ConfigDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig callDaoConfig;
    private final DaoConfig configDaoConfig;

    private final CallDao callDao;
    private final ConfigDao configDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        callDaoConfig = daoConfigMap.get(CallDao.class).clone();
        callDaoConfig.initIdentityScope(type);

        configDaoConfig = daoConfigMap.get(ConfigDao.class).clone();
        configDaoConfig.initIdentityScope(type);

        callDao = new CallDao(callDaoConfig, this);
        configDao = new ConfigDao(configDaoConfig, this);

        registerDao(Call.class, callDao);
        registerDao(Config.class, configDao);
    }
    
    public void clear() {
        callDaoConfig.getIdentityScope().clear();
        configDaoConfig.getIdentityScope().clear();
    }

    public CallDao getCallDao() {
        return callDao;
    }

    public ConfigDao getConfigDao() {
        return configDao;
    }

}
