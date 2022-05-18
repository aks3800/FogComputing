package staticClasses;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import global.Variables;
import utils.Util;

/**this thread updates an existing group graph
 * @author Basil
 *
 */
public class UpdateGroupGraph {

	/**String value that contains all the edges of a graph (source, target, weight)
	 * 
	 */

	/**the contact of the new node that caused the update
	 * 
	 */

	public static void run(String groupEdgesString, String contactNode) {
		//create graph out of groupEdgesString
		SimpleWeightedGraph<String, DefaultWeightedEdge> updatedGroupGraph;
		updatedGroupGraph=Util.convertGraphEdgesStringToGraph(groupEdgesString);
		//if the updated graph does not contain the contact node, it is the 
		//special case of replacing a parent node in HPI
		if(Variables.Organization.equals("HPI") &&
				!updatedGroupGraph.containsVertex(contactNode)){
			Variables.groupGraphs.set(0, updatedGroupGraph);
		}else {
			//find a neighbour node inside the updatedGroupGraph in order to find 
			//which one of the groups is updated. first pick contact
			String neighbour=contactNode;//**************missing the case of neighbour being the new node
										//**************wont happen because the new node is at the end
			if(Variables.hostNodeIP.equals(contactNode)) {
				neighbour=(String) updatedGroupGraph.vertexSet().toArray()[0];
				//if the neighbor is the same as this node
				if(neighbour.equals(Variables.hostNodeIP)) {
					neighbour=(String) updatedGroupGraph.vertexSet().toArray()[1];
				}
			}
			//if this node is the root, update the one group that the root belongs to
			if(Variables.isRoot) {
				Variables.groupGraphs.set(1, updatedGroupGraph);
			}else {
				//find the group from Variables.groupGraphs that contains the neighbour
				//and replace it by the updatedGroupGraph
				for(int i=0;i<Variables.groupGraphs.size();i++) {
					if(Variables.groupGraphs.get(i).vertexSet().contains(neighbour)) {
						Variables.groupGraphs.set(i, updatedGroupGraph);
						break;
					}
				}
			}
			//if the size of the updated graph is greater that the maximum group size
			if(updatedGroupGraph.vertexSet().size()>Variables.groupSize) {
				//find most distant node
				String mostDistantNode=Util.findMostDistantNode(updatedGroupGraph, contactNode, "");
				//remove most distant node
				updatedGroupGraph.removeVertex(mostDistantNode);
				//if the most distant node is this node
				if(mostDistantNode.equals(Variables.hostNodeIP)) {
					//the most distant node leaves the group
					Variables.groupGraphs.remove(updatedGroupGraph);
					Variables.formerGroupNodes.add(contactNode);
					//the most distant node joins the network again
					//in HPA and FPA there is no most distant node
					if(Variables.Organization.equals("HPI")) {
						JoinHPI.run(Variables.initialContactNode);
					}else if(Variables.Organization.equals("FPI")) {
						JoinFPI.run(contactNode);
					}
				}
			}
		}
	}
}
