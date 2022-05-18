package threads;


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
public class JoinFPA  implements Runnable {
	/**another node that implements this protocol which is used as contact
	 * 
	 */
	String contactNode;	
	
	/**public constructor
	 * @param contactNode another node that implements this protocol which
	 * is used as contact
	 */
	public JoinFPA(String contactNode){
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
			//add group to groupGraphs, i.e., the new node has joined
			Variables.groupGraphs.add(groupGraph);
			//update groupGraph in neighbours
			Util.updateGroupGraphInNeighbours(groupGraph, contactNode, "");
		}
	}
}
