package staticClasses;

import global.Variables;
import utils.Util;

/**this thread creates a new group graph which is represented by a string and
 * given to the constructor as a parameter
 * @author Basil
 *
 */
public class CreateGroupGraph {
	
	/**String value that contains all the edges of a graph (source, target, weight)
	 * 
	 */
	public static void run(String groupEdgesString) {
		Variables.groupGraphs.add(Util.convertGraphEdgesStringToGraph(groupEdgesString));
	}
}
