package net.citybility.connect;

import net.citybility.dao.CallDao;
import net.citybility.dao.ConfigDao;
import net.citybility.dao.DaoMaster;
import net.citybility.dao.DaoSession;
import net.citybility.dao.DaoMaster.DevOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DaoHelper {

	private SQLiteDatabase db;
	private DevOpenHelper helper;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	
	private Context context;
	private static DaoHelper instance;

	public static DaoHelper getIstance(Context context) {
		if(instance == null)
			instance = new DaoHelper();
		instance.context = context;
		return instance;
	}
	
	private DaoHelper() {
	}
	

	public CallDao getCallDao() {
		return getDaoSession().getCallDao();
	}
	
	public ConfigDao getConfigDao() {
		return getDaoSession().getConfigDao();
	}
	
	public DaoSession getDaoSession() {
		if (helper == null)
			helper = new DaoMaster.DevOpenHelper(this.context, "citybility-db", null);
		if (db  == null)
			db = helper.getWritableDatabase();
		if (daoMaster == null)
			daoMaster = new DaoMaster(db);
		if (daoSession == null)
			daoSession = daoMaster.newSession();
		return daoSession;
	}
	
	
}
