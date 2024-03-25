import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HashingDinamico implements Serializable {
    private static final long serialVersionUID = 2L;
    private int p;
    private List<Bucket> buckets;

    int getP(){
        return p;
    }

    void setP(int p){
        this.p = p;
    }

    public HashingDinamico(int p){
        this.p = p;
        this.buckets = new ArrayList<>();
        for (int i = 0; i < Math.pow(2, p); i++) {
            buckets.add(new Bucket(1));
        }
    }

    private int hash(int x){
        return x % (int) Math.pow(2, p);
    }

    private int hashExpansao(int x, int novoP) {
        return x % (int) Math.pow(2, novoP);
    }

    public void insertIndex(Index registro){
        int indiceBucket = hash(registro.getId());
        Bucket bucket = buckets.get(indiceBucket);
        // Insere o indice no bucket
        if (!bucket.insert(registro)) {

            // Aumenta o diretório
            rebalancear(indiceBucket, registro);
        }
    }

    public void rebalancear(int indiceBucketCheio, Index registroNovo){
        int pLocal = buckets.get(indiceBucketCheio).getPLocal();
        int novoPlocal = pLocal + 1;
        int novoP = p;
        if(pLocal == p){
            novoP = p + 1;
        }
        List<Bucket> novosBuckets = new ArrayList<>(buckets); // Lista com os valores de buckets

        // Adiciona novos buckets ao final o diretorio para duplicar a capacidade.
        if(novoP > p){
            for (int i = 0; i < Math.pow(2, p); i++) {
                novosBuckets.add(new Bucket(novoPlocal));
            }
        }
        
        
        Bucket bucketCheio = novosBuckets.get(indiceBucketCheio);
        List<Index> registrosParaRebalancear = new ArrayList<>(bucketCheio.getRegistros());
        registrosParaRebalancear.add(registroNovo); // Inclui o novo registro que causou o overflow

        bucketCheio.limparRegistros(); // Limpa o bucket cheio original para reutilização

        // Redistribui os registros apenas do bucket cheio
        for (Index registro : registrosParaRebalancear) {
            int novoIndice = hashExpansao(registro.getId(), novoP);
            novosBuckets.get(novoIndice).insert(registro);
        }

        this.p = novoP; // Atualiza p depois da redistribuição
        
        this.buckets = novosBuckets; // Atualiza a referência para os novos buckets
    }

    public Index search(int id){
        int indice = hash(id);
        return buckets.get(indice).search(id);
    }

    public void imprimirHash(){
        for(int i = 0; i < buckets.size(); i++){
            buckets.get(i).imprimir();
        }
    }

}
