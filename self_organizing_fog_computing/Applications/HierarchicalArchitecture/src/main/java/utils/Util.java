package utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import core.MyNode;
import global.Global;


/**
 * @author Basil
 * this class contains general purpose utilities including the methods
 * that simulate sending messages on the overlay network
 *
 */
public class Util {

    /**prints the topology to a file that can be visualized with gnuplot
     * e.g. plot "groups.dat" with lines e.g. set term wxt 0 (for multiple windows)
     * @param filename the name of the generated file
     */
    public static void printTopology(String filename) {
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            System.out.println("Writing to file " + filename);
            PrintStream pstr = new PrintStream(fos);
            //print vertices
            for (MyNode node : Global.topologyGraph.vertexSet()) {
                double x_from = node.getxAxis();
                double y_from = node.getyAxis();

                pstr.println(x_from + " " + y_from);
                pstr.println();
            }
            //print arcs
            for (DefaultEdge edge : Global.topologyGraph.edgeSet()) {
                MyNode sourceNode = Global.topologyGraph.getEdgeSource(edge);
                MyNode targetNode = Global.topologyGraph.getEdgeTarget(edge);
                double x_from = sourceNode.getxAxis();
                double y_from = sourceNode.getyAxis();
                double x_to = targetNode.getxAxis();
                double y_to = targetNode.getyAxis();

                pstr.println(x_from + " " + y_from);
                pstr.println(x_to + " " + y_to);
                pstr.println();
            }
            fos.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * prints only the arcs that connect nodes of the same group
     * @param endDevices list of the end-devices
     * @param filename the name of the generated file
     */
    public static void printGroups(ArrayList<MyNode> endDevices, String filename) {
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            System.out.println("Writing to file " + filename);
            PrintStream pstr = new PrintStream(fos);
            //print vertices
            for (MyNode node : endDevices) {
                double x_from = node.getxAxis();
                double y_from = node.getyAxis();

                pstr.println(x_from + " " + y_from);
                pstr.println();
            }
            //print arcs
            for(MyNode node : endDevices) {
                for (DefaultWeightedEdge edge : node.getNeighborGroups().get(0).edgeSet()) {
                    MyNode sourceNode = node.getNeighborGroups().get(0).getEdgeSource(edge);
                    MyNode targetNode = node.getNeighborGroups().get(0).getEdgeTarget(edge);
                    double x_from = sourceNode.getxAxis();
                    double y_from = sourceNode.getyAxis();
                    double x_to = targetNode.getxAxis();
                    double y_to = targetNode.getyAxis();

                    pstr.println(x_from + " " + y_from);
                    pstr.println(x_to + " " + y_to);
                    pstr.println();

                }
            }
            fos.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * generates a topology of nodes that resembles the Internet architecture
     * the type of the model is HOT (heuristically optimal topology) and is taken
     * from the InetTopology of the peersim simulator
     * @param numberOfNodes the number of nodes in the generated infrastructure
     */
    public static void generateTopologyGraph(int numberOfNodes){
        MyNode nodes[]=new MyNode[numberOfNodes];
        int nodeId=0;
        nodes[0]=new MyNode(0.5, 0.5, String.valueOf(nodeId++));
        Global.topologyGraph.addVertex(nodes[0]);
        for(int i=1;i<nodes.length;i++) {
            nodes[i]=new MyNode(Global.random.nextDouble(), Global.random.nextDouble(), String.valueOf(nodeId++));
            Global.topologyGraph.addVertex(nodes[i]);
        }
        double alpha = 20;
        /** Contains the distance in hops from the root node for each node. */
        int[] hops = new int[nodes.length];
        // connect all the nodes other than roots
        for (int i = 1; i < nodes.length; i++) {
            MyNode node = nodes[i];
            // Look for a suitable parent node between those already part of
            // the overlay topology: alias FIND THE MINIMUM!
            // Node candidate = null;
            int candidate_index = 0;
            double min = Double.POSITIVE_INFINITY;
            for (int j = 0; j < i; j++) {
                MyNode parent = nodes[j];
                double jHopDistance = hops[j];
                double value = jHopDistance+(alpha * findEuclideanDistance(node.getxAxis()
                        , node.getyAxis(), parent.getxAxis(), parent.getyAxis()));
                if (value < min) {
                    // candidate = parent; // best parent node to connect to
                    min = value;
                    candidate_index = j;
                }
            }
            hops[i] = hops[candidate_index] + 1;
            Global.topologyGraph.addEdge(nodes[i], nodes[candidate_index]);
        }
    }

    /**
     * finds the shortest path between two nodes (based on hop count) on the topology graph
     * i.e. the global variable "Global.topologyGraph"
     * @param node1 a node in the graph
     * @param node2 a node in the graph
     * @return number of hops
     */
    public static double findShortestPathWeight(MyNode node1, MyNode node2) {
        DijkstraShortestPath<MyNode, DefaultEdge> dijkstra=new DijkstraShortestPath<MyNode
                , DefaultEdge>(Global.topologyGraph);
        return dijkstra.getPathWeight(node1, node2);
    }

    /**
     * finds the euclidean distance between two points on the 2-dimensional space
     * @param x1 cordinate of x1
     * @param y1 cordinate of y1
     * @param x2 cordinate of x2
     * @param y2 cordinate of y2
     * @return euclidean distance
     */
    public static double findEuclideanDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)));
    }

    /**
     * initiates an atomic broadcast from senderNode that disseminates the value
     * of the variable: messageId
     * @param senderNode the node that initiates the broadcast
     */
    public static void broadcast(MyNode senderNode) {
        for(int i=0;i<senderNode.getNeighborGroups().size();i++) {
            if(!senderNode.getNeighborGroups().get(i).vertexSet().contains(senderNode.getOriginalSender())) {
                groupBroadcast(senderNode, senderNode.getNeighborGroups().get(i));
            }
        }
    }

    /**
     * broadcasts the messageId to all the nodes of the groupGraph
     * @param senderNode the node that initiates the group-broadcast
     * @param groupGraph one of the groups that senderNode belongs to
     */
    public static void groupBroadcast(MyNode senderNode, SimpleWeightedGraph<MyNode, DefaultWeightedEdge> groupGraph) {
        ArrayList<MyNode> receivers=new ArrayList<MyNode>();
        for(MyNode node : groupGraph.vertexSet()) {
            if(node!=senderNode) {
                receivers.add(node);
            }
        }
        Util.groupMulticast(senderNode, receivers, groupGraph);
    }

    /**
     * send the variable "messageId" of the node "senderNode" to the nodes of the list "receivers"
     * based on a hop minimisation logic. the nodes of the list and senderNode must all elong to
     * the same group
     * @param senderNode the node that initiates the multicast
     * @param receivers the destinations of the multicast
     */
    public static void groupMulticast(MyNode senderNode, ArrayList<MyNode> receivers
            , SimpleWeightedGraph<MyNode, DefaultWeightedEdge> groupGraph) {
        if(receivers.remove(senderNode)){
            //this node is a receiver of the message not just a liaison
        }
        if(Global.hopAwareEnabled==0) {
            //without hop minimization
            for(MyNode receiverNode : receivers) {
                //send message
                Global.broadcastMessages++;
                double pathLength=Util.findShortestPathWeight(senderNode, receiverNode);
                Global.paths.add(Global.paths.size(), pathLength);
                Global.broadcastHops+=pathLength;
                if(receiverNode.getDisconnectedFlag()) {
                    groupGraph.removeVertex(receiverNode);
                    //notify neighbors of the disconnected node
                    Global.reEstablishConnectionOverhead+=groupGraph.vertexSet().size()-1;
                    senderNode.reEstablishConnection(receiverNode);
                    for(MyNode node : groupGraph.vertexSet()) {
                        //upon notification every node removes connectivity insurance without extra messaging
                        node.getConnectivityInsuranceL1().remove(receiverNode.getNeighborGroups());
                    }
                }else {
                    //send response
                    //	Global.broadcastMessages++;
                    //	Global.broadcastHops+=Util.findShortestPathWeight(senderNode, receiverNode);
                    if(receiverNode.getMessageId()==senderNode.getMessageId()) {
                        Global.redundantMessages++;
                    }
                    receiverNode.setMessageId(senderNode.getMessageId());
                    receiverNode.setOriginalSender(senderNode);
                    receiverNode.setLatencyInMessages(senderNode.getLatencyInMessages()+1);
                    receiverNode.setLatencyInHops(senderNode.getLatencyInHops()+(int)Util.findShortestPathWeight
                            (senderNode, receiverNode));
                    Util.broadcast(receiverNode);
                }
            }
        }else {
            //with hop minimization
            //in tempGraph,the vertices are references to the actual nodes, thus do not modify.
            //the edges are newly generated thus, do modify at will
            SimpleWeightedGraph<MyNode, DefaultWeightedEdge> tempGraph=new SimpleWeightedGraph<MyNode
                    , DefaultWeightedEdge>(DefaultWeightedEdge.class);
            for(MyNode node : groupGraph.vertexSet()) {
                tempGraph.addVertex(node);
            }
            for(DefaultWeightedEdge edge : groupGraph.edgeSet()) {
                DefaultWeightedEdge tempEdge=tempGraph.addEdge(groupGraph.getEdgeSource(edge)
                        , groupGraph.getEdgeTarget(edge));
                tempGraph.setEdgeWeight(tempEdge, groupGraph.getEdgeWeight(edge));
            }
            ArrayList<ArrayList<MyNode>> sendPaths=new ArrayList<ArrayList<MyNode>>();
            DijkstraShortestPath<MyNode, DefaultWeightedEdge> dijkstra=new DijkstraShortestPath<MyNode
                    , DefaultWeightedEdge>(tempGraph);
            for(int i=0;i<receivers.size();i++) {
                double minShortestPath=Double.POSITIVE_INFINITY;
                double currentShortestPath=0;
                MyNode shortestPathNode=null;
                for(MyNode node : receivers) {
                    currentShortestPath=dijkstra.getPathWeight(senderNode, node);
                    if(currentShortestPath<minShortestPath && currentShortestPath>0) {
                        minShortestPath=currentShortestPath;
                        shortestPathNode=node;
                    }
                }
                sendPaths.add((ArrayList<MyNode>)dijkstra.getPath(senderNode, shortestPathNode).getVertexList());
                for(DefaultWeightedEdge edge : dijkstra.getPath(senderNode, shortestPathNode).getEdgeList()) {
                    //maybe something very small but greater than zero, so that it also achieves min latency
                    tempGraph.setEdgeWeight(edge, 0);
                }
            }
            ArrayList<MyNode> oneHopNodes=new ArrayList<MyNode>();
            ArrayList<ArrayList<MyNode>> sendPathsReduced=new ArrayList<ArrayList<MyNode>>();
            for(ArrayList<MyNode> path : sendPaths) {
                if(!oneHopNodes.contains(path.get(1))) {
                    oneHopNodes.add(path.get(1));
                }
            }
            for(MyNode node : oneHopNodes) {
                ArrayList<MyNode> pathReduced=new ArrayList<MyNode>();
                pathReduced.add(node);
                sendPathsReduced.add(pathReduced);
            }
            for(ArrayList<MyNode> path : sendPaths) {
                for(ArrayList<MyNode> pathReduced : sendPathsReduced) {
                    if(path.get(1)==pathReduced.get(0)) {
                        pathReduced.add(path.get(path.size()-1));
                    }
                }
            }
            for(ArrayList<MyNode> sendPath : sendPathsReduced) {
                //the first node of the list is the sender
                MyNode receiverNode=sendPath.remove(0);
                //send message
                Global.broadcastMessages++;
                double pathLength=Util.findShortestPathWeight(senderNode, receiverNode);
                Global.paths.add(Global.paths.size(), pathLength);
                Global.broadcastHops+=pathLength;
                if(receiverNode.getDisconnectedFlag()) {
                    //in case the receiverNode was also a destination not just a liaison
                    sendPath.remove(receiverNode);
                    groupGraph.removeVertex(receiverNode);
                    //notify neighbors of the disconnected node
                    Global.reEstablishConnectionOverhead+=groupGraph.vertexSet().size()-1;
                    senderNode.reEstablishConnection(receiverNode);
                    for(MyNode node : groupGraph.vertexSet()) {
                        //every node removes connectivity insurance without extra messaging
                        node.getConnectivityInsuranceL1().remove(receiverNode.getNeighborGroups());
                    }
                    Util.groupMulticast(senderNode, sendPath, groupGraph);
                }else {
                    //send response
                    //	Global.broadcastMessages++;
                    //	Global.broadcastHops+=Util.findShortestPathWeight(senderNode, receiverNode);
                    if(receiverNode.getMessageId()==senderNode.getMessageId()) {
                        Global.redundantMessages++;
                    }
                    receiverNode.setMessageId(senderNode.getMessageId());
                    receiverNode.setOriginalSender(senderNode);
                    receiverNode.setLatencyInMessages(senderNode.getLatencyInMessages()+1);
                    receiverNode.setLatencyInHops(senderNode.getLatencyInHops()+(int)Util.findShortestPathWeight
                            (senderNode, receiverNode));
                    Util.groupMulticast(receiverNode, sendPath, groupGraph);
                    Util.broadcast(receiverNode);
                }
            }
        }
    }





}
