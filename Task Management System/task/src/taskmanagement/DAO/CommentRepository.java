package taskmanagement.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import taskmanagement.Model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByTaskIdOrderByCreatedAtDesc(int id);

    Optional<Comment> findByTaskIdAndTextAndAuthor(int taskId, String text, String author);

}
