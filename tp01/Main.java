import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

class Main {
    private static Scanner in = new Scanner(System.in);
    private static String pathCSV = "./tp01/dados/filmes.csv";

    public static void menu() {

        int op;

        System.out.println("1. Realizar carga da base de dados\n"
                + "5. Sair do Programa");

        System.out.print("Digite sua opção : ");
        op = in.nextInt();

        if (op < 1 || op > 5) {
            System.out.println("\n---------------------------------");
            System.out.println("Opção inválida!");
            System.out.println("---------------------------------\n");

            menu();
        } else if (op == 1) {

        }
    }

    public static Filme manipularLinha(String linha) {
        String[] partes = linha.split("\"");
        String[] str = partes[0].split(",");

        Filme filme = new Filme();

        filme.setId(Integer.parseInt(str[0]));
        filme.setReleaseDate(str[1]);
        filme.setTitle(str[2]);
        filme.setVoteAvarage(Float.parseFloat(str[3]));
        filme.setOriginalLanguage(str[4]);
        filme.setGenres(partes[1].split(","));

        return filme;
    }

    public static void lerArquivoCSV() {
        BufferedReader arq;

        try {
            arq = new BufferedReader(new FileReader(pathCSV));
            String linha;
            arq.readLine(); // Excluir a primeira linha do arquivo

            while ((linha = arq.readLine()) != null) {
                System.out.println(linha);
            }

        } catch (FileNotFoundException e) {
            System.err.println("Arquivo não encontrado :" + e);
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo :" + e);
        }
    }

    public static void main(String[] args) {

        // menu();
        lerArquivoCSV();
    }
}