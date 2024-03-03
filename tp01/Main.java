import java.io.*;
import java.util.*;

class Main {
    private static Scanner in = new Scanner(System.in);
    private static final String pathCSV = "./tp01/dados/filmes.csv";
    private static final String pathBin = "./tp01/dados/filmesBin.db";
    private static final int bloco = 1000;

    /*----------------------------------------- Menu -----------------------------------------*/

    public static void menu() {

        int op;

        System.out.println("1. Realizar carga da base de dados\n"
                + "2. Ler um registro\n"
                + "3. Criar um registro\n"
                + "4. Deletar um registro\n"
                + "5. Atualizar um registro\n"
                + "6. Ordenação Externa\n"
                + "7. Sair do Programa");

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

            System.out.print("Digite a língua original (sigla com apenas duas letras): ");
            filme.setOriginalLanguage(in.nextLine());

            System.out.print("Digite os gêneros desse filme (separe com vírgula): ");
            String aux = in.nextLine();
            String[] genres = aux.split(",");
            filme.setGenres(genres);

            update(filme);

            System.out.println("---------------------------------\n");

        } else if (op == 6) {
            System.out.println("\n---------------------------------");

            ordenacaoExterna();
            System.out.println("Ordenação feita com sucesso!");

            System.out.println("---------------------------------\n");
        }

        if (op != 7) {
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
            }

            binaryFile.seek(pos + 5);
            int id = binaryFile.readInt();
            binaryFile.seek(0);
            binaryFile.writeInt(id);

            // Fechar o arquivo
            binaryFile.close();
            arqCSV.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /*----------------------------------------- CRUD -----------------------------------------*/

