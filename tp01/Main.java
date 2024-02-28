import java.io.*;
import java.util.*;

class Main {
    private static Scanner in = new Scanner(System.in);
    private static final String pathCSV = "./tp01/dados/filmes.csv";
    private static final String pathBin = "./tp01/dados/filmesBin.db";
    private static final int bloco = 1000;

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
        } else if (op == 3) {
            Filme filme = new Filme();

            System.out.println("\n---------------------------------");

            System.out.print("\nDigite o título do filme: ");
            filme.setTitle(in.next());

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
            if (!delete(in.nextInt())) {
                System.out.print("Erro ao deletar o filme!");
            }
            System.out.println("---------------------------------\n");
        } else if (op == 5) {
            Filme filme = new Filme();

            System.out.println("\n---------------------------------");

            System.out.print("Digite o id do filme que você deseja atualizar: ");
            filme.setId(in.nextInt());

            System.out.print("Digite o título do filme: ");
            filme.setTitle(in.next());

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

        }

        if (op != 6) {
            menu();
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

    /*
     * Função responsável por ler um registro no arquivo binário
     */
    public static void read(int idBuscada) {

        try {
            RandomAccessFile binaryFile = new RandomAccessFile(pathBin, "rw");
            binaryFile.readInt();

            boolean eof = false;
            while (!eof) {
                Registro registro = new Registro();
                registro.fbaLapideTamanho(binaryFile);

                long pos = binaryFile.getFilePointer();
                if (!registro.getLapide()) {

                    registro.fbaFilme(binaryFile);
                    if (registro.getFilmeById() == idBuscada) {
                        System.out.println(registro.toString());
                        break;
                    }
                } else {
                    binaryFile.seek(pos + registro.getTamanho());
                }
                // Verifica se chegou ao fim do arquivo
                eof = (binaryFile.getFilePointer() == binaryFile.length());
            }
            if (eof) {
                System.out.println("\nFilme de id " + idBuscada + " não encontrado");
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
            binaryFile.readInt();

            while (in.hasNextLine()) {
                Registro registro = new Registro();
                long pos = binaryFile.getFilePointer();
                registro.fbaLapideTamanho(binaryFile);
                long posFilme = binaryFile.getFilePointer();
                if (!registro.getLapide()) {
                    registro.fbaFilme(binaryFile);
                    if (registro.getFilmeById() == idBuscado) {
                        binaryFile.seek(pos);
                        binaryFile.writeBoolean(true);
                        System.out.println("\nFilme com id " + idBuscado + " deletado com sucesso\n");
                        return true;
                    }
                } else {
                    binaryFile.seek(posFilme + registro.getTamanho());
                }
            }
            System.out.println("\nFilme de id " + idBuscado + " não encontrado");

            // Fechar o arquivo
            binaryFile.close();
            return false;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /*
     * Função responsável por atualizar um registro no arquivo binário
     */
    public static boolean update(Filme filme) {
        try {
            RandomAccessFile binaryFile = new RandomAccessFile(pathBin, "rw");
           binaryFile.readInt();

           boolean eof = false;
            while (!eof) {
                Registro registro = new Registro();
                long pos = binaryFile.getFilePointer();
                registro.fbaLapideTamanho(binaryFile);

                long posFilme = binaryFile.getFilePointer();

                if (!registro.getLapide()) {
                    registro.fbaFilme(binaryFile);

                    if (registro.getFilmeById() == filme.getId()) {

                        Registro novoRegistro = new Registro(filme);
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

                        System.out.println("\nFilme com id " + filme.getId() + " atualizado com sucesso\n");
                        return true;
                    } else {
                        binaryFile.seek(posFilme + registro.getTamanho());
                    }
                }
                // Verifica se chegou ao fim do arquivo
                eof = (binaryFile.getFilePointer() == binaryFile.length());
            }
            if (eof) {
                System.out.println("\nFilme de id " + filme.getId() + " não encontrado");
            }

            // Fechar o arquivo
            binaryFile.close();
            return false;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

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
        int qtdRegistros = registersCounter();
        int qtdIntercalacao = (int) Math.ceil((double) qtdRegistros / bloco) / 2;

        /*
         * tmp1.seek(0);
         * tmp2.seek(0);
         * 
         * for (int i = 0; i < qtdIntercalacao; i++) {
         * RandomAccessFile target = (i % 2 == 0) ? tmp3 : tmp4;
         * int contador = 0;
         * 
         * while (contador < bloco && tmp1.getFilePointer() < tmp1.length()
         * && tmp2.getFilePointer() < tmp2.length()) {
         * Registro registro1 = new Registro();
         * registro1.fromBinaryArray(tmp1);
         * 
         * Registro registro2 = new Registro();
         * registro2.fromBinaryArray(tmp2);
         * 
         * if (registro1.getFilmeById() < registro2.getFilmeById()) {
         * target.seek(target.length());
         * target.write(registro1.toBinaryArray());
         * } else {
         * target.seek(target.length());
         * target.write(registro2.toBinaryArray());
         * }
         * 
         * contador++;
         * }
         * 
         * while (tmp1.getFilePointer() < tmp1.length()) {
         * Registro registro1 = new Registro();
         * registro1.fromBinaryArray(tmp1);
         * target.seek(target.length());
         * target.write(registro1.toBinaryArray());
         * }
         * 
         * while (tmp2.getFilePointer() < tmp2.length()) {
         * Registro registro2 = new Registro();
         * registro2.fromBinaryArray(tmp2);
         * target.seek(target.length());
         * target.write(registro2.toBinaryArray());
         * }
         * 
         * tmp1.setLength(0);
         * tmp2.setLength(0);
         * 
         * // Checar se apenas um dos arquivos está vazio
         * if (tmp1.length() == 0 && tmp2.length() == 0) {
         * eof = true;
         * }
         * }
         */
    }

    public static void ordenacaoExterna() {
        distribuicao();
        // intercalacao();
    }

    public static void main(String[] args) {
        menu();
        // ordenacaoExterna();
    }
}