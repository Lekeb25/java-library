package model;

import java.util.Objects;

public class Book extends Document {

    private String author;



    public Book( String title, String author,  int publicationYear, int numberOfCopies) {
       super(title, publicationYear, numberOfCopies);
        this.author= Validation.requireText(author, "author");
    }

    public void setAuthor(String author) {
        this.author = Validation.requireText(author, "author");
    }

    public String getAuthor() {
        return this.author;
    }

    @Override
    public void showDocumentInfos() {
        super.showDocumentInfos();
        System.out.println("Author  :  " + author);
    }

    @Override
    public String getType() {
        return  "Book";
    }

    @Override
    public String toFileLine() {
        return getTitle() + ";" + getAuthor() + ";" + getPublicationYear() + ";" + getNumberOfCopies();
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