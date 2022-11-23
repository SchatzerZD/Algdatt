import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLOutput;
import java.util.*;

public class Navigasjon {
  static class Node implements Comparable<Node> {

    int distance;
    double latitude;
    double longtitude;
    final List<Edge> edges;
    final int nodeNr;
    byte code;
    String name;
    Node previousNode;
    boolean inQueue;

    int estDest;

    Node(int nodeNr, double latitude, double longtitude){
      this.nodeNr = nodeNr;
      this.latitude = latitude;
      this.longtitude = longtitude;

      this.edges = new ArrayList<>();
      this.distance = Integer.MAX_VALUE;

      this.code = 0;
      this.name = null;
      this.previousNode = null;
      this.inQueue = false;
      this.estDest = 0;
    }

    void addEdge(Edge edge){
      edges.add(edge);
    }

    int getPriority(){
      return estDest + distance;
    }


    @Override
    public int compareTo(Node o) {
      if(this.getPriority() == o.getPriority()) return 0;
      return (this.getPriority() > o.getPriority()) ? 1 : -1;
    }

  }

  static class Edge{

    int weight;
    Node fromNode;
    Node toNode;

    Edge(int weight, Node fromNode, Node toNode) {
      this.weight = weight;
      this.fromNode = fromNode;
      this.toNode = toNode;
    }

  }

  static class DijkstraAlt{

    int N,K,I;

    Queue<Node> nodePriorityQueue = new PriorityQueue<>();
    List<Node> landmarkNodes = new ArrayList<>();
    Node[] nodeList;
    int numberOfNodesVisited;
    List<String> fromLandmarkLines;
    List<String> toLandmarkLines;

    void init(){
      nodePriorityQueue.clear();
      for (Node node: nodeList) {
        node.distance = Integer.MAX_VALUE;
        node.previousNode = null;
        node.inQueue = false;
        node.estDest = 0;
      }
    }

    void dijkstra(Node startNode, Node destinationNode){
      startNode.distance = 0;

      nodePriorityQueue.add(startNode);
      numberOfNodesVisited = 0;

      Node currentNode;
      while(nodePriorityQueue.peek() != destinationNode){
        currentNode = nodePriorityQueue.remove();
        currentNode.inQueue = true;
        numberOfNodesVisited++;

        iterateOverEdges(currentNode);
      }
    }

    void dijkstra(Node startNode){
      startNode.distance = 0;

      nodePriorityQueue.add(startNode);
      Node currentNode;
      numberOfNodesVisited = 0;

      while(nodePriorityQueue.peek() != null){
        currentNode = nodePriorityQueue.remove();
        currentNode.inQueue = true;
        numberOfNodesVisited++;

        iterateOverEdges(currentNode);
      }
    }


    void alt(Node startNode, Node destinationNode) {

      startNode.distance = 0;
      startNode.estDest = getBestEstimate(startNode,destinationNode,fromLandmarkLines,toLandmarkLines);
      nodePriorityQueue.add(startNode);
      numberOfNodesVisited = 0;

      Node currentNode;
      while(nodePriorityQueue.peek() != destinationNode){
        currentNode = nodePriorityQueue.remove();
        currentNode.inQueue = true;
        numberOfNodesVisited++;

        for (Edge edge: currentNode.edges) {
          Node adjNode = edge.toNode;
          if((edge.weight + currentNode.distance) < adjNode.distance){
            adjNode.distance = edge.weight + currentNode.distance;
            adjNode.previousNode = currentNode;
            if(adjNode.inQueue && nodePriorityQueue.remove(adjNode)){
              nodePriorityQueue.add(adjNode);
            }
          }
          if(adjNode.estDest == 0)adjNode.estDest = getBestEstimate(adjNode,destinationNode,fromLandmarkLines,toLandmarkLines);

          if(!adjNode.inQueue){
            nodePriorityQueue.add(adjNode);
            adjNode.inQueue = true;
          }
        }
      }
    }


    int getBestEstimate(Node startNode, Node destinationNode,List<String> fromLandmarkLines, List<String> toLandmarkLines){
      int bestEstimateToDestination = -1;
      for (int i = 0; i < landmarkNodes.size(); i++) {
        int distanceFromLandmarkToDestination = Integer.parseInt(fromLandmarkLines.get(destinationNode.nodeNr).split(",")[i]);
        int distanceFromLandmarkToStart = Integer.parseInt(fromLandmarkLines.get(startNode.nodeNr).split(",")[i]);

        int differenceFromLandmark = distanceFromLandmarkToDestination - distanceFromLandmarkToStart;
        if(differenceFromLandmark < 0)differenceFromLandmark = 0;

        int distanceFromStartToLandmark = Integer.parseInt(toLandmarkLines.get(startNode.nodeNr).split(",")[i]);
        int distanceFromDestinationToLandmark = Integer.parseInt(toLandmarkLines.get(destinationNode.nodeNr).split(",")[i]);

        int differenceToLandmark = distanceFromStartToLandmark - distanceFromDestinationToLandmark;

        int biggestNumber = Math.max(differenceFromLandmark, differenceToLandmark);
        bestEstimateToDestination = Math.max(bestEstimateToDestination, biggestNumber);
      }
      return bestEstimateToDestination;
    }


