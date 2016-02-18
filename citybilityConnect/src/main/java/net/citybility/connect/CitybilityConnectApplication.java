package net.citybility.connect;

import net.citybility.dao.Config;
import net.citybility.services.CommunicationManagerService;
import android.app.Application;
import android.content.Intent;
import android.util.Log;

public class CitybilityConnectApplication extends Application {

	private static CitybilityConnectApplication instance;
	private static DaoHelper connectDaoHelper;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		connectDaoHelper = DaoHelper.getIstance(instance);
		this.startCommunicationManagerService();
	}
	
	public static CitybilityConnectApplication getInstance() {
		return instance;
	}
	
	public static boolean isLoggedIn() {
		Config identity = connectDaoHelper.getDaoSession().getConfigDao().load(Param.IDENTITY);
		return identity != null && identity.getValue() != null;
	}
	
	public static boolean setCitibilityID(long value){
		return setConfigEntity(Param.CITIBILITY_ID, String.valueOf(value));
	}

	public static long getCitibilityID(){
		String id = getConfigEntity(Param.CITIBILITY_ID);
		return id!=null ? Long.parseLong(id):0;
	}
	public static boolean setFirstName(String value){
		return setConfigEntity(Param.FIRSTNAME, value);
	}
	
	public static String getFirstName(){
		return getConfigEntity(Param.FIRSTNAME);
	}
	
	public static String getLastName(){
		return getConfigEntity(Param.LASTNAME);
	}

	public static boolean setLastName(String value){
		return setConfigEntity(Param.LASTNAME, value);
	}

	public static String getIdentity(){
		return getConfigEntity(Param.IDENTITY);
	}

	public static String getEmail(){
		return getConfigEntity(Param.EMAIL);
	}

	public static void setEmail(String value){
		setConfigEntity(Param.EMAIL, value);
	}
	
	public static String getPassword(){
		return getConfigEntity(Param.PASSWORD);
	}
	
	public static void removeIdentity(){
		removeConfigEntity(Param.IDENTITY);
	}

	public static void removePassword(){
		removeConfigEntity(Param.PASSWORD);
	}

	public static void removeCredentials(){
		removeConfigEntity(Param.IDENTITY);
		removeConfigEntity(Param.EMAIL);
		removeConfigEntity(Param.PASSWORD);
	}

	private static boolean setConfigEntity(long param, String value){
		return connectDaoHelper.getDaoSession().getConfigDao().insertOrReplace(new Config(param, value)) > 0;
	}
	
	private static String getConfigEntity(long param){
		String value = null;
		Config entity = connectDaoHelper.getDaoSession().getConfigDao().load(param);
		if(entity != null)
			value = entity.getValue();
		return value;
	}
	private static void removeConfigEntity(long param){
		Config entity = connectDaoHelper.getDaoSession().getConfigDao().load(param);
		if(entity != null)
			connectDaoHelper.getDaoSession().getConfigDao().delete(entity);
	}
	
	public void startCommunicationManagerService(){
		Log.d(this.getClass().getName(), "@@@@@ startCommunicationManagerService");
		if (CommunicationManagerService.isStopped()) {
			startService(new Intent(this, CommunicationManagerService.class));
		}
	}

}
