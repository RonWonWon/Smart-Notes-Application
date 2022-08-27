package SmartNotesApplication.demo.Note;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class NoteService {
    
    @Autowired
    private NoteRepository repo;

    public List<Note> listAll(){
        return repo.findAll();
    }

    public Note getById(int id){
        return repo.findById(id);
    }

    public void deleteById(int id){
        repo.deleteById(id);
    }

    public void update(int id, String title, String notes){
        repo.updateNote(id, title, notes);
    }

    public void save(Note note){
        repo.save(note);
    }
}
