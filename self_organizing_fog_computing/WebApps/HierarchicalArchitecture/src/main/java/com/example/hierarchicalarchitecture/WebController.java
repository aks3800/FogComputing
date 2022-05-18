package com.example.hierarchicalarchitecture;


import global.Variables;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import staticClasses.*;
import threads.PostMessage;
import utils.Util;

import java.util.Date;
import java.util.Map;

/**
 * this class contains all the resources of the API
 *
 * @author Basil
 */
@CrossOrigin(origins = "*")
@RestController
@Configuration
@PropertySource("classpath:application.properties")
public class WebController {

    /**
     * triggers the node to join the network through the contactNode
     *
     * @param contactNode the address of the node used as contact point
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/putContactNode")
    public ResponseEntity<String> putContactNode(@RequestParam Map<String,
            String> customQuery) {
        Variables.initialContactNode = customQuery.get("contactNode");
        if (Variables.Organization.equals("HPA")) {
            JoinHPA.run(Variables.initialContactNode);
        } else if (Variables.Organization.equals("HPI")) {
            JoinHPI.run(Variables.initialContactNode);
        } else if (Variables.Organization.equals("FPA")) {
            JoinFPA.run(Variables.initialContactNode);
        } else if (Variables.Organization.equals("FPI")) {
            JoinFPI.run(Variables.initialContactNode);
        }
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    /**
     * return empty response in order for the sender to measure
     * RTT latency
     *
     * @return The system call to traceroute is implemented in Utils
     */
    @RequestMapping(method = RequestMethod.GET, value = "/checkLatency")
    public ResponseEntity<String> checkLatency() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    /**
     * return all the neighbors of this node (comma separated)
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/joinRequest")
    public ResponseEntity<String> getAllNeighbourNodes() {
        String response = "";
        if (Variables.Organization.equals("HPA")) {
            //either a group with space or an IP to forward the request or empty
            if (Variables.groupGraphs.size() == Variables.numOfGroups) {
                //group with space
                if (Variables.groupGraphs.get(1).vertexSet().size() < Variables.groupSize) {
                    response = Util.convertGraphEdgesToString(Variables.groupGraphs.get(1));
                    // IP to forward the join-request
                } else {
                    response = Util.getContactChild(Variables.groupGraphs.get(1));
                }
            }//else empty response
        } else if (Variables.Organization.equals("HPI")) {
            //either a group or empty
            if (Variables.groupGraphs.size() == Variables.numOfGroups) {
                //group
                response = Util.convertGraphEdgesToString(Variables.groupGraphs.get(1));
            }//else empty response
        } else if (Variables.Organization.equals("FPA")) {
            //either a group or empty
            for (SimpleWeightedGraph<String, DefaultWeightedEdge> groupGraph
                    : Variables.groupGraphs) {
                if (groupGraph.vertexSet().size() < Variables.groupSize) {
                    response = Util.convertGraphEdgesToString(groupGraph);
                    break;
                }
            }//else empty response
        } else if (Variables.Organization.equals("FPI")) {
            //all the neighbor nodes
            for (SimpleWeightedGraph<String, DefaultWeightedEdge> groupGraph
                    : Variables.groupGraphs) {
                for (String neighbourNode : groupGraph.vertexSet()) {
                    response = response + neighbourNode + ",";
                }
            }
        }
        return new ResponseEntity<String>(response, HttpStatus.OK);
    }

    /**
     * return the group that contains GroupNode
     *
     * @param GroupNode the address of a neighbor node
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/getGroupEdges")
    public ResponseEntity<String> getGroupNodes(@RequestParam Map<String,
            String> customQuery) {
        for (SimpleWeightedGraph<String, DefaultWeightedEdge> groupGraph
                : Variables.groupGraphs) {
            if (groupGraph.vertexSet().contains(customQuery.get("containsGroupNode"))) {
                return new ResponseEntity<String>(Util.convertGraphEdgesToString(groupGraph),
                        HttpStatus.OK);
            }
        }
        return new ResponseEntity<String>("", HttpStatus.OK);
    }

    /**
     * return the latency RTT from this node to targetNode
     *
     * @param targetNode the address of any node (in the network)
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/requestLatencyCheck")
    public ResponseEntity<String> requestLatencyCheck(@RequestParam Map<String,
            String> customQuery) {
        return new ResponseEntity<String>(String.valueOf(Util.findLatency(customQuery.
                get("targetNode"))), HttpStatus.OK);
    }

    /**
     * update an existing groupGraph
     *
     * @param groupEdgesString string representation of a group
     * @param contactNode      the address of a node that belong in the group
     *                         of groupEdgesString
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/putGroupGraph")
    public ResponseEntity<String> putGroupGraph(@RequestParam Map<String,
            String> customQuery) {
        UpdateGroupGraph.run(customQuery.get("groupEdgesString"),
                customQuery.get("contactNode"));
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    /**
     * create new groupGraph
     *
     * @param groupEdgesString string representation of the new group to be
     *                         created
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/postGroupGraph")
    public ResponseEntity<String> postGroupGraph(@RequestParam Map<String,
            String> customQuery) {
        CreateGroupGraph.run(customQuery.get("groupEdgesString"));
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    /**
     * return all the group graphs of this node
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/getGroupGraphs")
    public ResponseEntity<String> getGroupGraphs() {
        String allGroupGraphEdges = "";
        for (SimpleWeightedGraph<String, DefaultWeightedEdge> groupGraph :
                Variables.groupGraphs) {
            if (groupGraph != null) {
                allGroupGraphEdges = allGroupGraphEdges + Util.convertGraphEdgesToString2(
                        groupGraph) + "\n" + "\n";
            }
        }
        return new ResponseEntity<String>(allGroupGraphEdges, HttpStatus.OK);
    }

    /**
     * update the value of groupSize
     *
     * @param groupSize the maximum number of nodes in a group
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/putGroupSize")
    public ResponseEntity<String> putGroupSize(@RequestParam Map<String,
            String> customQuery) {
        Variables.groupSize = Integer.valueOf(customQuery.get("groupSize"));
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    /**
     * update the address of this node
     *
     * @param customQuery
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/putHostNodeIP")
    public ResponseEntity<String> putHostNodeIP(@RequestParam Map<String,
            String> customQuery) {
        Variables.hostNodeIP = customQuery.get("hostNodeIP");
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    /**
     * update the organization of this node
     * HPA hierarchical proximity-agnostic
     * HPI hierarchical proximity-integrated
     * FPA flat proximity-agnostic
     * FPI flat proximity-integrated
     *
     * @param customQuery
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/putOrganizationType")
    public ResponseEntity<String> putOrganizationType(@RequestParam Map<String,
            String> customQuery) {
        Variables.Organization = customQuery.get("OrganizationType");
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    /**
     * terminate the app on this node (used for debugging)
     *
     * @param customQuery
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/terminateApp")
    public ResponseEntity<String> terminateApplication(@RequestParam Map<String,
            String> customQuery) {
        System.exit(0);
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    /**
     * retutn this node's address
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/getHostNodeIP")
    public ResponseEntity<String> getHostNodeIP() {
        return new ResponseEntity<String>(Variables.hostNodeIP, HttpStatus.OK);
    }

    /**
     * return the maximum group size of this node
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/getGroupSize")
    public ResponseEntity<String> getGroupSize() {
        return new ResponseEntity<String>(String.valueOf(Variables.groupSize),
                HttpStatus.OK);
    }

    //obsolete

    /**
     * broadcast a message to all the nodes of the network
     *
     * @param message string message to be broadcast to all nodes in the
     *                group graphs
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/broadcast")
    public ResponseEntity<String> broadcast(@RequestParam Map<String,
            String> customQuery) {
//	    	new Thread(new GlobalBroadcast(customQuery.get("message"))).start();

        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    //obsolete

    /**
     * send a message to a list of receivers in the same group
     *
     * @param message        the message to be sent
     * @param receivers      a list of receivers of the message
     * @param originalSender the address of the original sender of this message
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/sendMessage")
    public ResponseEntity<String> sendMessage(@RequestParam Map<String,
            String> customQuery) {
//	    	new Thread(new SendMessage(customQuery.get("message"),
//	    			customQuery.get("receivers"), customQuery.get("originalSender")
//	    			)).start();
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    /**
     * Update the connectivity insurance information used to recover form
     * failure
     *
     * @param nodeAddress the address of the node which updates the
     *                    connectivity insurance
     * @param groupGraphs the updated connectivity insurance information
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/postConnectivityInsurance")
    public ResponseEntity<String> postConnectivityInsurance(@RequestParam
                                                                    Map<String, String> customQuery) {
//	    	new Thread(new UpdateConnectivityInsurance(customQuery.
//	    			get("nodeAddress"), customQuery.get("groupGraphs"))).start();
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    /**
     * make this node the root of the hierarchical organization
     *
     * @param customQuery
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/putRootNode")
    public ResponseEntity<String> putRootNode(@RequestParam Map<String,
            String> customQuery) {
        if (Variables.Organization.equals("HPA") ||
                Variables.Organization.equals("HPI")) {
            //Variables.isRoot=customQuery.get("rootNode");
            Variables.isRoot = true;
            Variables.groupGraphs.add(null);
        }
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    /**
     * reset all groups of the node
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/resetGroups")
    public ResponseEntity<String> resetGroups(@RequestParam Map<String,
            String> customQuery) {
        Util.resetGroups();
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    /**
     * return the graph the includes the children nodes
     *
     * @return the graph that includes the children nodes
     */
    @RequestMapping(method = RequestMethod.GET, value = "/removeChildrenGroup")
    public ResponseEntity<String> removeChildrenGoup(@RequestParam Map<String,
            String> customQuery) {
        //if there is a children group
        if (Variables.groupGraphs.size() == Variables.numOfGroups) {
            SimpleWeightedGraph<String, DefaultWeightedEdge> childrenGroup =
                    Variables.groupGraphs.remove(1);
            return new ResponseEntity<String>(Util.convertGraphEdgesToString(
                    childrenGroup), HttpStatus.OK);
        }
        return new ResponseEntity<String>("", HttpStatus.OK);
    }

