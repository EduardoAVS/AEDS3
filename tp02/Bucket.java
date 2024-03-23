
import java.util.ArrayList;
import java.util.List;

public class Bucket {
    private final int tamanho = 200;
    private int pLocal;
    private List<Index> registros;
    private HashingDinamico diretorio;
    
    public Bucket(){
        this.pLocal = 1;
        this.registros = new ArrayList<>();
    }

    public boolean insert(Index registro){
        if(registros.size() < tamanho){
            registros.add(registro);
            return true;
        }
        else{
            
            return false;
            //Bucket cheio
        }
    }

    void imprimir(){
        for(int i = 0; i < registros.size(); i++){
            System.out.println(registros.get(i).getId() + "  " + registros.get(i).getPos());

        }
    }
}
