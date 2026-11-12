package model;

import exception.NoCopiesAvailableException;
import exception.ReaderAlreadyBorrowedDocumentException;
import exception.ReaderHasNotBorrowedDocumentException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Document implements Borrowable, Comparable<Document> {
    private String title;
    private int publicationYear;
    private int numberOfCopies;
    private List<Reader> borrowedBy= new ArrayList<>();

    public Document (String title, int publicationYear, int numberOfCopies) {
        this.title= Validation.requireText(title, "title");
        this.publicationYear= Validation.requireAtLeast(publicationYear, 1, "publicationYear");
        this.numberOfCopies= Validation.requireAtLeast(numberOfCopies, 1, "numberOfCopies");
    }

    public void setTitle(String title) {
        this.title = Validation.requireText(title, "title");
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = Validation.requireAtLeast(publicationYear, 1, "publicationYear");
    }

    public void setNumberOfCopies(int numberOfCopies) {
        this.numberOfCopies= Validation.requireAtLeast(numberOfCopies, 1, "numberOfCopies");
    }

    public String getTitle() {
        return this.title;
    }

    public int getPublicationYear() {
        return this.publicationYear;
    }

    public int getNumberOfCopies() {
        return this.numberOfCopies;
    }

    // ------------------------------- Behavior ----------------------------
    public int getAvailableCopies() {
        return this.numberOfCopies - borrowedBy.size();
    }

    public boolean hasAllCopiesAvailable() {
        return this.numberOfCopies == this.getAvailableCopies();
    }

    public List<Reader> getBorrowedBy() {
        return new ArrayList<Reader>(borrowedBy);
    }

    public void showDocumentInfos() {
        System.out.println("Title  :  " + title);
        System.out.println("Publication year  :  " + publicationYear);
        System.out.println("Number of copies : " + numberOfCopies);
        System.out.println("Borrowed by : " );
        borrowedBy.forEach(reader -> System.out.println("\t" + reader.getFirstName() + "  " + reader.getLastName()) );
    }

    // ------------------------------ Abstract methods -------------------
    public abstract String getType();
    public abstract String toFileLine();

    // ------------------------------- Overrides --------------------------
    @Override
    public void borrow(Reader borrower) {
        if (this.hasAlreadyBorrowed(borrower)) {
            throw new ReaderAlreadyBorrowedDocumentException("Reader " +  borrower.getFirstName() + " has already borrowed the document '" + this.getTitle() + "'." );
        }
        if ( ! this.isAvailable()) {
            throw  new NoCopiesAvailableException("All copies of the document " + this.getTitle() + " have been borrowed. Please check back later");
        }
        borrowedBy.add(borrower);
    }

    @Override
    public void returnDocument(Reader borrower) {

        if ( !borrowedBy.contains(borrower) ) {
            throw new ReaderHasNotBorrowedDocumentException("Reader " + borrower.getFirstName() + " has not borrowed the document '" + this.getTitle() + "'.");
        }
        borrowedBy.remove(borrower);
    }

    @Override
    public boolean isAvailable() {
        return this.getAvailableCopies() > 0;
    }

    /* Comparable = one natural order (title, then year); Comparator = multiple external sorts. */
    @Override
    public int compareTo( Document other ) {
        int byTitle = this.title.compareTo(other.title);
        if ( byTitle != 0 ) {
            return byTitle;
        }

        return Integer.compare(this.publicationYear, other.publicationYear);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj== null || getClass() != obj.getClass()) return false;

        Document document = (Document) obj;
        return publicationYear == document.publicationYear &&
                Objects.equals(title, document.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(publicationYear, title);
    }

    // ---------------------------------- Helpers or  Private methods ------------------------
    protected boolean hasAlreadyBorrowed(Reader borrower) {
        return borrowedBy.contains(borrower);
    }

}
