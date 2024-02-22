import java.io.*;

class Registro {

    // Atributos
    private boolean lapide;
    private int tamanho;
    private Filme filme;

    // Getters
    public boolean getLapide() {
        return this.lapide;
    }

    public int getTamanho() {
        return this.tamanho;
    }

    public Filme getFilme() {
        return this.filme;
    }

    public int getFilmeById() {
        return this.filme.getId();
    }
    
    // Setters
    public void setTamanho(int tamanho){
        this.tamanho = tamanho;
    }

    // imprimir informacões do filme
    public String toString() {
        return filme.toString();
    }

    // Construtores
    public Registro() {
        this.filme = null;
        this.lapide = false;
        this.tamanho = 0;
    }

    public Registro(Filme filme) throws IOException {
        this.filme = filme;
        this.lapide = false;
        this.tamanho = filme.toBinaryArray().length;
    }

    public Registro(boolean lapide, int tamanho, Filme filme) {
        this.lapide = lapide;
        this.tamanho = tamanho;
        this.filme = filme;
    }

    // Função para transformar um registro em um array de bytes
    public byte[] toBinaryArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeBoolean(this.lapide);
        dos.writeInt(this.tamanho);
        dos.write(filme.toBinaryArray());

        return baos.toByteArray();
    }

    // Recebe RandomAccessFile porque precisa ler o tamanho para declarar o vetor de
    // bytes
    public void fromBinaryArray(RandomAccessFile binaryFile) throws IOException {

        lapide = binaryFile.readBoolean();
        tamanho = binaryFile.readInt();

        filme = new Filme(); // Criando objeto filme para atribuir os valores lidos
        byte[] ba = new byte[tamanho]; // Alocando um vetor de byte com o tamanho do filme
        binaryFile.read(ba); // Lê o array de bytes correspondente aos dados do filme
        filme.fromBinaryArray(ba);  // Lê os dados binários e converte em dados de filme
    }

    public void fbaLapideTamanho(RandomAccessFile binaryFile) throws IOException {
        lapide = binaryFile.readBoolean();
        tamanho = binaryFile.readInt();
    }

    public void fbaFilme(RandomAccessFile binaryFile) throws IOException{
        filme = new Filme(); // Criando objeto filme para atribuir os valores lidos
        byte[] ba = new byte[tamanho]; // Alocando um vetor de byte com o tamanho do filme
        binaryFile.read(ba); // Lê o array de bytes correspondente aos dados do filme
        filme.fromBinaryArray(ba);  // Lê os dados binários e converte em dados de filme
    }
}
