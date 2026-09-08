import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Document implements Borrowable {
    private String title;
    private int publicationYear;
    private int numberOfCopies;
    private List<Reader> borrowedBy= new ArrayList<>();

    public Document (String title, int publicationYear, int numberOfCopies) {
        this.title= title;
        this.publicationYear= publicationYear;
        this.numberOfCopies= numberOfCopies;
    }

    public void showDocumentInfos() {
        System.out.println("Title  :  " + title);
        System.out.println("Publication year  :  " + publicationYear);
        System.out.println("Number of copies : " + numberOfCopies);
        System.out.println("Borrowed by : " );
        borrowedBy.forEach(reader -> System.out.println("\t" + reader.getFirstName() + "  " + reader.getLastName()) );
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public void setNumberOfCopies(int numberOfCopies) {
        this.numberOfCopies= numberOfCopies;
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

    public int getAvailableCopies() {
        return this.numberOfCopies - borrowedBy.size();
    }

    public boolean hasAllCopiesAvailable() {
        return this.numberOfCopies == this.getAvailableCopies();
    }

    public abstract String getType();

    @Override
    public boolean borrow(Reader borrower) {
        if (this.hasAlreadyBorrowed(borrower)) {
            System.out.println("Already borrowed");
            return false;
        }
        if ( this.isAvailable()) {
            borrowedBy.add(borrower);
            return true;
        }
        return false;
    }

    @Override
    public boolean returnDocument(Reader borrower) {
        return borrowedBy.remove(borrower);
    }

    @Override
    public boolean isAvailable() {
        return this.getAvailableCopies() > 0;
    }

    protected boolean hasAlreadyBorrowed(Reader borrower) {
        return borrowedBy.contains(borrower);
    }

   /* public void borrow(Reader borrower) {
        if ( borrowedBy.contains(borrower) ) {
            System.out.println("This user has already borrow this document");
            return;
        }
        if (this.getAvailableCopies() > 0) {
            borrowedBy.add(borrower);
            System.out.println("Borrowed successfully");
        } else {
            System.out.println("All copies of this Document have been borrowed. Please check back later");
        }
    }

    public boolean returnDocument(Reader borrower) {
        return borrowedBy.remove(borrower);
    }*/

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

}