    /*
     * Função responsável por ler um registro no arquivo binário
     */
    public static boolean read(int idBuscada) {

        try {
            RandomAccessFile binaryFile = new RandomAccessFile(pathBin, "rw");
            binaryFile.seek(4);

            while (binaryFile.getFilePointer() < binaryFile.length()) {
                Registro registro = new Registro();
                registro.fbaLapideTamanho(binaryFile);

                long pos = binaryFile.getFilePointer();
                if (!registro.getLapide()) {

                    registro.fbaFilme(binaryFile);
                    if (registro.getFilmeById() == idBuscada) {
                        System.out.println(registro.toString());
                        return true;
                    }
                } else {
                    binaryFile.seek(pos + registro.getTamanho());
                }

            }
            System.out.println("\nFilme de id " + idBuscada + " não encontrado");

            // Fechar o arquivo
            binaryFile.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return false;
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

    /*
     * Função responsável por deletar um registro no arquivo binário
     */
    public static boolean delete(int idBuscado) {
        try {
            RandomAccessFile binaryFile = new RandomAccessFile(pathBin, "rw");
            binaryFile.seek(4);

            while (binaryFile.getFilePointer() < binaryFile.length()) {
                Registro registro = new Registro();
                long pos = binaryFile.getFilePointer();
                registro.fbaLapideTamanho(binaryFile);

                if (!registro.getLapide()) {
                    registro.fbaFilme(binaryFile);
                    if (registro.getFilmeById() == idBuscado) {
                        binaryFile.seek(pos);
                        binaryFile.writeBoolean(true);
                        System.out.println("\nFilme com id " + idBuscado + " deletado com sucesso\n");
                        return true;
                    }
                } else {
                    binaryFile.seek(pos + 5 + registro.getTamanho());
                }

            }
            System.out.println("\nFilme de id " + idBuscado + " não encontrado");

            // Fechar o arquivo
            binaryFile.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    /*
     * Função responsável por atualizar um registro no arquivo binário
     */
    public static boolean update(Filme novoFilme) {
        RandomAccessFile binaryFile = null;

        try {
            binaryFile = new RandomAccessFile(pathBin, "rw");

            // Mover para o primeiro registro do arquivo (após cabeçalho)
            binaryFile.seek(4);

            while (binaryFile.getFilePointer() < binaryFile.length()) {
                long pos = binaryFile.getFilePointer();
                Registro registro = new Registro();
                registro.fbaLapideTamanho(binaryFile);

                if (!registro.getLapide()) {
                    registro.fbaFilme(binaryFile);

                    if (registro.getFilmeById() == novoFilme.getId()) {
                        Registro novoRegistro = new Registro(novoFilme);

                        if (novoRegistro.getTamanho() <= registro.getTamanho()) {
                            binaryFile.seek(pos);
                            novoRegistro.setTamanho(registro.getTamanho());
                            binaryFile.write(novoRegistro.toBinaryArray());
                        } else {
                            binaryFile.seek(pos);
                            binaryFile.writeBoolean(true);
                            binaryFile.seek(binaryFile.length());
                            binaryFile.write(novoRegistro.toBinaryArray());
                        }

                        System.out.println("\nFilme com id " + novoFilme.getId() + " atualizado com sucesso\n");

                        return true;
                    }
                } else {
                    // Se lápide é verdadeira, apenas pula para o próximo registro
                    binaryFile.seek(pos + 5 + registro.getTamanho());
                }
            }

            // Filme não encontrado após varrer todo o arquivo
            System.out.println("\nFilme com id " + novoFilme.getId() + " não encontrado.");

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

        return false;
    }

    /*----------------------------------------- Ordenação Externa -----------------------------------------*/

    public static int registersCounter() {
        int count = 0;

        try {
            RandomAccessFile binaryFile = new RandomAccessFile(pathBin, "r");

            long pos = binaryFile.getFilePointer();
            binaryFile.seek(pos + 4);

            while (pos < binaryFile.length()) {
                boolean lapide = binaryFile.readBoolean();

                if (lapide == false) {
                    count++;
                }

                int tamanho = binaryFile.readInt();
                pos = binaryFile.getFilePointer();
                binaryFile.seek(pos + tamanho);
                pos = binaryFile.getFilePointer();
            }

            binaryFile.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return count;
    }

    public static void distribuicao() {
        try {
            //distribui o arq em 2, ordena em blocos de 1000.
            RandomAccessFile binaryFile = new RandomAccessFile(pathBin, "r");
            RandomAccessFile tmp1 = new RandomAccessFile("./tp01/temp/tmp1.db", "rw");
            RandomAccessFile tmp2 = new RandomAccessFile("./tp01/temp/tmp2.db", "rw");

            long pos = binaryFile.getFilePointer();
            binaryFile.seek(pos + 4);

            int tmp = 0;
            boolean eof = false;
            ArrayList<Registro> registros;

            while (!eof) {
                registros = new ArrayList<>();
                for (int i = 0; i < bloco; i++) {
                    try {
                        boolean lapide = binaryFile.readBoolean();
                        int tamanho = binaryFile.readInt();
                        pos = binaryFile.getFilePointer();

                        if (lapide == false) {
                            Registro registro = new Registro();
                            registro.setTamanho(tamanho);
                            registro.fbaFilme(binaryFile);
                            registros.add(registro);
                        } else {
                            binaryFile.seek(pos + tamanho);
                        }

                    } catch (EOFException e) {
                        eof = true;
                        break;
                    }
                }

                Collections.sort(registros, Comparator.comparingInt(Registro::getFilmeById));

                RandomAccessFile temp = (tmp % 2 == 0) ? tmp1 : tmp2;

                for (Registro registro : registros) {
                    temp.seek(temp.length());
                    temp.write(registro.toBinaryArray());
                }

                tmp++;

            }

            binaryFile.close();
            tmp1.close();
            tmp2.close();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void intercalacao() {
        //junta os blocos ordenados e grava em outro arquivo
        try {
            RandomAccessFile tmp1 = new RandomAccessFile("./tp01/temp/tmp1.db", "rw");
            RandomAccessFile tmp2 = new RandomAccessFile("./tp01/temp/tmp2.db", "rw");
            RandomAccessFile tmp3 = new RandomAccessFile("./tp01/temp/tmp3.db", "rw");
            RandomAccessFile tmp4 = new RandomAccessFile("./tp01/temp/tmp4.db", "rw");

            int quantRegistros = registersCounter();
            int numIntercalacoes = (int) Math.ceil((double) quantRegistros / (bloco * 2));
            int k = 2;

            Registro registro1 = new Registro();
            Registro registro2 = new Registro();

            while (tmp1.length() != 0 && tmp2.length() > 0) {
                tmp1.seek(0);
                tmp2.seek(0);

                for (int i = 0; i < numIntercalacoes; i++) {
                    RandomAccessFile target = (i % 2 == 0) ? tmp3 : tmp4;

                    for (int j = 0; j < (bloco * k) && tmp1.getFilePointer() != tmp1.length()
                            && tmp2.getFilePointer() != tmp2.length(); j++) {
                        if (j == 0) {
                            registro1.fromBinaryArray(tmp1);
                            registro2.fromBinaryArray(tmp2);
                        }

                        if (registro1.getFilmeById() < registro2.getFilmeById()) {
                            target.write(registro1.toBinaryArray());
                            registro1.fromBinaryArray(tmp1);
                        } else if (registro1.getFilmeById() >= registro2.getFilmeById()) {
                            target.write(registro2.toBinaryArray());
                            registro2.fromBinaryArray(tmp2);
                        }
                    }
                    while (tmp1.getFilePointer() != tmp1.length() && tmp2.getFilePointer() == tmp2.length()) {
                        target.write(registro1.toBinaryArray());
                        registro1.fromBinaryArray(tmp1);
                    }

                    while (tmp1.getFilePointer() == tmp1.length() && tmp2.getFilePointer() != tmp2.length()) {
                        target.write(registro2.toBinaryArray());
                        registro2.fromBinaryArray(tmp2);
                    }
                }

                tmp1.setLength(0);
                tmp2.setLength(0);

                RandomAccessFile aux = tmp1;
                tmp1 = tmp3;
                tmp3 = aux;

                aux = tmp2;
                tmp2 = tmp4;
                tmp4 = aux;

                numIntercalacoes = (int) Math.ceil((double) numIntercalacoes / 2);
                k = k * 2;
            }

            tmp1.close();
            tmp2.close();
            tmp3.close();
            tmp4.close();
            copyBinaryFile();
            writeToFile();
        } catch (

        IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void ordenacaoExterna() {
        distribuicao();
        intercalacao();
        excluirArquivos();
    }

    public static void copyBinaryFile() throws IOException {

        RandomAccessFile sourceFile = new RandomAccessFile("./tp01/temp/tmp3.db", "r");
        RandomAccessFile destFile = new RandomAccessFile("./tp01/dados/filmesBin_ordenado.db", "rw");

        sourceFile.seek(0);
        destFile.seek(0);

        long sourceFileLength = sourceFile.length();
        int bufferSize = 1024;

        byte[] buffer = new byte[bufferSize];

        long sourcePos = 0;
        long destPos = 0;

        while (sourcePos < sourceFileLength) {
            // Calcula a quantidade de bytes a serem lidos nesta iteração
            int bytesToRead = (int) Math.min(bufferSize, sourceFileLength - sourcePos);

            // Lê os bytes do arquivo de origem para o buffer
            sourceFile.seek(sourcePos);
            sourceFile.readFully(buffer, 0, bytesToRead);

            // Escreve os bytes lidos no arquivo de destino
            destFile.seek(destPos);
            destFile.write(buffer, 0, bytesToRead);

            // Atualiza as posições nos arquivos de origem e destino
            sourcePos += bytesToRead;
            destPos += bytesToRead;
        }

        sourceFile.close();
        destFile.close();

    }

    public static void excluirArquivos() {
        File diretorio = new File("./tp01/temp");
        File[] arquivos = diretorio.listFiles();
        if (arquivos != null) {
            // Para cada arquivo na lista
            for (File arquivo : arquivos) {
                // Verifica se é um arquivo
                if (arquivo.isFile()) {
                    // Exclui o arquivo
                    arquivo.delete();

                }
            }
        }

    }

    public static void writeToFile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("./tp01/dados/filmesOrdenados.txt"));
        RandomAccessFile tmp = new RandomAccessFile("./tp01/temp/tmp3.db", "rw");
        tmp.seek(0);
        while (tmp.length() != tmp.getFilePointer()) {
            Registro registro = new Registro();
            tmp.readBoolean();
            registro.setTamanho(tmp.readInt());
            registro.fbaFilme(tmp);
            writer.write(registro.toString());
        }

        writer.close();
        tmp.close();
    }

    public static void main(String[] args) {
        menu();
    }
}