package interfaces.repository.hibernate;


import core.domain.authorization.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HibernateOperationRepository extends JpaRepository<Operation, Long> {
}
