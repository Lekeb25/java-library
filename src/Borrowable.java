public interface Borrowable {
    boolean borrow(Reader borrower);
    boolean returnDocument(Reader borrower);
    boolean isAvailable();
}
