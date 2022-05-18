package utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import core.ConnectivityInsurance;
import global.Variables;

/**
 * @author Basil
 *
 */
public class Util {

	/**sends a GET request to targetUrl
	 * @param targetUrl the address of the get request
	 * @return the response of the get request
	 */
	public static String sendGetRequest(String targetUrl) {
		StringBuffer response = new StringBuffer();
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(targetUrl).
					openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "x-www-form-urlencoded");
			connection.setConnectTimeout(600000);
			if (connection.getResponseCode() != 200) {
				throw new RuntimeException("GET Request to "+ targetUrl +
						" failed with Error code : "
						+ connection.getResponseCode());
			}
			BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
					(connection.getInputStream())));
			String output;
			while((output=responseBuffer.readLine()) != null){
				response.append(output+"\n");
			}   
			connection.disconnect();
		}catch (Exception e) {
			System.out.println(e.getMessage());
			return "connectionTimeout";
		}
		return response.toString();
	}

	/**sends a POST request to targetUrl
	 * @param targetUrl the address of the POST request
	 * @param input a string to be sent through the POST request
	 * @return the HTTP code the response of the POST request
	 */
	public static int postRequest(String targetUrl, String body) {
		int responseCode=0;
		try {
			URL url = new URL(targetUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(60000);//5 secs
			connection.setReadTimeout(60000);//5 secs
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "text/plain");

			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());  
			out.write(body);
			out.flush();
			out.close();

			responseCode = connection.getResponseCode();
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while((line = br.readLine() ) != null) {
				//  System.out.println(line);
			}

			connection.disconnect();
		}catch(Exception e){ // TODO: catch the thrown Exception! NEVER catch "Exception"
			// TODO: log instead of print
			System.out.println(e.getMessage());
		}
		return responseCode;
	}

	/**converts a String representation of a graph to a graph object
	 * @param graphEdgesString a string representation of a graph
	 * @return a graph object that corresponds to the string parameter
	 */
	public static SimpleWeightedGraph<String, DefaultWeightedEdge> 
	convertGraphEdgesStringToGraph(String graphEdgesString){
		SimpleWeightedGraph<String, DefaultWeightedEdge> graph=new 
				SimpleWeightedGraph<String, 
				DefaultWeightedEdge>(DefaultWeightedEdge.class);
		//split the string
		String[] edges=graphEdgesString.split(",");
		for(int i=0;i<edges.length;i++) {
			try {//there is an empty cell after split;
				//when the string is received from GET 
				//thats why this is done inside a try/catch
				String edgeSource=edges[i].split("-")[0]; 
				String edgeTarget=edges[i].split("-")[1];
				String edgeWeight=edges[i].split("-")[2];
				//add the fields from the string to the graph object
				graph.addVertex(edgeSource);
				graph.addVertex(edgeTarget);
				DefaultWeightedEdge edge=graph.addEdge(edgeSource, edgeTarget);
				graph.setEdgeWeight(edge, Double.valueOf(edgeWeight));
			}catch(Exception e) {}
		}
		return graph;
	}

	/**measures the latency RTT between this node and targetNode
	 * @param targetNode the address of the target node
	 * @return the latency RTT in milliseconds
	 */
	public static int findLatency(String targetNode) {
		long startTimer = System.nanoTime();
		Util.sendGetRequest("http://"+targetNode+"/checkLatency");
		long stopTimer = System.nanoTime();
		long timeElapsed = stopTimer-startTimer;
		return (int)(timeElapsed/1000000); //convert to milliseconds
	}

	/**convert a graph object to a string
	 * @param graph object
	 * @return the string representation of the graph object
	 */
	public static String convertGraphEdgesToString(
			SimpleWeightedGraph<String, DefaultWeightedEdge> graph) {
		String graphEdgesString="";
		for(DefaultWeightedEdge edge : graph.edgeSet()) {	
			graphEdgesString=graphEdgesString+
					graph.getEdgeSource(edge)+"-"+
					graph.getEdgeTarget(edge)+"-"+
					graph.getEdgeWeight(edge)+",";
		}
		return graphEdgesString;
	}

	/**convert a graph object to a human readable string, i.e., with
	 * new lines among the edges
	 * @param graph object
	 * @return the string representation of the graph object
	 */
	public static String convertGraphEdgesToString2(
			SimpleWeightedGraph<String, DefaultWeightedEdge> graph) {
		String graphEdgesString="";
		for(DefaultWeightedEdge edge : graph.edgeSet()) {	
			graphEdgesString=graphEdgesString+
					graph.getEdgeSource(edge)+"-"+
					graph.getEdgeTarget(edge)+"-"+
					graph.getEdgeWeight(edge)+","+"\n";
		}
		return graphEdgesString;
	}

	/**converts a list of graphs to a string
	 * @param graphsList a list of graphs
	 * @return a string representation of the list of graphs
	 */
	public static String convertListOfGraphsToString(
			ArrayList<SimpleWeightedGraph<String, 
			DefaultWeightedEdge>> graphsList) {
		String graphsString="";
		for(SimpleWeightedGraph<String, DefaultWeightedEdge> graph 
				: graphsList) {	
			graphsString=graphsString+Util.convertGraphEdgesToString(graph)+"!";
		}
		return graphsString;
	}

	/**converts a string to a list of graphs
	 * @param graphsString a string representaion of a list of graphs
	 * @return an Arraylist of graphs objects that correspond to the graphsString
	 */
	public static ArrayList<SimpleWeightedGraph<String,
	DefaultWeightedEdge>>convertStringToListOfGraphs(String graphsString) {
		ArrayList<SimpleWeightedGraph<String,DefaultWeightedEdge>>graphsList=
				new ArrayList<SimpleWeightedGraph<String
				,DefaultWeightedEdge>>();
		String[] graphs=graphsString.split("!");
		for(int i=0;i<graphs.length;i++) {
			try {//there is an empty cell after split;
				//when the string is received from GET 
				//thats why this is done inside a try/catch
				graphsList.add(Util.convertGraphEdgesStringToGraph(graphs[i]));
			}catch(Exception e) {}
		}
		return graphsList;
	}

	/**finds the node in a graph that has the maximum sum of weights of
	 * incoming edges
	 * @param graph the graph to be examined
	 * @param contactNode to be excluded from the search because it is
	 * used as contact for most distant node to join again
	 * @return the most distant node
	 */
	public static String findMostDistantNode(SimpleWeightedGraph
			<String, DefaultWeightedEdge> graph, String contactNode, String newNode) {
		double maxSumDistance=0;
		String maxSumDistanceNode="";
		//if there is a newNode parameter, use newNode as initial maxDistantNode
		if(!newNode.equals("")) {
			//newNode is the initial most distant
			maxSumDistanceNode=newNode;
			//find newNode max distance
			for(DefaultWeightedEdge edge : graph.incomingEdgesOf(newNode)) {
				maxSumDistance=maxSumDistance+graph.getEdgeWeight(edge);
			}
		}
		//most distant node search, excludes the contactNode because the
		//contactNode is used as contact for mostDistantNode to join again
		for(String node : graph.vertexSet()) {
			if(!node.equals(contactNode)) {
				double sumDistance=0;
				for(DefaultWeightedEdge edge : graph.incomingEdgesOf(node)) {
					sumDistance=sumDistance+graph.getEdgeWeight(edge);
				}
				if(sumDistance>maxSumDistance) {
					maxSumDistance=sumDistance;
					maxSumDistanceNode=node;
				}
			}
		}
		return maxSumDistanceNode;
	}

	/**finds the node in a graph that has the minimum distance to this node
	 * excluding the contact node. used in hierarchical organization
	 * @param graph the graph to be examined
	 * @param parentNode the parent of the group (excluded from the search)
	 * @return the closest node
	 */
	public static String findClosestNode(SimpleWeightedGraph
			<String, DefaultWeightedEdge> graph, String parentNode) {
		double minDistance=Double.POSITIVE_INFINITY;
		String closestNode="";
		for(String neighborNode : graph.vertexSet()) {
			if(!neighborNode.equals(parentNode) && !neighborNode.equals(Variables.hostNodeIP)) {
				DefaultWeightedEdge edge=graph.getEdge(neighborNode, Variables.hostNodeIP);
				if(graph.getEdgeWeight(edge)<minDistance) {
					minDistance=graph.getEdgeWeight(edge);
					closestNode=neighborNode;
				}
			}
		}
		return closestNode;
	}

	/**converts a String of comma separated values into an Arraylist of values
	 * @param commaSeparatedString a string of comma seperated values
	 * @return an ArrayList object witht the values of commaSeparatedString
	 */
	public static ArrayList<String> convertCommaSeparatedStringToArrayList(
			String commaSeparatedString){
		ArrayList<String> valuesList=new ArrayList<String>();
		String[] valuesTable=commaSeparatedString.split(",");
		for(int i=0;i<valuesTable.length-1;i++) {//last cell is empty
			valuesList.add(valuesTable[i]);
		}
		return valuesList;
	}

	/**converts a list into a String of comma separated values
	 * @param arrayList a list of values
	 * @return a string of comma separated values from arrayList
	 */
	public static String convertArrayListToCommaSeparatedString(
			ArrayList<String> arrayList){
		String string="";
		for(int i=0;i<arrayList.size()-1;i++) {
			string=arrayList.get(i)+",";
		}
		return string;
	}

	/**updates a group graph in all the nodes of the group
	 * @param groupGraph the group graph to be updated
	 * @param contactNode the contact node that led to this update
	 * used for to distinguishing the group from the other groups
	 * @return void
	 */
	public static void updateGroupGraphInNeighbours(SimpleWeightedGraph
			<String, DefaultWeightedEdge> groupGraph, String contactNode, 
			String mostDistantNode) {
		String updatedGroupEdgesString=Util.convertGraphEdgesToString(
				groupGraph);
		//if the contactNode is not in the groupGraph, it is the case that instead
		//of the contact, the mostDistant is used, and only the children should be
		//updated.
		if(!contactNode.equals("")) {
			//update contactNode first because it might be used by new joinRequests
			Util.sendGetRequest("http://"+contactNode+"/putGroupGraph?"+
					"groupEdgesString="+updatedGroupEdgesString+
					"&contactNode="+contactNode);
		}
		//then update all neighbours
		for(String groupNode : groupGraph.vertexSet()) {
			if(!groupNode.equals(Variables.hostNodeIP) &&
					!groupNode.equals(contactNode) &&
					!groupNode.equals(mostDistantNode)) {
				Util.sendGetRequest("http://"+groupNode+"/putGroupGraph?"+
						"groupEdgesString="+updatedGroupEdgesString+
						"&contactNode="+contactNode);
			}
		}
		//update most distant last
		if(!mostDistantNode.equals("")) {
			//update contactNode first because it might be used by new joinRequests
			Util.sendGetRequest("http://"+mostDistantNode+"/putGroupGraph?"+
					"groupEdgesString="+updatedGroupEdgesString+
					"&contactNode="+contactNode);
		}
	}

	/**updates the connectivity insurance in all the neighbors
	 * @return void
	 */
	public static void updateConnectivityInsuranceInNeighbours() {
		for(SimpleWeightedGraph<String, DefaultWeightedEdge> group
				: Variables.groupGraphs) {
			for(String groupNode : group.vertexSet()) {
				Util.sendGetRequest("http://"+groupNode+
						"/postConnectivityInsurance?"+
						"nodeAddress="+Variables.hostNodeIP+
						"&groupGraphs="+Util.convertListOfGraphsToString(
								Variables.groupGraphs));
			}
		}
	}

	/**makes a system call to traceroute in order to find hop count
	 * @param targetNode the address of a node in the network
	 * @return the number of hops
	 */
	public static int findHops(String targetNode) {
		targetNode=targetNode.split(":")[0];
		String os = System.getProperty("os.name").toLowerCase();
		String output="";
		int hops=0;
		//if 
		if(Variables.Organization.equals("HPA") ||
				Variables.Organization.equals("FPA")) {
			return 1;
		}
		try {
			Process traceRt;
			if(os.contains("win")) {
				traceRt=Runtime.getRuntime().exec("tracert -h 40 -w 1000 -d "+targetNode);
				//-h 20 for max hop, -w 1000 time to to live, -d no hostnames
			}else{
				traceRt=Runtime.getRuntime().exec("sudo traceroute -m 40 -q 1 -n -I "+targetNode);
				//-m 20 for max hop, -w 1 time to to live, -I icmp echo, -n no hostname
			}
			// read the output from the command
			BufferedInputStream bis = new BufferedInputStream(traceRt.
					getInputStream());
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			int result = bis.read();
			while(result != -1) {
				buf.write((byte) result);
				result = bis.read();
			}
			// StandardCharsets.UTF_8.name() > JDK 7
			output=buf.toString("UTF-8");
			int unrechableFlag=0;
			String[] lines=output.split("\n");
			if(os.contains("win")) {
				for (int i=lines.length-3;i>3;i--) {
					lines[i]=lines[i].trim();
					String[] fields=lines[i].split("\\s+");
					if(!fields[1].equals("*")) {
						hops=Integer.valueOf(fields[0]);
						break;
					}else {
						unrechableFlag=1;
					}
				}
			}else {
				for (int i=lines.length-1;i>0;i--) {
					lines[i]=lines[i].trim();
					String[] fields=lines[i].split("\\s+");
					if(!fields[1].equals("*")) {
						hops=Integer.valueOf(fields[0]);
						break;
					}else {
						unrechableFlag=1;
					}
				}
			}
			hops=hops+unrechableFlag;
			//for testing			
			//			Random random=new Random();
			//			return (random.nextInt(30))+1;

		}catch(Exception e){
			System.out.println("error while executing traceroute");
		}

		return hops;
	}

	/**
	 * broadcasts a message to all the groups apart from the group of the
	 * original sender
	 * @param message the message to be broadcast
	 */
	public static void globalBroadcast(String message) {
		for(int i=0;i<Variables.groupGraphs.size();i++) {
			if(!Variables.groupGraphs.get(i).vertexSet().contains(Variables.
					originalSender)) {
				Util.groupBroadcast(message, Variables.groupGraphs.get(i));
			}
		}
	}

	/**
	 * broadcasts a message to all the neighbors of a group graph
	 * @param message the message to be broadcast
	 * @param groupGraph the group to which the message will be broadcast.
	 */
	public static void groupBroadcast(String message, SimpleWeightedGraph
			<String, DefaultWeightedEdge> groupGraph) {
		ArrayList<String> receivers=new ArrayList<String>();
		for(String node : groupGraph.vertexSet()) {
			if(!node.equals(Variables.hostNodeIP)) {
				receivers.add(node);
			}
		}
		Util.groupMulticast(message, receivers, groupGraph);
	}	

	/**the fault tolerance mechanism. this node join each one of the groups
	 * of the unresponsive node
	 * @param unrNode a node that does not respond to HTTP requests
	 */
	public static void reEstablishConnection(String unrNode) {
		for(ConnectivityInsurance con : Variables.connectivityInsurance) {
			if(con.getNodeAddress().equals(unrNode)) {
				for(SimpleWeightedGraph<String, DefaultWeightedEdge>
				groupGraph  : con.getGroupGraphs()) {
					if(!groupGraph.vertexSet().contains(Variables.hostNodeIP)) {
						groupGraph.removeVertex(unrNode);
						//						new Thread(new JoinFPI((String) groupGraph.vertexSet()
						//							.toArray()[0])).start();
					}
				}
			}
		}	
	}

	/**
	 * sends a message to the nodes of the list "receivers" based on
	 * a weight minimisation logic. the nodes of the list and senderNode
	 * all belong to the same group
	 * @param message the message to be sent
	 * @param receivers a list of the destinations of the multicast
	 */
	public static void groupMulticast(String message, ArrayList<String> receivers
			, SimpleWeightedGraph<String, DefaultWeightedEdge> groupGraph) {
		if(receivers.remove(Variables.hostNodeIP)){
			//this node is a receiver of the message not just a liaison
			Variables.message=message;
		}
		//to deactivate intelligent messaging
		/*if(Global.hopAwareEnabled==0) {
			//without hop minimization
			for(MyNode receiverNode : receivers) {
				//send message
				Global.broadcastMessages++;
				Global.broadcastHops+=Util.findShortestPathWeight(senderNode, receiverNode);
				if(receiverNode.getDisconnectedFlag()) {
					groupGraph.removeVertex(receiverNode);
					//notify neighbors of the disconnected node
					Global.reEstablishConnectionOverhead+=groupGraph.vertexSet().size()-1;
					senderNode.reEstablishConnection(receiverNode);
					for(MyNode node : groupGraph.vertexSet()) {
						//upon notification every node removes connectivity insurance without extra messaging
						node.getConnectivityInsuranceL1().remove(receiverNode.getNeighborGroups());
					}
				}else {
					//send response
					//	Global.broadcastMessages++;
					//	Global.broadcastHops+=Util.findShortestPathWeight(senderNode, receiverNode);
					if(receiverNode.getMessageId()==senderNode.getMessageId()) {
						Global.redundantMessages++;
					}
					receiverNode.setMessageId(senderNode.getMessageId());
					receiverNode.setOriginalSender(senderNode);
					receiverNode.setLatencyInMessages(senderNode.getLatencyInMessages()+1);
					receiverNode.setLatencyInHops(senderNode.getLatencyInHops()+(int)Util.findShortestPathWeight
							(senderNode, receiverNode));
					Util.broadcast(receiverNode);
				}
			}
		}else {*/
		//with hop minimization
		//in tempGraph,the vertices are references to the actual nodes, 
		//thus do not modify. the edges are newly generated thus,
		//do modify at will
		SimpleWeightedGraph<String, DefaultWeightedEdge> tempGraph=
				new SimpleWeightedGraph<String, DefaultWeightedEdge>
		(DefaultWeightedEdge.class);
		for(String node : groupGraph.vertexSet()) {
			tempGraph.addVertex(node);
		}
		for(DefaultWeightedEdge edge : groupGraph.edgeSet()) {
			DefaultWeightedEdge tempEdge=tempGraph.addEdge(groupGraph.
					getEdgeSource(edge), groupGraph.getEdgeTarget(edge));
			tempGraph.setEdgeWeight(tempEdge, groupGraph.getEdgeWeight(edge));
		}
		ArrayList<ArrayList<String>> sendPaths=new ArrayList<ArrayList<String>>();
		DijkstraShortestPath<String, DefaultWeightedEdge> dijkstra=
				new DijkstraShortestPath<String, DefaultWeightedEdge>(tempGraph);
		for(int i=0;i<receivers.size();i++) {
			double minShortestPath=Double.POSITIVE_INFINITY;
			double currentShortestPath=0;
			String shortestPathNode=null;
			for(String node : receivers) {
				currentShortestPath=dijkstra.getPathWeight(Variables.hostNodeIP,
						node);
				if(currentShortestPath<minShortestPath && currentShortestPath>0){
					minShortestPath=currentShortestPath;
					shortestPathNode=node;
				}
			}
			sendPaths.add((ArrayList<String>)dijkstra.getPath(Variables.
					hostNodeIP, shortestPathNode).getVertexList());
			for(DefaultWeightedEdge edge : dijkstra.getPath(Variables.hostNodeIP,
					shortestPathNode).getEdgeList()) {
				//maybe something very small but greater than zero,
				//so that it also achieves min latency
				tempGraph.setEdgeWeight(edge, 0);
			}
		}
		ArrayList<String> oneHopNodes=new ArrayList<String>();
		ArrayList<ArrayList<String>> sendPathsReduced=new ArrayList<ArrayList
				<String>>();
		for(ArrayList<String> path : sendPaths) {
			if(!oneHopNodes.contains(path.get(1))) {
				oneHopNodes.add(path.get(1));
			}
		}
		for(String node : oneHopNodes) {
			ArrayList<String> pathReduced=new ArrayList<String>();
			pathReduced.add(node);
			sendPathsReduced.add(pathReduced);
		}
		for(ArrayList<String> path : sendPaths) {
			for(ArrayList<String> pathReduced : sendPathsReduced) {
				if(path.get(1)==pathReduced.get(0)) {
					pathReduced.add(path.get(path.size()-1));
				}				
			}
		}
		for(ArrayList<String> sendPath : sendPathsReduced) {
			//the first node of the list is the next hop receiver
			String receiverNode=sendPath.remove(0);
			//send message within the group to the next hop receiver
			String sendResponse=Util.sendGetRequest("http://"+receiverNode+
					"/sendMessage?message="+Variables.message+
					"&receivers="+convertArrayListToCommaSeparatedString(sendPath)+
					"&originalSender="+Variables.hostNodeIP);
			//if the node is unresponsive
			if(sendResponse.equals("connectionTimeout")) {
				//in case the receiverNode was also a destination not just a liaison
				sendPath.remove(receiverNode);
				groupGraph.removeVertex(receiverNode);
				//notify neighbors of the disconnected node
//				Util.updateGroupGraphInNeighbours(groupGraph, Variables.hostNodeIP);
				Util.reEstablishConnection(receiverNode);	
				//calculate another path to reach the nodes in "sendPath"
				Util.groupMulticast(message, sendPath, groupGraph);
			}else {

			}
		}
	}

	/**returns a neighbor node in the group using a cyclic counter.
	 *each time this function is called, it return the next neighbor.
	 * @param groupGraph the group that is used for finding neighbors.
	 */
	public static String getContactChild(SimpleWeightedGraph<String, 
			DefaultWeightedEdge> groupGraph) {
		String contactChild="";
		//check if cyclic counter is within limits
		if(Variables.childCounter==Variables.groupSize) {
			Variables.childCounter=0;
		}
		//check if cyclic counter points to the same node
		if(Variables.hostNodeIP.equals((String)groupGraph.vertexSet().
				toArray()[Variables.childCounter])) {
			if(Variables.childCounter==Variables.groupSize) {
				Variables.childCounter=0;
			}else{
				Variables.childCounter++;
			}
		}
		//get the child indicated by the counter
		contactChild=(String)groupGraph.vertexSet().toArray()[Variables.childCounter];
		//increase counter
		Variables.childCounter++;
		return contactChild;
	}

	/**creates a new group with this node and the contact node
	 * @param contactNode the contact node
	 */
	public static void createNewGroup(String contactNode) {
		//create new group
		SimpleWeightedGraph<String, DefaultWeightedEdge> newGroupGraph=new 
				SimpleWeightedGraph<String, 
				DefaultWeightedEdge>(DefaultWeightedEdge.class);
		//add group to groupGraphs
		Variables.groupGraphs.add(newGroupGraph);
		//add this node and contact node
		newGroupGraph.addVertex(Variables.hostNodeIP);
		newGroupGraph.addVertex(contactNode);
		//add edge between this node and contact node
		DefaultWeightedEdge edge=newGroupGraph.addEdge(Variables.hostNodeIP,
				contactNode);
		//find distance in hops
		newGroupGraph.setEdgeWeight(edge, Util.findHops(contactNode));	
		//update groupGraph in contactNode
		String newGroupEdgesString=Util.convertGraphEdgesToString(newGroupGraph);
		Util.sendGetRequest("http://"+contactNode+"/postGroupGraph?"+
				"groupEdgesString="+newGroupEdgesString);
	}

	/**adds this node to a group of nodes
	 * @param groupGraph the group to be used for adding this node
	 */
	public static void addThisNodeToGroup(SimpleWeightedGraph<String, 
			DefaultWeightedEdge> groupGraph) {
		groupGraph.addVertex(Variables.hostNodeIP);
		//find distance to each group node
		for(String groupNode : groupGraph.vertexSet()) {
			if(!groupNode.equals(Variables.hostNodeIP)) {
				DefaultWeightedEdge edge=groupGraph.addEdge(Variables.
						hostNodeIP, groupNode);
				groupGraph.setEdgeWeight(edge, Util.findHops(groupNode));	
			}
		}
	}
	/**reset all the fields of the node except the hostNodeIP
	 */
	public static void resetGroups() {
		Variables.groupGraphs=new ArrayList
				<SimpleWeightedGraph<String,DefaultWeightedEdge>>();
		Variables.formerGroupNodes=new ArrayList<String>();
		Variables.isRoot=false;
		Variables.childCounter=0;
		//messaging
		//   	Variables.messageSentTimes=new ArrayList<String>();
		Variables.messageArrivalDate=null;
		Variables.originalSender="";
		Variables.message="";
	}

	/**clone a graph
	 */
	public static SimpleWeightedGraph<String, DefaultWeightedEdge> cloneGraph(
			SimpleWeightedGraph<String, DefaultWeightedEdge> groupGraph) {
		//convert the group into a string
		String groupGraphString=Util.convertGraphEdgesToString(groupGraph);
		//convert the string to a graph
		SimpleWeightedGraph<String, DefaultWeightedEdge> groupClone=
				convertGraphEdgesStringToGraph(groupGraphString);		
		return groupClone;
	}




}
