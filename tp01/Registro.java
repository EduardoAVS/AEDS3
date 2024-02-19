import java.io.*;

class Registro {

    // Atributos
    private boolean lapide;
    private int tamanho;
    private Filme filme;

    // Construtores
    public Registro(Filme filme) throws IOException {
        this.filme = filme;
        this.lapide = true;
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
}
