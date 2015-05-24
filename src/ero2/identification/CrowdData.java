package ero2.identification;

import org.json.simple.JSONObject;

/**
 * Represents sensor data sent by crowd users
 * @author Blaise
 *
 */
public class CrowdData {
    private int accountId;
    private float data;
    private String dataType;
    
    public CrowdData(int accountId, float data, String dataType){
    	this.accountId = accountId;
    	this.data = data;
    	this.dataType = dataType;
    }
    
	@SuppressWarnings("unchecked")
	public JSONObject getJSON(){
		JSONObject representation = new JSONObject();
		representation.put("accountId", accountId);
		representation.put("value", data);
		representation.put("type", dataType);
		return representation;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public float getData() {
		return data;
	}

	public void setData(float data) {
		this.data = data;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

}
