package viewmodel;

import androidx.lifecycle.ViewModel;
import model.Book;

public class AddActivityViewModel extends ViewModel {

    private Book book;

    public AddActivityViewModel() {
        book=new Book();
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

}
