package com.tcorp.svg2pdf;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Main {
    private static Server server;
    public static void main(String[] args) {
        System.out.println("Starting svg2pdf server...");
        if(args.length != 1){
            throw new RuntimeException("Please only provide the port as an argument.");
        }
        server= new Server(Integer.valueOf(args[0]));
    }
}
