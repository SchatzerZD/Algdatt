import java.io.BufferedReader;
import java.io.IOException;
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

    void dijkstra(Node s){
        initforgj(s);
        Node []pri = new Node[N];

    }



    public int returnIndexOfNode(Node nodeInput){
        for (int i = 0; i < node.length; i++) {
            if(nodeInput == node[i]){
                return i;
            }
        }
        return -1;
    }


}
