import java.io.*;
import java.util.*;
import java.text.*;

class Filme {

    // Atributos
    protected int id;
    protected long release_date;
    protected String title;
    protected float vote_avarage;
    protected String original_language;
    protected String[] genres;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); // Formatador de datas

    // Construtores
    public Filme() {
        setId(0);
        setTitle("");
        setVoteAvarage(0);
        setOriginalLanguage("");
        setGenres(new String[] {});
    }

    public Filme(int id, String date, String title, float vote_avarage, String originalString, String[] genres) {
        setId(id);
        setReleaseDate(date);
        setTitle(title);
        setVoteAvarage(vote_avarage);
        setOriginalLanguage(originalString);
        setGenres(genres);
    }

    // Setters
    public void setId(int id) {
        this.id = id; // Fazer autenticação para ver qual foi o último id
    }

    public void setReleaseDate(String date) {
        try {
            Date aux = format.parse(date);
            this.release_date = aux.getTime() / 1000;

        } catch (ParseException e) {
            System.err.println(e);
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVoteAvarage(float vote_avarage) {
        this.vote_avarage = vote_avarage;
    }

    public void setOriginalLanguage(String original_language) {
        this.original_language = original_language;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    // Getters
    public int getId() {
        return this.id;
    }

    public String getReleaseDate() {
        return format.format(new Date(this.release_date * 1000));
    }

    public String getTitle() {
        return this.title;
    }

    public float getVoteAvarage() {
        return this.vote_avarage;
    }

    public String getOriginalLanguage() {
        return this.original_language;
    }

    public String[] getGenres() {
        return this.genres;
    }

    // Função para transformar um filme em um array de bytes
    public byte[] toBinaryArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.id);
        dos.writeLong(this.release_date);
        dos.writeUTF(this.title);
        dos.writeFloat(this.vote_avarage);
        dos.writeUTF(this.original_language);
        dos.writeInt(this.genres.length);
        for (String genre : this.genres) {
            dos.writeUTF(genre);
        }

        return baos.toByteArray();
    }

    public void fromBinaryArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        id = dis.readInt();
        release_date = dis.readLong();
        title = dis.readUTF();
        vote_avarage = dis.readFloat();
        original_language = dis.readUTF();
        int numGenres = dis.readInt();
        genres = new String[numGenres]; // Inicializando o array genres
        for (int i = 0; i < numGenres; i++) { // Loop para ler os gêneros
            genres[i] = dis.readUTF();
        }
    }

    @Override
    public String toString() {
        return "\n[ id = " + this.id + ", release_date = " + getReleaseDate() + ", title = " + this.title
                + ", vote_avarage = " + this.vote_avarage + ", original_language = " + this.original_language + "]\n";
    }

}