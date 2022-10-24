import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.util.*;

public class WeightedGraph {
    int N,K;
    Node []node;

    class Kant{
        Kant neste;
        Node til;
        public Kant(Node n, Kant nst){
            til = n;
            neste = nst;
        }
    }

    class Node implements Comparable<Node>{
        Kant kant1;
        Object d;

        @Override
        public int compareTo(Node o) {
            if(((Vkant)o.kant1).vekt < ((Vkant)kant1).vekt){
                return 1;
            }
            else{
                return -1;
            }
        }
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
        for (int i = 0; i < N; ++i) {
            node[i] = new Node();
        }

        K = Integer.parseInt(st.nextToken());
        for (int i = 0; i < K; ++i) {
            st = new StringTokenizer(br.readLine());
            int fra = Integer.parseInt(st.nextToken());
            int til = Integer.parseInt(st.nextToken());
            int vekt = Integer.parseInt(st.nextToken());
            Vkant k = new Vkant(node[til],(Vkant)node[fra].kant1, vekt);
            node[fra].kant1 = k;
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

    List<Node> getAdjacentNodes(Node n){
        List<Node> returnList = new ArrayList<>();
        for (Kant k = n.kant1; k != null; k = k.neste) {
                returnList.add(k.til);
        }
        return returnList;
    }

    Node getLowestWeightNode(Node s){
        int distance = Forgj.uendelig;
        Node smallestNode = null;

        Kant tempKant = s.kant1;

        while(tempKant != null){
            Vkant vTempKant = (Vkant) tempKant;
            if(vTempKant.vekt < distance){
                distance = vTempKant.vekt;
                smallestNode = vTempKant.til;
            }
            tempKant = tempKant.neste;
        }

        return smallestNode;
    }


    void dijkstra(Node s){
        initforgj(s);
        Node currentNode = s;
        ArrayList<Node> pQueue = new ArrayList<>();
        pQueue.add(currentNode);
        ArrayList<Node> visited = new ArrayList<>();
        int i = 0;

        while(!pQueue.isEmpty()){
            i++;
            if(visited.size() == N || i >= K * 2){
                break;
            }

            for (Node n: getAdjacentNodes(currentNode)) {
                pQueue.add(n);
                pQueue.remove(currentNode);
            }
            for (Kant k = currentNode.kant1; k != null; k = k.neste) {
                forkort(currentNode,(Vkant) k);
            }
            if(!visited.contains(currentNode)){
                visited.add(currentNode);
            }
            currentNode = getLowestWeightNode(currentNode);
            if(currentNode == null){
                currentNode = pQueue.get(0);
            }
        }

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

    public static void main(String[] args) throws IOException {
        WeightedGraph graf = new WeightedGraph();
        String fileName = "vg2";

        int startNodeIndex = 1;

        BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + System.getProperty("file.separator") + fileName + ".txt"));
        graf.ny_vgraf(br);

        graf.printGraph();
        System.out.println();

        graf.dijkstra(graf.node[startNodeIndex]);

        System.out.println();
        System.out.println();

        for (Node n: graf.node) {
            System.out.println(graf.returnIndexOfNode(n) + " || " + graf.returnIndexOfNode(((Forgj)n.d).forgj) + " || " + ((Forgj)n.d).dist);
        }




    }


}
