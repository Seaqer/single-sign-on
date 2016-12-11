package interfaces.repository.hibernate;

import core.domain.authorization.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HibernateRoleRepository extends JpaRepository<Role, Long> {
}
