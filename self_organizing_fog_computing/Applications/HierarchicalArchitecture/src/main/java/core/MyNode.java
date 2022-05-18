package core;

import java.util.ArrayList;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import global.Global;
import utils.Util;





/**
 * @author Basil
 */
/**
 *the class that represents the object of a node (i.e., an end-device)
 *it also includes the methods for joining the overlay
 */
public class MyNode {
    /**
     * the x coordinate of the node in a 2-dimensional space
     */
    private double xAxis;
    /**
     * the y coordinate of the node in a 2-dimensional space
     */
    private double yAxis;
    /**
     * a unique identifier of this node
     */
    private String nodeIdentifier;
    /**
     * a list of the graphs that include neighbors to this node
     */
    private ArrayList<SimpleWeightedGraph<MyNode, DefaultWeightedEdge>> neighborGroups;
    /**
     * encapsulated lists that contain neighbor nodes and their neighbors.
     * this encapsulated list structure achieves level 1 connectivity insurance
     */
    private ArrayList<ArrayList<SimpleWeightedGraph<MyNode, DefaultWeightedEdge>>>
            connectivityInsuranceL1;
    /**
     * encapsulated lists that contain neighbor nodes and their neighbors.
     * this encapsulated list structure achieves level 2 connectivity insurance
     * "NOT USED IN THIS VERSION"
     */
    private ArrayList<ArrayList<SimpleWeightedGraph<MyNode, DefaultWeightedEdge>>>
            connectivityInsuranceL2;
    /**
     * the id of the last message that the node received
     */
    private int messageId;
    /**
     * the original sender that sent the last message to this node
     */
    private MyNode originalSender;
    /**
     * flag that denotes if this node is disconnected
     */
    private boolean disconnectedFlag;
    /**
     * latency in message transmissions from broadcast until this node
     * received the last message
     */
    private int latencyInMessages;
    /**
     * latency in hops from broadcast until this node
     * received the last message	 */
    private int latencyInHops;
    /**
     * The nodeIdentifier of the parent node when building a tree topology	 */
    private String parentId;
    /**
     * The nodeIdentifier of the parent node when building a tree topology	 */
    private int forwardJoinCounter;
    /**
     * creates a new node object
     * @param xAxis the x coordinate of the node in a 2-dimensional space
     * @param yAxis the y coordinate of the node in a 2-dimensional space
     * @param nodeIdentifier a unique identifier of this node
     */
    public MyNode(double xAxis, double yAxis, String nodeIdentifier) {
        this.xAxis=xAxis;
        this.yAxis=yAxis;
        this.nodeIdentifier=nodeIdentifier;
        this.neighborGroups=new ArrayList<SimpleWeightedGraph<MyNode, DefaultWeightedEdge>>();
        this.messageId=0;
        this.disconnectedFlag=false;
        this.connectivityInsuranceL1=new ArrayList<ArrayList<SimpleWeightedGraph<MyNode
                , DefaultWeightedEdge>>>();
        this.latencyInMessages=0;
        this.latencyInHops=0;
        this.forwardJoinCounter=0;
    }
    public double getxAxis() {
        return xAxis;
    }
    public void setxAxis(double xAxis) {
        this.xAxis = xAxis;
    }
    public double getyAxis() {
        return yAxis;
    }
    public void setyAxis(double yAxis) {
        this.yAxis = yAxis;
    }
    public String getNodeIdentifier() {
        return nodeIdentifier;
    }
    public void setNodeIdentifier(String nodeIdentifier) {
        this.nodeIdentifier = nodeIdentifier;
    }
    public ArrayList<SimpleWeightedGraph<MyNode, DefaultWeightedEdge>>
    getNeighborGroups() {
        return neighborGroups;
    }
    public void setNeighborGroups(ArrayList<SimpleWeightedGraph<MyNode
            , DefaultWeightedEdge>> neighborGroups) {
        this.neighborGroups = neighborGroups;
    }
    public int getMessageId() {
        return messageId;
    }
    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
    public MyNode getOriginalSender() {
        return originalSender;
    }
    public void setOriginalSender(MyNode originalSender) {
        this.originalSender = originalSender;
    }
    public String toString() {
        return this.nodeIdentifier;
    }
    public boolean getDisconnectedFlag() {
        return disconnectedFlag;
    }
    public void setDisconnectedFlag(boolean disconnectedFlag) {
        this.disconnectedFlag = disconnectedFlag;
    }
    public ArrayList<ArrayList<SimpleWeightedGraph<MyNode, DefaultWeightedEdge>>>
    getConnectivityInsuranceL1() {
        return connectivityInsuranceL1;
    }
    public void setConnectivityInsuranceL1(ArrayList<ArrayList<SimpleWeightedGraph<MyNode
            , DefaultWeightedEdge>>> connectivityInsuranceL1) {
        this.connectivityInsuranceL1 = connectivityInsuranceL1;
    }
    public ArrayList<ArrayList<SimpleWeightedGraph<MyNode, DefaultWeightedEdge>>> getConnectivityInsuranceL2() {
        return connectivityInsuranceL2;
    }
    public void setConnectivityInsuranceL2(
            ArrayList<ArrayList<SimpleWeightedGraph<MyNode, DefaultWeightedEdge>>> connectivityInsuranceL2) {
        this.connectivityInsuranceL2 = connectivityInsuranceL2;
    }
    public int getLatencyInMessages() {
        return latencyInMessages;
    }
    public void setLatencyInMessages(int latencyInMessages) {
        this.latencyInMessages = latencyInMessages;
    }
    public int getLatencyInHops() {
        return latencyInHops;
    }
    public void setLatencyInHops(int latencyInHops) {
        this.latencyInHops = latencyInHops;
    }
    public String getParentId() {
        return parentId;
    }
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    public int getForwardJoinCounter() {
        return forwardJoinCounter;
    }
    public void setForwardJoinCounter(int forwardJoinCounter) {
        this.forwardJoinCounter = forwardJoinCounter;
    }
    /**
     * makes this node part of a group that the groupNode belongs to
     * @param groupNode
     */
    public void joinGroup(MyNode groupNode, MyNode root) {
        //connectivity insurance review,
        //not every node has to share a message when the info resides in all nodes
        int groupSize=Global.groupSize;
        //numOfGroups is not implemented as a variable. currently only works for 2
        int numOfGroups=Global.numOfGroups;
        int joinGroupFlag=0;
        Global.joinOverhead++;//join request/response

        if(Global.hopAwareEnabled==0) {
            //without putting new nodes to groups in proximity
            if(groupNode.getNeighborGroups().size()==numOfGroups) {
                if(groupNode.getNeighborGroups().get(1).vertexSet().size()<groupSize) {
                    //response from groupnode overhead is added later
                    SimpleWeightedGraph<MyNode, DefaultWeightedEdge> groupGraph=
                            groupNode.getNeighborGroups().get(1);
                    this.getNeighborGroups().add(groupGraph);
                    groupGraph.addVertex(this);
                    for(MyNode neighbor : groupGraph.vertexSet()) {
                        if(this != neighbor) {
                            DefaultWeightedEdge edge=groupGraph.addEdge(this, neighbor);
                            groupGraph.setEdgeWeight(edge, Util.findShortestPathWeight(this, neighbor));
                            //accept join request from groupnode and send connectivity insurance to new node
                            //and send updated group to all the other neighbors
                            Global.joinOverhead++;
                        }
                    }
                    joinGroupFlag=1;
                }else {
                    if(groupNode.getForwardJoinCounter()==groupNode.getNeighborGroups().get(1).vertexSet().toArray().length) {
                        groupNode.setForwardJoinCounter(0);
                    }
                    MyNode newGroupNode=(MyNode)groupNode.getNeighborGroups().get(1).vertexSet().toArray()
                            [groupNode.getForwardJoinCounter()];
                    if(groupNode.getNodeIdentifier().equals(newGroupNode.getNodeIdentifier())){
                        groupNode.setForwardJoinCounter(groupNode.getForwardJoinCounter()+1);
                        newGroupNode=(MyNode)groupNode.getNeighborGroups().get(1).vertexSet().toArray()
                                [groupNode.getForwardJoinCounter()];
                    }
                    groupNode.setForwardJoinCounter(groupNode.getForwardJoinCounter()+1);
                    this.joinGroup(newGroupNode, root);
                    joinGroupFlag=1;
                }
            }
            if(joinGroupFlag==0){
                SimpleWeightedGraph<MyNode, DefaultWeightedEdge> groupGraph=new SimpleWeightedGraph<MyNode
                        , DefaultWeightedEdge>(DefaultWeightedEdge.class);
                groupGraph.addVertex(groupNode);
                groupGraph.addVertex(this);
                //Global.joinOverhead++;//groupnode accepts join request as new group and sends
                //part of the response thus no extra message
                DefaultWeightedEdge edge=groupGraph.addEdge(this, groupNode);
                groupGraph.setEdgeWeight(edge, Util.findShortestPathWeight(this, groupNode));
                groupNode.getNeighborGroups().add(groupGraph);
                this.getNeighborGroups().add(groupGraph);
            }
        }else{
            //with putting new nodes to groups in proximity
            if(groupNode.getNeighborGroups().size()==numOfGroups) {
                SimpleWeightedGraph<MyNode, DefaultWeightedEdge> secGraphOfGroupNode=groupNode.getNeighborGroups().get(1);
                secGraphOfGroupNode.addVertex(this);
//				Global.joinOverhead+=2;//to get the current groupgraph
                for(MyNode neighborNode : secGraphOfGroupNode.vertexSet()) {
                    if(this != neighborNode) {
                        //plus connectivity insurance if any(it had other neighbors already)
                        DefaultWeightedEdge edge=secGraphOfGroupNode.addEdge(this, neighborNode);
                        secGraphOfGroupNode.setEdgeWeight(edge, Util.findShortestPathWeight(this, neighborNode));
                        Global.joinOverhead+=Util.findShortestPathWeight(this, neighborNode);//to find the distance in hops
                        Global.joinOverhead++;//to send the  updated groupgraph to each groupnode
                    }
                }
                MyNode maxHopsNode=null;
                if(secGraphOfGroupNode.vertexSet().size() <= groupSize){
                    this.getNeighborGroups().add(secGraphOfGroupNode);
                    joinGroupFlag=1;
                }else {
                    double sumHops;
                    double maxSumHops=0;
                    for(MyNode destinationNode : secGraphOfGroupNode.vertexSet()) {
                        if(this!=destinationNode) {
                            maxSumHops+=Util.findShortestPathWeight(this, destinationNode);
                        }
                    }
                    maxHopsNode=this;
                    for(MyNode sourceNode : secGraphOfGroupNode.vertexSet()) {
                        //avoid moving the root because the root is the cloud
                        //if replace groupNode by root, it can create deadlocks
                        if(sourceNode!=this && sourceNode!=groupNode) {
                            sumHops=0;
                            for(MyNode destinationNode : secGraphOfGroupNode.vertexSet()) {
                                if(sourceNode!=destinationNode) {
                                    sumHops+=Util.findShortestPathWeight(sourceNode, destinationNode);
                                }
                            }
                            if(sumHops>maxSumHops) {//what if there are many nodes with the same maxSumHops
                                maxSumHops=sumHops;//but do not fix because initially maxSumHops is the new node
                                maxHopsNode=sourceNode;
                            }
                        }
                    }
                    if(maxHopsNode==this) {
                        secGraphOfGroupNode.removeVertex(this);
                        double minHops=Double.MAX_VALUE;
                        MyNode closestNeighbor=null;
                        for(MyNode neighborNode : secGraphOfGroupNode.vertexSet()) {
                            if(neighborNode!=groupNode) {
                                if(Util.findShortestPathWeight(this, neighborNode)<minHops) {
                                    minHops=Util.findShortestPathWeight(this, neighborNode);
                                    closestNeighbor=neighborNode;
                                }
                            }
                        }
                        this.resetConnectivity();
                        joinGroupFlag=1;
                        this.joinGroup(closestNeighbor, root);
/*					}else if(maxHopsNode==groupNode) {
						//add first groupGraph
						SimpleWeightedGraph<MyNode, DefaultWeightedEdge> firGraphOfGroupNode=groupNode.getNeighborGroups().get(0);
						firGraphOfGroupNode.removeVertex(groupNode);
						firGraphOfGroupNode.addVertex(this);
						Global.joinOverhead+=2;//to get the current groupgraph
						for(MyNode neighborNode : firGraphOfGroupNode.vertexSet()) {
							if(this != neighborNode) {
								Global.joinOverhead++;//to send the  updated groupgraph to each groupnode
								//plus connectivity insurance if any(it had other neighbors already)
								DefaultWeightedEdge edge=firGraphOfGroupNode.addEdge(this, neighborNode);
								firGraphOfGroupNode.setEdgeWeight(edge, Util.findShortestPathWeight(this, neighborNode));
							}
						}
						this.getNeighborGroups().add(firGraphOfGroupNode);
						//add second groupGraph
						secGraphOfGroupNode.removeVertex(groupNode);
						this.getNeighborGroups().add(secGraphOfGroupNode);
						groupNode.resetConnectivity();
						joinGroupFlag=1;
						groupNode.joinGroup(root, root);//*/
                    }else {//if maxHopsNode!=this and (maxHopsNode!=groupNode because it has been removed in the search of the maxHopeNode
                        //add first groupGraph of maxHopsNode != first groupGraph of groupNode
                        secGraphOfGroupNode.removeVertex(maxHopsNode);
                        this.getNeighborGroups().add(secGraphOfGroupNode);
                        //add second groupGraph
                        if(maxHopsNode.getNeighborGroups().size()==2) {
                            SimpleWeightedGraph<MyNode, DefaultWeightedEdge> secGraphOfMaxHopsNode=maxHopsNode.
                                    getNeighborGroups().get(1);
                            Global.joinOverhead++;//to get this groupgraph: secGraphOfMaxHopsNode
                            secGraphOfMaxHopsNode.removeVertex(maxHopsNode);
                            secGraphOfMaxHopsNode.addVertex(this);
                            for(MyNode neighborNode : secGraphOfMaxHopsNode.vertexSet()) {
                                if(this != neighborNode) {
                                    DefaultWeightedEdge edge=secGraphOfMaxHopsNode.addEdge(this, neighborNode);
                                    secGraphOfMaxHopsNode.setEdgeWeight(edge, Util.findShortestPathWeight(this, neighborNode));
                                    Global.joinOverhead+=Util.findShortestPathWeight(this, neighborNode);//to find the distance in hops
                                    Global.joinOverhead++;//to send the  updated groupgraph to each groupnode
                                }
                            }
                            this.getNeighborGroups().add(secGraphOfMaxHopsNode);
                        }
                        maxHopsNode.resetConnectivity();
                        joinGroupFlag=1;
                        maxHopsNode.joinGroup(root, root);
                    }
                }
            }
            if(joinGroupFlag==0){
                SimpleWeightedGraph<MyNode, DefaultWeightedEdge> groupGraph=new SimpleWeightedGraph<MyNode
                        , DefaultWeightedEdge>(DefaultWeightedEdge.class);
                groupGraph.addVertex(groupNode);
                groupGraph.addVertex(this);
                Global.joinOverhead+=Util.findShortestPathWeight(this, groupNode)+1;// groupNode finds hop distance
                Global.joinOverhead++;//groupnode accepts join request as new group
                //new node measures distance and sends updated graph to groupNode
                DefaultWeightedEdge edge=groupGraph.addEdge(this, groupNode);
                groupGraph.setEdgeWeight(edge, Util.findShortestPathWeight(this, groupNode));
                groupNode.getNeighborGroups().add(groupGraph);
                this.getNeighborGroups().add(groupGraph);
            }
        }
    }

