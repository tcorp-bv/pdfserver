package com.tcorp.svg2pdf;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.fop.svg.PDFTranscoder;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDStream;
import spark.Request;

import java.io.*;
import java.util.Base64;

import static spark.Spark.*;

public class Server {
    public Server(String keyStoreLocation, String keyStorePassword , int port) {
        secure(keyStoreLocation, keyStorePassword, null, null);
        start(port);
    }
    public Server(int port){
        start(port);
    }
    private void start( int port){
        port(port);

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Request-Method", "*");
            response.header("Access-Control-Allow-Headers", "*");
            // Note: this may or may not be necessary in your particular application
        });

        post("/transcode", (req, res) -> {
            Transcoder transcoder = new PDFTranscoder();
            TranscoderInput transcoderInput = new TranscoderInput(req.raw().getInputStream());
            final CountingOutputStream os = new CountingOutputStream(
                    Base64.getEncoder().wrap(res.raw().getOutputStream()));
            TranscoderOutput transcoderOutput = new TranscoderOutput(os);
            transcoder.transcode(transcoderInput, transcoderOutput);
            res.raw().setContentLength(os.getCount());
            os.flush();
            os.close();
            res.header("content-type", "application/pdf");
            return res;
        });
        post("/transcode/GS1PALLET", (req, res) -> {
            //check params
            PDDocument labelpdf = getDocumentFromRequest(req);
            PDDocument document = PalletDrawer.draw(labelpdf, req.queryParams("sscc"), req.queryParams("delivery"), req.queryParams("palletnumber"), req.queryParams("date"));
            document.save("test.pdf");
            document.close();
            res.header("content-type", "application/pdf");
            return res;
        });
        get("/transcode/EAN13", (req, res) -> {
            if(!req.queryParams().contains("code"))
                throw new RuntimeException("Query params must contain code!");
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            Barcodes.loadEAN123(req.queryParams("code"), data);

            PDDocument document = PDFGenerator.getDocumentFromSvgInput(new ByteArrayInputStream(data.toByteArray()),
                    70,
                    30);
            document.save(Base64.getEncoder().wrap(res.raw().getOutputStream()));
//            Transcoder transcoder = new PDFTranscoder();
//            TranscoderInput transcoderInput = new TranscoderInput(new ByteArrayInputStream(data.toByteArray()));
//            final CountingOutputStream os = new CountingOutputStream(
//                    Base64.getEncoder().wrap(res.raw().getOutputStream()));
//            TranscoderOutput transcoderOutput = new TranscoderOutput(os);
//            transcoder.transcode(transcoderInput, transcoderOutput);
//            res.raw().setContentLength(os.getCount());
//            os.flush();
//            os.close();
            res.header("content-type", "application/pdf");
            return res;
        });
    }
    private static PDDocument getDocumentFromRequest(Request req) throws IOException, TranscoderException {
        PDStream pdStream = new PDStream(new COSStream());
        OutputStream pdos = pdStream.createOutputStream();

        Transcoder transcoder = new PDFTranscoder();
        InputStream inStream = req.raw().getInputStream();
        TranscoderInput transcoderInput = new TranscoderInput(inStream);
        TranscoderOutput transcoderOutput = new TranscoderOutput(pdos);
        transcoder.transcode(transcoderInput, transcoderOutput);
        pdos.close();

        return  PDDocument.load(pdStream.createInputStream());
    }
}
