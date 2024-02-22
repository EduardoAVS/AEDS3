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

            System.out.print("Digite o título do filme: ");
            filme.setTitle(in.next());

            System.out.print("Digite a data de lançamento (yyyy-MM-dd): ");
            filme.setReleaseDate(in.next());

            System.out.print("Digite a média de votos: ");
            filme.setVoteAvarage(in.nextFloat());
            in.nextLine(); // Consumir a nova linha

            System.out.print("Digite a língua original: ");
            filme.setOriginalLanguage(in.nextLine());

            System.out.print("Digite os gêneros desse filme: ");
            String aux = in.nextLine();
            String[] genres = aux.split(" ");
            filme.setGenres(genres);

            System.out.println("---------------------------------\n");

        } else if(op == 4){
            System.out.println("\n---------------------------------");
            System.out.print("Digite o id do filme que você deseja deletar: ");
            delete(in.nextInt());
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

            while(in.hasNextLine()) {
                Registro registro = new Registro(); // Cria registro vazio
                registro.fbaLapideTamanho(binaryFile); // Lê a lapide e o tamanho em binario

                long pos = binaryFile.getFilePointer(); // Posicao do proximo registro
                if (!registro.getLapide()) { // Se lápide está marcado o registro foi excluido e deve ser ignorado

                    registro.fbaFilme(binaryFile);// Informacões do filme lidas 
                    if (registro.getFilmeById() == idBuscada) {
                        System.out.println(registro.toString()); // Transforma o registro em texto
                        return true; // Retorna true se encontrar o id
                    }
                }
                else{
                    binaryFile.seek(pos + registro.getTamanho());
                }
            }
              System.out.println("\nFilme de id " + idBuscada + " não encontrado");

            // Fechar o arquivo
            binaryFile.close();
            return false; // Retorna falso se não encontrar o id
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public static boolean create(Filme filme) {
        try {
            // Abre o arquivo binário já escrito
            RandomAccessFile binaryFile = new RandomAccessFile(pathBin, "rw");
            Registro registro = new Registro(filme);

            binaryFile.seek(0); // Posiciona o ponteiro no início do arquivo
            int id = binaryFile.readInt() + 1; // Lê o id do cabaçalho e o incrementa
            filme.setId(id); // Atribui o id incrementado ao filme que vai ser adicionado ao arquivo

            binaryFile.seek(0); // Posiciona o ponteiro no início do arquivo
            binaryFile.writeInt(id); // Atualiza o id do cabeçalho

            binaryFile.seek(binaryFile.length()); // Posiciona o ponteiro no final do arquivo
            binaryFile.write(registro.toBinaryArray()); // Adiciona o novo registro ao arquivo

            // Fechar o arquivo
            binaryFile.close();
            return true;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public static boolean delete(int idBuscado){
        try {
            // Abre o arquivo binário já escrito
            RandomAccessFile binaryFile = new RandomAccessFile(pathBin, "rw");
            int id = binaryFile.readInt();

            while(in.hasNextLine()) {
                Registro registro = new Registro(); // Cria registro vazio
                long pos = binaryFile.getFilePointer(); // Posicao do início do registro(lápide)
                registro.fbaLapideTamanho(binaryFile); // Passa o arquivo diretamente para o método fromBinaryArray

                long posFilme = binaryFile.getFilePointer(); // Posicao do proximo registro
                if (!registro.getLapide()) { // Se lápide está marcado o registro foi excluido e deve ser ignorado
                    registro.fbaFilme(binaryFile);
                    if (registro.getFilmeById() == idBuscado) {
                        binaryFile.seek(pos); // Volta o ponteiro para a posicao inicial do registro(lápide)
                        binaryFile.writeBoolean(true);; // Coloca a lápide como true
                        System.out.println("\nFilme com id "+ idBuscado + " deletado com sucesso\n");
                        return true; // Retorna true se encontrar o id
                    }
                }
                else{
                    binaryFile.seek(posFilme + registro.getTamanho()); // Caso o registro foi deletado pula para o próximo
                }
            }
            System.out.println("\nFilme de id " + idBuscado + " não encontrado");

            // Fechar o arquivo
            binaryFile.close();
            return false; // Retorna falso se não encontrar o id
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public static boolean update(Filme filme){
        try {
            // Abre o arquivo binário já escrito
            RandomAccessFile binaryFile = new RandomAccessFile(pathBin, "rw");
            int id = binaryFile.readInt(); // Último id escrito no arquivo

            for(int i = 0 ; i <= id; i++) {
                Registro registro = new Registro(); // Cria registro vazio
                long pos = binaryFile.getFilePointer(); // Posicao do início do registro(lápide)
                registro.fbaLapideTamanho(binaryFile); // Passa o arquivo diretamente para o método fromBinaryArray

                long posFilme = binaryFile.getFilePointer(); // Posicao do proximo registro

                if (!registro.getLapide()) { // Se lápide está marcado o registro foi excluido e deve ser ignorado
                    registro.fbaFilme(binaryFile);
                    if (registro.getFilmeById() == filme.getId()) {

                        Registro novoRegistro = new Registro(filme); // Criando registro com o filme atualizado
                        if(novoRegistro.getTamanho() < registro.getTamanho()){

                            binaryFile.seek(pos); // Mudando a posicao do ponteiro para o inicio do registro
                            novoRegistro.setTamanho(registro.getTamanho()); // O tamanho do novo registro deve ser o mesmo do anterior
                            binaryFile.write(novoRegistro.toBinaryArray()); // Adiciona o novo registro ao arquivo

                        }
                        else{
                            binaryFile.seek(pos); // Mudando a posicao do ponteiro para o inicio do registro
                            binaryFile.writeBoolean(true);; // Coloca a lápide como true
                            binaryFile.seek(binaryFile.length()); // Posiciona o ponteiro no final do arquivo
                            binaryFile.write(novoRegistro.toBinaryArray()); // Adiciona o novo registro ao arquivo
                        }

                        System.out.println("\nFilme com id "+ filme.getId() + " atualizado com sucesso\n");
                        return true; // Retorna true se encontrar o id
                    }
                    else{
                        binaryFile.seek(posFilme + registro.getTamanho()); // Caso o registro foi deletado pula para o próximo
                    }
                }
            }
            System.out.println("\nFilme de id " + filme.getId() + " não encontrado");

            // Fechar o arquivo
            binaryFile.close();
            return false; // Retorna falso se não encontrar o id
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        escreverArquivoBin();
        String[] s = {"acao", "comedia", "aventura", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"};
        Filme f = new Filme("1111-11-11", "Teste", 6, "pt", s);
        f.setId(1);
        update(f);
        menu();
    }
}