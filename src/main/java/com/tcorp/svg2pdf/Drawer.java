package com.tcorp.svg2pdf;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Drawer {
    private PDDocument document;
    private PDPage page;
    private PDPageContentStream cos;
    private PDRectangle contentBox;
    private float height = 0;

    public Drawer(PDDocument document, PDPage page) throws IOException {
        this.document = document;
        this.page = page;
        this.cos = new PDPageContentStream(document, page);
        this.contentBox = page.getMediaBox();

    }

    public void drawKVLine(String key, String value, PDFont keyFont, PDFont valueFont, int fontSize) throws IOException{
        drawString(key, keyFont, fontSize);
        drawStringRight(value, valueFont,  fontSize, false);
    }

    public void drawString(String text, PDFont font, int fontSize) throws IOException {
        drawString(20, text, font, fontSize, true);
    }

    public void drawStringRight(String text, PDFont font, int fontSize, boolean newLine) throws IOException {
        float text_width = (font.getStringWidth(text) / 1000.0f) * fontSize;
        float offset = (contentBox.getWidth() - 40) - text_width + 20;
        drawString(offset, text, font, fontSize, newLine);
    }
    public void drawStringCenter(String text, PDFont font, int fontSize, boolean newLine) throws IOException {
        float text_width = (font.getStringWidth(text) / 1000.0f) * fontSize;
        float offset = (contentBox.getWidth() -40 - text_width)/2 + 20;
        drawString(offset, text, font, fontSize, newLine);
    }
    public void drawString(float offset, String text, PDFont font, int fontSize, boolean newLine) throws IOException {
        if (newLine)
            height += fontSize + 10;
        cos.beginText();
        cos.setFont(font, fontSize);
        cos.newLineAtOffset(offset, contentBox.getHeight() - height);
        cos.showText(text);
        cos.newLineAtOffset(-offset, contentBox.getHeight() - height);
        cos.endText();
    }

    public void drawPDFCenter(PDPage toDraw) throws IOException {
        drawPDF(toDraw, (contentBox.getWidth() -40 - toDraw.getMediaBox().getWidth())/2 + 20);
    }
    public void drawPDF(PDPage toDraw) throws IOException {
        drawPDF(toDraw, 0);
    }
    public void drawPDF(PDPage toDraw, float xOffset) throws IOException {
        PDDocument target = new PDDocument();
        target.addPage(toDraw);
        PDFormXObject xobject = importAsXObject(target, toDraw);
        AffineTransform transform = xobject.getMatrix().createAffineTransform();
        transform.translate(xOffset, contentBox.getHeight() - height - toDraw.getMediaBox().getHeight());
        xobject.setMatrix(transform);
        toDraw.getResources().add(xobject, "X");
        cos.drawForm(xobject);
        height += toDraw.getMediaBox().getHeight();
    }

    public void drawSpace(int amount) {
        height += amount;
    }

    public void drawBreak() throws IOException {
        height += 10;
        cos.setLineWidth(2);
        cos.moveTo(20, contentBox.getHeight() - height);
        cos.lineTo(contentBox.getWidth() - 20, contentBox.getHeight() - height);
        cos.closeAndStroke();
    }

    public void close() throws IOException {
        cos.close();
    }

    private PDFormXObject importAsXObject(PDDocument target, PDPage page) throws IOException
    {
        final InputStream is = page.getContents();
        if (is != null)
        {
            final PDFormXObject xobject = new PDFormXObject(target);

            OutputStream os = xobject.getStream().createOutputStream();
            try
            {
                IOUtils.copy(is, os);
            }
            finally
            {
                IOUtils.closeQuietly(is);
                IOUtils.closeQuietly(os);
            }

            xobject.setResources(page.getResources());
            xobject.setBBox(page.getCropBox());

            return xobject;
        }
        return null;
    }
}
