package com.tcorp.svg2pdf;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.batik.transcoder.TranscoderException;
import org.krysalis.barcode4j.output.BarcodeCanvasSetupException;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class OpenWhisk {
    private static final Gson gson = new Gson();
    private static final String TYPE_GS1_PALLET = "GS1PALLET";
    private static final String TYPE_EAN13 = "EAN13";

    public static JsonObject main(JsonObject args){
        if (!args.has("type") || !(args.get("type") instanceof JsonPrimitive) || !(args.getAsJsonPrimitive("type").isString()))
            throw new RuntimeException("No type argument of type string");
        if (!args.has("params") || !(args.get("params") instanceof JsonObject))
            throw new RuntimeException("No params argument of type jsonobject");
        String type = args.get("type").getAsString();
        JsonObject params = args.get("params").getAsJsonObject();
        try {
            String base64pdf = getPdf(type, params);
            JsonObject result = new JsonObject();
            result.addProperty("pdf.base64", base64pdf);
            return result;
        }catch (Exception e){
            JsonObject error = new JsonObject();
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            error.addProperty("error", writer.toString());
            error.addProperty("stacktrace", e.getStackTrace().toString());
            return error;
        }
    }

    /**
     * @param type
     * @param params
     * @return The pdf as a base64 encoded string
     */
    private static String getPdf(String type, JsonObject params) throws TransformerException, TranscoderException, BarcodeCanvasSetupException, IOException {
        if (type.equals(TYPE_GS1_PALLET)) {
            return handleGS1Pallet(params);
        } else if (type.equals(TYPE_EAN13)) {
            return handleEAN13(params);
        } else {
            throw new IllegalArgumentException("Type not found!");
        }
    }

    private static class GS1PalletParams {
        public String code;
        public String sscc;
        public String delivery;
        public String palletNumber;
        public String date;

        public String getNullFieldAndType() {
            if (code == null)
                return "code [String]";
            if (sscc == null)
                return "sscc [String]";
            if (delivery == null)
                return "delivery [String]";
            if (palletNumber == null)
                return "palletNumber [String]";
            if (date == null)
                return "date [String]";
            return null;
        }
    }

    private static String handleGS1Pallet(JsonObject params) throws IOException, TranscoderException, BarcodeCanvasSetupException, TransformerException {
        GS1PalletParams paramsObj = gson.fromJson(params, GS1PalletParams.class);
        String nullField = paramsObj.getNullFieldAndType();
        if (nullField != null)
            throw new IllegalArgumentException("Field " + nullField + " was not found in the params");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        LabelPDFGenerator.getGS1PalletLabel(bos, paramsObj.code, paramsObj.delivery, paramsObj.palletNumber, paramsObj.date);
        return  new String(bos.toByteArray(), StandardCharsets.UTF_8);
    }


    private static class EAN13Params {
        public String code;

        public String getNullFieldAndType() {
            if (code == null)
                return "code [String]";
            return null;
        }
    }
    private static String handleEAN13(JsonObject params) throws IOException, TranscoderException, BarcodeCanvasSetupException, TransformerException {
        EAN13Params paramsObj = gson.fromJson(params, EAN13Params.class);
        String nullField = paramsObj.getNullFieldAndType();
        if (nullField != null)
            throw new IllegalArgumentException("Field " + nullField + " was not found in the params");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        LabelPDFGenerator.getEAN13Label(bos, paramsObj.code);
        return  bos.toString(StandardCharsets.UTF_8.toString());
    }
}
