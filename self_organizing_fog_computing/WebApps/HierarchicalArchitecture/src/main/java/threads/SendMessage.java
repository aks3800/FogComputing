package threads;

import java.util.ArrayList;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import global.Variables;
import utils.Util;

/**this thread sends a message to a list of receivers
 * @author Basil
 *
 */
public class SendMessage implements Runnable {
	
	/**the message to be sent
	 * 
	 */
	String message;
	
	/**the list of the receivers of the message
	 * 
	 */
	String receivers;
	
	/**the node that initiated the transmission
	 * 
	 */
	String originalSender;
	
	/**public constructor
	 * @param message the message to be sent
	 * @param receivers the list of the receivers of the message
	 * @param originalSender the node that initiated the transmission
	 */
	public SendMessage(String message, String receivers, String originalSender){
		this.message=message;
		this.receivers=receivers;
		this.originalSender=originalSender;
	}

	@Override
	public void run() {
		ArrayList<String> receiversList=Util.convertCommaSeparatedStringToArrayList
				(receivers);
		//if this node is a receiver of the message
		if(receiversList.remove(Variables.hostNodeIP)) {
			Variables.message=message;
			Variables.originalSender=originalSender;
		}
		SimpleWeightedGraph<String,DefaultWeightedEdge> groupForMulticast=
				new SimpleWeightedGraph<String,DefaultWeightedEdge>(
						DefaultWeightedEdge.class);
		//find the group to which this message is addressed
		for(SimpleWeightedGraph<String,DefaultWeightedEdge> group : 
				Variables.groupGraphs) {
			if(group.containsVertex(receiversList.get(0))) {
				groupForMulticast=group;
			}
		}
		//send the message to the receivers
		Util.groupMulticast(message, receiversList, groupForMulticast);
		//send the message to the other groups of this node
		Util.globalBroadcast(message);
	}



}
