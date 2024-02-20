import java.io.*;
import java.util.*;

class Main {
    private static Scanner in = new Scanner(System.in);
    private static String pathCSV = "./tp01/dados/filmes.csv";
    private static String pathBin = "./tp01/dados/filmesBin.db";

    public static void menu() {

        int op;

        System.out.println("1. Realizar carga da base de dados\n"
                + "2. Ler um registro\n"
                + "3. Atualizar um registro\n"
                + "4. Deletar um registro\n"
                + "5. Criar um registro\n"
                + "6. Sair do Programa");

        System.out.print("Digite sua opção : ");
        op = in.nextInt();

        if (op < 1 || op > 6) {
            System.out.println("\n---------------------------------");
            System.out.println("Opção inválida!");
            System.out.println("---------------------------------\n");

            menu();
        } else if (op == 1) {

        }
    }

    /*
     * Função que recebe uma linha do arquivo CSV e separa cada atributo do filme
     */
    public static Filme manipularLinha(String linha) {
        String[] partes = linha.split("\"");
        String[] str;

        Filme filme = new Filme();

        if (partes.length > 2) {
            str = partes[0].split(",");

            filme.setId(Integer.parseInt(str[0]));
            filme.setReleaseDate(str[1]);
            filme.setTitle(partes[1]);

            str = partes[2].split(",");

            filme.setVoteAvarage(Float.parseFloat(str[1]));
            filme.setOriginalLanguage(str[2]);

            if (partes.length == 4) {
                filme.setGenres(partes[3].split(","));
            } else {
                String[] genres = { str[3] };
                filme.setGenres(genres);
            }

        } else {
            str = partes[0].split(",");

            filme.setId(Integer.parseInt(str[0]));
            filme.setReleaseDate(str[1]);
            filme.setTitle(str[2]);
            filme.setVoteAvarage(Float.parseFloat(str[3]));
            filme.setOriginalLanguage(str[4]);

            if (partes.length > 1) {
                filme.setGenres(partes[1].split(","));
            } else {
                filme.setGenres(new String[] {});
            }

        }

        return filme;
    }

    /*
     * Função que lê todos os filmes contidos no arquivo CSV e cria um array de
     * filmes
     */
    public static Filme[] lerArquivoCSV() {
        ArrayList<Filme> filmes = new ArrayList<>();
        BufferedReader arq;

        try {
            arq = new BufferedReader(new FileReader(pathCSV));
            arq.readLine(); // Excluir a primeira linha do arquivo

            String linha;

            while ((linha = arq.readLine()) != null) {
                filmes.add(manipularLinha(linha));
            }

            // Fechar o arquivo
            arq.close();
        } catch (FileNotFoundException e) {
            System.err.println("Arquivo não encontrado :" + e);
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo :" + e);
        }

        return filmes.toArray(new Filme[0]);
    }

    public static void escreverArquivoBin() {

        try {
            RandomAccessFile binaryFile = new RandomAccessFile(pathBin, "rw");

            Filme[] filmes = lerArquivoCSV();

            int id = filmes[filmes.length - 1].getId(); // Pegar o último id

            binaryFile.writeInt(id); // Escrver o último id no cabeçalho

            for (Filme filme : filmes) {
                Registro registro = new Registro(filme);

                binaryFile.write(registro.toBinaryArray());
            }

            // Fechar o arquivo
            binaryFile.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) {

        // menu();
        escreverArquivoBin();

    }
}