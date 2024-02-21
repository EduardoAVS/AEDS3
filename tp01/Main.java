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
            escreverArquivoBin();
            System.out.println("\n---------------------------------");
            System.out.println("Arquivo criado com sucesso!");
            System.out.println("---------------------------------\n");
        } else if (op == 2) {
            System.out.println("\n---------------------------------");
            System.out.print("Digite o id do filme que você deseja procurar: ");
            read(in.nextInt());
            System.out.println("---------------------------------\n");
        }

        menu();
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

            binaryFile.writeInt(id); // Escrever o último id no cabeçalho

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

    public static boolean read(int idBuscada) {

        try {
            // Abre o arquivo binário já escrito
            RandomAccessFile binaryFile = new RandomAccessFile(pathBin, "rw");
            int id = binaryFile.readInt();
            boolean achou = false;
            for (int i = 0; i <= id; i++) {
                Registro registro = new Registro(); // Cria registro vazio
                registro.fromBinaryArray(binaryFile); // Passa o arquivo diretamente para o método fromBinaryArray

                if (!registro.getLapide()) { // Se lápide está marcado o registro foi excluido e deve ser ignorado

                    if (registro.getFilmeById() == idBuscada) {
                        achou = true;
                        System.out.println(registro.toString());
                        break;
                    }
                }
            }
            if(!achou){
                System.out.println("\nFilme de id " + idBuscada + " não encontrado");
            }
            // Fechar o arquivo
            binaryFile.close();
            return true;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public static boolean create(Filme filme) {
        try {
            RandomAccessFile binaryFile = new RandomAccessFile(pathBin, "rw");

            binaryFile.seek(0);
            int id = binaryFile.readInt() + 1;
            filme.setId(id);

            Registro registro = new Registro(filme);

            binaryFile.seek(0);
            binaryFile.writeInt(id);

            binaryFile.seek(binaryFile.length());
            binaryFile.write(registro.toBinaryArray());

            // Fechar o arquivo
            binaryFile.close();
            return true;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        // menu();

        /*
         * String[] genres = { "Drama", "Science Fiction", "Adventure" };
         * Filme filme = new Filme("2014-10-26", "Interestelar", (float) 8.7, "en",
         * genres);
         * 
         * create(filme);
         */
        read(9827);
    }
}