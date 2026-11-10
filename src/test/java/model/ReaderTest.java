package model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ReaderTest {

    @Test
    void equals_returnsTrue_whenSameIdButDifferentName() {
        Reader nabi = new Reader("Nabi", "Kenne", 8);
        Reader kebodel = new Reader("Kebodel", "Bauer", 8);

        boolean resultEquals = nabi.equals(kebodel);

        assertTrue(resultEquals);
        assertEquals(nabi.hashCode(), kebodel.hashCode());
    }

    @Test
    void equals_returnsFalse_whenDifferentId() {
        Reader nabi1 = new Reader("Nabi", "Kenne", 1);
        Reader nabi2 = new Reader("Nabi", "Kenne", 2);

        boolean resultEquals = nabi1.equals(nabi2);

        assertFalse(resultEquals);
    }

    @Test
    void hashSetContains_returnsTrue_whenReaderIdAlreadyExistInSet() {
        Reader nabi = new Reader("Nabi", "Kenne", 8);
        Reader kebodel = new Reader("Kebodel", "Bauer", 8);
        Set<Reader> readers = new HashSet<>();

        readers.add(nabi);

        boolean containsResult = readers.contains(kebodel);

        assertTrue(containsResult);
    }

    @Test
    void toFileLine_returnsExpectedLine() {
        Reader kebodel = new Reader("Kebodel", "Bauer", 8);

        String expectedString = "Kebodel;Bauer;8";
        String toFileLineResult = kebodel.toFileLine();

        assertEquals(expectedString, toFileLineResult);
    }

    @Test
    void readerConstructor_throwsIllegalArgumentException_whenFirstNameIsBlank() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Reader(" ", "Kebodel", 2));
        assertTrue(exception.getMessage().contains("firstName"));
    }

    @Test
    void readerConstructor_throwsIllegalArgumentException_whenFirstNameContainsSemicolon() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Reader("Kebo;del", "Kebodel", 2));
        assertTrue(exception.getMessage().contains("firstName"));
        assertTrue(exception.getMessage().contains(";"));
    }

    @Test
    void readerConstructor_throwsIllegalArgumentException_whenIdIsZero() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Reader("Kebodel", "Kenne", 0));
        assertTrue(exception.getMessage().contains("id"));
    }

    @Test
    void setFirstName_throwsIllegalArgumentException_whenContainsSemicolon() {
        Reader kebodel = new Reader("Kebodel", "Bauer", 8);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> kebodel.setFirstName("Kebo;Kenne"));
        assertTrue(exception.getMessage().contains("firstName"));
        assertTrue(exception.getMessage().contains(";"));
        assertEquals("Kebodel", kebodel.getFirstName());
    }

    @Test
    void setLastName_throwsIllegalArgumentException_whenValueIsBlank() {
        Reader kebodel = new Reader("Kebodel", "Bauer", 8);

        IllegalArgumentException expect = assertThrows(IllegalArgumentException.class, () -> kebodel.setLastName(""));
        assertTrue(expect.getMessage().contains("lastName"));
        assertEquals("Bauer" , kebodel.getLastName());
    }
}
