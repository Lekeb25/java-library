import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Book extends Document{

    private String author;



    public Book( String title, String author,  int publicationYear, int numberOfCopies) {
       super(title, publicationYear, numberOfCopies);
        this.author= author;
    }

    @Override
    public void showDocumentInfos() {
        super.showDocumentInfos();
        System.out.println("Author  :  " + author);
    }

    @Override
    public void getType() {
        System.out.println("Book");
    }


    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return this.author;
    }


    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;

        Book book = (Book) obj;
        return Objects.equals(author, book.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), author);
    }
}