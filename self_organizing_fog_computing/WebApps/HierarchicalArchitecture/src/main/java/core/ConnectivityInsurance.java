package core;

import java.util.ArrayList;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 * @author Basil
 *this class represents an object which should contain the name of a neighbor and
 *the group graphs it belongs to. it is used for maintaining connectivity when
 *nodes become unresponsive
 */
public class ConnectivityInsurance {
	private String nodeAddress;
	private ArrayList<SimpleWeightedGraph<String, 
	DefaultWeightedEdge>> groupGraphs;
	
	/**public constructor
	 * @param nodeAddress the address of the node
	 * @param groupGraphs a list with the group graphs
	 */
	public ConnectivityInsurance(String nodeAddress, 
			ArrayList<SimpleWeightedGraph<String,DefaultWeightedEdge
			>>groupGraphs) {
		this.nodeAddress=nodeAddress;
		this.groupGraphs=groupGraphs;
	}

	public String getNodeAddress() {
		return nodeAddress;
	}

	public void setNodeAddress(String nodeAddress) {
		this.nodeAddress = nodeAddress;
	}

	public ArrayList<SimpleWeightedGraph<String, DefaultWeightedEdge>> 
	getGroupGraphs() {
		return groupGraphs;
	}

	public void setGroupGraphs(ArrayList<SimpleWeightedGraph<String, 
			DefaultWeightedEdge>> groupGraphs) {
		this.groupGraphs = groupGraphs;
	}
	

	

}
