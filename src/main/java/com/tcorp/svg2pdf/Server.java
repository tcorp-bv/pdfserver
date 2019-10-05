package com.tcorp.svg2pdf;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.fop.svg.PDFTranscoder;
import java.util.Base64;

import static spark.Spark.*;

public class Server {
    public Server(String keyStoreLocation, String keyStorePassword , int port){
        port(port);
        secure(keyStoreLocation, keyStorePassword, null, null);
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

            res.header("content-type", "application/pdf");
            return res;
        });

    }
}
