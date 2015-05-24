package ero2.identification;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CrowdController {
	private static CrowdController instance;
	private ArrayList<CrowdUser> users;
	
	public CrowdController(){
		this.users = new ArrayList<CrowdUser>();
	}
	
	public static synchronized CrowdController getInstance(){
		if(instance == null){
			instance = new CrowdController();
		}
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getJSON(){
		JSONObject representation = new JSONObject();
		JSONArray listUsers = new JSONArray();
		for(CrowdUser user : users){
			listUsers.add(user.getJSON());
		}
		representation.put("users", listUsers);
		return representation;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getDataJSON(){
		JSONObject representation = new JSONObject();
		JSONArray listData = new JSONArray();
		for(CrowdUser user : users){
			CrowdData lastData = user.getLastData();
			if(lastData != null){
				listData.add(user.getLastData().getJSON());
			}
		}
		representation.put("data", listData);
		return representation;
	}
	
	public ArrayList<CrowdUser> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<CrowdUser> users) {
		this.users = users;
	}
}
