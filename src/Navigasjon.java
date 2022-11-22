import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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



    Node(int nodeNr, double latitude, double longtitude){
      this.nodeNr = nodeNr;
      this.latitude = latitude;
      this.longtitude = longtitude;

      this.edges = new ArrayList<>();
      this.distance = Integer.MAX_VALUE;

      this.code = 0;
      this.name = null;
      this.previousNode = null;
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

    void start(Node startNode, Node destinationNode){
      startNode.distance = 0;
      dijkstra(startNode,destinationNode);
    }

    void dijkstra(Node startNode, Node destinationNode){

      nodePriorityQueue.add(startNode);
      List<Node> visitedNodes = new ArrayList<>();

      Node currentNode;
      while(nodePriorityQueue.peek() != destinationNode){
        currentNode = nodePriorityQueue.remove();
        visitedNodes.add(currentNode);

        for (Edge edge : currentNode.edges) {
          if(currentNode.distance + edge.weight < edge.toNode.distance){
            edge.toNode.distance = currentNode.distance + edge.weight;
            edge.toNode.previousNode = edge.fromNode;
          }

          if(!visitedNodes.contains(edge.toNode)){
            nodePriorityQueue.add(edge.toNode);
          }
        }

        System.out.printf("%d %d\n",nodePriorityQueue.element().nodeNr,nodePriorityQueue.element().distance);

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

    void createLandmarks(BufferedReader br)throws IOException{
      StringTokenizer st = new StringTokenizer(br.readLine());

      L = Integer.parseInt(st.nextToken());
      for (int i = 0; i < L; i++) {
        st = new StringTokenizer(br.readLine());
        Node landmarkNode = nodeList[Integer.parseInt(st.nextToken())];
        landmarkNode.code = Byte.parseByte(st.nextToken());
        landmarkNode.name = st.nextToken();
      }
    }



  }




  public static void main(String[] args) throws IOException {

    String nodeFileName = "noder.txt";
    String edgeFileName = "kanter.txt";
    String landmarkFileName = "interessepkt.txt";

    BufferedReader nodesBr = new BufferedReader(new FileReader(System.getProperty("user.dir") + System.getProperty("file.separator") + nodeFileName));
    BufferedReader edgeBr = new BufferedReader(new FileReader(System.getProperty("user.dir") + System.getProperty("file.separator") + edgeFileName));
    BufferedReader landmarkBr = new BufferedReader(new FileReader(System.getProperty("user.dir") + System.getProperty("file.separator") + landmarkFileName));

    DijkstraAlt graph = new DijkstraAlt();
    graph.createNodes(nodesBr);
    graph.createEdges(edgeBr);
    graph.createLandmarks(landmarkBr);

    System.out.println("Graph created");

    graph.start(graph.nodeList[0],graph.nodeList[4]);

    System.out.println();
    System.out.println();

    Node currentNode = graph.nodeList[4];
    while(currentNode != null){
      System.out.printf("%d \n",currentNode.nodeNr);
      currentNode = currentNode.previousNode;
    }

  }


}
