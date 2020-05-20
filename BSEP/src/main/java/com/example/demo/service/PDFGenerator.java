package com.example.demo.service;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

public class PDFGenerator {

    public PDFGenerator() {
    }

    public void createCertificatePDF(X509Certificate cert) throws DocumentException, IOException, CertificateEncodingException {

        String certName = cert.getSubjectDN().getName();
//        Document document = new Document();
//        PdfWriter.getInstance(document, new FileOutputStream("dataPDF/" + certName + ".pdf"));
//
//        document.open();
//        document.add(new Paragraph(" ================================== Sertifikat =================================="));
//        document.add(new Paragraph("--------------------------------------------------------------------------------------------------------------"));
//        document.add(new Paragraph(cert.toString()));
//        document.add(new Paragraph("--------------------------------------------------------------------------------------------------------------"));
//        document.close();

        File file = null;
        byte[] buf = cert.getEncoded();

        FileOutputStream os = new FileOutputStream("dataCERT/" + certName + ".crt");
        os.write(buf);
        os.close();

    }

}
