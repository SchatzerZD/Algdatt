import java.io.*;
import java.nio.charset.StandardCharsets;
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
    int heuristicValue;



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
    }

    void addEdge(Edge edge){
      edges.add(edge);
    }


    @Override
    public int compareTo(Node o) {
      if(this.distance == o.distance) return 0;
      return (this.distance > o.distance) ? 1 : -1;
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
      for (Node node: nodeList) {
        node.distance = Integer.MAX_VALUE;
        node.previousNode = null;
        node.visited = false;
      }
    }


    void start(Node startNode, Node destinationNode){
      startNode.distance = 0;
      dijkstra(startNode,destinationNode);
    }

    void start(Node startNode){
      startNode.distance = 0;
      dijkstra(startNode);
    }

    void dijkstra(Node startNode, Node destinationNode){

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

    }

    void createEdges(BufferedReader br) throws IOException{
      StringTokenizer st = new StringTokenizer(br.readLine());

      K = Integer.parseInt(st.nextToken());
      for (int i = 0; i < K; i++) {
        st = new StringTokenizer(br.readLine());
        Node fromNode = nodeList[Integer.parseInt(st.nextToken())];
        Node toNode = nodeList[Integer.parseInt(st.nextToken())];
        int weight = Integer.parseInt(st.nextToken());
        Edge edge = new Edge(weight,fromNode,toNode);
        fromNode.addEdge(edge);

      }
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
    }

    void createLandmarks(String filename)throws IOException{

      landmarkNodes.add(nodeList[894067]); //Helsinki
      landmarkNodes.add(nodeList[3109952]); //Tromsø
      landmarkNodes.add(nodeList[5474505]); //Stockholm
      landmarkNodes.add(nodeList[2315409]); //Mandal
      landmarkNodes.add(nodeList[4677168]); //Bergen

      String[][] landmarkDistanceTable = new String[landmarkNodes.size()][N];

      for (int i = 0; i < landmarkNodes.size(); i++) {
        init();
        start(landmarkNodes.get(i));
        for (int j = 0; j < N; j++) {
          if(nodeList[j].distance == Integer.MAX_VALUE){
            System.out.println("Landmark: " + i + "\tNode: " + j);
          }
          landmarkDistanceTable[i][j] = String.valueOf(nodeList[j].distance);
        }
      }
      init();

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

    DijkstraAlt graph = new DijkstraAlt();
    graph.createNodes(nodesBr);
    graph.createEdges(edgeBr);
    graph.createInterestNodes(interestBr);

    //graph.createLandmarks("landmarks.csv");

    graph.start(graph.nodeList[0]);

    /*for (Node node: graph.nodeList) {
      System.out.print((node.distance == Integer.MAX_VALUE) ? node.nodeNr + "\t" + node.distance + "\n" : "                  \r");
    }*/

    System.out.println("Graph created");



  }


}
