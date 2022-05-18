package threads;

import core.ConnectivityInsurance;
import global.Variables;
import utils.Util;

/**this thread updates an existing connectivity insurance
 * @author Basil
 *
 */
public class UpdateConnectivityInsurance  implements Runnable {
	
	/**the address of the node whose connectivity is updated
	 * 
	 */
	String nodeAddress;
	
	/**the updates group graphs
	 * 
	 */
	String groupGraphs;
	
	/**public constructor
	 * @param nodeAddress the address of the node whose connectivity is updated
	 * @param groupGraphs the updates group graphs
	 */
	public UpdateConnectivityInsurance(String nodeAddress, String groupGraphs){
		this.nodeAddress=nodeAddress;
		this.groupGraphs=groupGraphs;
	}

	@Override
	public void run() {
		int nodeExistsFlag=0;
		//find the connectivity insurance that requests update
		for(ConnectivityInsurance con : Variables.connectivityInsurance) {
			if(con.getNodeAddress().equals(nodeAddress)) {
				//update the connectivity insurance of nodeAddress
				con.setGroupGraphs(Util.convertStringToListOfGraphs(groupGraphs));
				nodeExistsFlag=1;
				break;
			}
		} 
		//if the connectivity insurance is new, create a new connectivity insurance
		//for nodeAddress
		if(nodeExistsFlag==0) {
			ConnectivityInsurance newCon=new ConnectivityInsurance(nodeAddress,
					Util.convertStringToListOfGraphs(groupGraphs));
			Variables.connectivityInsurance.add(newCon);
		}
	}
}