    void readLinesFromFile(String fromLandmarksFileName, String toLandmarksFileName) throws IOException {
      fromLandmarkLines = getLinesFromFile(fromLandmarksFileName);
      toLandmarkLines = getLinesFromFile(toLandmarksFileName);
    }

    List<String> getLinesFromFile(String fileName) throws IOException {
      return Files.readAllLines(Path.of(fileName));
    }

    void createNodes(BufferedReader br) throws IOException {
      StringTokenizer st = new StringTokenizer(br.readLine());

      N = Integer.parseInt(st.nextToken());
      nodeList = new Node[N];

      for (int i = 0; i < N; i++) {
        st = new StringTokenizer(br.readLine());
        int nodeNr = Integer.parseInt(st.nextToken());
        double lat = Double.parseDouble(st.nextToken());
        double lon = Double.parseDouble(st.nextToken());

        nodeList[nodeNr] = new Node(nodeNr,lat,lon);
      }
      br.close();

    }

    void createEdges(BufferedReader br) throws IOException{
      StringTokenizer st = new StringTokenizer(br.readLine());

      for (Node node: nodeList) {
        node.edges.clear();
      }

      K = Integer.parseInt(st.nextToken());
      for (int i = 0; i < K; i++) {
        st = new StringTokenizer(br.readLine());
        Node fromNode = nodeList[Integer.parseInt(st.nextToken())];
        Node toNode = nodeList[Integer.parseInt(st.nextToken())];
        int weight = Integer.parseInt(st.nextToken());
        Edge edge = new Edge(weight,fromNode,toNode);
        fromNode.addEdge(edge);

      }
      br.close();
    }

    void createOppositeEdges(BufferedReader br) throws IOException{
      StringTokenizer st = new StringTokenizer(br.readLine());

      for (Node node: nodeList) {
        node.edges.clear();
      }

      K = Integer.parseInt(st.nextToken());
      for (int i = 0; i < K; i++) {
        st = new StringTokenizer(br.readLine());
        Node toNode = nodeList[Integer.parseInt(st.nextToken())];
        Node fromNode = nodeList[Integer.parseInt(st.nextToken())];
        int weight = Integer.parseInt(st.nextToken());
        Edge edge = new Edge(weight,fromNode,toNode);
        fromNode.addEdge(edge);

      }
      br.close();
    }

    void createInterestNodes(BufferedReader br, int[] landmarkNodeNrList)throws IOException{
      StringTokenizer st = new StringTokenizer(br.readLine());

      I = Integer.parseInt(st.nextToken());
      for (int i = 0; i < I; i++) {
        st = new StringTokenizer(br.readLine());
        Node landmarkNode = nodeList[Integer.parseInt(st.nextToken())];
        landmarkNode.code = Byte.parseByte(st.nextToken());
        landmarkNode.name = st.nextToken();
      }
      br.close();

      for (int j : landmarkNodeNrList) {
        landmarkNodes.add(nodeList[j]);
      }
    }


    Node[] getNearestInterestingPlaces(Node startNode,byte interestCode, int amount) throws IOException {
      Node[] interestingNodes = new Node[amount];

      init();
      startNode.distance = 0;
      int nodesAdded = 0;

      nodePriorityQueue.add(startNode);
      Node currentNode;

      while(nodePriorityQueue.peek() != null && nodesAdded < amount){
        currentNode = nodePriorityQueue.remove();
        currentNode.inQueue = true;

        if(currentNode.code > 0 && currentNode != startNode){
          if((currentNode.code & interestCode) == interestCode){
            interestingNodes[nodesAdded] = currentNode;
            nodesAdded++;
          }
        }

        iterateOverEdges(currentNode);
      }

      return interestingNodes;
    }

    private void iterateOverEdges(Node currentNode) {
      for (Edge edge : currentNode.edges) {
        if(currentNode.distance + edge.weight < edge.toNode.distance){
          edge.toNode.distance = currentNode.distance + edge.weight;
          edge.toNode.previousNode = edge.fromNode;
          if(edge.toNode.inQueue && nodePriorityQueue.remove(edge.toNode)){
            nodePriorityQueue.add(edge.toNode);
          }else{
            nodePriorityQueue.add(edge.toNode);
            edge.toNode.inQueue = true;
          }
        }
      }
    }


