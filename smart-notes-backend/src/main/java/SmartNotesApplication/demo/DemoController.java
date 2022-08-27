package SmartNotesApplication.demo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lowagie.text.DocumentException;

import SmartNotesApplication.demo.Note.Note;
import SmartNotesApplication.demo.Note.NoteService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class DemoController {
    
    @Autowired
    private NoteService service;

    @PostMapping("/note/add")
    public ResponseEntity<String> create(@RequestBody Note note){
        try{
            service.save(note);
            return new ResponseEntity<String>("Note added",HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/note/update/{id}")
    public ResponseEntity<String> update(@PathVariable Integer id, @RequestBody Note note){
        try{
            service.update(id, note.getTitle(), note.getNotes());
            return new ResponseEntity<String>("Note updated",HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/note/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id){
        try{
            service.deleteById(id);
            return new ResponseEntity<String>("Note deleted", HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/note/show")
    public ResponseEntity<List<Note>> show(){
        try{
            List<Note> res = service.listAll();
            return new ResponseEntity<List<Note>>(res, HttpStatus.OK);
        } catch(NoSuchElementException e){
            return new ResponseEntity<List<Note>>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/note/pdf/{id}")
    public ResponseEntity<String> exportToPdf(HttpServletResponse response, @PathVariable Integer id) throws DocumentException, IOException{
        try{
            response.setContentType("application/pdf");
            Note curnote = service.getById(id);
                    
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date());
            
            String headerKey = "Content-Disposition";
            String title = curnote.getTitle();
            String headerValue = "attachment; filename="+title+"_"+currentDateTime+".pdf";
            response.setHeader(headerKey, headerValue);
    
            NotePdfExporter exporter = new NotePdfExporter(); 
            exporter.export(response, curnote);
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/note/share/mail/{id}/{mailId}")
    public ResponseEntity<String> sendEmail(@PathVariable("id") Integer id, @PathVariable("mailId") String mailId) throws AddressException, MessagingException, IOException {
        
        try{
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Note curnote = service.getById(id);
            NotePdfExporter exporter = new NotePdfExporter(); 
            exporter.export(outputStream, curnote);
            byte[] bytes = outputStream.toByteArray();

            DataSource dataSource = new ByteArrayDataSource(bytes, "application/pdf");
            MimeBodyPart pdfBodyPart = new MimeBodyPart();
            pdfBodyPart.setDataHandler(new DataHandler(dataSource));

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date());
            String name = curnote.getTitle()+"_"+currentDateTime+".pdf";
            pdfBodyPart.setFileName(name);

            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("smtp.gmail.com");
            mailSender.setPort(587);
            mailSender.setUsername("RonWonBot@gmail.com");
            mailSender.setPassword("evwsruhsolpeaibl");
            
            Properties properties = new Properties();
            properties.setProperty("mail.smtp.auth", "true");
            properties.setProperty("mail.smtp.starttls.enable", "true");
            mailSender.setJavaMailProperties(properties);
            String from = "RonWonBot@gmail.com";
            String to = mailId;
                
            MimeMessage message = mailSender.createMimeMessage();
            message.setSender(new InternetAddress(from));
            message.setSubject("Here's your note");
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));

            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(pdfBodyPart);
            message.setContent(multipart);

            mailSender.send(message);
            return new ResponseEntity<String>("Email Sent",HttpStatus.OK);
        } catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<String>("Email not sent",HttpStatus.NOT_FOUND);
        } 
    }
}
