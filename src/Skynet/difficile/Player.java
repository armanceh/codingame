package Skynet.difficile;

import java.util.*;

import static java.util.stream.Collectors.toSet;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int N = in.nextInt(); // the total number of nodes in the level, including the gateways
        int L = in.nextInt(); // the number of links
        int E = in.nextInt(); // the number of exit gateways

        Map<Integer, ArrayList<Integer>> nodeMap = new HashMap<>();
        for (int i = 0; i < L; i++) {
            int N1 = in.nextInt(); // N1 and N2 defines a link between these nodes
            int N2 = in.nextInt();
            if(nodeMap.get(N1)==null){
                ArrayList<Integer> l = new ArrayList<>();
                l.add(N2);
                nodeMap.put(N1,l);
            } else {
                nodeMap.get(N1).add(N2);
            }
            if(nodeMap.get(N2)==null){
                ArrayList<Integer> l = new ArrayList<>();
                l.add(N1);
                nodeMap.put(N2,l);
            } else {
                nodeMap.get(N2).add(N1);
            }
        }

        Map<Integer,Passerelle> passerelleMap = new HashMap<>();
        List<Integer> exits = new ArrayList<>();
        for (int i = 0; i < E; i++) {
            int EI = in.nextInt(); // the index of a gateway node
            exits.add(EI);
            for(int node : nodeMap.get(EI)){
                if(passerelleMap.get(node) != null){
                    passerelleMap.get(node).addExit(EI);
                } else {
                    passerelleMap.put(node, new Passerelle(node,EI));
                }
            }
        }

        // game loop
        while (true) {
            int SI = in.nextInt(); // The index of the node on which the Skynet agent is positioned this turn

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            Passerelle solution;
            if(passerelleMap.containsKey(SI)){;

                solution = passerelleMap.get(SI);
            } else {
                Set<Integer> maxSizePasserelle = getBiggestPasserelles(passerelleMap);

                int passerelle = -1;
                int distMax = 1000000;
                for(int p : maxSizePasserelle) {
                    int d = distanceAlgo(SI, p, nodeMap, passerelleMap, exits);
                    if(d<distMax) {
                        passerelle = p;
                        distMax = d;
                    }
                }
                solution = passerelleMap.get(passerelle);
            }
            int exit = solution.exits.iterator().next();
            System.out.println(solution.nodeNumber + " " + exit); // Example: 3 4 are the indices of the nodes you wish to sever the link between
            solution.exits.remove(exit);
            if(solution.exits.size() == 0){
                passerelleMap.remove(solution.nodeNumber);
            }
        }
    }

    private static Set<Integer> getBiggestPasserelles(Map<Integer, Passerelle> passerelleMap) {
        Set<Integer> maxSizePasserelle = new HashSet<>();
        int tailleMax=0;
        for(Map.Entry<Integer,Passerelle> p : passerelleMap.entrySet()) {
            if(p.getValue().exits.size() > tailleMax){
                tailleMax = p.getValue().exits.size() ;
                maxSizePasserelle.clear();
                maxSizePasserelle.add(p.getKey());
            } else if (p.getValue().exits.size()  == tailleMax){
                maxSizePasserelle.add(p.getKey());
            }
        }
        return maxSizePasserelle;
    }

    public static int distanceAlgo(int depart, int arrivee, Map<Integer,ArrayList<Integer>> nodeMap,Map<Integer,Passerelle> passerelleMap, List<Integer> exits) {
        Map<Integer, Integer> distanceForEachNode = new HashMap<>();
        //init
        for(Integer n : nodeMap.keySet()){
            distanceForEachNode.put(n, 10000000);
        }
        ArrayList<Integer> nodeDone = new ArrayList<>();
        distanceForEachNode.put(depart, 0);
        while (nodeDone.size() != nodeMap.keySet().size()- exits.size()) {
            //choix du noeud
            int node = choixDuNoeud(nodeDone, nodeMap, passerelleMap, distanceForEachNode, depart, exits);
            for(Integer n : nodeMap.get(node)){
                if(!nodeDone.contains(n)){
                    int distance = 10;
                    if(passerelleMap.containsKey(n)){
                        distance = 1;
                    }

                    int d = Math.min(distanceForEachNode.get(n), distanceForEachNode.get(node) + distance);
                    distanceForEachNode.put(n,d);
                }
            }
            nodeDone.add(node);
        }
        return distanceForEachNode.get(arrivee);
    }

    private static int choixDuNoeud(ArrayList<Integer> nodeDone, Map<Integer, ArrayList<Integer>> nodeMap, Map<Integer, Passerelle> passerelleMap, Map<Integer, Integer> distanceForEachNode, int depart,List<Integer> exits) {

        if(nodeDone.isEmpty()){
            return depart;
        } else {

            Set<Integer> noeudsVoisins = nodeDone.stream().flatMap(nde ->  nodeMap.get(nde).stream().filter(val -> !exits.contains(val) && !nodeDone.contains(val))).collect(toSet());

            int result = -1;
            for (int i : noeudsVoisins) {
                if (result == -1 || distanceForEachNode.get(i) < distanceForEachNode.get(result)) {
                    result = i;
                }
            }
            return result;
        }
    }

}

class Passerelle{
    public int nodeNumber;
    public Set<Integer> exits = new HashSet<>();

    public Passerelle(int node, int exit){
        this.nodeNumber = node;
        this.exits.add(exit);
    }

    public void addExit(int e){
        this.exits.add(e);
    }
}