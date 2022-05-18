package global;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import core.ConnectivityInsurance;

import java.util.ArrayList;
import java.util.Date;


/**this class contains globally public variables
 * @author Basil
 *
 */ 
public class Variables {
////////////////////////////joining//////////////////////////////////////////
	/**the IP of the hostnode
	 * 
	 */
	public static String hostNodeIP="xxx.xxx.xxx.xxx:xxxx";	
	
	/**the IP of the node used in the initial contact request
	 * /putContactNode
	 * 
	 */
	public static String initialContactNode="xxx.xxx.xxx.xxx:xxxx";	
	
	/**the size of the groups (system parameter)
	 * 
	 */
	public static int groupSize=4;
	
	/**a list of all the group graphs that the node belongs to
	 * 
	 */
	public static ArrayList<SimpleWeightedGraph
	<String,DefaultWeightedEdge>>groupGraphs=new ArrayList
	<SimpleWeightedGraph<String,DefaultWeightedEdge>>();
	
	/**the nodes of the group that the hostNode belonged to before trying
	 *to rejoin for being the most distant. this is used for the joining procedure
	 */
	public static ArrayList<String> formerGroupNodes=new ArrayList<String>();
	
	/**defines the organization of the nodes i.e.:
	 * HPA hierarchical proximity agnostic
	 * HPI hierarchical proximity-integrated
	 * FPA flat proximity agnostic
	 * FPI flat proximity-integrated 
	 */
	public static String Organization="xxx"; //HPA, HPI, FPA, FPI
///////////////////////////hierarchical///////////////////////////////////////
	/**shows if this node is the root of the hierarchical organization
	 * 
	 */
	public static boolean isRoot=false;
	
	/**internal counter for forwarding new nodes to children interchangeably 
	 * 
	 */
	public static int childCounter=0;
	
	/** in the hierarchical organization each node has 1 or 2 groups
	 * 
	 */
	public static int numOfGroups=2;
	
	
////////////////////////////messaging/////////////////////////////////////////
	/**
	 * a message field
	 */
	public static String message;
	
	/**
	 * the original sender of the message
	 */
	public static String originalSender;
	
	/**
	 * the time that the message arrived
	 */
	public static Date messageArrivalDate=null;
	
	/**
	 * the time that the message was sent to the destination
	 */
//	public static ArrayList<String> messageSentTimes=new ArrayList<String>();
	
///////////////////////////fault tolerance///////////////////////////////////
	/**list of ConnectivityInsurance objects. this is used for maintaining
	 * connectivity when unresponsive nodes appear
	 */
	public static ArrayList<ConnectivityInsurance> connectivityInsurance=
			new ArrayList<ConnectivityInsurance>();
}
