import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
    boolean visited;

    int estDest;
    int priority;

    Node(int nodeNr, double latitude, double longtitude){
      this.nodeNr = nodeNr;
      this.latitude = latitude;
      this.longtitude = longtitude;

      this.edges = new ArrayList<>();
      this.distance = Integer.MAX_VALUE;

      this.code = 0;
      this.name = null;
      this.previousNode = null;
      this.visited = false;
      this.estDest = 0;
    }

    void addEdge(Edge edge){
      edges.add(edge);
    }


    @Override
    public int compareTo(Node o) {
      this.priority = this.estDest + this.distance;
      o.priority = o.estDest + o.distance;
      if(this.priority == o.priority) return 0;
      return (this.priority > o.priority) ? 1 : -1;
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

    int N,K,L;

    Queue<Node> nodePriorityQueue = new PriorityQueue<>();
    List<Node> landmarkNodes = new ArrayList<>();
    Node[] nodeList;

    void init(){
      nodePriorityQueue.clear();
      for (Node node: nodeList) {
        node.distance = Integer.MAX_VALUE;
        node.previousNode = null;
        node.visited = false;
        node.estDest = 0;
      }
    }

    void dijkstra(Node startNode, Node destinationNode){
      startNode.distance = 0;

      nodePriorityQueue.add(startNode);

      Node currentNode;
      while(nodePriorityQueue.peek() != destinationNode){
        currentNode = nodePriorityQueue.remove();
        currentNode.visited = true;

        for (Edge edge : currentNode.edges) {
          if(!edge.toNode.visited){
            if(currentNode.distance + edge.weight < edge.toNode.distance){
              edge.toNode.distance = currentNode.distance + edge.weight;
              edge.toNode.previousNode = edge.fromNode;
            }
            nodePriorityQueue.remove(edge.toNode);
            nodePriorityQueue.add(edge.toNode);
          }
        }

       // System.out.printf("%d %d\n",nodePriorityQueue.element().nodeNr,nodePriorityQueue.element().distance);


      }
    }

    void dijkstra(Node startNode){
      startNode.distance = 0;

      nodePriorityQueue.add(startNode);
      Node currentNode;

      while(nodePriorityQueue.peek() != null){
        currentNode = nodePriorityQueue.remove();
        currentNode.visited = true;

        for (Edge edge : currentNode.edges) {
          if(!edge.toNode.visited){
            if(currentNode.distance + edge.weight < edge.toNode.distance){
              edge.toNode.distance = currentNode.distance + edge.weight;
              edge.toNode.previousNode = edge.fromNode;
            }
            nodePriorityQueue.remove(edge.toNode);
            nodePriorityQueue.add(edge.toNode);
          }
        }
       //System.out.printf("%d %d\n",nodePriorityQueue.element().nodeNr,nodePriorityQueue.element().distance);
      }
    }


    void alt(Node startNode, Node destinationNode, String fromLandmarksFileName, String toLandmarksFileName) throws IOException {
      List<String> fromLandmarkLines = getLinesFromFile(fromLandmarksFileName);
      List<String> toLandmarkLines = getLinesFromFile(toLandmarksFileName);

      System.out.println(getBestEstimate(startNode,destinationNode,fromLandmarkLines,toLandmarkLines));

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

    void createInterestNodes(BufferedReader br)throws IOException{
      StringTokenizer st = new StringTokenizer(br.readLine());

      L = Integer.parseInt(st.nextToken());
      for (int i = 0; i < L; i++) {
        st = new StringTokenizer(br.readLine());
        Node landmarkNode = nodeList[Integer.parseInt(st.nextToken())];
        landmarkNode.code = Byte.parseByte(st.nextToken());
        landmarkNode.name = st.nextToken();
      }
      br.close();

      landmarkNodes.add(nodeList[894067]); //Helsinki
      landmarkNodes.add(nodeList[3109952]); //Tromsø
      landmarkNodes.add(nodeList[5474505]); //Stockholm
      landmarkNodes.add(nodeList[2315409]); //Mandal
      landmarkNodes.add(nodeList[4677168]); //Bergen
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
  }




  public static void main(String[] args) throws IOException {

    String nodeFileName = "noder.txt";
    String edgeFileName = "kanter.txt";
    String interestFileName = "interessepkt.txt";

    BufferedReader nodesBr = new BufferedReader(new FileReader(System.getProperty("user.dir") + System.getProperty("file.separator") + nodeFileName));
    BufferedReader edgeBr = new BufferedReader(new FileReader(System.getProperty("user.dir") + System.getProperty("file.separator") + edgeFileName));
    BufferedReader interestBr = new BufferedReader(new FileReader(System.getProperty("user.dir") + System.getProperty("file.separator") + interestFileName));

    BufferedReader edgeOBr = new BufferedReader(new FileReader(System.getProperty("user.dir") + System.getProperty("file.separator") + edgeFileName));

    DijkstraAlt graph = new DijkstraAlt();

    graph.createNodes(nodesBr);
    graph.createEdges(edgeBr);
    graph.createInterestNodes(interestBr);
    System.out.println("Graph created");

    /*graph.createLandmarks("fromLandmarks.csv");
    graph.createOppositeEdges(edgeOBr);
    graph.createLandmarks("toLandmarks.csv");*/

    graph.init();
    graph.alt(graph.nodeList[0],graph.nodeList[2],"fromLandmarks.csv","toLandmarks.csv");
    graph.init();
    graph.dijkstra(graph.nodeList[0],graph.nodeList[2]);
    System.out.println(graph.nodeList[2].distance);

    graph.init();
    graph.alt(graph.nodeList[0],graph.nodeList[4],"fromLandmarks.csv","toLandmarks.csv");
    graph.init();
    graph.dijkstra(graph.nodeList[0],graph.nodeList[4]);
    System.out.println(graph.nodeList[4].distance);

    graph.init();
    graph.alt(graph.nodeList[0],graph.nodeList[33],"fromLandmarks.csv","toLandmarks.csv");
    graph.init();
    graph.dijkstra(graph.nodeList[0],graph.nodeList[33]);
    System.out.println(graph.nodeList[33].distance);

    graph.init();
    graph.alt(graph.nodeList[5],graph.nodeList[7],"fromLandmarks.csv","toLandmarks.csv");
    graph.init();
    graph.dijkstra(graph.nodeList[5],graph.nodeList[7]);
    System.out.println(graph.nodeList[7].distance);

    /*for (Node node: graph.nodeList) {
      System.out.print((node.distance == Integer.MAX_VALUE) ? node.nodeNr + "\t" + node.distance + "\n" : "                  \r");
    }*/





  }


}
