package com.tcorp.svg2pdf;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.krysalis.barcode4j.output.BarcodeCanvasSetupException;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

public class LabelPDFGenerator {
    /**
     * writes the pdf base64 encoded to the outputstream!
     * @param os
     * @param code
     * @param delivery
     * @param palletnumber
     * @param date
     * @throws TransformerException
     * @throws BarcodeCanvasSetupException
     * @throws IOException
     * @throws TranscoderException
     */
    public static void getGS1PalletLabel(OutputStream os, String code, String delivery, String palletnumber, String date) throws TransformerException, BarcodeCanvasSetupException, IOException, TranscoderException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        Barcodes.loadGS1Pallet(code, data);
        PDDocument labelPDF = PDFGenerator.getDocumentFromSvgInput(new ByteArrayInputStream(data.toByteArray()),
                72, 36);
        PDDocument document = PalletDrawer.draw(labelPDF, code, delivery, palletnumber, date);
        document.save(Base64.getEncoder().wrap(os));
        document.close();
        labelPDF.close();
        os.close();
    }

    public static void getEAN13Label(OutputStream os, String code) throws TransformerException, BarcodeCanvasSetupException, IOException, TranscoderException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        Barcodes.loadEAN13(code, data);
        PDDocument document = PDFGenerator.getDocumentFromSvgInput(new ByteArrayInputStream(data.toByteArray()),
                70,
                30);
        document.save(Base64.getEncoder().wrap(os));
        document.close();
        os.close();
    }
}
