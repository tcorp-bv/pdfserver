package com.tcorp.svg2pdf;

import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code128.EAN128Bean;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.output.BarcodeCanvasSetupException;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;

public class Barcodes {
    private static final float dpi = 150;
    private static final EAN13Bean ean13Bean = new EAN13Bean();
    private static final EAN128Bean ean128Bean = new EAN128Bean();

    public static void loadEAN13(String code, OutputStream outputStream) throws BarcodeCanvasSetupException, TransformerException {
        SVGCanvasProvider provider = new SVGCanvasProvider(false, 0);
        ean13Bean.generateBarcode(provider, code);

        loadSVG(provider, outputStream);
    }

        public static void loadGS1Pallet(String code, OutputStream outputStream) throws BarcodeCanvasSetupException, TransformerException {
        SVGCanvasProvider provider = new SVGCanvasProvider(false, 0);
        ean128Bean.setQuietZone(25/dpi);
        ean128Bean.generateBarcode(provider, code);

        loadSVG(provider, outputStream);
    }
    private static void loadSVG(SVGCanvasProvider provider, OutputStream outputStream) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer trans = factory.newTransformer();
        Source src = new DOMSource(provider.getDOMFragment());
        Result res = new StreamResult(outputStream);

        trans.transform(src, res);
    }
}
