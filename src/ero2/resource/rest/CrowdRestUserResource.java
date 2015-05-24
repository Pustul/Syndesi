package ero2.resource.rest;

import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import ero2.identification.CrowdController;
import ero2.identification.CrowdUser;

/**
 * Rest resource to view / add users
 * @author Blaise
 *
 */
public class CrowdRestUserResource extends ServerResource {
	private CrowdController crowdController;

	public CrowdRestUserResource(){
		this.crowdController = CrowdController.getInstance();
	}

	@SuppressWarnings("unchecked")
	@Get()
	public String getUsers() {
		JSONObject listUsers = crowdController.getJSON();
		return listUsers.toJSONString();
	}
	
	@SuppressWarnings("unchecked")
	@Post()
	public String addUser(String request) throws IOException {
		JSONObject userJSON = (JSONObject) JSONValue.parse(request);
		CrowdUser newUser = new CrowdUser(crowdController.getUsers().size(), getRequest().getClientInfo().getAddress(), getRequest().getClientInfo().getAgent(), (String)userJSON.get("mName"), (String)userJSON.get("mSurname"), (String)userJSON.get("mOffice"));
		crowdController.getUsers().add(newUser);
		userJSON.replace("mId", newUser.getId());
		return userJSON.toJSONString();
	}
	
	@SuppressWarnings("unchecked")
	@Put()
	public String updateUser(String request) throws IOException {
		JSONObject userJSON = (JSONObject) JSONValue.parse(request);
		CrowdUser updatedUser = new CrowdUser(((Long)userJSON.get("mId")).intValue(), getRequest().getClientInfo().getAddress(), getRequest().getClientInfo().getAgent(), (String)userJSON.get("mName"), (String)userJSON.get("mSurname"), (String)userJSON.get("mOffice"));
		ArrayList<CrowdUser> users =crowdController.getUsers();
		if(users.contains(updatedUser)){
			CrowdUser oldUser = users.get(updatedUser.getId());
			oldUser.setName(updatedUser.getName());
			oldUser.setSurname(updatedUser.getSurname());
			oldUser.setOffice(updatedUser.getOffice());
		}else{
			updatedUser.setId(users.size());
			users.add(updatedUser);
			userJSON.replace("mId", updatedUser.getId());
		}
		return userJSON.toJSONString();
	}
	
}
