package seoulmate.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seoulmate.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
