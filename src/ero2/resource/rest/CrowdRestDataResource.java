package ero2.resource.rest;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import ero2.identification.CrowdController;
import ero2.identification.CrowdData;
import ero2.identification.CrowdUser;

/**
 * Rest resource to view / send data
 * 
 * @author Blaise
 *
 */
public class CrowdRestDataResource extends ServerResource {
	private CrowdController crowdController;

	public CrowdRestDataResource(){
		this.crowdController = CrowdController.getInstance();
	}

	@SuppressWarnings("unchecked")
	@Get()
	public String getData() {
		JSONObject dataJSON = crowdController.getDataJSON();
		return dataJSON.toJSONString();
	}
	
	@Post()
	public String addData(String request) throws IOException, ParseException {
		JSONObject dataJSON = (JSONObject) JSONValue.parse(request);
		System.out.println(dataJSON.toJSONString());
		int accountId = ((Long) dataJSON.get("mAccountId")).intValue();
		CrowdData newData = new CrowdData(accountId, ((Number) dataJSON.get("mData")).floatValue(), (String)dataJSON.get("mDataType"));
		crowdController.getUsers().get(accountId).setLastData(newData);;
		return dataJSON.toJSONString();
	}
	
}
