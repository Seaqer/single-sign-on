package interfaces.repository.hibernate;

import core.domain.authorization.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HibernateUserRepository extends JpaRepository<User, Long> {
}
