package mate.academy;

import java.math.BigDecimal;
import mate.academy.model.Book;
import mate.academy.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBootIntroApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootIntroApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book angelAndDevil = new Book();
            angelAndDevil.setAuthor("Dan Brown");
            angelAndDevil.setPrice(BigDecimal.valueOf(150));
            angelAndDevil.setIsbn("978-33-0");
            angelAndDevil.setTittle("Angel and Devil");
            angelAndDevil.setDescription("awesome plot");
            bookService.save(angelAndDevil);
            System.out.println(bookService.findAll());
        };
    }
}
