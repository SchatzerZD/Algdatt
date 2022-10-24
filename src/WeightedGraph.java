import java.io.BufferedReader;
import java.io.FileReader;
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

    Node getLowestWeightNode(HashMap<Node,Integer> map){

        List<Map.Entry<Node, Integer>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, Map.Entry.comparingByValue());

        return list.get(0).getKey();
    }


    void dijkstra(Node s){
        initforgj(s);
        Node currentNode = s;
        HashMap<Node, Integer> prioQueue = new HashMap<>();
        prioQueue.put(currentNode,0);
        ArrayList<Node> visited = new ArrayList<>();
        ArrayList<Node> unobtainable = new ArrayList<>();

        while(!prioQueue.isEmpty()){

            Kant tempKant = currentNode.kant1;
            while(tempKant != null){
                Vkant vTempKant = (Vkant) tempKant;
                if(!unobtainable.contains(tempKant.til)){
                    prioQueue.put(tempKant.til, vTempKant.vekt);
                }
                tempKant = tempKant.neste;
            }
            prioQueue.remove(currentNode);
            if(!visited.contains(currentNode)){
                visited.add(currentNode);
            }

            if(!prioQueue.isEmpty() && visited.contains(getLowestWeightNode(prioQueue))){
                unobtainable.add(getLowestWeightNode(prioQueue));
                prioQueue.remove(getLowestWeightNode(prioQueue));
            }

            if(!prioQueue.isEmpty()){
                currentNode = getLowestWeightNode(prioQueue);
            }
        }

        for (Node n: visited) {
            for (Kant k = n.kant1; k != null; k = k.neste) {
                forkort(n,(Vkant) k);
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
        System.out.println("Node  || Previous || Distance");
        for (Node n: graf.node) {
            System.out.print(String.format("%-2s",graf.returnIndexOfNode(n)) + "    || ");
            if(graf.returnIndexOfNode(((Forgj)n.d).forgj) == -1 && graf.returnIndexOfNode(n) != startNodeIndex){
                System.out.print(String.format("%5s"," "));
            }else if(graf.returnIndexOfNode(((Forgj)n.d).forgj) == -1 && graf.returnIndexOfNode(n) == startNodeIndex){
                System.out.print(String.format("%-5s","start"));
            }else{
                System.out.print(String.format("%5s",graf.returnIndexOfNode(((Forgj)n.d).forgj)));
            }
            System.out.print("    || ");
            if(((Forgj)n.d).dist == Forgj.uendelig){
                System.out.print(" Cant Reach");
            }else{
                System.out.print(((Forgj)n.d).dist);
            }
            System.out.println();
        }


    }


}
