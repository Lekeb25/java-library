package persistence;

import exception.InvalidSaveFileException;
import library.Library;
import model.Book;
import model.Document;
import model.Magazine;
import model.Reader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LibraryFileStorageTest {

    @TempDir
    Path tempDir;

    private Path savePath;
    private LibraryFileStorage storage;
    private Library library;

    private static final String READER = "READER";
    private static final String BOOK = "BOOK";
    private static  final String MAGAZINE = "MAGAZINE";
    private static final String BORROW = "BORROW";
    private static final String SEPARATOR = ";";

    @BeforeEach
    void setUp() {
        savePath = tempDir.resolve("library-data.txt");
        storage = new LibraryFileStorage(savePath);
        library = new Library();
    }

    private void writeSaveFile(String... lines) throws IOException {
        Files.write(savePath, List.of(lines));
    }

    private void fillLibrary() {
        Book dune = new Book("Dune", "Frank Herbert", 1965, 3);
        Magazine natGeo = new Magazine("National Geographic", 2024, 2, 245, "NatGeo Society");

        library.addDocument(dune);
        library.addDocument(natGeo);

        Reader nabi = new Reader("Nabi", "Kenne", 1);
        Reader kebodel = new Reader("Kebodel", "Bauer", 2);

        library.addReader(nabi);
        library.addReader(kebodel);

        library.borrowDocument(1, dune);
        library.borrowDocument(1, natGeo);
        library.borrowDocument(2, natGeo);
    }

    @Test
    void saveAndLoad_restoresDocumentsReadersAndBorrows_whenLibraryIsFilled() throws IOException {
        fillLibrary();

        storage.save(library);
        Library loadedLibrary = new Library();

        storage.load(loadedLibrary);

        assertEquals(2, loadedLibrary.getDocuments().size());
        assertEquals(2, loadedLibrary.getReaders().size());

        Reader reader1 = loadedLibrary.findReaderById(1);
        Reader reader2 = loadedLibrary.findReaderById(2);
        assertEquals("Nabi", reader1.getFirstName());
        assertEquals("Kenne", reader1.getLastName());
        assertEquals("Kebodel", reader2.getFirstName());
        assertEquals("Bauer", reader2.getLastName());

        assertEquals(1, loadedLibrary.getBooks().size());
        Document foundBook = loadedLibrary.findDocumentByTitleAndYear("Dune", 1965);
        Book dune = assertInstanceOf(Book.class, foundBook);
        assertEquals("Frank Herbert", dune.getAuthor());
        assertEquals(3, dune.getNumberOfCopies());
        assertEquals(2, dune.getAvailableCopies());
        assertEquals(1, dune.getBorrowedBy().size());
        assertTrue(dune.getBorrowedBy().contains(reader1));

        assertEquals(1, loadedLibrary.getMagazines().size());
        Document foundMagazine = loadedLibrary.findDocumentByTitleAndYear("National Geographic", 2024);
        Magazine natGeo = assertInstanceOf(Magazine.class, foundMagazine);
        assertEquals("NatGeo Society", natGeo.getPublisher());
        assertEquals(2, natGeo.getNumberOfCopies());
        assertEquals(0, natGeo.getAvailableCopies());
        assertEquals(245, natGeo.getIssueNumber());
        assertEquals(2, natGeo.getBorrowedBy().size());
        assertEquals(List.of(reader1, reader2), natGeo.getBorrowedBy());

    }

    @Test
    void save_writesExpectedLines_whenLibraryIsFilled() throws IOException {
        fillLibrary();

        storage.save(library);
        List<String> lines = Files.readAllLines(savePath);

        assertEquals(7, lines.size());

        String readerNabi = READER + SEPARATOR + "Nabi" + SEPARATOR + "Kenne" + SEPARATOR + "1";
        String readerKebodel = READER + SEPARATOR + "Kebodel" + SEPARATOR +  "Bauer" + SEPARATOR + "2";
        List<String> line12 = lines.subList(0,2);
        assertTrue(line12.contains(readerNabi));
        assertTrue(line12.contains(readerKebodel));


        String bookDune = BOOK + SEPARATOR + "Dune" + SEPARATOR + "Frank Herbert" + SEPARATOR + "1965" + SEPARATOR + "3";
        String borrowDune = BORROW + SEPARATOR + "Dune" + SEPARATOR + "1965" + SEPARATOR + "1";
        String magazineNatGeo = MAGAZINE + SEPARATOR + "National Geographic" + SEPARATOR + "2024" + SEPARATOR + "2" + SEPARATOR + "245" + SEPARATOR + "NatGeo Society";
        String borrowNatGeo1 = BORROW + SEPARATOR + "National Geographic" + SEPARATOR + "2024" + SEPARATOR + "1";
        String borrowNatGeo2 = BORROW + SEPARATOR + "National Geographic" + SEPARATOR + "2024" + SEPARATOR + "2";
        List<String> expectedDocumentLines = List.of(
                bookDune,
                borrowDune,
                magazineNatGeo,
                borrowNatGeo1,
                borrowNatGeo2
        );

        assertEquals(expectedDocumentLines, lines.subList(2, 7));

    }

    @Test
    void load_doesNothing_whenFileDoesNotExist() {
        assertDoesNotThrow(() -> storage.load(library));
        assertTrue(library.getDocuments().isEmpty());
        assertTrue(library.getReaders().isEmpty());
        assertFalse(Files.exists(savePath));
    }

    @Test
    void load_doesNothing_whenFileIsEmpty() throws IOException {
        Files.createFile(savePath);

        assertTrue(Files.exists(savePath));
        assertDoesNotThrow(() -> storage.load(library));
        assertTrue(library.getDocuments().isEmpty());
        assertTrue(library.getReaders().isEmpty());
    }

    @Test
    void load_ignoresBlankLines_whenFileContainsEmptyLines() throws IOException {
        writeSaveFile(
                READER + SEPARATOR + "Nabi" + SEPARATOR + "Kenne" + SEPARATOR + "1",
                        "",
                        BOOK + SEPARATOR + "Dune" + SEPARATOR + "Frank Herbert" + SEPARATOR + "1965" + SEPARATOR + "2",
                        "   ",
                        BORROW + SEPARATOR + "Dune" + SEPARATOR + "1965" + SEPARATOR + "1"
                );

        storage.load(library);

        assertEquals(1, library.getReaders().size());
        assertEquals(1, library.getDocuments().size());

        Document dune = library.findDocumentByTitleAndYear("Dune", 1965);
        assertEquals(1, dune.getAvailableCopies());
    }

    @Test
    void load_throwsInvalidSaveFileException_whenLineTypeIsUnknown() throws IOException {
        writeSaveFile(
                READER + SEPARATOR + "Nabi" + SEPARATOR +  "Kenne" + SEPARATOR + "1",
                      BOOK + SEPARATOR + "Dune" + SEPARATOR + "Frank Herbert" + SEPARATOR + "1965" + SEPARATOR + "2",
                      "HELLO" + SEPARATOR + "x"
        );

        InvalidSaveFileException exception = assertThrows(InvalidSaveFileException.class, () -> storage.load(library));
        assertTrue(exception.getMessage().contains("line 3") && exception.getMessage().contains("HELLO"));
    }

    @Test
    void load_throwsInvalidSaveFileException_whenFieldCountIsWrong() throws IOException {
        writeSaveFile(
                READER + SEPARATOR + "Nabi" + SEPARATOR + "Kenne"
        );

        InvalidSaveFileException exception = assertThrows(InvalidSaveFileException.class, () -> storage.load(library));
        assertTrue(exception.getMessage().contains("READER line expects 4 fields"));
    }

    @Test
    void load_throwsInvalidSaveFileException_whenNumberIsNotReadable() throws IOException {
        writeSaveFile(
                READER + SEPARATOR + "Nabi" + SEPARATOR + "Kenne" + SEPARATOR + "abc"
        );

        InvalidSaveFileException exception = assertThrows(InvalidSaveFileException.class, () -> storage.load(library));
        assertTrue(exception.getMessage().contains("line 1"));
    }

    @Test
    void load_throwsInvalidSaveFileException_whenModelRejectsValue() throws  IOException {
        writeSaveFile(
                BOOK + SEPARATOR + "Dune" + SEPARATOR + "Frank Herbert" + SEPARATOR + "1965" + SEPARATOR + "0"
        );

        InvalidSaveFileException exception = assertThrows(InvalidSaveFileException.class, () -> storage.load(library));
        assertTrue(exception.getMessage().contains("line 1") && exception.getMessage().contains("numberOfCopies"));
    }

    @Test
    void load_throwsInvalidSaveFileException_whenBorrowReaderIsUnknown() throws IOException{
        writeSaveFile(
                BOOK + SEPARATOR + "Dune" + SEPARATOR + "Frank Herbert" + SEPARATOR + "1965" + SEPARATOR + "2",
                BORROW + SEPARATOR + "Dune" + SEPARATOR + "1965" + SEPARATOR + "9"
        );

        InvalidSaveFileException exception = assertThrows(InvalidSaveFileException.class, () -> storage.load(library));
        assertTrue(exception.getMessage().contains("line 2") && exception.getMessage().contains("9"));
    }

    @Test
    void load_throwsInvalidSaveFileException_whenBorrowDocumentIsUnknown() throws IOException {
        writeSaveFile(
                READER + SEPARATOR + "Nabi" + SEPARATOR + "Kenne" + SEPARATOR + "1",
                BORROW + SEPARATOR + "Dune" + SEPARATOR + "1965" + SEPARATOR + "1"
        );

        InvalidSaveFileException exception = assertThrows(InvalidSaveFileException.class, () -> storage.load(library));
        assertTrue(exception.getMessage().contains("line 2") && exception.getMessage().contains("Dune"));
    }

    @Test
    void load_throwsInvalidSaveFileException_whenSameBorrowAppearsTwice() throws IOException {
        writeSaveFile(
                READER + SEPARATOR + "Nabi" + SEPARATOR + "Kenne" + SEPARATOR + "1",
                BOOK + SEPARATOR + "Dune" + SEPARATOR + "Frank Herbert" + SEPARATOR + "1965" + SEPARATOR + "2",
                BORROW + SEPARATOR + "Dune" + SEPARATOR + "1965" + SEPARATOR + "1",
                BORROW + SEPARATOR + "Dune" + SEPARATOR + "1965" + SEPARATOR + "1"
        );

        InvalidSaveFileException exception = assertThrows(InvalidSaveFileException.class, () -> storage.load(library));
        assertTrue(exception.getMessage().contains("line 4"));
    }

}
