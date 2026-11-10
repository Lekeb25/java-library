package persistence;

import exception.InvalidSaveFileException;
import library.Library;
import model.Book;
import model.Document;
import model.Magazine;
import model.Reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LibraryFileStorage {

    private final Path savePath;

    private static final String READER = "READER";
    private static final String BOOK = "BOOK";
    private static  final String MAGAZINE = "MAGAZINE";
    private static final String BORROW = "BORROW";
    private static final String SEPARATOR = ";";

    public LibraryFileStorage( Path savePath) {
        this.savePath = savePath;
    }

    public void save( Library library ) throws IOException {
        List<String> lines = new ArrayList<>();

        for ( Reader reader : library.getReaders() ) {
            lines.add(READER + SEPARATOR + reader.toFileLine());
        }

        for ( Document document : library.getDocuments() ) {
            if ( document instanceof Book ) {
                lines.add(BOOK + SEPARATOR + document.toFileLine());
            }
            if ( document instanceof Magazine ) {
                lines.add(MAGAZINE + SEPARATOR + document.toFileLine());
            }

            for ( Reader borrower : document.getBorrowedBy() ) {
                lines.add(BORROW + SEPARATOR + document.getTitle() + SEPARATOR + document.getPublicationYear()
                            + SEPARATOR + borrower.getId());
            }
        }

        Files.write(savePath, lines);
    }

    public void load( Library library ) throws IOException {

        if ( !Files.exists(savePath) ) {
            return;
        }

        List<String> readLines = Files.readAllLines(savePath);

        for ( int i = 0; i < readLines.size(); i++ ) {

            String line = readLines.get(i);
            int lineNumber = i + 1;

            if ( line.isBlank() || line.startsWith(BORROW + SEPARATOR) ) {
                continue;
            }

            try {
                String [] fields = line.split(SEPARATOR);
                String type = fields[0];
                switch (type) {
                    case READER -> {
                        if ( fields.length != 4) {
                            throw  new IllegalArgumentException(READER + " line expects 4 fields but found " + fields.length );
                        }

                        String firstName = fields[1];
                        String lastName = fields[2];
                        int id = Integer.parseInt(fields[3]);
                        library.addReader( new Reader(firstName, lastName, id));
                    }

                    case BOOK -> {
                        if ( fields.length != 5) {
                            throw  new IllegalArgumentException(BOOK + " line expects 5 fields but found " + fields.length );
                        }

                        String title = fields[1];
                        String author = fields[2];
                        int publicationYear = Integer.parseInt(fields[3]);
                        int numberOfCopies = Integer.parseInt(fields[4]);
                        library.addDocument( new Book(title, author, publicationYear, numberOfCopies) );
                    }

                    case MAGAZINE -> {
                        if ( fields.length != 6) {
                            throw  new IllegalArgumentException(MAGAZINE + " line expects 6 fields but found " + fields.length );
                        }

                        String title = fields[1];
                        int publicationYear = Integer.parseInt(fields[2]);
                        int numberOfCopies = Integer.parseInt(fields[3]);
                        int issueNumber = Integer.parseInt(fields[4]);
                        String publisher = fields[5];
                        library.addDocument( new Magazine(title, publicationYear, numberOfCopies, issueNumber, publisher));
                    }

                    default -> throw new IllegalArgumentException( "Unknown line type: " + type);
                }
            } catch ( RuntimeException e) {
                throw new InvalidSaveFileException("Invalid save file, line " + lineNumber + ": " + e.getMessage(), e);
            }

        }

        for ( int i = 0; i < readLines.size(); i++ ) {
            String line = readLines.get(i);
            int lineNumber = i + 1;

            if ( ! line.startsWith(BORROW +  SEPARATOR) ) {
                continue;
            }

            try {
                String [] fields = line.split(SEPARATOR);

                if ( fields.length != 4) {
                    throw  new IllegalArgumentException(BORROW + " line expects 4 fields but found " + fields.length );
                }

                String documentTitle = fields[1];
                int documentYear = Integer.parseInt(fields[2]);
                int readerId = Integer.parseInt(fields[3]);

                Reader reader = library.findReaderById(readerId);
                Document document = library.findDocumentByTitleAndYear(documentTitle, documentYear);
                document.borrow(reader);
            } catch (RuntimeException e ) {
                throw new InvalidSaveFileException("Invalid save file, line " + lineNumber + ": " + e.getMessage(), e);
            }
        }

    }
}
