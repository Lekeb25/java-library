package library;

import exception.*;
import model.Book;
import model.Document;
import model.Magazine;
import model.Reader;

import java.util.*;
import java.util.function.Predicate;


public class Library {
    private List<Document> documents = new ArrayList<>();
    private Map<Integer, Reader> readers = new HashMap<>();

    public List<Document> getDocuments() {
        return new ArrayList<>(documents);
    }

    public List<Reader> getReaders() {
        return new ArrayList<>(readers.values());
    }

    // ------------------------------------ Behavior -------------------------------------
    public void addReader( Reader reader ) {
        if (readers.containsKey(reader.getId())) {
            throw new ReaderAlreadyExistsException("A reader with id " + reader.getId() + " is already registered in the library.");
        }
        readers.put(reader.getId(), reader);
    }

    public void removeReader(int readerId) {
        Reader reader = this.findReaderById(readerId);

        boolean hasBorrowedDocuments = documents.stream().anyMatch( document -> document.getBorrowedBy().contains(reader));

        if (hasBorrowedDocuments) {
            throw  new ReaderCannotBeRemovedException("Cannot remove reader " + reader.getId() + ": all borrowed documents must be returned first.");
        }

        readers.remove(readerId);
    }

    public Reader findReaderById(int readerId) {
        Reader reader = readers.get(readerId);
        if (reader == null) {
            throw new ReaderNotFoundException("No reader found with id " + readerId + " in the library.");
        }
        return reader;
    }


    public void addDocument(Document document ) {
        if (documents.contains(document) ) {
            throw new DocumentAlreadyExistsException("The document " + document.getTitle() + " already exists in the library.");
        }
        documents.add(document);
    }

    public void removeDocument(Document document) {
        if ( ! document.hasAllCopiesAvailable()) {
            throw new DocumentCannotBeRemovedException("Cannot remove '" + document.getTitle() + "': all borrowed copies must be returned first.");
        }
        if (! documents.remove(document)) {
            throw  new DocumentNotFoundException("The document " + document.getTitle() + " does not exist in the library.");
        }
    }

    public List<Document> searchDocuments(Predicate<Document> criteria) {

        return documents.stream()
                .filter(criteria)
                .toList();
    }

    public Document searchDocument(Document document) {
        for (Document searchedDocument : documents) {
            if (searchedDocument.equals(document)) {
                return searchedDocument;
            }
        }
        throw new DocumentNotFoundException("The document " + document.getTitle() + " does not exist in the library.");
    }


    public void borrowDocument( int readerId, Document document ) {
        Reader reader = findReaderById(readerId);

        ensureDocumentExists(document);
        document.borrow(reader);
    }

    public void returnBorrowedDocument(int readerId, Document document) {
        Reader reader = findReaderById(readerId);

        ensureDocumentExists(document);
        document.returnDocument(reader);
    }


    public Optional<Document> findFirstDocument( Predicate<Document> criteria ) {

        return documents.stream()
                .filter(criteria)
                .findFirst();
    }

    public Document findDocumentByTitleAndYear ( String title, int publicationYear) {

        return  findFirstDocument(
                DocumentCriteria.withTitle(title).and(DocumentCriteria.publishedInYear(publicationYear))
        ).orElseThrow(() ->
                new DocumentNotFoundException("The document " + title + " does not exist in the library.")
        );
        /*
         * Before, we used a for loop to iterate over the list until we found the element.
         * Now we use a Predicate from the DocumentCriteria class to make it easier and more flexible.
        */
        
        /* for (Document document : documents) {
            if ( document.getTitle().equals(title) && document.getPublicationYear() == publicationYear ) {
                return document;
            }
        }*/
    }


    public List<Document> getDocumentsSorted() {
        List<Document> sortedDocuments = new ArrayList<>(documents);

        Collections.sort(sortedDocuments);
        return sortedDocuments;
    }

    public List<Document> getDocumentsSortedBy(Comparator<Document> comparator ) {
        List<Document> sorted = new ArrayList<>(documents);

        sorted.sort(comparator);
        return sorted;
    }

    public List<Book> getBooks() {
        return documents.stream()
                            .filter( document -> document instanceof Book)
                            .map( document -> (Book) document)
                            .toList();
    }

    public List<Magazine> getMagazines() {
        return documents.stream()
                            .filter( document -> document instanceof Magazine)
                            .map( document -> (Magazine) document)
                            .toList();
    }

    public List<String> getTitles() {
        return documents.stream()
                            .map(Document::getTitle)
                            .toList();
    }

    public List<Integer> getPublicationYears()  {
        return documents.stream()
                            .map(Document::getPublicationYear)
                            .distinct()
                            .sorted()
                            .toList();
    }


    public boolean hasAvailableDocument() {
        return documents.stream()
                .anyMatch(Document::isAvailable);
    }

    public boolean areAllDocumentsAvailable() {
        return documents.stream()
                .allMatch(Document::isAvailable);
    }


    public int countDocuments() {
        return documents.size();
    }

    public long countAvailableDocuments() {
        return documents.stream()
                .filter( Document::isAvailable)
                .count();
    }

    public int getTotalCopies() {
        return documents.stream()
                .mapToInt(Document::getNumberOfCopies)
                .sum();
    }

    public OptionalInt getOldestPublicationYear() {
        return documents.stream()
                            .mapToInt(Document::getPublicationYear)
                            .min();
    }

    public OptionalInt getNewestPublicationYear() {
        return documents.stream()
                            .mapToInt(Document::getPublicationYear)
                            .max();
    }


    // -------------------------------------- Private helpers ------------------------------------
    private void ensureDocumentExists(Document document) {
        if ( ! documents.contains(document) ) {
            throw  new DocumentNotFoundException("The document " + document.getTitle() + " does not exist in the library.");
        }
    }

}
