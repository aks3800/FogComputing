package threads;

import java.util.ArrayList;
import java.util.Date;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import global.Variables;
import utils.Util;

/**this thread sends a message to a list of receivers
 * @author Basil
 *
 */
public class PostMessage implements Runnable {
	
	/**the message to be sent
	 * 
	 */
	String message;
	
	/**the node which is the destination of this message
	 * 
	 */
	String destinationNode;
	
	/**public constructor
	 * @param message the message to be sent
	 * @param receivers the list of the receivers of the message
	 * @param originalSender the node that initiated the transmission
	 */
	public PostMessage(String destinationNode, String message){
		this.destinationNode=destinationNode;
		this.message=message;
	}

	@Override
	public void run() {
		String targetUrl="http://"+destinationNode+"/postMessage?originalSender="+Variables.hostNodeIP;
//		Variables.messageSentTimes.add(destinationNode+" "+new Date().getTime());
		Util.postRequest(targetUrl, message);
	}



}
