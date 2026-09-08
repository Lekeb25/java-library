

public class Main {

    public static void main( String[] args ) {

        Document book1= new Book("Steppenwolf", "Herman Hesse", 1927, 4 );
        Book book2= new Book("Peter Camenzind", "Herman Hesse", 1904, 5 );
        Book book3= new Book("Crime et Chatiment", "Fiodor Dostoïevski", 1867, 2 );

        Reader reader1= new Reader("Kebodel", "Bauer", 1);
        Reader reader2= new Reader("Nabi", "Kenne", 2);
        Reader reader3= new Reader("Ruth", "Kenne", 3);

        Library myLibrary= new Library();

        myLibrary.addDocument(book1);
        myLibrary.addDocument(book2);
        myLibrary.addDocument(book3);
        myLibrary.addDocument(book2); /*Try to add a book, that already exist in the library*/

        myLibrary.addReader(reader1);
        myLibrary.addReader(reader2);
        myLibrary.addReader(reader3);

        myLibrary.borrowDocument(reader1, book3);
        myLibrary.borrowDocument(reader1, book3); /*Try to borrow with the same reader the same book */
        myLibrary.borrowDocument(reader2, book3);
        book3.showDocumentInfos();
        myLibrary.borrowDocument(reader3, book3); /*All the copies of this book have been borrowed and I try to borrow it*/


    }
}