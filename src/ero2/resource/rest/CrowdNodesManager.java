package ero2.resource.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ero2.identification.CrowdController;
import ero2.identification.CrowdData;
import ero2.identification.CrowdNode;
import ero2.identification.CrowdUser;
import ero2.identification.NodeType;

public class CrowdNodesManager extends TimerTask{
	private static CrowdNodesManager instance;
	private ArrayList<CrowdNode> nodesList;
	private String url;
	
	public CrowdNodesManager(String url){
		this.url = url;
		this.nodesList = new ArrayList<>();
		//Update the nodes list every 30 minutes
		new Timer().schedule(this, 0, 1800000);
	}
	
	public static synchronized CrowdNodesManager getInstance(String url){
		if(instance == null){
			instance = new CrowdNodesManager(url);
		}
		return instance;
	}
	
	@Override
	public void run() {
		this.nodesList.clear();
		this.fetchNodes();
	}
	
	public void regulateLight(CrowdData newLightData){
		if(newLightData.getValue() < CrowdController.getInstance().getUsers().get(newLightData.getAccountId()).getTargetLight()){
			for(CrowdNode node : nodesList){
				if(node.getmType() == NodeType.bulb){
					//Change the node status only if it haven't been changed in the last 5 minutes to avoid annoying the users
					if(System.currentTimeMillis()-node.getLastChangeTS() < 300000){
						this.sendMediateRequest(node, "on");
					}
				}
				if(node.getmType() == NodeType.curtain){
					//Change the node status only if it haven't been changed in the last 5 minutes to avoid annoying the users
					if(System.currentTimeMillis()-node.getLastChangeTS() < 300000){
						this.sendMediateRequest(node, "up");
					}
				}
			}
		}else{
			for(CrowdNode node : nodesList){
				if(node.getmType() == NodeType.bulb){
					//Change the node status only if it haven't been changed in the last 5 minutes to avoid annoying the users
					if(System.currentTimeMillis()-node.getLastChangeTS() < 300000){
						this.sendMediateRequest(node, "off");
					}
				}
				//If direct sunlight -> close curtains
				if(newLightData.getValue() > 30000){
					if(node.getmType() == NodeType.curtain){
						//Change the node status only if it haven't been changed in the last 5 minutes to avoid annoying the users
						if(System.currentTimeMillis()-node.getLastChangeTS() < 300000){
							this.sendMediateRequest(node, "down");
						}
					}
				}
			}
		}
	}
	
	public void regulateTemp(CrowdData newLightData){
		if(newLightData.getValue() < CrowdController.getInstance().getUsers().get(newLightData.getAccountId()).getTargetTemp()){
			for(CrowdNode node : nodesList){
				if(node.getmType() == NodeType.fan){
					//Change the node status only if it haven't been changed in the last 5 minutes to avoid annoying the users
					if(System.currentTimeMillis()-node.getLastChangeTS() < 300000){
						this.sendMediateRequest(node,"off");
					}
				}
				if(node.getmType() == NodeType.heater){
					//Change the node status only if it haven't been changed in the last 5 minutes to avoid annoying the users
					if(System.currentTimeMillis()-node.getLastChangeTS() < 300000){
						this.sendMediateRequest(node, "on");
					}
				}
			}
		}else{
			for(CrowdNode node : nodesList){
				if(node.getmType() == NodeType.fan){
					//Change the node status only if it haven't been changed in the last 5 minutes to avoid annoying the users
					if(System.currentTimeMillis()-node.getLastChangeTS() < 300000){
						this.sendMediateRequest(node, "on");
					}
				}
				if(node.getmType() == NodeType.heater){
					//Change the node status only if it haven't been changed in the last 5 minutes to avoid annoying the users
					if(System.currentTimeMillis()-node.getLastChangeTS() < 300000){
						this.sendMediateRequest(node, "off");
					}
				}
			}
		}
	}
	
	public void sendMediateRequest(CrowdNode node, String status){
		String request_url = url+"/mediate?service="+node.getmNID()+"&resource="+node.getmType()+"&status="+status;
		System.out.println(request_url);
		
		try {
			URL urlObj = new URL(request_url);
			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
			connection.setRequestMethod("GET");
			BufferedReader inputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = inputStream.readLine()) != null) {
				response.append(inputLine);
			}
			inputStream.close();
			System.out.println(response.toString());
			//If node is not found, request new nodes list
			if(response.equals("ERROR")){
				this.nodesList.clear();
				this.fetchNodes();
			}else{
				node.setLastChangeTS(System.currentTimeMillis());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void fetchNodes(){
		String request_url = url+"/service/type/xml_rspec";
		StringBuffer response = null;
		
		try {
			URL urlObj = new URL(request_url);
			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
			connection.setRequestMethod("GET");
			BufferedReader inputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			response = new StringBuffer();
	 
			while ((inputLine = inputStream.readLine()) != null) {
				response.append(inputLine);
			}
			inputStream.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
 
		this.parseNodes(response.toString());
	}
	
	public void parseNodes(String response){
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(response)));

            NodeList nl = document.getElementsByTagName("gpio");
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                Element e = (Element) n.getFirstChild();
                String device = e.getAttribute("name");
                NodeType nodeType = NodeType.getType(device);
                this.addNode(new CrowdNode(device.substring(device.indexOf("NID: ")+5, device.length()), nodeType, nodeType.getStatus("default")));
            }
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addNode(CrowdNode node){
		Boolean nodeExist = false;
        for(CrowdNode currentNode : nodesList){
            if(currentNode.getmNID().equals(node.getmNID())) {
                currentNode.setmStatus(node.getmStatus());
                nodeExist = true;
            }
        }
        if(!nodeExist){
        	nodesList.add(node);
        }
	}
}
