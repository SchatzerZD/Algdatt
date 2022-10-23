import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class WeightedGraph {
    int N,K;
    Node []node;
    boolean[] addedList;

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
        addedList = new boolean[N];

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

    Node getNearestNode(Node s){
        int minDist = Forgj.uendelig;
        Node nearestNode = s;
        for (Kant k = s.kant1; k != null; k = k.neste) {
            if(minDist > ((Vkant)k).vekt){
                minDist = ((Vkant)k).vekt;
                nearestNode = k.til;
            }
        }
        return nearestNode;
    }





    List<Node> lag_priko(Node startNode){
        ArrayList<Node> nodeArrayList = new ArrayList<>();
        Node tempNode = startNode;

        while(!nodeArrayList.contains(tempNode)){
            nodeArrayList.add(tempNode);
            if(getNearestNode(tempNode) != tempNode){
                tempNode = getNearestNode(tempNode);
            }else{
                break;
            }
        }


        System.out.println();

        for (Node n: nodeArrayList) {
            System.out.print(returnIndexOfNode(n) + " ");
        }
        System.out.println();
        System.out.println();

        return nodeArrayList;
    }

    void dijkstra(Node s){
        initforgj(s);
        ArrayList<Node> nodeArrayList = (ArrayList<Node>) lag_priko(s);

        for (int i = 0; i < nodeArrayList.size(); i++) {
            Node n = nodeArrayList.get(i);
            for (Kant k = n.kant1; k != null; k = k.neste) {
                forkort(n, (Vkant) k);
            }
        }
        for (Node n:node) {
            if(!nodeArrayList.contains(n)){
                for (Kant k = n.kant1; k != null; k = k.neste) {
                    forkort(n, (Vkant) k);
                }
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
        String fileName = "vg5";

        int startNodeIndex = 1;

        BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + System.getProperty("file.separator") + fileName + ".txt"));
        graf.ny_vgraf(br);

        graf.printGraph();
        System.out.println();

        graf.dijkstra(graf.node[startNodeIndex]);

        System.out.println();
        System.out.println();
        for (Node n: graf.node) {
            System.out.println(graf.returnIndexOfNode(n) + " " + graf.returnIndexOfNode(((Forgj)n.d).forgj) + " " + ((Forgj)n.d).dist);
        }

    }


}
