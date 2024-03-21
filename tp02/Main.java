import java.io.*;
import java.util.Scanner;

class Main {
    private static Scanner in = new Scanner(System.in);
    private static final String pathCSV = "./dados/filmes.csv";
    private static final String pathBin = "./dados/filmesBin.db";
    private static final String pathIndex = "./dados/filmesIndex.db";
    private static BTree tree;
    private static HashingDinamico hash;

    /*----------------------------------------- Menu -----------------------------------------*/

    public static void menu() {

        int op;

        System.out.println("1. Realizar carga da base de dados\n"
                + "2. Ler um registro\n"
                + "3. Criar um registro\n"
                + "4. Deletar um registro\n"
                + "5. Atualizar um registro\n"
                + "6. Sair do Programa");

        System.out.print("Digite sua opção : ");
        op = in.nextInt();

        if (op < 1 || op > 7) {
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
        } /*
           * else if (op == 3) {
           * Filme filme = new Filme();
           * 
           * System.out.println("\n---------------------------------");
           * in.nextLine();
           * 
           * System.out.print("\nDigite o título do filme: ");
           * filme.setTitle(in.nextLine());
           * 
           * System.out.print("Digite a data de lançamento (yyyy-MM-dd): ");
           * filme.setReleaseDate(in.next());
           * 
           * System.out.print("Digite a média de votos: ");
           * filme.setVoteAvarage(in.nextFloat());
           * in.nextLine(); // Consumir a nova linha
           * 
           * System.out.print("Digite a língua original (sigla com apenas duas letras): "
           * );
           * filme.setOriginalLanguage(in.nextLine());
           * 
           * System.out.print("Digite os gêneros desse filme (separe com vírgula): ");
           * String aux = in.nextLine();
           * String[] genres = aux.split(",");
           * filme.setGenres(genres);
           * 
           * if (create(filme)) {
           * System.out.println("\nFilme adicionado com sucesso!");
           * } else {
           * System.out.println("\nErro ao adicionar o filme!");
           * }
           * 
           * System.out.println("---------------------------------\n");
           * 
           * } else if (op == 4) {
           * System.out.println("\n---------------------------------");
           * System.out.print("Digite o id do filme que você deseja deletar: ");
           * delete(in.nextInt());
           * 
           * System.out.println("---------------------------------\n");
           * } else if (op == 5) {
           * Filme filme = new Filme();
           * 
           * System.out.println("\n---------------------------------");
           * 
           * System.out.print("Digite o id do filme que você deseja atualizar: ");
           * filme.setId(in.nextInt());
           * in.nextLine();
           * 
           * System.out.print("Digite o título do filme: ");
           * filme.setTitle(in.nextLine());
           * 
           * System.out.print("Digite a data de lançamento (yyyy-MM-dd): ");
           * filme.setReleaseDate(in.next());
           * 
           * System.out.print("Digite a média de votos: ");
           * filme.setVoteAvarage(in.nextFloat());
           * in.nextLine(); // Consumir a nova linha
           * 
           * System.out.print("Digite a língua original (sigla com apenas duas letras): "
           * );
           * filme.setOriginalLanguage(in.nextLine());
           * 
           * System.out.print("Digite os gêneros desse filme (separe com vírgula): ");
           * String aux = in.nextLine();
           * String[] genres = aux.split(",");
           * filme.setGenres(genres);
           * 
           * update(filme);
           * 
           * System.out.println("---------------------------------\n");
           * 
           * }
           */

        if (op != 6) {
            menu();
        }
    }

    /*----------------------------------------- Carregar Arquivo Bin -----------------------------------------*/

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
     * Função que gera um arquivo binário através das informações de um arquivo .CSV
     */
    public static void escreverArquivoBin() {

        try {
            RandomAccessFile binaryFile = new RandomAccessFile(pathBin, "rw");
            RandomAccessFile indexFile = new RandomAccessFile(pathIndex, "rw");

            BufferedReader arqCSV = new BufferedReader(new FileReader(pathCSV));
            arqCSV.readLine(); // Excluir a primeira linha do arquivo

            binaryFile.writeInt(0); // Adicionar o espaço do último id no cabeçalho

            long pos = 0;
            String linha;
            tree = new BTree(8);
            hash = new HashingDinamico(1);
            while ((linha = arqCSV.readLine()) != null) {
                Filme filme = manipularLinha(linha);
                Registro registro = new Registro(filme);
                pos = binaryFile.getFilePointer();
                binaryFile.write(registro.toBinaryArray());

                Index index = new Index(filme.getId(), pos);
                tree.insert(index);
                hash.insertIndex(index);
                //indexFile.writeInt(filme.getId());
                //indexFile.writeLong(pos);
            }

            binaryFile.seek(pos + 5);
            int id = binaryFile.readInt();
            binaryFile.seek(0);
            binaryFile.writeInt(id);

            // Fechar o arquivo
            binaryFile.close();
            indexFile.close();
            arqCSV.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /*----------------------------------------- CRUD -----------------------------------------*/

    /*
     * Função responsável por ler um registro no arquivo binário
     */
    public static void read(int idBuscada) {

        try {
            RandomAccessFile binaryFile = new RandomAccessFile(pathBin, "rw");

            Index index = tree.search(idBuscada);

            if (index == null) {
                System.out.println("\nFilme de id " + idBuscada + " não encontrado");
            } else {
                binaryFile.seek(index.getPos());
                Registro registro = new Registro();
                registro.fromBinaryArray(binaryFile);
                if (!registro.getLapide()) {
                    System.out.println(registro.toString());
                }
            }

            // Fechar o arquivo
            binaryFile.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] rags) {
        escreverArquivoBin();
        hash.imprimirHash();
    }
}
