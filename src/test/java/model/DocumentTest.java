package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentTest {

    private Book annaKarenina;
    private Magazine natGeo;
    private Reader nabi;
    private Reader kebodel;

    @BeforeEach
    void setUp() {
        annaKarenina= new Book("Anna Karenina", "Leo Tolstoy", 1878, 4);
        natGeo = new Magazine("National Geographic", 2024, 3, 245, "NatGeo Society");
        nabi = new Reader("Nabi", "Kenne", 1);
        kebodel = new Reader("Kebodel", "Bauer", 2);
    }

    @Test
    void getAvailableCopies_returnsCopiesMinusBorrowers_whenDocumentIsBorrowed() {

        annaKarenina.borrow(nabi);

        assertEquals(3, annaKarenina.getAvailableCopies());
        assertEquals(1, annaKarenina.getBorrowedBy().size());
    }

    @Test
    void hasAllCopiesAvailable_returnsTrue_whenNoCopyIsBorrowed() {

        boolean beforeBorrow = annaKarenina.hasAllCopiesAvailable();

        assertTrue(beforeBorrow);

    }

    @Test
    void hasAllCopiesAvailable_returnsFalse_whenCopiesAreBorrowed() {

       annaKarenina.borrow(nabi);

        boolean afterBorrow = annaKarenina.hasAllCopiesAvailable();

        assertFalse(afterBorrow);
    }

    @Test
    void getBorrowedBy_notModifyBorrowerList_whenCleaningBorrowerList() {
        annaKarenina.borrow(nabi);
        annaKarenina.borrow(kebodel);

        List<Reader> expectedBorrowerList = List.of(nabi, kebodel);
        annaKarenina.getBorrowedBy().clear();

        assertEquals(expectedBorrowerList, annaKarenina.getBorrowedBy());
        assertTrue(annaKarenina.getBorrowedBy().containsAll(List.of(nabi, kebodel)));
    }

    @Test
    void equals_returnsTrue_whenSameAttributeButDifferentNumberOfCopies() {
        Book annaKarenina2 = new Book("Anna Karenina", "Leo Tolstoy", 1878, 3);

        boolean equalsResult = annaKarenina.equals(annaKarenina2);

        assertTrue(equalsResult);
        assertEquals(annaKarenina2.hashCode(), annaKarenina.hashCode());
    }

    @Test
    void equals_returnsFalse_whenSameTitleYearButDifferentAuthor() {
        Book annaKarenina2 = new Book("Anna Karenina", "Herman Hesse", 1878, 3);

        boolean equalsResult = annaKarenina.equals(annaKarenina2);

        assertFalse(equalsResult);
    }

    @Test
    void equals_returnsFalse_whenComparingBookAndMagazine() {
        Book annaKareninaBook = new Book("Anna Karenina", "Leo Tolstoy", 1878, 4);
        Magazine annaKareninaMagazine = new Magazine("Anna Karenina", 1878, 3, 245, "NatGeo Society");

        boolean expectedResult = annaKareninaBook.equals(annaKareninaMagazine);

        assertFalse(expectedResult);
    }

    @Test
    void toFileLine_returnsExpectedLine_forBook() {

        String expectedString = "Anna Karenina;Leo Tolstoy;1878;4";
        String toFileLineResult = annaKarenina.toFileLine();

        assertEquals(expectedString, toFileLineResult);
    }

    @Test
    void toFileLine_returnsExpectedLine_forMagazine() {

        String expectedString = "National Geographic;2024;3;245;NatGeo Society";
        String toFileLineResult = natGeo.toFileLine();

        assertEquals(expectedString, toFileLineResult);
    }

    @Test
    void bookConstructor_throwsIllegalArgumentException_whenTitleIsEmpty() {
        IllegalArgumentException except = assertThrows(IllegalArgumentException.class, () -> new Book("", "Leo Tolstoy", 1878, 4));
        assertTrue(except.getMessage().contains("title"));
    }

    @Test
    void bookConstructor_throwsIllegalArgumentException_whenTitleContainsSemicolon() {
        IllegalArgumentException except = assertThrows(IllegalArgumentException.class, () -> new Book("Anna;Karenina", "Leo Tolstoy", 1878, 4));
        assertTrue(except.getMessage().contains("title"));
        assertTrue(except.getMessage().contains(";"));
    }

    @Test
    void bookConstructor_throwsIllegalArgumentException_whenPublicationYearIsZero() {
        IllegalArgumentException except = assertThrows(IllegalArgumentException.class, () -> new Book("Anna Karenina", "Leo Tolstoy", 0, 4));
        assertTrue(except.getMessage().contains("publicationYear"));
    }

    @Test
    void bookConstructor_throwsIllegalArgumentException_whenNumberOfCopiesIsZero() {
        IllegalArgumentException except = assertThrows(IllegalArgumentException.class, () -> new Book("Anna Karenina", "Leo Tolstoy", 1878, 0));
        assertTrue(except.getMessage().contains("numberOfCopies"));
    }

    @Test
    void bookConstructor_throwsIllegalArgumentException_whenAuthorIsEmpty() {
        IllegalArgumentException except = assertThrows(IllegalArgumentException.class, () -> new Book("Anna Karenina", "", 1878, 4));
        assertTrue(except.getMessage().contains("author"));
    }

    @Test
    void magazineConstructor_throwsIllegalArgumentException_whenPublisherIsEmpty() {
        IllegalArgumentException except = assertThrows(IllegalArgumentException.class, () -> new Magazine("Science Today", 1999, 1, 12, ""));
        assertTrue(except.getMessage().contains("publisher"));
    }

    @Test
    void magazineConstructor_throwsIllegalArgumentException_whenIssueNumberIsZero() {
        IllegalArgumentException except = assertThrows(IllegalArgumentException.class, () -> new Magazine("Science Today", 1999, 1, 0, "SciPub"));
        assertTrue(except.getMessage().contains("issueNumber"));
    }

    @Test
    void setTitle_throwsIllegalArgumentException_whenTitleIsBlank() {
        IllegalArgumentException except = assertThrows(IllegalArgumentException.class, () -> annaKarenina.setTitle(""));
        assertTrue(except.getMessage().contains("title"));
        assertEquals("Anna Karenina", annaKarenina.getTitle());
    }

    @Test
    void setNumberOfCopies_throwsIllegalArgumentException_whenNumberOfCopiesIsZero() {
        IllegalArgumentException except = assertThrows(IllegalArgumentException.class, () -> annaKarenina.setNumberOfCopies(0));
        assertTrue(except.getMessage().contains("numberOfCopies"));
        assertEquals(4, annaKarenina.getNumberOfCopies());
    }

    @Test
    void setAuthor_throwsIllegalArgumentException_whenAuthorContainSemicolon() {
        IllegalArgumentException except = assertThrows(IllegalArgumentException.class, () -> annaKarenina.setAuthor("Leo;Tolstoy"));
        assertTrue(except.getMessage().contains("author"));
        assertEquals("Leo Tolstoy", annaKarenina.getAuthor());
    }

    @Test
    void setPublisher_throwsIllegalArgumentException_whenPublisherIsEmpty() {
        IllegalArgumentException except = assertThrows(IllegalArgumentException.class, () -> natGeo.setPublisher(""));
        assertTrue(except.getMessage().contains("publisher"));
        assertEquals("NatGeo Society",  natGeo.getPublisher());
    }

    @Test
    void setIssueNumber_throwsIllegalArgumentException_whenIssueNumberIsZero() {
        IllegalArgumentException except = assertThrows(IllegalArgumentException.class, () -> natGeo.setIssueNumber(0));
        assertTrue(except.getMessage().contains("issueNumber"));
        assertEquals(245, natGeo.getIssueNumber());
    }

    @Test
    void setPublicationYear_throwsIllegalArgumentException_whenYearIsZero() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> annaKarenina.setPublicationYear(0));
        assertTrue(exception.getMessage().contains("publicationYear"));
        assertEquals(1878, annaKarenina.getPublicationYear());
    }

}
