import java.io.Serializable;

class BTree implements Serializable {
    private static final long serialVersionUID = 1L;

    private int t; // Ordem da árvore
    private Node root; // Raiz da árvore

    public BTree(int t) {
        this.t = t;
        this.root = new Node(t);
    }

    public void setNewPos(Index index) {
        find(index, root);
    }

    private void find(Index index, Node i) {
        int j;
        for (j = 0; j < i.n; j++) {
            if (index.getId() < i.key[j].getId()) {
                break;
            } else if (index.getId() == i.key[j].getId()) {
                i.key[j].setPos(index.getPos());
                return;
            }
        }

        if (i.leaf) {
            return;
        } else {
            find(index, i.child[j]);
        }
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

    // Remover
    private Index Remove(Node x, int id) {
        int i = 0;
        if (x.leaf) {
            for (i = 0; i < x.n; i++) {
                if (id == x.key[i].getId()) {
                    break;
                }
            }
            if (i == x.n) {
                return null; // Chave não encontrada
            }
            Index removedKey = x.key[i];
            for (int j = i; j < x.n - 1; j++) {
                x.key[j] = x.key[j + 1];
            }
            x.n--;
            return removedKey;
        } else {
            for (i = 0; i < x.n; i++) {
                if (id < x.key[i].getId()) {
                    break;
                }
            }
            Index removedKey = Remove(x.child[i], id);
            if (removedKey == null) {
                for (int j = i; j < x.n - 1; j++) {
                    x.key[j] = x.key[j + 1];
                    x.child[j + 1] = x.child[j + 2];
                }
                x.n--;
                if (x.n == 0) {
                    if (x == root) {
                        root = x.child[0];
                    }
                    x = x.child[0];
                }
            }
            return removedKey;
        }
    }

    public Index Remove(int id) {
        Index removedKey = Remove(root, id);
        if (root.n == 0) {
            root = root.child[0];
        }
        return removedKey;
    }
}
