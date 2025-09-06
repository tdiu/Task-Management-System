package taskmanagement.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import taskmanagement.DTO.TaskWithCommentsDTO;
import taskmanagement.DTO.TaskWithCommentsDTO;
import taskmanagement.Model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    @Query("SELECT new taskmanagement.DTO.TaskWithCommentsDTO(" +
            " CAST(t.id AS string), t.title, t.description, t.status, t.author.email, COALESCE(a.email, 'none') , COUNT(c)" +
            ") " +
            "FROM Task t LEFT JOIN t.comments c " +
            "LEFT JOIN t.assignee a " +
            "WHERE LOWER(t.author.username) = LOWER(:author) " +
            "GROUP BY t.id, t.title, t.description, t.status, t.author.email, COALESCE(a.email, '') " +
            "ORDER BY t.createdAt DESC")
    List<TaskWithCommentsDTO> findByAuthorCaseInsensitive(@Param("author") String username);

    @Query("SELECT new taskmanagement.DTO.TaskWithCommentsDTO(" +
            " CAST(t.id AS string), t.title, t.description, t.status, t.author.email, COALESCE(a.email, 'none'), COUNT(c)" +
            ") " +
            "FROM Task t LEFT JOIN t.comments c " +
            "LEFT JOIN t.assignee a " +
            "WHERE LOWER(t.author.email) = LOWER(:author) " +
            "AND LOWER(COALESCE(a.username, '')) = LOWER(:assignee) " +
            "GROUP BY t.id, t.title, t.description, t.status, t.author.email, COALESCE(a.email, '')  " +
            "ORDER BY t.createdAt DESC")
    List<TaskWithCommentsDTO> findByAuthorAndAssignee(@Param("author") String author, @Param("assignee") String assignee);

    @Query("SELECT new taskmanagement.DTO.TaskWithCommentsDTO(" +
            " CAST(t.id AS string), t.title, t.description, t.status, t.author.email, coalesce(a.email, 'none') , COUNT(c)" +
            ") " +
            "FROM Task t LEFT JOIN t.comments c " +
            "LEFT JOIN t.assignee a " +
            "WHERE LOWER(COALESCE(a.email, '')) = LOWER(:assignee) " +
            "GROUP BY t.id, t.title, t.description, t.status, t.author.email, COALESCE(a.email, '') " +
            "ORDER BY t.createdAt DESC")
    List<TaskWithCommentsDTO> findByAssignee(@Param("assignee") String assignee);

    @Query("SELECT new taskmanagement.DTO.TaskWithCommentsDTO(" +
            " CAST(t.id AS string), t.title, t.description, t.status, t.author.email, COALESCE(a.email, 'none') , COUNT(c)" +
            ") " +
            "FROM Task t LEFT JOIN t.comments c " +
            "LEFT JOIN t.assignee a " +
            "GROUP BY t.id, t.title, t.description, t.status, t.author.email, COALESCE(a.email, '') " +
            "ORDER BY t.createdAt DESC")
    List<TaskWithCommentsDTO> findAllTaskCommentCounts();

    Optional<Task> findById(int id);

}
