package SmartNotesApplication.demo;

import java.awt.Color;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import SmartNotesApplication.demo.Note.Note;


public class NotePdfExporter {
    private void writeTableHeader(PdfPTable t1,PdfPTable t2){
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setPhrase(new Phrase("Title"));
        t1.addCell(cell);
        cell.setPhrase(new Phrase("Note"));
        t2.addCell(cell);
    }

    private void writeTableData(PdfPTable t1, PdfPTable t2, Note curNote){
        t1.addCell(curNote.getTitle());
        t2.addCell(curNote.getNotes());
    }

    protected void export(HttpServletResponse response, Note curNote) throws DocumentException, IOException{
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font font = FontFactory.getFont(FontFactory.COURIER_BOLD);
        font.setSize(18);
        font.setColor(Color.BLACK);

        Paragraph p = new Paragraph("My Note", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p);
        p = new Paragraph(" ",font);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p);

        PdfPTable table1 = new PdfPTable(1);
        PdfPTable table2 = new PdfPTable(1);

        table1.setWidthPercentage(100F);
        table2.setWidthPercentage(100F);
        
        writeTableHeader(table1,table2);
        writeTableData(table1,table2,curNote);

        document.add(table1);
        document.add(p);

        document.add(table2);
        document.add(p);

        document.close();
    }

    protected void export(ByteArrayOutputStream outputStream, Note curNote) throws DocumentException, IOException{
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);

        document.open();
        Font font = FontFactory.getFont(FontFactory.COURIER_BOLD);
        font.setSize(18);
        font.setColor(Color.BLACK);

        Paragraph p = new Paragraph("Receipt", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p);
        p = new Paragraph(" ",font);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p);

        PdfPTable table1 = new PdfPTable(1);
        PdfPTable table2 = new PdfPTable(1);

        table1.setWidthPercentage(100F);
        table2.setWidthPercentage(100F);
        
        writeTableHeader(table1,table2);
        writeTableData(table1,table2,curNote);

        document.add(table1);
        document.add(p);

        document.add(table2);
        document.add(p);

        document.close();
    }
}
