package library;

import exception.*;
import model.Book;
import model.Document;
import model.Magazine;
import model.Reader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryTest {

    private  Library library;

    // Reference values for the dataset created by fillLibrary().
    // If fillLibrary() changes, update these values too.
    private static final int TOTAL_DOCUMENTS = 5;
    private static final int TOTAL_BOOKS = 3;
    private static final int TOTAL_MAGAZINES = 2;
    private static final int TOTAL_COPIES = 15;
    private static final int AVAILABLE_DOCUMENTS = 3;
    private static final int OLDEST_YEAR = 1867;
    private static final int NEWEST_YEAR = 2024;
    private static final List<Integer> PUBLICATION_YEARS = List.of(1867, 1904, 1927, 1999, 2024);

    private void fillLibrary() {
        Book steppenwolf = new Book("Steppenwolf", "Herman Hesse", 1927, 4);
        Book peterCamenzind = new Book("Peter Camenzind", "Herman Hesse", 1904, 5);
        Book crimeAndPunishment = new Book("Crime and Punishment", "Fyodor Dostoevsky", 1867, 2);
        Magazine natGeo = new Magazine("National Geographic", 2024, 3, 245, "NatGeo Society");
        Magazine sciPub = new Magazine("Science Today", 1999, 1, 12, "SciPub");

        Reader kebodel = new Reader("Kebodel", "Bauer", 1);
        Reader nabi = new Reader("Nabi", "Kenne", 2);
        Reader ruth = new Reader("Ruth", "Djiala", 3);

        library.addDocument(steppenwolf);
        library.addDocument(peterCamenzind);
        library.addDocument(crimeAndPunishment);
        library.addDocument(natGeo);
        library.addDocument(sciPub);

        library.addReader(kebodel);
        library.addReader(nabi);
        library.addReader(ruth);

        library.borrowDocument(1, crimeAndPunishment);
        library.borrowDocument(2, crimeAndPunishment);
        library.borrowDocument(3, sciPub);
    }

    @BeforeEach
    void setUp() {
        library = new Library();
    }

    /* Document Tests */

    @Test
    void addDocument_addsDocumentToLibrary_whenLibraryIsEmpty() {
        Book erenVeyr= new Book("Le Second Role Devore Le Monde", "Dimi_Noups", 2026, 1);

        library.addDocument(erenVeyr);

        assertTrue(library.getDocuments().contains(erenVeyr));
    }

    @Test
    void addDocument_throwsDocumentAlreadyExists_whenDocumentIsAlreadyPresent() {
        library.addDocument( new Book("Crime and Punishment", "Fyodor Dostoevsky", 1867, 2));

        assertThrows(DocumentAlreadyExistsException.class, () -> library.addDocument( new Book("Crime and Punishment", "Fyodor Dostoevsky", 1867, 5)));
    }

    @Test
    void removeDocument_documentIsRemoved_whenNoCopiesAreBorrowed() {
        Book annaKarenina= new Book("Anna Karenina", "Leo Tolstoy", 1878, 4);

        library.addDocument(annaKarenina);
        library.removeDocument(annaKarenina);

        assertTrue(library.getDocuments().isEmpty());
    }

    @Test
    void removeDocument_throwsDocumentCannotBeRemoved_whenDocumentIsBorrowed() {
        Book annaKarenina= new Book("Anna Karenina", "Leo Tolstoy", 1878, 4);
        Reader nabi = new Reader("Nabi", "Watto", 1);
        library.addDocument(annaKarenina);
        library.addReader(nabi);

        library.borrowDocument(1, annaKarenina);

        assertThrows(DocumentCannotBeRemovedException.class, () -> library.removeDocument(annaKarenina));
        assertTrue(library.getDocuments().contains(annaKarenina));

    }

    @Test
    void removeDocument_throwsDocumentNotFound_whenDocumentNotExist() {
        Book annaKarenina= new Book("Anna Karenina", "Leo Tolstoy", 1878, 4);
        fillLibrary();

        assertThrows(DocumentNotFoundException.class, () -> library.removeDocument(annaKarenina));
    }

    @Test
    void getDocuments_returnsCopy_whenReturnedListIsCleared() {
        fillLibrary();

        library.getDocuments().clear();

        assertFalse(library.getDocuments().isEmpty());
    }


    /* Reader Tests */

    @Test
    void findReaderById_getAReader_whenReaderIsAddToLibrary() {
        Reader nabi = new Reader("Nabi", "Watto", 1);
        library.addReader(nabi);

        assertEquals(nabi, library.findReaderById(1));
    }

    @Test
    void addReader_throwsReaderAlreadyExists_whenAddReaderWithSameId() {
        Reader nabi = new Reader("Nabi", "Watto", 1);
        Reader kebodel = new Reader("Kebodel", "Bauer", 1);

        library.addReader(nabi);

        assertThrows(ReaderAlreadyExistsException.class, () -> library.addReader(kebodel));
    }

    @Test
    void findReaderById_throwsReaderNotFound_whenReaderIsRemoved() {
        Reader nabi = new Reader("Nabi", "Watto", 1);
        Reader kebodel = new Reader("Kebodel", "Bauer", 2);
        library.addReader(nabi);
        library.addReader(kebodel);

        library.removeReader(1);

        assertThrows(ReaderNotFoundException.class, () -> library.findReaderById(1));
    }

    @Test
    void removeReader_throwsReaderNotFound_whenReaderNotExists() {
        Reader kebodel = new Reader("Kebodel", "Bauer", 2);
        library.addReader(kebodel);

        assertThrows(ReaderNotFoundException.class, () -> library.removeReader(1));
    }

    @Test
    void findReaderById_throwsReaderNotFound_whenReaderIdNotExists() {
        Reader kebodel = new Reader("Kebodel", "Bauer", 2);
        library.addReader(kebodel);

        assertThrows(ReaderNotFoundException.class, () -> library.findReaderById(10));
    }

    @Test
    void removeReader_throwReaderCannotBeRemoved_whenReaderHasBorrowedDocuments() {

        Book steppenwolf = new Book("Steppenwolf", "Herman Hesse", 1927, 4);
        Reader kebodel = new Reader("Kebodel", "Bauer", 1);
        library.addDocument(steppenwolf);
        library.addReader(kebodel);
        library.borrowDocument(1, steppenwolf);

        ReaderCannotBeRemovedException exception = assertThrows(ReaderCannotBeRemovedException.class, () -> library.removeReader(1));

        assertTrue(exception.getMessage().contains("Cannot remove reader " + kebodel.getId()));
        assertNotNull(library.findReaderById(1));
        assertTrue(steppenwolf.getBorrowedBy().contains(kebodel));
    }

    @Test
    void removeReader_removesSuccessfully_whenReaderHasReturnedBorrowedDocument() {
        Book steppenwolf = new Book("Steppenwolf", "Herman Hesse", 1927, 4);
        Reader kebodel = new Reader("Kebodel", "Bauer", 1);
        library.addDocument(steppenwolf);
        library.addReader(kebodel);
        library.borrowDocument(1, steppenwolf);

        library.returnBorrowedDocument(1, steppenwolf);

        assertDoesNotThrow( () -> library.removeReader(1) );
        assertThrows(ReaderNotFoundException.class, () -> library.findReaderById(1));
    }


    /* Borrow and return documents */

    @Test
    void borrowDocument_borrowSuccessfully_whenDocumentIsAvailableAndReaderExists() {
        Reader nabi = new Reader("Nabi", "Watto", 1);
        Book annaKarenina= new Book("Anna Karenina", "Leo Tolstoy", 1878, 4);
        library.addReader(nabi);
        library.addDocument(annaKarenina);

        library.borrowDocument(1, annaKarenina);

        assertEquals(3 , annaKarenina.getAvailableCopies());
        assertTrue(annaKarenina.getBorrowedBy().contains(nabi));
    }

    @Test
    void borrowDocument_documentIsNotLongerAvailable_whenLastCopyIsBorrowed() {
        Reader nabi = new Reader("Nabi", "Watto", 1);
        Book annaKarenina= new Book("Anna Karenina", "Leo Tolstoy", 1878, 1);
        library.addReader(nabi);
        library.addDocument(annaKarenina);

        library.borrowDocument(1, annaKarenina);

        assertFalse(annaKarenina.isAvailable());
        assertEquals(0, annaKarenina.getAvailableCopies());
    }

    @Test
    void borrowDocument_throwsReaderAlreadyBorrowedDocument_whenReaderTryBorrowedAlreadyBorrowedBook() {
        Reader nabi = new Reader("Nabi", "Watto", 1);
        Book annaKarenina= new Book("Anna Karenina", "Leo Tolstoy", 1878, 2);
        library.addReader(nabi);
        library.addDocument(annaKarenina);

        library.borrowDocument(1, annaKarenina);

        assertThrows(ReaderAlreadyBorrowedDocumentException.class, () -> library.borrowDocument(1, annaKarenina));
    }

    @Test
    void borrowDocument_throwsNoCopiesAvailable_whenAllDocumentCopiesAreBorrowed() {
        Reader nabi = new Reader("Nabi", "Watto", 1);
        Reader kebodel = new Reader("Kebodel", "Bauer", 2);
        library.addReader(nabi);
        library.addReader(kebodel);
        Book annaKarenina= new Book("Anna Karenina", "Leo Tolstoy", 1878, 1);
        library.addDocument(annaKarenina);

        library.borrowDocument(1, annaKarenina);

        assertThrows(NoCopiesAvailableException.class, () -> library.borrowDocument(2, annaKarenina));
    }

    @Test
    void borrowDocument_throwsReaderNotFound_whenBorrowerNotExist() {
        Reader nabi = new Reader("Nabi", "Watto", 1);
        Book annaKarenina= new Book("Anna Karenina", "Leo Tolstoy", 1878, 2);
        library.addReader(nabi);
        library.addDocument(annaKarenina);

        assertThrows(ReaderNotFoundException.class, () -> library.borrowDocument(10, annaKarenina));
    }

    @Test
    void borrowDocument_throwsDocumentNotFound_whenDocumentNotExists() {
        fillLibrary();
        Book annaKarenina= new Book("Anna Karenina", "Leo Tolstoy", 1878, 2);

        assertThrows(DocumentNotFoundException.class, () -> library.borrowDocument(1, annaKarenina));

    }

    @Test
    void returnBorrowedDocument_returnSuccessfully_whenReaderHaveBorrowedThisDocument() {
        Reader nabi = new Reader("Nabi", "Watto", 1);
        Book annaKarenina= new Book("Anna Karenina", "Leo Tolstoy", 1878, 2);
        library.addReader(nabi);
        library.addDocument(annaKarenina);

        library.borrowDocument(1, annaKarenina);
        library.returnBorrowedDocument(1, annaKarenina);

        assertFalse(annaKarenina.getBorrowedBy().contains(nabi));
        assertEquals(2, annaKarenina.getAvailableCopies());
    }

    @Test
    void returnBorrowedDocument_throwsReaderHasNotBorrowedDocument_whenReaderNeverBorrowedIt() {
        fillLibrary();
        Document steppenwolf= library.findDocumentByTitleAndYear("Steppenwolf", 1927);

        assertThrows(ReaderHasNotBorrowedDocumentException.class, () -> library.returnBorrowedDocument(1, steppenwolf ));
    }

    @Test
    void borrowDocument_borrowSuccessfully_whenLastAvailableCopyAreReturn() {
        fillLibrary();
        Reader berenice = new Reader("Berenice", "Pamap", 4);
        library.addReader(berenice);
        Document crimeAndPunishment = library.findDocumentByTitleAndYear("Crime and Punishment", 1867);

        library.returnBorrowedDocument(1, crimeAndPunishment);
        library.borrowDocument(4, crimeAndPunishment);

        assertTrue(crimeAndPunishment.getBorrowedBy().contains(berenice));

    }



    /* Searching and sorting in library */

    @Test
    void searchDocuments_returnsMatchingDocuments_whenPublishedYearMatches() {
        fillLibrary();

        List<Document> expectedDocument = List.of(library.findDocumentByTitleAndYear("Steppenwolf", 1927));
        List<Document> searchingResult = library.searchDocuments(DocumentCriteria.publishedInYear(1927));

        assertEquals(1, searchingResult.size());
        assertEquals(expectedDocument, searchingResult);
    }

    @Test
    void searchDocuments_returnsEmptyList_whenLibraryIsEmpty() {
        List<Document> searchingResult = library.searchDocuments(DocumentCriteria.publishedInYear(1927));

        assertTrue(searchingResult.isEmpty());
    }

    @Test
    void searchDocuments_returnsMatchingDocuments_whenAuthorMatches() {
        fillLibrary();
        Document steppenwolf = library.findDocumentByTitleAndYear("Steppenwolf", 1927);
        Document peterCamenzind = library.findDocumentByTitleAndYear("Peter Camenzind", 1904);

        List<Document> expectedDocuments = List.of(steppenwolf, peterCamenzind);
        List<Document> searchingResult = library.searchDocuments(DocumentCriteria.byAuthor("Herman Hesse"));

        assertEquals(2, searchingResult.size());
        assertEquals(expectedDocuments, searchingResult);
    }

    @Test
    void searchDocuments_returnsMatchingDocuments_whenPublishedAfterGivenYear() {
        fillLibrary();

        Document natGeo= library.findDocumentByTitleAndYear("National Geographic", 2024);
        Document sciPub= library.findDocumentByTitleAndYear("Science Today", 1999);

        List<Document> searchedDocument = library.searchDocuments(DocumentCriteria.publishedAfter(1950));

        assertEquals(2, searchedDocument.size());
        assertTrue(searchedDocument.containsAll(List.of(natGeo, sciPub)));
    }

    @Test
    void searchDocuments_returnsMatchingDocuments_whenIsAvailable()  {
        fillLibrary();

        Document steppenwolf = library.findDocumentByTitleAndYear("Steppenwolf", 1927);
        Document peterCamenzind = library.findDocumentByTitleAndYear("Peter Camenzind", 1904);
        Document natGeo = library.findDocumentByTitleAndYear("National Geographic", 2024);

        List<Document> searchedDocument = library.searchDocuments(DocumentCriteria.isAvailable());

        assertEquals(3, searchedDocument.size());
        assertTrue(searchedDocument.containsAll(List.of(steppenwolf, peterCamenzind, natGeo)));
    }

    @Test
    void searchDocuments_returnsMatchingDocuments_whenIsAvailableAndAuthorMatches() {
        fillLibrary();

        Document steppenwolf = library.findDocumentByTitleAndYear("Steppenwolf", 1927);
        Document peterCamenzind = library.findDocumentByTitleAndYear("Peter Camenzind", 1904);
        List<Document> searchedDocument = library.searchDocuments(DocumentCriteria.byAuthor("Herman Hesse").and(DocumentCriteria.isAvailable()));

        assertEquals(2, searchedDocument.size());
        assertTrue(searchedDocument.containsAll(List.of(steppenwolf, peterCamenzind)));
    }

    @Test
    void searchDocuments_returnsEmptyList_whenNothingMatches() {
        fillLibrary();

        List<Document> searchedDocument = library.searchDocuments(DocumentCriteria.publishedInYear(1800));

        assertTrue(searchedDocument.isEmpty());
    }

    @Test
    void findFirstDocument_returnsEmptyOptional_whenTitleNotExists() {
        fillLibrary();

        Optional<Document> searchedDocument = library.findFirstDocument(DocumentCriteria.withTitle("Anna Karenina"));

        assertTrue(searchedDocument.isEmpty());
    }

    @Test
    void findDocumentByTitleAndYear_throwsDocumentNotFound_whenTitleOrYearNotExists() {
        fillLibrary();

        assertThrows(DocumentNotFoundException.class, () -> library.findDocumentByTitleAndYear("Steppenwolf", 2026));
    }

    @Test
    void getDocumentsSorted_documentSortedByTitleThenYear() {
        fillLibrary();

        Document crimeAndPunishment = library.findDocumentByTitleAndYear("Crime and Punishment", 1867);
        Document natGeo = library.findDocumentByTitleAndYear("National Geographic", 2024);
        Document peterCamenzind = library.findDocumentByTitleAndYear("Peter Camenzind", 1904);
        Document sciPub = library.findDocumentByTitleAndYear("Science Today", 1999);
        Document steppenwolf = library.findDocumentByTitleAndYear("Steppenwolf", 1927);

        List<Document> expectedSortedDocuments = List.of(crimeAndPunishment, natGeo, peterCamenzind, sciPub, steppenwolf);
        List<Document> sortedDocuments = library.getDocumentsSorted();

        assertEquals(expectedSortedDocuments, sortedDocuments);
    }

    @Test
    void getDocumentsSortedBy_documentsInAscendingYearOrder() {
        fillLibrary();

        Document crimeAndPunishment = library.findDocumentByTitleAndYear("Crime and Punishment", 1867);
        Document peterCamenzind = library.findDocumentByTitleAndYear("Peter Camenzind", 1904);
        Document steppenwolf = library.findDocumentByTitleAndYear("Steppenwolf", 1927);
        Document sciPub = library.findDocumentByTitleAndYear("Science Today", 1999);
        Document natGeo = library.findDocumentByTitleAndYear("National Geographic", 2024);

        List<Document> expectedSortedDocuments = List.of(crimeAndPunishment, peterCamenzind, steppenwolf, sciPub, natGeo );
        List<Document> sortedDocuments = library.getDocumentsSortedBy(Comparator.comparing(Document::getPublicationYear));

        assertEquals(expectedSortedDocuments, sortedDocuments);
    }

    @Test
    void getDocumentsSortedBy_documentsInDescendingYearOrder() {
        fillLibrary();

        Document crimeAndPunishment = library.findDocumentByTitleAndYear("Crime and Punishment", 1867);
        Document peterCamenzind = library.findDocumentByTitleAndYear("Peter Camenzind", 1904);
        Document steppenwolf = library.findDocumentByTitleAndYear("Steppenwolf", 1927);
        Document sciPub = library.findDocumentByTitleAndYear("Science Today", 1999);
        Document natGeo = library.findDocumentByTitleAndYear("National Geographic", 2024);

        List<Document> expectedSortedDocuments = List.of(natGeo, sciPub, steppenwolf,  peterCamenzind, crimeAndPunishment );
        List<Document> expectedOriginalDocuments = library.getDocuments();
        List<Document> sortedDocuments = library.getDocumentsSortedBy(Comparator.comparing(Document::getPublicationYear).reversed());

        assertEquals(expectedSortedDocuments, sortedDocuments);
        assertEquals(expectedOriginalDocuments, library.getDocuments());
    }

    @Test
    void getDocumentsSorted_originalDocumentsOrderNotChange_whenMakingSorting() {
        fillLibrary();

        List<Document> documentsBefore = library.getDocuments();

        library.getDocumentsSorted();

        assertEquals(documentsBefore, library.getDocuments());
    }



    /* Filter, Transform, • Aggregate */

    @Test
    void countDocuments_returnsNumberOfDocument() {
        fillLibrary();

        int numberOfDocument = library.countDocuments();

        assertEquals(TOTAL_DOCUMENTS, numberOfDocument);
    }

    @Test
    void countDocuments_returnsZero_whenLibraryIsEmpty() {
        int numberOfDocuments = library.countDocuments();

        assertEquals(0, numberOfDocuments);
    }

    @Test
    void getTotalCopies_returnsTotalNumberOfCopies() {
        fillLibrary();

        int numberOfCopies = library.getTotalCopies();

        assertEquals(TOTAL_COPIES, numberOfCopies);
    }

    @Test
    void getTotalCopies_returnsZero_whenLibraryIsEmpty() {
        int numberOfCopies = library.getTotalCopies();

        assertEquals(0, numberOfCopies);
    }

    @Test
    void countAvailableDocuments_returnsNumberOfAvailableDocument() {
        fillLibrary();

        long numberOfAvailableDocument= library.countAvailableDocuments();

        assertEquals(AVAILABLE_DOCUMENTS, numberOfAvailableDocument);
    }

    @Test
    void countAvailableDocuments_returnsZero_whenLibraryIsEmpty() {
        long numberOfAvailableDocument = library.countAvailableDocuments();

        assertEquals(0, numberOfAvailableDocument);
    }

    @Test
    void getBooks_returnsNumberOfBooks() {
        fillLibrary();

        int numberOfBooks = library.getBooks().size();

        assertEquals(TOTAL_BOOKS, numberOfBooks);
    }

    @Test
    void getBooks_returnsEmptyList_whenLibraryIsEmpty() {
        List<Book> listOfBooks = library.getBooks();

        assertTrue(listOfBooks.isEmpty());
    }

    @Test
    void getMagazines_returnNumberOfMagazines() {
        fillLibrary();

        int numberOfMagazines = library.getMagazines().size();

        assertEquals(TOTAL_MAGAZINES, numberOfMagazines);
    }

    @Test
    void getMagazines_returnEmptyList_whenLibraryIsEmpty() {
        List<Magazine> listOfMagazines = library.getMagazines();

        assertTrue(listOfMagazines.isEmpty());
    }

    @Test
    void getTitles_returnsTitleAndNumberOfTitle() {
        fillLibrary();

        int numberOfTitle = library.getTitles().size();
        List<String> titles= library.getTitles();

        assertEquals(TOTAL_DOCUMENTS, numberOfTitle);
        assertTrue(titles.containsAll(List.of("Steppenwolf", "Peter Camenzind", "Crime and Punishment", "National Geographic", "Science Today")));
    }

    @Test
    void getTitles_returnsEmptyList_whenLibraryIsEmpty() {
        List<String> listOfTitles = library.getTitles();

        assertTrue(listOfTitles.isEmpty());
    }

    @Test
    void getPublicationYears_returnsPublicationYears() {
        fillLibrary();

        List<Integer> publicationYears = library.getPublicationYears();

        assertEquals(PUBLICATION_YEARS, publicationYears);
    }

    @Test
    void getOldestPublicationYear_returnsMatchesPublicationYear() {
        fillLibrary();

        assertEquals(OptionalInt.of(OLDEST_YEAR), library.getOldestPublicationYear());
    }

    @Test
    void getOldestPublicationYear_returnsEmpty_whenLibraryIsEmpty() {
        OptionalInt oldestPublicationYear = library.getOldestPublicationYear();

        assertTrue(oldestPublicationYear.isEmpty());
    }

    @Test
    void getNewestPublicationYear_returnsMatchesPublicationYear() {
        fillLibrary();

        assertEquals(OptionalInt.of(NEWEST_YEAR), library.getNewestPublicationYear());
    }

    @Test
    void getNewestPublicationYear_returnsEmpty_whenLibraryIsEmpty() {
        OptionalInt newestPublicationYear = library.getNewestPublicationYear();

        assertTrue(newestPublicationYear.isEmpty());
    }

    @Test
    void hasAvailableDocument_returnsTrue_whenLibraryHasAvailableDocuments() {
        fillLibrary();

        assertTrue(library.hasAvailableDocument());
    }

    @Test
    void hasAvailableDocument_returnsFalse_whenLibraryIsEmpty() {
        boolean hasAvailableDocumentResult = library.hasAvailableDocument();

        assertFalse(hasAvailableDocumentResult);
    }

    @Test
    void areAllDocumentsAvailable_returnsFalse_whenOneDocumentIsNotAvailable() {
        fillLibrary();

        assertFalse(library.areAllDocumentsAvailable());
    }

    @Test
    void areAllDocumentsAvailable_returnsTrue_whenLibraryIsEmpty() {
        boolean areAllDocumentsAvailableResult = library.areAllDocumentsAvailable();

        assertTrue(areAllDocumentsAvailableResult);
    }
}
