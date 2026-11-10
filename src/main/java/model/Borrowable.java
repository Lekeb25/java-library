package model;

public interface Borrowable {
    void borrow(Reader borrower);
    void returnDocument(Reader borrower);
    boolean isAvailable();
}