    public void reEstablishConnection(MyNode disconnectedNode) {
        ArrayList<MyNode> reconnectNodes=new ArrayList<MyNode>();
        for(ArrayList<SimpleWeightedGraph<MyNode, DefaultWeightedEdge>> neighborGroupList
                : this.getConnectivityInsuranceL1()) {
            for(SimpleWeightedGraph<MyNode, DefaultWeightedEdge> neighborGroup : neighborGroupList) {
                if(neighborGroup.vertexSet().contains(disconnectedNode) ) {
                    neighborGroup.removeVertex(disconnectedNode); //overhead is added later
                    for(MyNode node : neighborGroup.vertexSet()) {
                        if(node!=disconnectedNode) {
                            //check if node is connected
                            Global.reEstablishConnectionOverhead++;
                            if(!node.getDisconnectedFlag()) {
                                //respond
                                //	Global.reEstablishConnectionOverhead++;
                                reconnectNodes.add(node);
                                //notify neighbors of the disconnected node
                                Global.reEstablishConnectionOverhead+=neighborGroup.vertexSet().size()-1;
                                //neighbors remove connectivity insurance of disconnected node
                                for(MyNode neighbor : neighborGroup.vertexSet()) {
                                    neighbor.getConnectivityInsuranceL1().remove(disconnectedNode.getNeighborGroups());
                                }
                                break;
                            }else {
                                //here neighbors of other disconnected nodes can be notified
                                //but at this version we assume detection of disconnected nodes only
                                //when a neighbor tries to send a message. additional changes might be
                                //required if detection takes place here
                            }
                        }
                    }
                }
            }
        }
        for(MyNode reconnectNode : reconnectNodes) {
            //overhead of join request
            Global.reEstablishConnectionOverhead++;
            this.joinGroup(reconnectNode, reconnectNode);// this is not tested !!!
            //connectivity insurance is not considered in this version
        }
    }

    public void resetConnectivity() {
        this.messageId=0;
        this.disconnectedFlag=false;
        this.connectivityInsuranceL1=new ArrayList<ArrayList<SimpleWeightedGraph<MyNode
                , DefaultWeightedEdge>>>();
        for(SimpleWeightedGraph<MyNode, DefaultWeightedEdge> group : this.neighborGroups) {
            group.removeVertex(this);
        }
        this.neighborGroups=new ArrayList<SimpleWeightedGraph<MyNode, DefaultWeightedEdge>>();
        this.originalSender=null;
        this.parentId=null;
    }



}
