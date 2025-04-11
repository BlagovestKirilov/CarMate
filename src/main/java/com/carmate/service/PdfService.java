package com.carmate.service;

import com.carmate.entity.account.Account;
import com.carmate.entity.tripSheet.TripSheet;
import com.carmate.enums.LanguageEnum;
import com.carmate.repository.TripSheetRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

    private final TripSheetRepository tripSheetRepository;
    private final EmailService emailService;
    private final DateTimeFormatter dateTimeFormatter;
    private final AuthService authService;

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfService.class);

    @Autowired
    public PdfService(TripSheetRepository tripSheetRepository,
                      EmailService emailService,
                      AuthService authService) {
        this.tripSheetRepository = tripSheetRepository;
        this.authService = authService;
        this.emailService = emailService;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    }

    public void generateTripSheetPdf(List<Long> tripSheetsIDs) throws DocumentException, IOException {
        Account account = authService.getAccountByPrincipal();
        LanguageEnum language = account.getLanguage();

        // Define text based on language
        String titleText = language.equals(LanguageEnum.BULGARIAN) ? "Пътни листове" : "Trip Sheets";
        String[] headers = language.equals(LanguageEnum.BULGARIAN) ?
                new String[]{"Имейл", "Рег. номер", "Дата на тръгване", "Час на тръгване", "Място на тръгване",
                        "Причина за пътуване", "Дата на пристигане", "Час на пристигане", "Място на пристигане",
                        "Начален километраж", "Краен километраж"} :
                new String[]{"User Email", "Pl. number", "Departure Date", "Departure Time", "Departure Location",
                        "Trip Reason", "Arrival Date", "Arrival Time", "Arrival Location", "Start Odometer", "End Odometer"};
        String generatedOnText = language.equals(LanguageEnum.BULGARIAN) ? "Генерирано на: " : "Generated on: ";

        List<TripSheet> tripSheets = tripSheetRepository.findAllById(tripSheetsIDs);

        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);

        document.open();

        // Load a font that supports Cyrillic characters
        BaseFont baseFont = BaseFont.createFont("font/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font titleFont = new Font(baseFont, 18, Font.BOLD);
        Font headerFont = new Font(baseFont, 12, Font.BOLD);
        Font cellFont = new Font(baseFont, 10, Font.NORMAL);
        Font dateFont = new Font(baseFont, 10, Font.NORMAL);

        // Add title
        Paragraph title = new Paragraph(titleText, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20f);
        document.add(title);

        // Create table
        PdfPTable table = new PdfPTable(11);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        float[] columnWidths = {3f, 2f, 2f, 2f, 2f, 2f, 2.3f, 2.3f, 2.3f, 2.5f, 2.5f};
        table.setWidths(columnWidths);

        // Add table headers
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Add table rows
        for (int i = 0; i < tripSheets.size(); i++) {
            TripSheet tripSheet = tripSheets.get(i);
            addRow(table, tripSheet, i % 2 == 0, cellFont);
        }

        document.add(table);

        // Add generated date
        Paragraph generatedOn = new Paragraph(generatedOnText + LocalDate.now().format(dateTimeFormatter), dateFont);
        generatedOn.setAlignment(Element.ALIGN_RIGHT);
        generatedOn.setSpacingBefore(10f);
        document.add(generatedOn);

        document.close();

        // Send email with PDF attachment
        String emailSubject = language.equals(LanguageEnum.BULGARIAN) ? "Пътни листи" : "Trip sheets";
        String emailBody = language.equals(LanguageEnum.BULGARIAN) ? "Пътни листи" : "Trip sheets";
        String fileName = "trip_sheets_" + LocalDate.now().format(dateTimeFormatter) + ".pdf";

        emailService.sendEmailWithPdfAttachment(account.getEmail(), emailSubject, emailBody, outputStream.toByteArray(), fileName);
        LOGGER.info("Email sent to: {}", account.getEmail());
    }

    private void addRow(PdfPTable table, TripSheet tripSheet, boolean isEvenRow, Font cellFont) {
        BaseColor rowColor = isEvenRow ? BaseColor.WHITE : new BaseColor(240, 240, 240);

        addCell(table, tripSheet.getVehicle().getAccount().getEmail(), rowColor, cellFont);
        addCell(table, tripSheet.getVehicle().getPlateNumber(), rowColor, cellFont);
        addCell(table, tripSheet.getDepartureDate().format(dateTimeFormatter), rowColor, cellFont);
        addCell(table, tripSheet.getDepartureTime().toString(), rowColor, cellFont);
        addCell(table, tripSheet.getDepartureLocation(), rowColor, cellFont);
        addCell(table, tripSheet.getTripReason(), rowColor, cellFont);
        addCell(table, tripSheet.getArrivalDate().format(dateTimeFormatter), rowColor, cellFont);
        addCell(table, tripSheet.getArrivalTime().toString(), rowColor, cellFont);
        addCell(table, tripSheet.getArrivalLocation(), rowColor, cellFont);
        addCell(table, String.valueOf(tripSheet.getStartOdometer()), rowColor, cellFont);
        addCell(table, String.valueOf(tripSheet.getEndOdometer()), rowColor, cellFont);
    }

    private void addCell(PdfPTable table, String text, BaseColor backgroundColor, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
}