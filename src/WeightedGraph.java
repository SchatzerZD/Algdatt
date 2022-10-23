import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

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

    //TODO: Add functionality
    public void lag_priko(Node[] pri){

    }

    //TODO: Add functionality
    public Node hent_min(int i, Node[] pri){
        return null;
    }

    void dijkstra(Node s){
        initforgj(s);
        Node []pri = new Node[N];
        lag_priko(pri);
        for (int i = N; i > 1; --i) {
            Node n = hent_min(i,pri);
            for (Vkant k = (Vkant)n.kant1 ;  k != null; k = k.neste) {
                forkort(n,k);
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

            System.out.println(" ");
        }
    }

    public static void main(String[] args) throws IOException {
        WeightedGraph graf = new WeightedGraph();
        String fileName = "vg1";

        BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + System.getProperty("file.separator") + fileName + ".txt"));
        graf.ny_vgraf(br);

        graf.printGraph();

    }


}
