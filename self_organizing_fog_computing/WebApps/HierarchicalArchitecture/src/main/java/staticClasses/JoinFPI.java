package staticClasses;


import java.util.ArrayList;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;


import global.Variables;
import utils.Util;

/**this thread performs all the actions needed for a node to join the 
 * network as a self-organizing compute node
 * @author Basil
 *
 */
public class JoinFPI {
	/**another node that implements this protocol which is used as contact
	 * 
	 */
	String contactNode;	

	public static void run(String contactNode) {
		int joinGroupFlag=0;
		//get all neighbours from the contactNode
		ArrayList<String> contactNodeNeighbours=Util.
				convertCommaSeparatedStringToArrayList(Util.sendGetRequest("http://"+
						contactNode+"/joinRequest"));
		//remove neighbours from previous attempts to join the contactNode
		contactNodeNeighbours.removeAll(Variables.formerGroupNodes);
		//if the contactNode has neighbours
		if(contactNodeNeighbours.size()>0) {
			//while there are neighbours in the list
			while(contactNodeNeighbours.size()!=0 && joinGroupFlag==0) {
				//find node of closest distance
				int minDistance=Integer.MAX_VALUE;
				String minDistanceNode="";
				for(String node : contactNodeNeighbours) {
					int distance=Util.findHops(node);
					if(distance<minDistance) {
						minDistance=distance;
						minDistanceNode=node;
					}
				}
				//get the groupGraph of the node with the minimum distance
				SimpleWeightedGraph<String,DefaultWeightedEdge> groupGraph;
				String groupEdgesString=Util.sendGetRequest("http://"+contactNode+
						"/getGroupEdges?containsGroupNode="+minDistanceNode);
				groupGraph=Util.convertGraphEdgesStringToGraph(groupEdgesString);
				//add this node to groupGraph and find distance to other nodes
				Util.addThisNodeToGroup(groupGraph);
				//find most distant node
				String mostDistantNode=Util.findMostDistantNode(groupGraph, contactNode,
						Variables.hostNodeIP);
				if(groupGraph.vertexSet().size()<=Variables.groupSize ||
						!mostDistantNode.equals(Variables.hostNodeIP)) {
					//clone the group
					SimpleWeightedGraph<String, DefaultWeightedEdge> cloneGroup=
							Util.cloneGraph(groupGraph);
					//if groupGraph.size>groupSize remove most distant node
					if(groupGraph.vertexSet().size()>Variables.groupSize) {
						groupGraph.removeVertex(mostDistantNode);
					}
					//add new group to groupGraphs
					Variables.groupGraphs.add(groupGraph);
					//empty the list of former neighbour nodes
					Variables.formerGroupNodes.clear();
					//raise joinGroupFlag
					joinGroupFlag=1;
					//update graph in all groupNodes
					Util.updateGroupGraphInNeighbours(cloneGroup, contactNode, 
							mostDistantNode);
				//else if group is full and hostNode is the most distant node
				}else {
					//remove group nodes from contactNodeNeighbours and find 
					//another group
					contactNodeNeighbours.removeAll(groupGraph.vertexSet());
				}
				//if the contactNode does not have neighbours
			}
		}
		//no group has been joined so far, which mean that a new group will be created
		if(joinGroupFlag==0){
			//create new group with this node and contactNode
			//and send group contact node
			Util.createNewGroup(contactNode);	
		}		
	}
}
