package library;

import model.Book;
import model.Document;

import java.util.function.Predicate;

public class DocumentCriteria {

    public static Predicate<Document> withTitle(String title) {
        return document -> document.getTitle().equalsIgnoreCase(title);
    }

    public static Predicate<Document> publishedInYear( int year) {
        return document -> document.getPublicationYear() == year;
    }

    public static Predicate<Document> publishedAfter( int year) {
        return document -> document.getPublicationYear() > year;
    }

    public static Predicate<Document> isAvailable () {
        return Document::isAvailable;
    }

    public static Predicate<Document> byAuthor ( String author ) {
        return document -> document instanceof Book b && b.getAuthor().equalsIgnoreCase(author);
    }
}
