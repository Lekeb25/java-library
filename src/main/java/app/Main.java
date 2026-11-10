package app;

import exception.*;
import library.Library;
import model.Book;
import model.Document;
import model.Magazine;
import model.Reader;
import persistence.LibraryFileStorage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import library.DocumentCriteria;
import java.util.Comparator;
import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        Path filePath = Path.of("library-data.txt");
        LibraryFileStorage libraryFileStorage = new LibraryFileStorage(filePath);
        Library myLibrary = new Library();

        try {
            libraryFileStorage.load(myLibrary);
        } catch (IOException e) {
            System.out.println("Error: could not load saved data: " + e.getMessage());
        }

        boolean libraryIsEmpty = myLibrary.getDocuments().isEmpty() && myLibrary.getReaders().isEmpty();

        if ( libraryIsEmpty ) {
            System.out.println("\n\n### No saved data found. Setting up the library for the first time. ###\n");
            initializeLibrary(myLibrary);
        } else {
            System.out.println("\n\n### Existing library data loaded successfully. ###\n");
        }

        Scanner scanner = new Scanner(System.in);

        runMenu(myLibrary, scanner);

        scanner.close();


        try {
            libraryFileStorage.save(myLibrary);
            System.out.println("\n\nOK: library data saved.");
        } catch ( IOException e) {
            System.out.println("Error: could not save data: " + e.getMessage());
        }


    }



    private static void initializeLibrary( Library myLibrary) {

        Document steppenwolf = new Book("Steppenwolf", "Herman Hesse", 1927, 4);
        Book peterCamenzind = new Book("Peter Camenzind", "Herman Hesse", 1904, 5);
        Book crimeAndPunishment = new Book("Crime and Punishment", "Fyodor Dostoevsky", 1867, 2);
        Magazine natGeo = new Magazine("National Geographic", 2024, 3, 245, "NatGeo Society");


        Reader kebodel = new Reader("Kebodel", "Bauer", 1);
        Reader nabi = new Reader("Nabi", "Kenne", 2);
        Reader ruth = new Reader("Ruth", "Kenne", 3);


        addDocument(myLibrary, steppenwolf);
        addDocument(myLibrary, peterCamenzind);
        addDocument(myLibrary, crimeAndPunishment);
        addDocument(myLibrary, natGeo);
        addDocument(myLibrary, peterCamenzind);  /*Try to add a document already present in the library*/


        addReader(myLibrary, kebodel);
        addReader(myLibrary, nabi);
        addReader(myLibrary, ruth);


        System.out.println("### Borrowing 'Crime and Punishment' (2 copies) ###");
        borrowDocument(myLibrary, 1, crimeAndPunishment);
        borrowDocument(myLibrary, 2, crimeAndPunishment);


        System.out.println("\n### All copies of this book are borrowed, trying to borrow it anyway ###");
        borrowDocument(myLibrary, 3, crimeAndPunishment);

        System.out.println("\n### Returning one copy, then borrowing it again ###");
        returnDocument(myLibrary, 1, crimeAndPunishment);
        borrowDocument(myLibrary, 3, crimeAndPunishment);

        System.out.println("\n### Returning a copy, then trying to borrow the same book with the same reader again ###");
        returnDocument(myLibrary, 3, crimeAndPunishment);
        borrowDocument(myLibrary, 3, crimeAndPunishment);

        /* System.out.println("\n### Magazine details ###");
        natGeo.showDocumentInfos();*/

    }


    // --- Library content display, as fixed-width ASCII tables ---

    private static void showLibraryContent(Library myLibrary) {
        showReadersTable(myLibrary);
        System.out.println();
        showBooksTable(myLibrary);
        System.out.println();
        showMagazinesTable(myLibrary);
    }

    private static void showReadersTable(Library library) {
        String[] headers = {"ID", "First name", "Last name"};
        int[] columnWidths = {4, 15, 15};

        List<String[]> rows = new ArrayList<>();
        for (Reader reader : library.getReaders()) {
            rows.add(new String[]{
                    String.valueOf(reader.getId()),
                    reader.getFirstName(),
                    reader.getLastName()
            });
        }

        System.out.println("Readers:");
        printTable(headers, columnWidths, rows);
    }

    private static void showBooksTable(Library library) {
        String[] headers = {"Title", "Author", "Year", "Copies", "Borrowers"};
        int[] columnWidths = {20, 18, 6, 8, 25};

        /*for (Document document : library.getDocuments()) {
            if (document instanceof Book book) {
                rows.add(new String[]{
                        book.getTitle(),
                        book.getAuthor(),
                        String.valueOf(book.getPublicationYear()),
                        String.valueOf(book.getNumberOfCopies()),
                        borrowersToString(book)
                });
            }
        }*/

        List<String[]> rows = library.getBooks().stream().map( book -> {
            return new String[]{
                    book.getTitle(),
                    book.getAuthor(),
                    String.valueOf(book.getPublicationYear()),
                    String.valueOf(book.getNumberOfCopies()),
                    borrowersToString(book)
            };
        }).toList();

        System.out.println("Books:");
        printTable(headers, columnWidths, rows);
    }

    private static void showMagazinesTable(Library library) {
        String[] headers = {"Title", "Year", "Copies", "Issue", "Publisher", "Borrowers"};
        int[] columnWidths = {20, 6, 8, 6, 15, 25};

        List<String[]> rows = library.getMagazines().stream()
                        .map( magazine -> new String[] {
                                magazine.getTitle(),
                                String.valueOf(magazine.getPublicationYear()),
                                String.valueOf(magazine.getNumberOfCopies()),
                                String.valueOf(magazine.getIssueNumber()),
                                magazine.getPublisher(),
                                borrowersToString(magazine)
                        }).toList();

       /* for (Document document : library.getDocuments()) {
            if (document instanceof Magazine magazine) {
                rows.add(new String[]{
                        magazine.getTitle(),
                        String.valueOf(magazine.getPublicationYear()),
                        String.valueOf(magazine.getNumberOfCopies()),
                        String.valueOf(magazine.getIssueNumber()),
                        magazine.getPublisher(),
                        borrowersToString(magazine)
                });
            }
        }*/

        System.out.println("Magazines:");
        printTable(headers, columnWidths, rows);
    }

    private static void printDocumentList(List<Document> documents) {
        if (documents.isEmpty()) {
            System.out.println("No document found.");
            return;
        }
        documents.forEach(document -> System.out.println(
                "- " + document.getTitle()
                        + " (" + document.getPublicationYear() + ")"
                        + " - " + document.getType()
                        + " - " + document.getAvailableCopies() + "/" + document.getNumberOfCopies() + " available"));
    }

    // Builds a single "Firstname Lastname, Firstname Lastname" string from a document's borrowers.
    private static String borrowersToString(Document document) {
        List<Reader> borrowers = document.getBorrowedBy();
        if (borrowers.isEmpty()) {
            return "-";
        }

        /*for (int i = 0; i < borrowers.size(); i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(borrowers.get(i).getId());
        }*/
        return borrowers.stream().map( borrower -> String.valueOf(borrower.getId())).collect(Collectors.joining(", "));
    }

    // --- Generic fixed-width ASCII table printer: knows nothing about Reader/Book/Magazine ---

    private static void printTable(String[] headers, int[] columnWidths, List<String[]> rows) {
        printTableBorder(columnWidths);
        printTableRow(headers, columnWidths);
        printTableBorder(columnWidths);
        for (String[] row : rows) {
            printTableRow(row, columnWidths);
        }
        printTableBorder(columnWidths);
    }

    private static void printTableBorder(int[] columnWidths) {
        StringBuilder border = new StringBuilder("+");
        for (int width : columnWidths) {
            border.append("-".repeat(width + 2)).append("+");
        }
        System.out.println(border);
    }

    private static void printTableRow(String[] values, int[] columnWidths) {
        StringBuilder row = new StringBuilder("|");
        for (int i = 0; i < values.length; i++) {
            row.append(" ").append(String.format("%-" + columnWidths[i] + "s", values[i])).append(" |");
        }
        System.out.println(row);
    }


    // Utility methods: call a library operation and report success or failure to the console.

    private static void addDocument(Library library, Document document) {
        try {
            library.addDocument(document);
            System.out.println("OK: \"" + document.getTitle() + "\" added to the library.");
        } catch (DocumentAlreadyExistsException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void removeDocument(Library library, String title, int year) {
        try {
            Document document = library.findDocumentByTitleAndYear(title, year);
            library.removeDocument(document);
            System.out.println("OK: "+ title +  " removed from the library.");
        } catch ( DocumentCannotBeRemovedException | DocumentNotFoundException e ) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void addReader(Library library, Reader reader) {
        try {
            library.addReader(reader);
            System.out.println("OK: " + reader.getFirstName() + " " + reader.getLastName() + " registered in the library.");
        } catch (ReaderAlreadyExistsException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static  void removeReader( Library library, int readerId) {
        try {
            library.removeReader(readerId);
            System.out.println("OK: " +  readerId + " is successfully removed in a library");
        } catch ( ReaderNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void borrowDocument(Library library, int readerId, Document document) {
        try {
            library.borrowDocument(readerId, document);
            System.out.println("OK: " + readerId + " borrowed \"" + document.getTitle() + "\".");
        } catch (ReaderNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (DocumentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (ReaderAlreadyBorrowedDocumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NoCopiesAvailableException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void returnDocument(Library library, int readerId, Document document) {
        try {
            library.returnBorrowedDocument(readerId, document);
            System.out.println("OK: " + readerId + " returned \"" + document.getTitle() + "\".");
        } catch (ReaderNotFoundException | DocumentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (ReaderHasNotBorrowedDocumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void printMenu() {
        System.out.println("\n================ MENU =====================");
        System.out.println("1. Show library");
        System.out.println("2. Add a reader");
        System.out.println("3. Remove a reader");
        System.out.println("4. Add a document");
        System.out.println("5. Remove a document");
        System.out.println("6. Borrow a document");
        System.out.println("7. Return a document");
        System.out.println("8. Search documents");
        System.out.println("9. Show sorted documents");
        System.out.println("10. Library statistics");
        System.out.println("11. Quit");
        System.out.println("12. Print a menu");
    }

    private static void searchDocumentsMenu(Library library, Scanner scanner) {
        System.out.println("Search documents \n");
        System.out.println("1. By title");
        System.out.println("2. By year");
        System.out.println("3. By author");
        System.out.println("4. Available only");
        System.out.print("Your choice:  ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        Predicate<Document> criteria;
        switch (choice) {
            case 1 -> {
                System.out.print("Title:  ");
                criteria = DocumentCriteria.withTitle(scanner.nextLine());
            }
            case 2 -> {
                System.out.print("Publication year:  ");
                int year = scanner.nextInt();
                scanner.nextLine();
                criteria = DocumentCriteria.publishedInYear(year);
            }
            case 3 -> {
                System.out.print("Author:  ");
                criteria = DocumentCriteria.byAuthor(scanner.nextLine());
            }
            case 4 -> criteria = DocumentCriteria.isAvailable();
            default -> {
                System.out.println("Invalid choice");
                return;
            }
        }

        printDocumentList(library.searchDocuments(criteria));
    }

    private static void showSortedDocuments(Library library, Scanner scanner) {
        System.out.println("Show sorted documents \n");
        System.out.println("1. By title");
        System.out.println("2. By year (oldest first)");
        System.out.println("3. By year (newest first)");
        System.out.print("Your choice:  ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        List<Document> sorted;
        switch (choice) {
            case 1 -> sorted = library.getDocumentsSorted();
            case 2 -> sorted = library.getDocumentsSortedBy(
                    Comparator.comparingInt(Document::getPublicationYear));
            case 3 -> sorted = library.getDocumentsSortedBy(
                    Comparator.comparingInt(Document::getPublicationYear).reversed());
            default -> {
                System.out.println("Invalid choice");
                return;
            }
        }

        printDocumentList(sorted);
    }

    private static void showStatistics(Library library) {
        System.out.println("Library statistics \n");
        System.out.println("Documents:            " + library.countDocuments());
        System.out.println("Total copies:         " + library.getTotalCopies());
        System.out.println("Available documents:  " + library.countAvailableDocuments());
        System.out.println("Oldest publication:   " + yearToString(library.getOldestPublicationYear()));
        System.out.println("Newest publication:   " + yearToString(library.getNewestPublicationYear()));
    }

    private static String yearToString(OptionalInt year) {
        return year.isPresent() ? String.valueOf(year.getAsInt()) : "n/a";
    }

    private static void runMenu(Library library, Scanner scanner) {
        boolean running = true;
        printMenu();
        while ( running ) {

            System.out.print("Your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch ( choice ) {
                case 1 -> {
                    System.out.println();
                    System.out.println();
                    showLibraryContent(library);
                }case 2 -> {
                    System.out.println("Add a reader \n");
                    System.out.print("First name:  ");
                    String firstName = scanner.nextLine();
                    System.out.print("Last name:  ");
                    String lastName = scanner.nextLine();
                    System.out.print("Reader id:  ");
                    int id = scanner.nextInt();
                    scanner.nextLine();

                    addReader(library, new Reader(firstName, lastName, id));

                }case 3 -> {
                    System.out.println("Remove a reader \n");
                    System.out.print("Give the reader id :  ");
                    int id = scanner.nextInt();
                    scanner.nextLine();

                    removeReader(library, id);

                }case 4 -> {
                    System.out.println("Add a document \n");
                    System.out.print("Enter 1 for a book, or 2 for a magazine:  ");
                    int bookOrMagazine = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Title:  ");
                    String title= scanner.nextLine();
                    System.out.print("Publication year:  ");
                    int year = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Number of copies:  ");
                    int numberOfCopies = scanner.nextInt();
                    scanner.nextLine();

                    if (bookOrMagazine == 1) {
                        System.out.print("Author:  ");
                        String author = scanner.nextLine();

                        Book book = new Book(title,  author, year, numberOfCopies);
                        addDocument(library, book);
                    } else if (bookOrMagazine == 2) {
                        System.out.print("Issue number:  ");
                        int issueNumber = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Publisher:  ");
                        String publisher = scanner.nextLine();

                        Magazine magazine = new Magazine(title, year, numberOfCopies, issueNumber, publisher);
                        addDocument(library, magazine);
                    } else {
                        System.out.println("Invalid document type.");
                    }


                }case 5 -> {
                    System.out.println("Remove a document \n");
                    System.out.print("Title:  ");
                    String title = scanner.nextLine();
                    System.out.print("Publication year:  ");
                    int publicationYear = scanner.nextInt();
                    scanner.nextLine();

                    removeDocument(library, title, publicationYear);

                }case 6 -> {
                    System.out.println("Borrow a document \n");
                    System.out.print("reader id:  ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Title:  ");
                    String title = scanner.nextLine();
                    System.out.print("Publication year:  ");
                    int publicationYear = scanner.nextInt();
                    scanner.nextLine();

                    try {
                        Document document = library.findDocumentByTitleAndYear(title, publicationYear);
                        borrowDocument(library, id, document);
                    } catch ( DocumentNotFoundException e ) {
                        System.out.println("Error: " + e.getMessage());
                    }

                }case 7 -> {
                    System.out.println("Return a document \n");
                    System.out.print("reader id:  ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Title:  ");
                    String title = scanner.nextLine();
                    System.out.print("Publication year:  ");
                    int publicationYear = scanner.nextInt();
                    scanner.nextLine();

                    try {
                        Document document = library.findDocumentByTitleAndYear(title, publicationYear);
                        returnDocument(library, id, document);
                    } catch ( DocumentNotFoundException e ) {
                        System.out.println("Error: " + e.getMessage());
                    }

                } case 8 -> searchDocumentsMenu(library, scanner);

                case 9 -> showSortedDocuments(library, scanner);

                case 10 -> showStatistics(library);

                case 11 -> running = false;

                case 12 -> printMenu();

                default -> System.out.println("Invalid choice");
            }

        }
    }
}