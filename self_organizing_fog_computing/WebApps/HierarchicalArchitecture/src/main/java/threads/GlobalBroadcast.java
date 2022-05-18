package threads;

import utils.Util;

/**this thread initiates the global broadcast of a message
 * @author Basil
 *
 */
public class GlobalBroadcast implements Runnable {
	
	/**the message to be broadcast
	 * 
	 */
	String message;
	
	/**public constructor
	 * @param message the message to be broadcast
	 */
	public GlobalBroadcast(String message){
		this.message=message;
	}

	@Override
	public void run() {
		Util.globalBroadcast(message);
	}

	

}
