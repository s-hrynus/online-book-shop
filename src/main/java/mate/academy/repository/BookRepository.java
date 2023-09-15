package mate.academy.repository;

import java.util.List;
import java.util.Optional;
import mate.academy.model.Book;

public interface BookRepository {
    Book save(Book book);

    Optional<Book> findById(Long id);

    List<Book> findAll();
}
