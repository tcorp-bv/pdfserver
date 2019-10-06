package com.tcorp.svg2pdf;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;

import java.awt.geom.AffineTransform;
import java.io.*;

public class PDFGenerator {
    private static final float POINTS_PER_MM = 2.8346457F;

    public static PDDocument getDocumentFromSvgInput(InputStream svgInput, float width, float height) throws IOException, TranscoderException {
        PDDocument toEmbed = getDocumentFromSvgInput(svgInput);
        PDPage embedPage = toEmbed.getPage(0);

        PDDocument result = new PDDocument();
        PDPage resultPage = new PDPage(new PDRectangle(width * POINTS_PER_MM, height * POINTS_PER_MM));
        result.addPage(resultPage);

        Drawer drawer = new Drawer(result, resultPage);
        drawer.drawPDFFullSize(toEmbed, embedPage);

        drawer.close();

        //consolidate this pdf so that it closes as one
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        result.save(bos);
        result.close();
        toEmbed.close();
        PDDocument consolidated = PDDocument.load(new ByteArrayInputStream(bos.toByteArray()));
        return consolidated;
    }
    public static PDDocument getDocumentFromSvgInput(InputStream svgInput) throws IOException, TranscoderException {
        PDStream pdStream = new PDStream(new COSStream());
        OutputStream pdos = pdStream.createOutputStream();

        Transcoder transcoder = new PDFTranscoder();
        TranscoderInput transcoderInput = new TranscoderInput(svgInput);
        TranscoderOutput transcoderOutput = new TranscoderOutput(pdos);
        transcoder.transcode(transcoderInput, transcoderOutput);
        pdos.close();
        PDDocument document = PDDocument.load(pdStream.createInputStream());
        return document;
    }
}
