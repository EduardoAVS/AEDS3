class Node {

    public int n; // Número de chaves no nó
    public Index key[];
    public Node child[];
    public boolean leaf;

    public Node(int t) {
        this.key = new Index[t - 1];
        this.child = new Node[t];
        this.n = 0;
        this.leaf = true;
    }
}
