class BTree {

    private int t; // Ordem da árvore
    private Node root; // Raiz da árvore

    public BTree(int t) {
        this.t = t;
        this.root = new Node(t);
    }

    /*
     * public Node search(int key) {
     * return search(root, key);
     * }
     * 
     * private Node search(Node i, int key) {
     * int j;
     * for (j = 0; j < i.n; j++) {
     * if (key < i.key[j]) {
     * break;
     * } else if (key == i.key[j]) {
     * return i;
     * }
     * }
     * if (i.leaf) {
     * return null;
     * } else {
     * return search(i.child[j], key);
     * }
     * }
     */
}
