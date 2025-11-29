package com.silvestre.web_applicationv1.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.silvestre.web_applicationv1.entity.Payments;
import com.silvestre.web_applicationv1.entity.Quotation;
import com.silvestre.web_applicationv1.entity.QuotationLineItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class PdfContractService {

    @Value("${app.contracts.dir:./contracts}")
    private String contractsDir;

    public byte[] generateContractPdf(Quotation quotation, Payments payment) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            document.open();

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.BOLD);
            Paragraph title = new Paragraph("EVENT CONTRACT AGREEMENT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            // Company
            Font companyFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Paragraph company = new Paragraph("Silvestre's Exquisite Events & Style", companyFont);
            company.setAlignment(Element.ALIGN_CENTER);
            company.setSpacingAfter(20);
            document.add(company);

            // Contract Date
            Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Paragraph contractDate = new Paragraph("Contract Date: " + LocalDate.now(), dateFont);
            contractDate.setAlignment(Element.ALIGN_RIGHT);
            contractDate.setSpacingAfter(20);
            document.add(contractDate);

            // Client Information
            document.add(createSectionTitle("CLIENT INFORMATION"));
            document.add(createInfoLine("Client Name:", quotation.getCustomerName()));
            document.add(createInfoLine("Contact Number:", quotation.getContactNumber()));
            document.add(createInfoLine("Event Date:", quotation.getRequestedEventDate().toString()));
            document.add(createInfoLine("Event Type:", quotation.getEventType()));
            document.add(createInfoLine("Number of Guests:", quotation.getPax().toString()));
            document.add(createInfoLine("Venue:", getVenueName(quotation)));
            if (quotation.getCelebrants() != null && !quotation.getCelebrants().isEmpty()) {
                document.add(createInfoLine("Celebrants:", quotation.getCelebrants()));
            }
            document.add(new Paragraph(" "));

            // Services Section
            document.add(createSectionTitle("SERVICES PROVIDED"));

            PdfPTable servicesTable = new PdfPTable(3);
            servicesTable.setWidthPercentage(100);
            servicesTable.setSpacingBefore(10);
            servicesTable.setSpacingAfter(10);

            // Table header
            servicesTable.addCell(createHeaderCell("Description"));
            servicesTable.addCell(createHeaderCell("Quantity"));
            servicesTable.addCell(createHeaderCell("Price"));

            // Table rows
            if (quotation.getLineItems() != null) {
                for (QuotationLineItem item : quotation.getLineItems()) {
                    servicesTable.addCell(createCell(item.getDescription()));
                    servicesTable.addCell(createCell(String.valueOf(item.getQuantity())));
                    servicesTable.addCell(createCell("₱" + formatMoney(item.getPriceAtQuotation())));
                }
            }

            document.add(servicesTable);
            document.add(new Paragraph(" "));

            // Payment Summary
            document.add(createSectionTitle("PAYMENT TERMS"));

            BigDecimal totalAmount = quotation.getTotal();
            BigDecimal amountPaid = payment.getAmount();
            BigDecimal balanceDue = totalAmount.subtract(amountPaid);
            BigDecimal percentagePaid = amountPaid.divide(totalAmount, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            document.add(createInfoLine("Total Contract Amount:", "₱" + formatMoney(totalAmount)));
            document.add(createInfoLine("Amount Paid:", "₱" + formatMoney(amountPaid) + " (" + percentagePaid + "%)"));
            document.add(createInfoLine("Balance Due:", "₱" + formatMoney(balanceDue)));
            document.add(new Paragraph(" "));

            // Terms and Conditions
            document.add(createSectionTitle("TERMS AND CONDITIONS"));

            List<String> terms = Arrays.asList(
                    "1. A non-refundable deposit of " + percentagePaid + "% is required to secure your event date.",
                    "2. The remaining balance of ₱" + formatMoney(balanceDue) + " is due 30 days prior to the event date.",
                    "3. Any cancellation must be made in writing. Deposits are non-refundable.",
                    "4. Changes to event details or services may incur additional charges.",
                    "5. Final guest count must be confirmed 7 days before the event."
            );

            for (String term : terms) {
                document.add(new Paragraph(term));
            }

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // Signatures Section
            document.add(createSectionTitle("AGREEMENT AND SIGNATURES"));

            Font italicFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10);
            Paragraph agreement = new Paragraph(
                    "By signing below, both parties agree to the terms and conditions outlined in this contract.",
                    italicFont
            );
            agreement.setSpacingAfter(20);
            document.add(agreement);

            // Signature table
            PdfPTable signatureTable = new PdfPTable(2);
            signatureTable.setWidthPercentage(100);
            signatureTable.setSpacingBefore(20);

            // Client signature
            signatureTable.addCell(createSignatureCell(
                    "_________________________",
                    "Client Signature",
                    "Name: ",
                    "Date: "
            ));

            // Company signature
            signatureTable.addCell(createSignatureCell(
                    "_________________________",
                    "Silvestre's Exquisite Events",
                    "Authorized Representative",
                    "Date: "
            ));

            document.add(signatureTable);

            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate contract PDF", e);
        }

        return baos.toByteArray();
    }

    private Paragraph createSectionTitle(String title) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.BOLD);
        Paragraph paragraph = new Paragraph(title, font);
        paragraph.setSpacingAfter(5);
        paragraph.setSpacingBefore(10);
        return paragraph;
    }

    private Paragraph createInfoLine(String label, String value) {
        return new Paragraph(label + " " + value);
    }

    private PdfPCell createHeaderCell(String text) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        return cell;
    }

    private PdfPCell createCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(5);
        return cell;
    }

    private PdfPCell createSignatureCell(String signatureLine, String title, String nameLabel, String dateLabel) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPadding(5);

        Paragraph p1 = new Paragraph(signatureLine);
        p1.setAlignment(Element.ALIGN_CENTER);

        Paragraph p2 = new Paragraph(title);
        p2.setAlignment(Element.ALIGN_CENTER);
        p2.setFont(FontFactory.getFont(FontFactory.HELVETICA, 9));

        Paragraph p3 = new Paragraph(" ");

        Paragraph p4 = new Paragraph(nameLabel);
        p4.setFont(FontFactory.getFont(FontFactory.HELVETICA, 9));

        Paragraph p5 = new Paragraph(dateLabel);
        p5.setFont(FontFactory.getFont(FontFactory.HELVETICA, 9));

        cell.addElement(p1);
        cell.addElement(p2);
        cell.addElement(p3);
        cell.addElement(p4);
        cell.addElement(p5);

        return cell;
    }

    private String getVenueName(Quotation quotation) {
        if (quotation.getVenue() != null) {
            return quotation.getVenue().getName();
        }
        return quotation.getClientVenue() != null ? quotation.getClientVenue() : "Client's Venue";
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "0.00";
        return String.format("%,.2f", amount);
    }

    public String saveContractPdf(Long quotationId, byte[] pdfBytes) throws IOException {
        String fileName = "contract-" + quotationId + ".pdf";
        Path dirPath = Paths.get(contractsDir);
        Path filePath = dirPath.resolve(fileName);

        Files.createDirectories(dirPath);
        Files.write(filePath, pdfBytes);

        return filePath.toString();
    }
}