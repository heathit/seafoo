package co.kr.myseafood.erp.util;

import java.util.HashMap;


/**
 * Data 저장 관리
 * @author minu
 *
 */
public class DataStorage {
	private HashMap<Integer, byte[]> dataMap;
	private static DataStorage instance;


	// 유저 data
	public static final int USER_ID 		= 50000;
	public static final int USER_UID 		= 50001;
	public static final int USER_NAME 		= 50002;
	public static final int USER_ADDR 		= 50003;
	public static final int USER_MOBILE 	= 50004;
	public static final int USER_DIV 		= 50005;
	public static final int USER_POS 		= 50006;
	public static final int USER_EMAIL 		= 50007;
	public static final int USER_STATE 		= 50008;
	public static final int USER_EID 		= 50009;
	public static final int USER_FAX 		= 50010;
	public static final int USER_COMPANY 	= 50011;

	public static final int WORK_CURRENT_ID	= 50100;
	public static final int WORK_ID 		= 50101;
	public static final int WORK_LIST_JSON 	= 50102;

	public static final int CURRENT_ALERT_CNT 	= 50110;

	public static final int COOKIE_NAME 	= 50201;
	public static final int COOKIE_VALUE 	= 50202;



	
	private DataStorage() {
		dataMap = new HashMap<>();
	}

	public static DataStorage getInstance(){
		if(instance == null){
			instance = new DataStorage();
		}

		return instance;
	}
	
	public static void initInstance(){
		instance = null;
	}
	
	/**
	 * 데이터 암호화 저장
	 * 이미 저장된 데이터가 있다면 교체
	 * @param id
	 * @param value
	 * @return
	 */
    public boolean setData(int id, String value){
    	if(value == null|| (value.trim()).length() == 0) return false; 

    	if(dataMap.containsKey(id)){ // 기 저장된 정보가 있다면 교체
			dataMap.remove(id);
    	}
		dataMap.put(id, value.getBytes());

    	return true;
    }
    
    public boolean setData(int id, byte[] value){
    	if(value == null|| value.length == 0) return false; 

    	if(dataMap.containsKey(id)){ // 기 저장된 정보가 있다면 교체
			dataMap.remove(id);
    	}
		dataMap.put(id, value);

    	return true;
    }

    /**
     * String으로 data 반환
     * @param id
     * @return
     */
    public String getData(int id){
    	return (hasData(id))?new String(dataMap.get(id)):"";
    }
    
    /**
     * Byte[]로 data 반환
     * @param id
     * @return
     */
    public byte[] getDataB(int id){
    	return (hasData(id))?dataMap.get(id): null;
    }

    
    public boolean hasData(int id){
    	return dataMap.containsKey(id);
    }

	/**
	 * 맵 클리어
	 */
	public void clearStorage(){
		dataMap.clear();
	}

	/**
	 * 지정된 필드 클리어
	 */
	public void clear(int id) {
		dataMap.remove(id);
	}
}
