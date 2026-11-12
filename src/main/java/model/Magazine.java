package model;

import java.util.Objects;

public class Magazine extends Document {

    private int issueNumber;
    private String publisher;

    public Magazine(String title, int publicationYear, int numberOfCopies, int issueNumber, String publisher) {
        super(title, publicationYear, numberOfCopies);
        this.issueNumber= Validation.requireAtLeast(issueNumber, 1, "issueNumber");
        this.publisher= Validation.requireText(publisher, "publisher");
    }

    public void setIssueNumber(int issueNumber) {
        this.issueNumber= Validation.requireAtLeast(issueNumber, 1, "issueNumber");
    }

    public void setPublisher(String publisher) {
        this.publisher= Validation.requireText(publisher, "publisher");
    }

    public int getIssueNumber() {
        return this.issueNumber;
    }

    public String getPublisher() {
        return publisher;
    }

    @Override
    public void showDocumentInfos() {
        super.showDocumentInfos();
        System.out.println("Issue Number : " + this.issueNumber);
        System.out.println("Publisher : " + this.publisher);
    }

    @Override
    public String getType() {
        return "Magazine";
    }

    @Override
    public String toFileLine() {
        return getTitle() +  ";" + getPublicationYear() + ";" + getNumberOfCopies() + ";" + getIssueNumber() + ";" + getPublisher();
    }

    @Override
    public boolean equals(Object obj) {
        if ( !super.equals(obj) ) return false;

        Magazine magazine = (Magazine) obj;
        return issueNumber== magazine.issueNumber &&
                Objects.equals(publisher, magazine.publisher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), issueNumber, publisher);
    }
}