    void createLandmarks(String filename)throws IOException{
      String[][] landmarkDistanceTable = new String[landmarkNodes.size()][N];

      for (int i = 0; i < landmarkNodes.size(); i++) {
        init();
        dijkstra(landmarkNodes.get(i));
        System.out.println("Landmark: " + i);
        for (int j = 0; j < N; j++) {
          landmarkDistanceTable[i][j] = String.valueOf(nodeList[j].distance);
        }
      }

      PrintWriter printWriter = new PrintWriter(filename, StandardCharsets.UTF_8);
      for (int i = 0; i < N; i++) {
        for (int j = 0; j < landmarkNodes.size(); j++) {
            printWriter.print(landmarkDistanceTable[j][i]);
            if(j != landmarkNodes.size()-1){
              printWriter.print(",");
            }
        }
        printWriter.print("\n");
      }
      printWriter.close();

    }



    void writeToFile(String filename) throws IOException {
      PrintWriter printWriter = new PrintWriter(filename, StandardCharsets.UTF_8);
      printWriter.println("Latitude,Longitude,Node name");

      for (Node node : nodeList) {
        printWriter.println(node.latitude + "," + node.longtitude + "," + node.nodeNr);
      }
      printWriter.close();
    }

    void writeCoordsToFile(String filename, Node node)throws IOException {
      PrintWriter printWriter = new PrintWriter(filename, StandardCharsets.UTF_8);
      printWriter.println("Latitude,Longitude,Node name");

      Node currentNode = node;
      while(currentNode != null){
        printWriter.println(currentNode.latitude + "," + currentNode.longtitude + "," + currentNode.nodeNr);
        currentNode = currentNode.previousNode;
      }
      printWriter.close();
    }

    void printNodePath(Node node){

      Node currentNode = node;
      while(currentNode != null){
        System.out.print(currentNode.nodeNr + ((currentNode.previousNode != null) ? "-" : ""));
        currentNode = currentNode.previousNode;
      }
      System.out.println();
    }

    int getNumberOfNodesInPath(Node node){
      int numberOfNodesInPath = 0;

      Node currentNode = node;
      while(currentNode != null){
        numberOfNodesInPath++;
        currentNode = currentNode.previousNode;
      }
      return numberOfNodesInPath;
    }

  }




