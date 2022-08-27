package SmartNotesApplication.demo.Note;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoteRepository extends JpaRepository<Note, Integer> {
    Note save(Note note);
    void deleteById(int id);
    Note findById(int id);
    
    @Transactional
    @Modifying
    @Query("update Note u set u.title = :title, u.notes = :notes where u.id = :id")
    int updateNote(@Param("id") int id, @Param("title") String title, @Param("notes") String notes);
}
