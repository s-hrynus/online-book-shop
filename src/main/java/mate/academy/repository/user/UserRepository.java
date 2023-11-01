package mate.academy.repository.user;

import java.util.Optional;
import mate.academy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @Query("FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :username")
    User getUserByUsername(String username);

    @Query("FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmail(String email);
}