  public static void main(String[] args) throws IOException {

    String nodeFileName = "noder.txt";
    String edgeFileName = "kanter.txt";
    String interestFileName = "interessepkt.txt";

    BufferedReader nodesBr = new BufferedReader(new FileReader(System.getProperty("user.dir") + System.getProperty("file.separator") + nodeFileName));
    BufferedReader edgeBr = new BufferedReader(new FileReader(System.getProperty("user.dir") + System.getProperty("file.separator") + edgeFileName));
    BufferedReader interestBr = new BufferedReader(new FileReader(System.getProperty("user.dir") + System.getProperty("file.separator") + interestFileName));

    BufferedReader edgeOBr = new BufferedReader(new FileReader(System.getProperty("user.dir") + System.getProperty("file.separator") + edgeFileName));

    int[] landmarkNodeNrList = new int[]{894067,3109952,5474505,2315409,4677168};
                                       //Helsinki,Tromsø,Stockholm,Mandal,Bergen

    DijkstraAlt graph = new DijkstraAlt();

    graph.createNodes(nodesBr);
    graph.createEdges(edgeBr);
    graph.createInterestNodes(interestBr,landmarkNodeNrList);
    System.out.println("Graph created");

    File fLFile = new File("fromLandmarks.csv");
    File tLFile = new File("toLandmarks.csv");

    if(!(fLFile.exists() && tLFile.exists())){
      //    ALT preprosessering
      System.out.println("Preproccesing for ALT started...");
      graph.createLandmarks("fromLandmarks.csv");
      graph.createOppositeEdges(edgeOBr);
      graph.createLandmarks("toLandmarks.csv");
      System.out.println("Preprocessing for ALT finished\nRun the program again");
      return;
    }

    Node fromNode = graph.nodeList[232073];
    Node toNode = graph.nodeList[2518780];

    graph.init();
    graph.readLinesFromFile("fromLandmarks.csv","toLandmarks.csv");
    long start = System.currentTimeMillis();
    graph.alt(fromNode,toNode);
    long finish = System.currentTimeMillis();

    int nodeDistance = toNode.distance/100;

    System.out.format("%-32s %s %2s %s","\n\nALT\n+ " + "-".repeat(31),"+","-".repeat(46),"+\n");
    System.out.format("%-32s %2s %-32s %16s","|Tur","|",fromNode.name + "-" + toNode.name,"|\n");
    System.out.format("%-32s %s %-32s %s","+ " + "-".repeat(31),"+","-".repeat(46),"+\n");
    System.out.format("%-32s %2s %-32s %16s","|Fra Node","|",fromNode.nodeNr,"|\n");
    System.out.format("%-32s %s %-32s %s","+ " + "-".repeat(31),"+","-".repeat(46),"+\n");
    System.out.format("%-32s %2s %-32s %16s","|Til Node","|",toNode.nodeNr,"|\n");
    System.out.format("%-32s %s %-32s %s","+ " + "-".repeat(31),"+","-".repeat(46),"+\n");
    System.out.format("%-32s %2s %-32s %16s","|Noder Besøkt","|",graph.numberOfNodesVisited,"|\n");
    System.out.format("%-32s %s %-32s %s","+ " + "-".repeat(31),"+","-".repeat(46),"+\n");
    System.out.format("%-32s %2s %-32s %16s","|Antall Noder i Ruta","|",graph.getNumberOfNodesInPath(toNode),"|\n");
    System.out.format("%-32s %s %-32s %s","+ " + "-".repeat(31),"+","-".repeat(46),"+\n");
    System.out.format("%-32s %2s %-32s %16s","|Kjøretid (Distanse)","|", String.format("%02d:%02d:%02d", nodeDistance/3600,(nodeDistance % 3600) / 60,nodeDistance % 60),"|\n");
    System.out.format("%-32s %s %-32s %s","+ " + "-".repeat(31),"+","-".repeat(46),"+\n");
    System.out.format("%-32s %2s %-32s %16s","|Algoritme Tid","|",((double)(finish - start)) / 1000 + "s","|\n");
    System.out.format("%-32s %s %-32s %s","+ " + "-".repeat(31),"+","-".repeat(46),"+\n");

    graph.init();
    start = System.currentTimeMillis();
    graph.dijkstra(fromNode,toNode);
    finish = System.currentTimeMillis();

    nodeDistance = toNode.distance/100;


    System.out.format("%-32s %s %2s %s","\n\nDIJKSTRA\n+ " + "-".repeat(31),"+","-".repeat(46),"+\n");
    System.out.format("%-32s %2s %-32s %16s","|Tur","|",fromNode.name + "-" + toNode.name,"|\n");
    System.out.format("%-32s %s %-32s %s","+ " + "-".repeat(31),"+","-".repeat(46),"+\n");
    System.out.format("%-32s %2s %-32s %16s","|Fra Node","|",fromNode.nodeNr,"|\n");
    System.out.format("%-32s %s %-32s %s","+ " + "-".repeat(31),"+","-".repeat(46),"+\n");
    System.out.format("%-32s %2s %-32s %16s","|Til Node","|",toNode.nodeNr,"|\n");
    System.out.format("%-32s %s %-32s %s","+ " + "-".repeat(31),"+","-".repeat(46),"+\n");
    System.out.format("%-32s %2s %-32s %16s","|Noder besøkt","|",graph.numberOfNodesVisited,"|\n");
    System.out.format("%-32s %s %-32s %s","+ " + "-".repeat(31),"+","-".repeat(46),"+\n");
    System.out.format("%-32s %2s %-32s %16s","|Antall Noder i Ruta","|",graph.getNumberOfNodesInPath(toNode),"|\n");
    System.out.format("%-32s %s %-32s %s","+ " + "-".repeat(31),"+","-".repeat(46),"+\n");
    System.out.format("%-32s %2s %-32s %16s","|Kjøretid (Distanse)","|", String.format("%02d:%02d:%02d", nodeDistance/3600,(nodeDistance % 3600) / 60,nodeDistance % 60),"|\n");
    System.out.format("%-32s %s %-32s %s","+ " + "-".repeat(31),"+","-".repeat(46),"+\n");
    System.out.format("%-32s %2s %-32s %16s","|Algoritme Tid","|",((double)(finish - start)) / 1000 + "s","|\n");
    System.out.format("%-32s %s %-32s %s","+ " + "-".repeat(31),"+","-".repeat(46),"+\n");

    graph.writeCoordsToFile("coords.csv",toNode);


    System.out.println("\n");
    Node[] interestingPlaces = graph.getNearestInterestingPlaces(graph.nodeList[3509663], (byte) 8,8);
    for (int i = 0; i < interestingPlaces.length; i++) {
      graph.writeCoordsToFile("interest" + i + ".csv",interestingPlaces[i]);
    }

  }


}
