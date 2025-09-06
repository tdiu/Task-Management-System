package taskmanagement.DAO;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import taskmanagement.Model.AppUser;

import java.util.Optional;

@Repository
public interface AppUserRepository extends CrudRepository<AppUser, Integer> {
    Optional<AppUser> findAppUserByUsernameIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String email);
}
