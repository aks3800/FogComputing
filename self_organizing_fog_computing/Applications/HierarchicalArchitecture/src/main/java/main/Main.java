package main;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;

/**
 *
 */


import java.util.ArrayList;
import java.util.Calendar;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import core.MyNode;
import global.Global;
import utils.Util;




/**
 * @author Basil
 * This is the main class for the evaluation of the ECO protocol.
 * It creates files and saves various metrics related to overhead
 * connectivity hot-count etc. The variables that measure these
 * metrics are defined in the class Global.java
 */
public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
//*
        int iterations=Global.iterations;
        int iterationCount=0;

        //create filename bases on time, system size etc.
        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss");
        Calendar calendar = Calendar.getInstance();
        String date=dateFormat.format(calendar.getTime());
        String file="-"+"hier"+"-"+Global.numOfEndDevices+"-"+Global.hopAwareEnabled+"-"+
                Global.groupSize+"-"+date+".txt";

        //number if iteration is defined in Global.java
        while(iterationCount<iterations) {

            //reset variables for each iteration
            Global.topologyGraph=new SimpleWeightedGraph<MyNode
                    , DefaultEdge>(DefaultEdge.class);
            Global.redundantMessages=0;
            Global.joinOverhead=0;
            Global.reEstablishConnectionOverhead=0;
            Global.broadcastMessages=0;
            Global.broadcastHops=0;
            Global.latencyInMessages=0;
            Global.latencyInHops=0;
            Global.paths=new ArrayList<Double>();

            //generate random topology
            Util.generateTopologyGraph(Global.numberOfNodes);
            ArrayList<MyNode> endDevices=new ArrayList<MyNode>();
            for(MyNode  node : Global.topologyGraph.vertexSet()) {
                if(Global.topologyGraph.degreeOf(node)==1) {
                    endDevices.add(node);
                }
            }
//			System.out.println("number of endDevices: "+endDevices.size());

            //if the topology contains the number of end-devices defined in Global.java
            //note that number of nodes and number of end-devices is different.
            //number of nodes contains access points and end-devices
            if(endDevices.size()==Global.numOfEndDevices) {
                iterationCount++;

                System.out.println("number of iterations: "+iterationCount);
/*iterationCount == 43 creates a scenario where the broadcast node does not belong to the largest connected
 * component. thus the connectedNodes.txt metric is not valid (only for iterationCount 43) .in order to
 * repeat iterationCount 43, all the simulations need to be run again so that the same seed is used in Random.
 * To avoid this, we only run the iterationCount 43 by skipping the previous ones which creates a different
 * scenario that we use as substitute for iterationCount 43.
if(iterationCount==43) {*/
                //first iteration node 1 joins node 0 and then each node (incrementally) joins a random node
                //that is already part of the overlay
                try {
                    FileOutputStream fos = new FileOutputStream("joinOverhead"+file, true);
                    PrintStream pstr = new PrintStream(fos);
                    //first node has zero join overhead
                    pstr.print(Global.joinOverhead+" ");
                    //endDevices.get(0) is the root so add a dummy group
                    endDevices.get(0).getNeighborGroups().add(null);
                    for (int i=1;i<endDevices.size();i++) {
//						int randomNodeIndex=Global.random.nextInt(i);
                        Global.joinOverhead=0;//reset metric
                        endDevices.get(i).joinGroup(endDevices.get(0), endDevices.get(0));
                        pstr.print(Global.joinOverhead+" ");
                    }
                    pstr.println();
                    fos.close();
                }catch(Exception e) {e.printStackTrace();}

                try {
                    FileOutputStream fos = new FileOutputStream("broadcastOverhead"+file, true);
                    PrintStream pstr = new PrintStream(fos);
                    MyNode senderNode=endDevices.get(0);
                    senderNode.getNeighborGroups().remove(null);
                    senderNode.setMessageId(1);
                    Util.broadcast(senderNode);
                    for (MyNode node : endDevices) {
                        node.setMessageId(0);
                    }
                    pstr.println(Global.broadcastMessages+" "+Global.broadcastHops);
                    fos.close();
                }catch(Exception e) {e.printStackTrace();}

                try {//create the graph of the network
                    SimpleWeightedGraph<String, DefaultWeightedEdge> networkGraph=
                            new SimpleWeightedGraph<String, DefaultWeightedEdge>
                                    (DefaultWeightedEdge.class);
                    for (MyNode node:endDevices) {
                        networkGraph.addVertex(node.getNodeIdentifier());
                    }
                    for (MyNode node:endDevices) {
                        for(SimpleWeightedGraph<MyNode, DefaultWeightedEdge> groupGraph:
                                node.getNeighborGroups()) {
                            for(DefaultWeightedEdge possibleEdge:groupGraph.edgeSet()) {
                                if(!networkGraph.containsEdge(groupGraph.getEdgeSource(possibleEdge).
                                        getNodeIdentifier(), groupGraph.getEdgeTarget(possibleEdge).
                                        getNodeIdentifier())){
                                    DefaultWeightedEdge edgeToAdd=networkGraph.addEdge(groupGraph.
                                            getEdgeSource(possibleEdge).getNodeIdentifier(), groupGraph.
                                            getEdgeTarget(possibleEdge).getNodeIdentifier());
                                    networkGraph.setEdgeWeight(edgeToAdd, groupGraph.getEdgeWeight(possibleEdge));
                                }
                            }
                        }
                    }
/*//print graphs for debugging
					System.out.println(networkGraph.vertexSet());
					for(DefaultWeightedEdge possibleEdge:networkGraph.edgeSet()) {
						System.out.println(networkGraph.getEdgeSource(possibleEdge)+" "+
										   networkGraph.getEdgeTarget(possibleEdge)+" "+
										   networkGraph.getEdgeWeight(possibleEdge));
					}
					System.out.println();

					for(MyNode node:endDevices) {
						System.out.print(node.getNodeIdentifier()+", ");
					}
					System.out.println();
					for(MyNode node:endDevices) {
						for(SimpleWeightedGraph<MyNode, DefaultWeightedEdge> groupGraph:
							node.getNeighborGroups()) {
							for(DefaultWeightedEdge possibleEdge:groupGraph.edgeSet()) {
								System.out.println(groupGraph.getEdgeSource(possibleEdge)+" "+
										groupGraph.getEdgeTarget(possibleEdge)+" "+
										groupGraph.getEdgeWeight(possibleEdge));
							}
						}
					}
*/
                    FileOutputStream fos = new FileOutputStream("networkDiameter"+file, true);
                    PrintStream pstr = new PrintStream(fos);
                    DijkstraShortestPath<String, DefaultWeightedEdge> dijkstra=new DijkstraShortestPath
                            <String, DefaultWeightedEdge>(networkGraph);
                    double diameter=0;
                    for(String sourceNode:networkGraph.vertexSet()) {
                        SingleSourcePaths<String, DefaultWeightedEdge> paths=dijkstra.getPaths(sourceNode);
                        for(String destinationNode:networkGraph.vertexSet()) {
                            double pathWeight=paths.getWeight(destinationNode);
                            if(pathWeight>diameter) {
                                diameter=pathWeight;
                            }
                        }

                    }
                    pstr.println(diameter+" ");
                    fos.close();
                }catch(Exception e) {e.printStackTrace();}

/*				try {
					FileOutputStream fos = new FileOutputStream("latency"+file, true);
					PrintStream pstr = new PrintStream(fos);
					int maxLatencyInMessages=0;
					int maxLatencyInHops=0;
					for (MyNode node : endDevices) {
						if(node.getLatencyInMessages()>maxLatencyInMessages) {
							maxLatencyInMessages=node.getLatencyInMessages();
						}
						if(node.getLatencyInHops()>maxLatencyInHops) {
							maxLatencyInHops=node.getLatencyInHops();
						}
					}
					pstr.print(maxLatencyInMessages+" "+maxLatencyInHops);
					pstr.println();
					fos.close();
				}catch(Exception e) {e.printStackTrace();}*/

                try {
                    FileOutputStream fos = new FileOutputStream("pathLength"+file, true);
                    PrintStream pstr = new PrintStream(fos);
                    for(double pathlength : Global.paths) {
                        pstr.print(pathlength+" ");
                    }
                    pstr.println();
                    fos.close();
                }catch(Exception e) {e.printStackTrace();}
/*no churn
				try {
					FileOutputStream fos = new FileOutputStream("connectedNodes"+file, true);
					PrintStream pstr = new PrintStream(fos);
					FileOutputStream foss = new FileOutputStream("reConnectionOverhead"+file, true);
					PrintStream pstrr = new PrintStream(foss);

					for(int i=1;i<=Global.disconnectedRepetitions;i++) {
						Global.joinOverhead=0;
						Global.reEstablishConnectionOverhead=0;

						ArrayList<MyNode> disconnectedNodes=new ArrayList<MyNode>();
						while(disconnectedNodes.size()<Global.disconnectedNodes*i) {
							int randomNodeIndex=Global.random.nextInt(endDevices.size());
							MyNode failNode=endDevices.get(randomNodeIndex);
							if(randomNodeIndex!=0 && !disconnectedNodes.contains(failNode)) {
								disconnectedNodes.add(failNode);
								failNode.setDisconnectedFlag(true);
							}
						}
						MyNode senderNode=endDevices.get(0);
						senderNode.setMessageId(1);
						Util.broadcast(senderNode);
						int count=0;
						for (MyNode node : endDevices) {
							if(node.getMessageId()==1) {
								node.setMessageId(0);
								count++;
							}
						}
						pstrr.print((Global.joinOverhead+Global.reEstablishConnectionOverhead)+" ");
						pstr.print(((double)count/(endDevices.size()-disconnectedNodes.size()))*100+" ");
						while(disconnectedNodes.size()>0) {
							MyNode disconnectedNode=disconnectedNodes.get(0);
							disconnectedNode.resetConnectivity();
							int randomgroupNodeIndex=Global.random.nextInt(endDevices.size());
							MyNode groupNode=endDevices.get(randomgroupNodeIndex);
							while(disconnectedNodes.contains(groupNode)) {
								randomgroupNodeIndex=Global.random.nextInt(endDevices.size());
								groupNode=endDevices.get(randomgroupNodeIndex);
							}
							disconnectedNode.findJoinGroup(groupNode);
							disconnectedNodes.remove(disconnectedNode);
						}

					}
					pstr.println();
					fos.close();
					pstrr.println();
					foss.close();
				}catch(Exception e) {e.printStackTrace();}//*/
//}for the instance 43. cf comment in the beginning line 49.
            }

        }

		/*/
		//system out stuff for debugging

		Util.generateTopologyGraph(Global.numberOfNodes);
		Util.printTopology("topology.dat");
		ArrayList<MyNode> endDevices=new ArrayList<MyNode>();
		for(MyNode  node : Global.topologyGraph.vertexSet()) {
			if(Global.topologyGraph.degreeOf(node)==1) {
				endDevices.add(node);
			}
		}
		System.out.println("number of end devices: "+endDevices.size());
		for (int i=1;i<endDevices.size();i++) {
			int randomNodeIndex=Global.random.nextInt(i);
			Global.joinOverhead=0;//reset metric
			Global.joinOverhead++;//join request
			endDevices.get(i).joinGroup(endDevices.get(randomNodeIndex));
			System.out.println(endDevices.get(i).getNodeIdentifier()+" "+endDevices.get(randomNodeIndex)+
				" "+"join overhead: "+Global.joinOverhead);
		}
		Util.printGroups(endDevices, "groups.dat");
		MyNode senderNode=null;//29 22
		for (int i=0;i<endDevices.size();i++) {
			if(endDevices.get(i).getNodeIdentifier().equals("10")) {
	//			senderNode=endDevices.get(i);
			}

			if(endDevices.get(i).getNodeIdentifier().equals("11") //||
			   //endDevices.get(i).getNodeIdentifier().equals("22")
			   ) {
	//			endDevices.get(i).setDisconnectedFlag(true);
			}
		}
		senderNode=endDevices.get(0);
		senderNode.setMessageId(1);
		Util.broadcast(senderNode);
		System.out.println("reconnect overhead: "+Global.reEstablishConnectionOverhead);
		int count=0;
		for (MyNode node : endDevices) {
			System.out.println("------------------------------------");
			for(SimpleWeightedGraph<MyNode, DefaultWeightedEdge> graph : node.getNeighborGroups()) {
				System.out.println(graph.vertexSet());
			}
			if(node.getMessageId()==1) {
				count++;
			}
		}
		int maxLatencyInMessages=0;
		int maxLatencyInHops=0;
		for (MyNode node : endDevices) {
			if(node.getLatencyInMessages()>maxLatencyInMessages) {
				maxLatencyInMessages=node.getLatencyInMessages();
			}
			if(node.getLatencyInHops()>maxLatencyInHops) {
				maxLatencyInHops=node.getLatencyInHops();
			}
		}
		System.out.println("count: "+count+"\n"
				+ "redundant messages: "+Global.redundantMessages+"\n"
				+ "broadcast messages: "+Global.broadcastMessages+"\n"
				+ "broadcast hops: "+Global.broadcastHops+"\n"
				+ "latency in messages: "+maxLatencyInMessages+"\n"
				+ "latency in hops "+maxLatencyInHops);
		//*/
    }
}

