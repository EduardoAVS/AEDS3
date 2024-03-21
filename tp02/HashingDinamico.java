
import java.util.ArrayList;
import java.util.List;

public class HashingDinamico {
    private int p;
    private List<Bucket> buckets;

    int getP(){
        return p;
    }

    void setP(int p){
        this.p = p;
    }

    public HashingDinamico(int p){
        this.p = 1;
        this.buckets = new ArrayList<>();
        for (int i = 0; i < Math.pow(2, p); i++) {
            buckets.add(new Bucket());
        }
    }

    private int hash(int x){
        return x % (int) Math.pow(2, p);
    }

    public void insertIndex(Index registro){
        int indiceBucket = hash(registro.getId());
        Bucket bucket = buckets.get(indiceBucket);

        if (!bucket.insert(registro)) {
            //aumenta o diretório
        }
        
    }

}
