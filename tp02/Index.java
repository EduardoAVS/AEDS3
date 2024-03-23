import java.io.Serializable;

class Index implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private long pos;

    public Index(int id, long pos) {
        this.id = id;
        this.pos = pos;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getPos() {
        return this.pos;
    }

    public void setPos(long pos) {
        this.pos = pos;
    }

}
