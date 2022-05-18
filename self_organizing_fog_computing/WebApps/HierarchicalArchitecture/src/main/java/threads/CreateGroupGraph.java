package threads;

import global.Variables;
import utils.Util;

/**this thread creates a new group graph which is represented by a string and
 * given to the constructor as a parameter
 * @author Basil
 *
 */
public class CreateGroupGraph  implements Runnable {
	
	/**String value that contains all the edges of a graph (source, target, weight)
	 * 
	 */
	String groupEdgesString;
	
	/**public constructor
	 * @param groupEdgesString String value that contains all the edges of a graph
	 * (source, target, weight)
	 */
	public CreateGroupGraph(String groupEdgesString){
		this.groupEdgesString=groupEdgesString;
	}

	@Override
	public void run() {
		Variables.groupGraphs.add(Util.convertGraphEdgesStringToGraph(groupEdgesString));
	}

	

}
