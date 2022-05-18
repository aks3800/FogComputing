package threads;


import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;


import global.Variables;
import utils.Util;

/**this thread performs all the actions needed for a node to join the 
 * network as a self-organizing compute node
 * @author Basil
 *
 */
public class JoinHPI  implements Runnable {
	/**another node that implements this protocol which is used as contact
	 * 
	 */
	String contactNode;	

	/**public constructor
	 * @param contactNode another node that implements this protocol which
	 * is used as contact
	 */
	public JoinHPI(String contactNode){
		this.contactNode=contactNode;
	}

	@Override
	public void run() {
		String contactNodeResponse=Util.sendGetRequest("http://"+
				contactNode+"/joinRequest");
		//empty is response
		if(contactNodeResponse.equals("")) {
			//create new group with this node and contactNode
			//and send group contact node
			Util.createNewGroup(contactNode);
			//response is a graph
		}else if(contactNodeResponse.contains(",")) {
			//add new node to this graph
			SimpleWeightedGraph<String, DefaultWeightedEdge> groupGraph=
					Util.convertGraphEdgesStringToGraph(contactNodeResponse);
			//add this node to groupGraph and find distance to other nodes
			Util.addThisNodeToGroup(groupGraph);
			//find the most distant node
			String mostDistantNode=Util.findMostDistantNode(groupGraph, contactNode, 
					Variables.hostNodeIP);
			//
			if(Variables.formerGroupNodes.contains(contactNode)) {
				mostDistantNode=Variables.hostNodeIP;
			}
			//if the group exceeds capacity
			if(groupGraph.vertexSet().size()>Variables.groupSize) {
				//if the most distant node is not this
				if(!mostDistantNode.equals(Variables.hostNodeIP)) {
					//add group to groupGraphs, i.e., the new node has joined
					Variables.groupGraphs.add(groupGraph);
					//check if the most distant has children
					String childrenGroupString=Util.sendGetRequest("http://"+
							mostDistantNode+"/removeChildrenGroup");
					//if there are children
					if(!childrenGroupString.equals("")) {
						//create the group of children
						SimpleWeightedGraph<String, DefaultWeightedEdge> childrenGroup=
								Util.convertGraphEdgesStringToGraph(childrenGroupString);
						//remove the most distant node
						childrenGroup.removeVertex(mostDistantNode);
						//add this node as the new parent
						Util.addThisNodeToGroup(childrenGroup);
						Variables.groupGraphs.add(childrenGroup);
						//update graph in all children nodes
						Util.updateGroupGraphInNeighbours(childrenGroup, "", "");	
					}
					//clone graph
					SimpleWeightedGraph<String, DefaultWeightedEdge> cloneGroup=
							Util.cloneGraph(groupGraph);
					//remove most distant node from group
					groupGraph.removeVertex(mostDistantNode);
					//update graph in all groupNodes
					Util.updateGroupGraphInNeighbours(cloneGroup, contactNode, 
							mostDistantNode);		
				}else{//this==mostDistanceNode
					String closestNode=Util.findClosestNode(groupGraph, contactNode);
					new Thread(new JoinHPI(closestNode));
				}
			}else {
				//add group to groupGraphs, i.e., the new node has joined
				Variables.groupGraphs.add(groupGraph);
				//update graph in all groupNodes
				Util.updateGroupGraphInNeighbours(groupGraph, contactNode, "");		
			}
		}	
	}
}