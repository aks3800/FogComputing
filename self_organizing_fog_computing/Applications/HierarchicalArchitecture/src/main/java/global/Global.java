package global;

import java.util.ArrayList;
import java.util.Random;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import core.MyNode;


/**
 * @author Basil
 * This class contains global information such as initial values of the simulation
 * (group size, number of nodes etc.), variables for measuring overhead etc.
 *
 */
public class Global {
    /**
     * weighted graph that represent the underlying infrastructure
     */
    public static SimpleWeightedGraph<MyNode, DefaultEdge> topologyGraph=new SimpleWeightedGraph<MyNode
            , DefaultEdge>(DefaultEdge.class);
    /**
     * global random for having the same sequence of randomised values (for recreating the scenarios)
     */
    public static Random random=new Random(0);   //1 for alternative runs
    /**
     * count redundant transmissions i.e., a node receiving the same message twice
     */
    public static int redundantMessages=0;
    /**
     * count number of messages when new nodes join the overlay
     */
    public static int joinOverhead=0;
    /**
     * count number of messages when recovering from failure
     * todo : currently not measured correctly. different messages can be merged together
     */
    public static int reEstablishConnectionOverhead=0;
    /**
     * count number of messages sent when broadcasting
     */
    public static int broadcastMessages=0;
    /**
     * count number of hops travelled when broadcasting
     */
    public static int broadcastHops=0;
    /**
     * list that has all the path weights (in hops) of the topology
     * used for finding the average path length (in hops)
     */
    public static ArrayList<Double> paths=new ArrayList<Double>();
    /**
     * measure latency and use messages as unit
     */
    public static int latencyInMessages=0;
    /**
     * measure latency and use hops as unit
     */
    public static int latencyInHops=0;
    /**
     * number of randomised scenarios for the emulation
     */
    public static int iterations=50;
    /**
     * number of nodes in the underlying infrastructure (including access points and end-devices)
     */
    public static int numberOfNodes=1250;   //10880 8780 6660 4525 2350 1250 650 146 34
    /**
     * number of end-devices in the underlying infrastructure
     */
    public static int numOfEndDevices=1000; //10000 8000 6000 4000 2000 1000 500 100 20
    /**
     * enable proximity awareness for all nodes
     */
    public static int hopAwareEnabled=0;
    /**
     * max number of nodes in a group
     */
    public static int groupSize=4;
    /**
     * max number of groups that a node belongs to
     */
    public static int numOfGroups=2;
    /**
     * number of nodes that fail during simulation
     */
    public static int disconnectedNodes=(numOfEndDevices*2)/100; //(numOfEndDevices*2)/100; //2%
    /**
     * number of repetitions for the failure scenario. in each repetition all disconnected nodes
     * rejoin and the number of disconnectedNodes is multiplied by an incremented (+1) variable
     * e.g., 1st iteration: disconnectedNodes*1, 2st iteration: disconnectedNodes*2  etc.
     */
    public static int disconnectedRepetitions=0;//in each repetition disconnectedNodes is multiplied
    //by an incremental variable (1++)
}
