package com.carmate.service;

import com.carmate.entity.account.Account;
import com.carmate.entity.tripSheet.TripSheet;
import com.carmate.enums.LanguageEnum;
import com.carmate.repository.TripSheetRepository;
import com.carmate.security.util.AuthService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

    private final TripSheetRepository tripSheetRepository;
    private final EmailService emailService;
    private final DateTimeFormatter dateTimeFormatter;
    private final AuthService authService;

    @Autowired
    public PdfService(TripSheetRepository tripSheetRepository,
                      EmailService emailService,
                      AuthService authService) {
        this.tripSheetRepository = tripSheetRepository;
        this.authService = authService;
        this.emailService = emailService;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    }

    public void generateTripSheetPdf(List<Long> tripSheetsIDs) throws DocumentException {
        Account account = authService.getAccountByPrincipal();
        LanguageEnum language = account.getLanguage();

        String titleText = language.equals(LanguageEnum.BULGARIAN) ? "Пътни листове" : "Trip Sheets";
        String[] headers = language.equals(LanguageEnum.BULGARIAN) ?
                new String[]{"Имейл", "Кола", "Дата на тръгване", "Час на тръгване", "Място на тръгване",
                        "Причина за пътуване", "Дата на пристигане", "Час на пристигане", "Място на пристигане",
                        "Начален километраж", "Краен километраж"} :
                new String[]{"User Email", "Car Name", "Departure Date", "Departure Time", "Departure Location",
                        "Trip Reason", "Arrival Date", "Arrival Time", "Arrival Location", "Start Odometer", "End Odometer"};
        String generatedOnText = language.equals(LanguageEnum.BULGARIAN) ? "Генерирано на: " : "Generated on: ";

        List<TripSheet> tripSheets = tripSheetRepository.findAllById(tripSheetsIDs);

        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
        Paragraph title = new Paragraph(titleText, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20f);
        document.add(title);

        PdfPTable table = new PdfPTable(11);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        float[] columnWidths = {2f, 2f, 3f, 2f, 2f, 2f, 3f, 2f, 2f, 2f, 2f};
        table.setWidths(columnWidths);

        addTableHeader(table, headers);

        for (int i = 0; i < tripSheets.size(); i++) {
            addRow(table, tripSheets.get(i), i % 2 == 0);
        }

        document.add(table);

        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        Paragraph generatedOn = new Paragraph(generatedOnText + LocalDate.now().format(dateTimeFormatter), dateFont);
        generatedOn.setAlignment(Element.ALIGN_RIGHT);
        generatedOn.setSpacingBefore(10f);
        document.add(generatedOn);

        document.close();

        if (language.equals(LanguageEnum.BULGARIAN)) {
            emailService.sendEmailWithPdfAttachment(account.getEmail(), "Пътни листи", "Пътни листи", outputStream.toByteArray(), "trip_sheets_" + LocalDate.now().format(dateTimeFormatter) + ".pdf");
        } else {
            emailService.sendEmailWithPdfAttachment(account.getEmail(), "Trip sheets", "Trip sheets", outputStream.toByteArray(), "trip_sheets_" + LocalDate.now().format(dateTimeFormatter) + ".pdf");

        }
    }

    private void addTableHeader(PdfPTable table, String[] headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell();
            cell.setPhrase(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE)));
            cell.setBackgroundColor(new BaseColor(59, 89, 182));
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private void addRow(PdfPTable table, TripSheet tripSheet, boolean isEvenRow) {
        BaseColor rowColor = isEvenRow ? BaseColor.WHITE : new BaseColor(240, 240, 240);

        addCell(table, tripSheet.getCar().getAccount().getEmail(), rowColor);
        addCell(table, tripSheet.getCar().getName(), rowColor);
        addCell(table, tripSheet.getDepartureDate().format(dateTimeFormatter), rowColor);
        addCell(table, tripSheet.getDepartureTime().toString(), rowColor);
        addCell(table, tripSheet.getDepartureLocation(), rowColor);
        addCell(table, tripSheet.getTripReason(), rowColor);
        addCell(table, tripSheet.getArrivalDate().format(dateTimeFormatter), rowColor);
        addCell(table, tripSheet.getArrivalTime().toString(), rowColor);
        addCell(table, tripSheet.getArrivalLocation(), rowColor);
        addCell(table, tripSheet.getStartOdometer().toString(), rowColor);
        addCell(table, tripSheet.getEndOdometer().toString(), rowColor);
    }

    private void addCell(PdfPTable table, String content, BaseColor backgroundColor) {
        PdfPCell cell = new PdfPCell(new Phrase(content, FontFactory.getFont(FontFactory.HELVETICA, 10)));
        cell.setBackgroundColor(backgroundColor);
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
}