class Node {

    public int n; // Número de chaves no nó
    public int key[];
    public Node child[];
    public boolean leaf;

    public Node(int t) {
        this.key = new int[t - 1];
        this.child = new Node[t];
        this.n = 0;
        this.leaf = true;
    }
}
