import java.io.*;
import java.util.Scanner;

class Main {
    private static Scanner in = new Scanner(System.in);
    private static final String pathCSV = "./tp02/dados/filmes.csv";
    private static final String pathBin = "./tp02/dados/filmesBin.db";
    private static final String pathIndex = "./tp02/dados/filmesIndex.db";
    private static final String pathBTree = "./tp02/dados/filmesBTree.ser";
    private static BTree tree = null;

    /*----------------------------------------- Menu -----------------------------------------*/

    public static void menu() {

        int op;
        readFromFile();

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
        } else if (op == 3) {
            Filme filme = new Filme();

            System.out.println("\n---------------------------------");
            in.nextLine();

            System.out.print("\nDigite o título do filme: ");
            filme.setTitle(in.nextLine());

            System.out.print("Digite a data de lançamento (yyyy-MM-dd): ");
            filme.setReleaseDate(in.next());

            System.out.print("Digite a média de votos: ");
            filme.setVoteAvarage(in.nextFloat());
            in.nextLine(); // Consumir a nova linha

            System.out.print("Digite a língua original (sigla com apenas duas letras): ");
            filme.setOriginalLanguage(in.nextLine());

            System.out.print("Digite os gêneros desse filme (separe com vírgula): ");
            String aux = in.nextLine();
            String[] genres = aux.split(",");
            filme.setGenres(genres);

            if (create(filme)) {
                System.out.println("\nFilme adicionado com sucesso!");
            } else {
                System.out.println("\nErro ao adicionar o filme!");
            }

            System.out.println("---------------------------------\n");

        } else if (op == 4) {
            System.out.println("\n---------------------------------");
            System.out.print("Digite o id do filme que você deseja deletar: ");
            delete(in.nextInt());

            System.out.println("---------------------------------\n");
        } else if (op == 5) {
            Filme filme = new Filme();

            System.out.println("\n---------------------------------");

            System.out.print("Digite o id do filme que você deseja atualizar: ");
            filme.setId(in.nextInt());
            in.nextLine();

            System.out.print("Digite o título do filme: ");
            filme.setTitle(in.nextLine());

            System.out.print("Digite a data de lançamento (yyyy-MM-dd): ");
            filme.setReleaseDate(in.next());

            System.out.print("Digite a média de votos: ");
            filme.setVoteAvarage(in.nextFloat());
            in.nextLine(); // Consumir a nova linha

            System.out.print("Digite a língua original (sigla com apenas duas letras):");
            filme.setOriginalLanguage(in.nextLine());

            System.out.print("Digite os gêneros desse filme (separe com vírgula): ");
            String aux = in.nextLine();
            String[] genres = aux.split(",");
            filme.setGenres(genres);

            update(filme);

            System.out.println("---------------------------------\n");

        }

        else if (op == 6) {
            saveInFile();
        }

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
            while ((linha = arqCSV.readLine()) != null) {
                Filme filme = manipularLinha(linha);
                Registro registro = new Registro(filme);
                pos = binaryFile.getFilePointer();
                binaryFile.write(registro.toBinaryArray());

                Index index = new Index(filme.getId(), pos);
                tree.insert(index);
                // indexFile.writeInt(filme.getId());
                // indexFile.writeLong(pos);
            }

            binaryFile.seek(pos + 5);
            int id = binaryFile.readInt();
            binaryFile.seek(0);
            binaryFile.writeInt(id);
            saveInFile();

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
                } else {
                    System.out.println("\nFilme de id " + idBuscada + " não encontrado");
                }
            }

            // Fechar o arquivo
            binaryFile.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /*
     * Função responsável por criar um registro no arquivo binário
     */
    public static boolean create(Filme filme) {
        try {
            RandomAccessFile binaryFile = new RandomAccessFile(pathBin, "rw");
            Registro registro = new Registro(filme);

            binaryFile.seek(0);
            int id = binaryFile.readInt() + 1;
            filme.setId(id);

            binaryFile.seek(0);
            binaryFile.writeInt(id);

            long pos = binaryFile.length();
            binaryFile.seek(pos);
            binaryFile.write(registro.toBinaryArray());

            Index index = new Index(filme.getId(), pos);
            tree.insert(index);
            saveInFile();

            // Fechar o arquivo
            binaryFile.close();
            return true;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /*
     * Função responsável por deletar um registro no arquivo binário
     */
    public static void delete(int idBuscado) {
        try {
            RandomAccessFile binaryFile = new RandomAccessFile(pathBin, "rw");
            binaryFile.seek(4);

            Index index = tree.Remove(idBuscado);
            if (index == null) {
                System.out.println("\nFilme de id " + idBuscado + " não encontrado");
            } else {
                binaryFile.seek(index.getPos());
                binaryFile.writeBoolean(true);
                System.out.println("\nFilme com id " + idBuscado + " deletado com sucesso\n");
            }
            saveInFile();

            // Fechar o arquivo
            binaryFile.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /*
     * Função responsável por atualizar um registro no arquivo binário
     */
    public static void update(Filme novoFilme) {
        RandomAccessFile binaryFile = null;

        try {
            binaryFile = new RandomAccessFile(pathBin, "rw");

            // Mover para o primeiro registro do arquivo (após cabeçalho)
            binaryFile.seek(4);

            Index index = tree.search(novoFilme.getId());
            Registro novoRegistro = new Registro(novoFilme);
            Registro registro = new Registro();
            if (index != null) {
                binaryFile.seek(index.getPos());
                registro.fromBinaryArray(binaryFile);

                if (novoRegistro.getTamanho() <= registro.getTamanho()) {
                    binaryFile.seek(index.getPos());
                    novoRegistro.setTamanho(registro.getTamanho());
                    binaryFile.write(novoRegistro.toBinaryArray());
                } else {
                    binaryFile.seek(index.getPos());
                    binaryFile.writeBoolean(true);
                    index.setPos(binaryFile.length());
                    binaryFile.seek(index.getPos());
                    binaryFile.write(novoRegistro.toBinaryArray());
                    tree.setNewPos(index);
                }

                System.out.println("\nFilme com id " + novoFilme.getId() + " atualizado com sucesso\n");
                saveInFile();
            } else {

                // Filme não encontrado após varrer todo o arquivo
                System.out.println("\nFilme com id " + novoFilme.getId() + " não encontrado.");
            }

        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        } finally {
            if (binaryFile != null) {
                try {
                    binaryFile.close();
                } catch (IOException e) {
                    System.err.println("IOException ao fechar o arquivo: " + e.getMessage());
                }
            }
        }
    }

    /*----------------------------------------- Salvar a Arvore B em um Arquivo-----------------------------------------*/

    public static void saveInFile() {
        try {
            // Armazenar o objeto em um arquivo
            FileOutputStream fileOut = new FileOutputStream(pathBTree);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(tree);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public static void readFromFile() {
        File file = new File(pathBTree);
        if (!file.exists() || file.length() == 0) {
            tree = new BTree(8);
        } else {
            try {
                // Recuperar o objeto de um arquivo
                FileInputStream fileIn = new FileInputStream(pathBTree);
                ObjectInputStream inp = new ObjectInputStream(fileIn);
                tree = (BTree) inp.readObject();
                inp.close();
                fileIn.close();
            } catch (IOException i) {
                i.printStackTrace();
            } catch (ClassNotFoundException c) {
                System.out.println("Classe BTree não encontrada");
                c.printStackTrace();
            }
        }
    }

    /*----------------------------------------- Main -----------------------------------------*/

    public static void main(String[] rags) {
        menu();
    }
}