    /**
     * post a message to this node and forward it to all the neighbor groups
     * apart from the group that includes the original sender of this message
     *
     * @param customQuery the original sender
     * @param body        the message
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/postMessage")
    public ResponseEntity<String> postMessage(@RequestParam Map<String,
            String> customQuery, @RequestBody String body) {
        Variables.originalSender = customQuery.get("originalSender");
        Variables.message = body;
        Variables.messageArrivalDate = new Date();

//			System.out.println(Variables.originalSender);
//			System.out.println();
//			System.out.println(Variables.message);

        //for every group
        for (SimpleWeightedGraph<String, DefaultWeightedEdge> group :
                Variables.groupGraphs) {
            //that does not contain the original sender and not null
            if (group != null) {
                if (!group.containsVertex(Variables.originalSender)) {
                    //send message to every node
                    for (String neighborNode : group.vertexSet()) {
                        if (!neighborNode.equals(Variables.hostNodeIP)) {
                            //different threads
                            new Thread(new PostMessage(neighborNode, body)).start();
                        }
                    }
                }
            }
        }
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    /**
     * get the time that the message arrived
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/getMessageArrivalTime")
    public ResponseEntity<String> getMessageArrivalTime(@RequestParam Map<String,
            String> customQuery) {

        while (Variables.messageArrivalDate == null) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return new ResponseEntity<String>(String.valueOf(Variables.
                messageArrivalDate.getTime()), HttpStatus.OK);
    }

    /**get the times that the messages were sent to neighbor nodes
     * @return
     */
/*	    @RequestMapping(method = RequestMethod.GET, value = "/getMessageSentTimes")
		public ResponseEntity<String> getMessageSentTimes(@RequestParam Map<String, 
				String> customQuery){
	    	String messageSentTimes="";
	    	for(String item : Variables.messageSentTimes) {
	    		messageSentTimes=messageSentTimes+item+"\n";
	    	}
	    	return new ResponseEntity<String>(messageSentTimes, HttpStatus.OK);
	    }*/

    /**
     * get the message of the node
     *
     * @return the message of this node
     */
    @RequestMapping(method = RequestMethod.GET, value = "/getMessage")
    public ResponseEntity<String> getMessage(@RequestParam Map<String,
            String> customQuery) {
        return new ResponseEntity<String>(Variables.message, HttpStatus.OK);
    }

    /**
     * get the message of the node
     *
     * @return the message of this node
     */
    @RequestMapping(method = RequestMethod.GET, value = "/getformerGroupNodes")
    public ResponseEntity<String> getformerGroupNodes(@RequestParam Map<String,
            String> customQuery) {
        return new ResponseEntity<String>(Variables.formerGroupNodes.toString(), HttpStatus.OK);
    }

    /**
     * reset the message
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/resetMessage")
    public ResponseEntity<String> resetMessage(@RequestParam Map<String,
            String> customQuery) {
        Variables.messageArrivalDate = null;
        Variables.originalSender = "";
        Variables.message = "";
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }


}