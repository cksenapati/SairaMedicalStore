package com.example.android.sairamedicalstore.models;

import android.os.Environment;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Map;

import static com.example.android.sairamedicalstore.ui.order.OrderDetailsActivity.mCurrentOrder;

/**
 * Created by chandan on 28-03-2018.
 */

public class FirstPdf {
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.RED);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);

    private static Font small = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);

    private static Font xSmall = new Font(Font.FontFamily.TIMES_ROMAN, 8,
            Font.NORMAL);
    private static Font xSmallBold = new Font(Font.FontFamily.TIMES_ROMAN, 8,
            Font.BOLD);

    public static Document document;
    static Order  mCurrentOrder;

    public FirstPdf(Order order) {
        try {

            this.mCurrentOrder = order;
            File sairaFolder = new File(Environment.getExternalStorageDirectory() +
                    File.separator + "Saira");
            String invoicePdf = Environment.getExternalStorageDirectory().getAbsolutePath()  +
                    "/Saira/OrderInvoice_"+mCurrentOrder.getOrderId()+".pdf";

            boolean success = true;
            if (!sairaFolder.exists()) {
                success = sairaFolder.mkdirs();
            }

            if (success) {
                document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(invoicePdf));
                document.open();
                addMetaData();
                addTitlePage();
                document.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // iText allows to add metadata to the PDF which can be viewed in your Adobe
    // Reader
    // under File -> Properties
    private static void addMetaData() {
        document.addTitle("OrderInvoice_"+mCurrentOrder.getOrderId()+".pdf");
        document.addSubject("Order Invoice");
        document.addKeywords("SairaMedicalStore");
        document.addAuthor("System Generated");
        document.addCreator("System Generated");
    }

    private static void addTitlePage()
            throws DocumentException {

        Paragraph preface = new Paragraph();
        // We add one empty line
        addEmptyLine(preface, 1);
        // Lets write a big header
        preface.add(new Paragraph("Order Invoice", catFont));
        preface.add(new Paragraph(
                "Order Id: " + mCurrentOrder.getOrderId() +"  |  " + new Date(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                smallBold));

        addEmptyLine(preface, 2);
        preface.add(new Paragraph("Sold By: Saira Medical Store",smallBold));
        preface.add(new Paragraph("Shop No. 3, Rajmahal Building, Bapuji Nagar",small));
        preface.add(new Paragraph("Bhubaneswar, Odisha 751009",small));
        preface.add(new Paragraph("PAN: xx1234yy",small));
        preface.add(new Paragraph("GSTIN: 12345xxyyzz",small));

        addEmptyLine(preface, 1);
        preface.add(new Paragraph("Shipping Address: "+ mCurrentOrder.getOrderDeliveryAddress().getFullName(),smallBold));
        preface.add(new Paragraph( mCurrentOrder.getOrderDeliveryAddress().getAddress(),small));
        preface.add(new Paragraph(mCurrentOrder.getOrderDeliveryAddress().getLandmark(),small));
        preface.add(new Paragraph(mCurrentOrder.getOrderDeliveryAddress().getCity() + ", " +
                mCurrentOrder.getOrderDeliveryAddress().getPinCode(),small));
        preface.add(new Paragraph(mCurrentOrder.getOrderDeliveryAddress().getState(),small));

        addEmptyLine(preface, 1);
        preface.add(new Paragraph("Payment Details: "+ mCurrentOrder.getPaymentMethod(),smallBold));

        addEmptyLine(preface, 1);
        preface.add(createTable());

        document.add(preface);
    }

    private static PdfPTable createTable()
            throws BadElementException {
        PdfPTable table = new PdfPTable(3);

        // t.setBorderColor(BaseColor.GRAY);
        // t.setPadding(4);
        // t.setSpacing(4);
        // t.setBorderWidth(1);

        PdfPCell c1 = new PdfPCell(new Phrase("ITEM"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("QTY"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("PRICE"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        table.setHeaderRows(1);


        for (Map.Entry eachEntry :mCurrentOrder.getCart().getProductIdAndItemCount().entrySet()) {

            c1 = new PdfPCell(new Phrase(eachEntry.getKey().toString()));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);

            double pricePerProduct =  mCurrentOrder.getIndividualProductPricing().get(eachEntry.getKey().toString());
            int quantity = (int) eachEntry.getValue();

            c1 = new PdfPCell(new Phrase(String.valueOf(quantity)));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase(String.valueOf(pricePerProduct)));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);
        }


        c1 = new PdfPCell(new Phrase("Sub Total"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase(""));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase(mCurrentOrder.getOrderPricingDetails().get("Subtotal").toString()));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        double totalPayable = 0.0;
        totalPayable += mCurrentOrder.getOrderPricingDetails().get("Subtotal");
        for (Map.Entry eachEntry:mCurrentOrder.getOrderPricingDetails().entrySet() ) {
            if ((double)eachEntry.getValue() > 0.0 && !eachEntry.getKey().toString().equals("Subtotal")) {

                c1 = new PdfPCell(new Phrase(eachEntry.getKey().toString()));
                c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(c1);

                c1 = new PdfPCell(new Phrase(""));
                c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(c1);

                c1 = new PdfPCell(new Phrase(eachEntry.getValue().toString()));
                c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(c1);

                totalPayable += (double) eachEntry.getValue();
            }
        }


        c1 = new PdfPCell(new Phrase("Total Payable"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase(""));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase(String.valueOf(totalPayable)));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);


        return table;
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
}
