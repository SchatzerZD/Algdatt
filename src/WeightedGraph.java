import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class WeightedGraph {

    int totalDistance = 0;

    int N,K;
    Node []node;
    int[] nodeDistances;
    Node[] previousNodes;
    boolean[] visited;

    class Kant{
        Kant neste;
        Node til;
        public Kant(Node n, Kant nst){
            til = n;
            neste = nst;
        }
    }

    class Node {
        Kant kant1;
        Object d;
    }

    class Vkant extends Kant{
        Vkant neste;
        int vekt;
        public Vkant(Node n, Vkant nst, int vkt){
            super(n,nst);
            vekt = vkt;
        }
    }

    class Forgj{
        int dist;
        Node forgj;
        static int uendelig = 1000000000;
        public int finn_dist() {return dist;}
        public Node finn_forgj() {return forgj;}
        public Forgj() {
            dist = uendelig;
        }
    }

    public void ny_vgraf(BufferedReader br) throws IOException{
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        node = new Node[N];

        nodeDistances = new int[N];
        visited = new boolean[N];
        previousNodes = new Node[N];

        for (int i = 0; i < N; ++i) {
            node[i] = new Node();
            nodeDistances[i] = Forgj.uendelig;
        }

        K = Integer.parseInt(st.nextToken());
        for (int i = 0; i < K; ++i) {
            st = new StringTokenizer(br.readLine());
            int fra = Integer.parseInt(st.nextToken());
            int til = Integer.parseInt(st.nextToken());
            int vekt = Integer.parseInt(st.nextToken());
            Vkant k = new Vkant(node[til],(Vkant)node[fra].kant1, vekt);
            node[fra].kant1 = (Vkant)k;
        }
    }

    void forkort(Node n, Vkant k){
        Forgj nd = (Forgj)n.d, md = (Forgj)k.til.d;
        if(md.dist > nd.dist + k.vekt){
            md.dist = nd.dist + k.vekt;
            md.forgj = n;
        }
    }

    public void initforgj(Node s){
        for (int i = N; i-- > 0;) {
            node[i].d = new Forgj();
        }
        ((Forgj)s.d).dist = 0;
    }

    Node getNearestNode(Node s){
        int minDist = Forgj.uendelig;
        Node nearestNode = s;
        for (Node n: node) {
            if(n != s){
                if(((Forgj)n.d).dist < minDist){
                    minDist = ((Forgj)n.d).dist;
                    nearestNode = n;
                }
            }
        }
        return nearestNode;
    }


    void dijkstra(Node s){

        initforgj(s);


        for (Kant k = s.kant1; k != null; k = k.neste) {
                forkort(s,(Vkant) k);
        }

        Node nearestNode = getNearestNode(s);
        int nearestNodeDistance = ((Forgj)nearestNode.d).dist;
        Node previousNode = ((Forgj)nearestNode.d).forgj;
        visited[returnIndexOfNode(s)] = true;

        if(visited[returnIndexOfNode(nearestNode)]){
            return;
        }


        System.out.println(returnIndexOfNode(nearestNode) + " " + returnIndexOfNode(previousNode));

        totalDistance += nearestNodeDistance;
        nodeDistances[returnIndexOfNode(nearestNode)] = totalDistance;
        previousNodes[returnIndexOfNode(nearestNode)] = previousNode;

        dijkstra(nearestNode);



    }

    public int returnIndexOfNode(Node nodeInput){
        for (int i = 0; i < node.length; i++) {
            if(nodeInput == node[i]){
                return i;
            }
        }
        return -1;
    }

    public void printGraph(){
        for (int i = 0; i < node.length; i++) {
            System.out.print(i);

            Kant tempKant = node[i].kant1;

            while(tempKant != null){
                Vkant vTempKant = (Vkant) tempKant;
                System.out.print(" " + returnIndexOfNode(tempKant.til) + "(" + vTempKant.vekt + ")");
                tempKant = tempKant.neste;
            }

            System.out.println();
        }
    }

    public void printAlgorithm(int startNodeIndex){

        System.out.print("Node || Forgjenger || Distanse\n");

        for (int i = 0; i < node.length; i++) {
            if(i != startNodeIndex){
                if(returnIndexOfNode(previousNodes[i]) != -1){
                    System.out.println(i + "    || " + returnIndexOfNode(previousNodes[i]) + "          || " + nodeDistances[i]);
                }else{
                    System.out.println(i + "    ||            || Cant reach");
                }

            }else{
                System.out.println(i + "    || start      || 0");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        WeightedGraph graf = new WeightedGraph();
        String fileName = "vg5";

        int startNodeIndex = 1;

        BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + System.getProperty("file.separator") + fileName + ".txt"));
        graf.ny_vgraf(br);

        graf.printGraph();
        System.out.println();
        graf.dijkstra(graf.node[startNodeIndex]);
        graf.printAlgorithm(startNodeIndex);

    }


}
