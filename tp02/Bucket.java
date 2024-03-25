
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Bucket implements Serializable{
    private static final long serialVersionUID = 2L;
    private final int tamanho = 200;
    private int pLocal;
    private List<Index> registros;

    private static final String hashTexto = "./dados/hash.txt";
    
    
    int getPLocal(){
        return pLocal;
    }

    void setPLocal(int p){
        this.pLocal = p;
    }

    List<Index> getRegistros(){
        return registros;
    }

    public Bucket(int pLocal){
        this.pLocal = pLocal;
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

    public void limparRegistros(){
        registros.clear();
    }

    public Index search(int id){
        for(int i = 0; i < registros.size(); i++){
            if(registros.get(i).getId() == id){
                System.out.println((registros.get(i).getId() + " " + registros.get(i).getPos()));
                return registros.get(i);
            }
        }
        return null;
    }

    void imprimir(){
        try (BufferedWriter hashArq = new BufferedWriter(new FileWriter(hashTexto, true))) {
            for(int i = 0; i < registros.size(); i++){
                hashArq.write((registros.get(i).getId() + " " + registros.get(i).getPos()) + " \n");
    
            }

        } catch (IOException e) {
            // Captura possíveis erros durante a escrita do arquivo
            System.err.println("Ocorreu um erro ao escrever o arquivo: " + e.getMessage());
        }
    }
}
