class BTree {

    private int t; // Ordem da árvore
    private Node root; // Raiz da árvore

    public BTree(int t) {
        this.t = t;
        this.root = new Node(t);
    }

    // Inserção
    public void insert(Index key) {
        Node i = root;
        if (i.n == t - 1) {
            Node temp = new Node(t);
            root = temp;
            temp.child[0] = i;
            splitChild(temp, 1, i);
            insertNotFull(temp, key);
        } else {
            insertNotFull(i, key);
        }

    }

    private void insertNotFull(Node i, Index key) {
        int j = i.n;
        if (i.leaf) {
            i.key[j] = key;
            i.n++;
        } else {
            while (j >= 1 && key.getId() < i.key[j - 1].getId()) {
                j--;
            }
            j++;
            Node filho = i.child[j - 1];
            if (filho.n == t - 1) {
                splitChild(i, j, filho);
                if (key.getId() > i.key[j - 1].getId()) {
                    j++;
                }
            }
            insertNotFull(i.child[j - 1], key);
        }
    }

    private void splitChild(Node x, int i, Node y) {
        Node z = new Node(t);
        z.leaf = y.leaf;
        z.n = t / 2 - 1;
        for (int j = 0; j < t / 2 - 1; j++) {
            z.key[j] = y.key[j + t / 2];
        }
        if (!y.leaf) {
            for (int j = 0; j < t / 2; j++) {
                z.child[j] = y.child[j + t / 2];
            }
        }
        y.n = t / 2 - 1;
        for (int j = x.n; j >= i; j--) {
            x.child[j + 1] = x.child[j];
        }
        x.child[i] = z;
        for (int j = x.n; j >= i; j--) {
            x.key[j] = x.key[j - 1];
        }
        x.key[i - 1] = y.key[t / 2 - 1];
        x.n++;
        x.leaf = false;
    }

    // Pesquisa
    public Index search(int id) {
        return search(id, root);
    }

    private Index search(int id, Node i) {
        int j;
        for (j = 0; j < i.n; j++) {
            if (id < i.key[j].getId()) {
                break;
            } else if (id == i.key[j].getId()) {
                return i.key[j];
            }
        }

        if (i.leaf) {
            return null;
        } else {
            return search(id, i.child[j]);
        }
    }

    public void imprimirArvore() {
        int[] contador = new int[1]; // contador para armazenar o número de IDs impressos
        imprimirNo(root, contador);
        System.out.println("Total de IDs impressos: " + contador[0]); // imprime o total de IDs
    }

    private void imprimirNo(Node no, int[] contador) {
        for (int i = 0; i < no.n; i++) {
            System.out.print(no.key[i].getId() + " ");
            contador[0]++; // incrementa o contador a cada ID impresso
        }
        System.out.println();

        if (!no.leaf) {
            for (int i = 0; i <= no.n; i++) {
                imprimirNo(no.child[i], contador);
            }
        }
    }

}
