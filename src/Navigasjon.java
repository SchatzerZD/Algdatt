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
    Node[] nodeList;

    void init(){
      for (Node node: nodeList) {
        node.distance = Integer.MAX_VALUE;
        node.previousNode = null;
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
          if(!edge.toNode.visited && currentNode.distance + edge.weight < edge.toNode.distance){
            edge.toNode.distance = currentNode.distance + edge.weight;
            edge.toNode.previousNode = edge.fromNode;
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
          if(!edge.toNode.visited && currentNode.distance + edge.weight < edge.toNode.distance){
            edge.toNode.distance = currentNode.distance + edge.weight;
            edge.toNode.previousNode = edge.fromNode;
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

    void writeToFile(String filename) throws IOException {
      PrintWriter printWriter = new PrintWriter(filename, StandardCharsets.UTF_8);

      for (Node node : nodeList) {
        printWriter.println(node.latitude + "," + node.longtitude + "," + node.nodeNr);
      }
      printWriter.close();
    }

    void writeCoordsToFile(String filename, Node node)throws IOException {
      PrintWriter printWriter = new PrintWriter(filename, StandardCharsets.UTF_8);

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

    System.out.println("Graph created");

    Node destinationNode = graph.nodeList[50];
    graph.start(graph.nodeList[0],destinationNode);

    System.out.println();
    System.out.println();

    graph.writeCoordsToFile("coords.txt",destinationNode);
    System.out.println(destinationNode.distance);



  }


}
