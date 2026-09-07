import java.util.ArrayList;

public class Library {
    private ArrayList<Document> documents = new ArrayList<>();
    private ArrayList<Reader> readers = new ArrayList<>();

    public void addDocument(Document document ) {
        if (documents.contains(document) ) {
            System.out.println("This Document already exists in the library.");
            return;
        }
        documents.add(document);
    }

    public void addReader( Reader reader ) {
        if (readers.contains(reader)) {
            System.out.println("This user is already registered in the library.");
            return;
        }
        readers.add(reader);
    }

    public void removeDocument(Document document) {
        if ( ! document.hasAllCopiesAvailable()) {
            System.out.println("All borrowed copies must be returned before removing this document from the library.");
            return;
        }
        if (! documents.remove(document)) {
            System.out.println("This Document don't exist in the Library");
        }
    }

    public void removeReader(Reader reader) {
        if (! readers.remove(reader)) {
            System.out.println("This user is not registered in the Library");
        }
    }

    public void borrowDocument( Reader reader, Document document ) {
        if ( ! readers.contains(reader) ) {
            System.out.println("This user is not registered in the Library");
            return;
        }
        if ( ! documents.contains(document) ) {
            System.out.println("This document is not available in the Library");
            return;
        }
        document.borrow(reader);
    }

    public void returnBorrowedDocument(Reader reader, Document document) {
        if ( ! readers.contains(reader) ) {
            System.out.println("This user is not registered in the Library");
            return;
        }
        if ( ! documents.contains(document) ) {
            System.out.println("This document is not available in the Library");
            return;
        }
        if (document.returnDocument(reader)) {
            System.out.println("Document returned successfully");
        } else {
            System.out.println("This user has not borrowed this document");
        }
    }

}
