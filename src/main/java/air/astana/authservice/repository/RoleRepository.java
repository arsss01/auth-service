package air.astana.authservice.repository;

import air.astana.authservice.model.RoleCode;
import air.astana.authservice.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByCode(RoleCode roleCode);
}
