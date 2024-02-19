import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;

class Filme {

    // Atributos
    protected int id;
    protected LocalDate release_date;
    protected String title;
    protected float vote_avarage;
    protected String original_language;
    protected String[] genres;

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
        this.release_date = LocalDate.parse(date);
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

    public LocalDate getReleasDate() {
        return this.release_date;
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

    public byte[] toBinaryArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.id);
        dos.writeUTF(this.release_date.toString());
        dos.writeUTF(this.title);
        dos.writeFloat(this.vote_avarage);
        dos.writeUTF(this.original_language);
        dos.writeInt(this.genres.length);
        for (String genre : this.genres) {
            dos.writeUTF(genre);
        }

        return baos.toByteArray();
    }

    public void mostrar() {
        System.out.println(id);
        System.out.println(release_date);
        System.out.println(title);
        System.out.println(vote_avarage);
        System.out.println(original_language);
        for (String genre : genres) {
            System.out.print(genre + " ");
        }
    }

    @Override
    public String toString() {
        return "[ id = " + this.id + ", release_date = " + this.release_date + ", title = " + this.title + "]";
    }

}