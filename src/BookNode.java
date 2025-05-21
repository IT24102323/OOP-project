public class BookNode {
    private Book book;
    private BookNode next;
    private BookNode prev;


    public BookNode(Book book) {
        this.book = book;
        this.next = null;
        this.prev = null;
    }


    public Book getBook() {
        return book;
    }


    public void setBook(Book book) {
        this.book = book;
    }


    public BookNode getNext() {
        return next;
    }


    public void setNext(BookNode next) {
        this.next = next;
    }


    public BookNode getPrev() {
        return prev;
    }


    public void setPrev(BookNode prev) {
        this.prev = prev;
    }


    @Override
    public String toString() {
        return "BookNode{" +
                "book=" + book +
                '}';
    }
}