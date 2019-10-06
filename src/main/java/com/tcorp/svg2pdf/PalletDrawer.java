package com.tcorp.svg2pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;

public class PalletDrawer {

    private static final PDFont FONT_PLAIN = PDType1Font.HELVETICA;;
    private static final PDFont FONT_BOLD = PDType1Font.HELVETICA_BOLD;

    public static PDDocument draw(PDDocument labelpdf, String sscc, String delivery, String palletnumber, String date) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A5);
        document.addPage(page);


        Drawer drawer = new Drawer(document, page);
        drawer.drawSpace(10);
        drawer.drawStringCenter("TCorp BV", FONT_PLAIN, 24, true);
        drawer.drawBreak();
        drawer.drawSpace(24);
        drawer.drawKVLine("SSCC", sscc, FONT_BOLD, FONT_PLAIN, 12);
        drawer.drawKVLine("Delivery", delivery, FONT_BOLD, FONT_PLAIN, 12);
        drawer.drawKVLine("Pallet number", palletnumber, FONT_BOLD, FONT_PLAIN, 12);
        drawer.drawKVLine("Date", date, FONT_BOLD, FONT_PLAIN, 12);
        drawer.drawSpace(24);
        drawer.drawBreak();
        drawer.drawSpace(24);
        drawer.drawPDFCenter(labelpdf, labelpdf.getPage(0));
        drawer.drawSpace(6);
        drawer.drawBreak();

        drawer.close();

        return document;
    }
}
