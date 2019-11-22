/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.barcode;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.dao.hibernate.PatientDAO;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.utility.FileUtil;
import org.fhi360.lamis.utility.Scrambler;

public class QRCodeService {

    String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
    String stcontextPath = ServletActionContext.getServletContext().getInitParameter("stcontextPath");
    private Boolean viewIdentifier;
    private Scrambler scrambler;

    public QRCodeService() {
        this.scrambler = new Scrambler();
        if(ServletActionContext.getRequest().getSession().getAttribute("viewIdentifier") != null) {
            this.viewIdentifier = (Boolean) ServletActionContext.getRequest().getSession().getAttribute("viewIdentifier");                        
        }
    }
    
    
    public String generate() {
        long patientId = Long.parseLong(ServletActionContext.getRequest().getParameter("patientId"));
        System.out.println("...."+patientId);
        Patient patient = PatientDAO.find(patientId);

        String surname = (viewIdentifier)? scrambler.unscrambleCharacters(patient.getSurname()) : patient.getSurname();
        surname = StringUtils.upperCase(surname);                
        String otherNames = (viewIdentifier)? scrambler.unscrambleCharacters(patient.getOtherNames()) : patient.getOtherNames();
        otherNames = StringUtils.capitalize(otherNames);
        String gender = patient.getGender() == null ? "" : patient.getGender();
        String address = (viewIdentifier)? scrambler.unscrambleCharacters(patient.getAddress()) : patient.getAddress();
        address = StringUtils.capitalize(address);                

        String codeText = "Name: " + surname + " " + otherNames + "\n" + "Address: " + address + "\n" + "Gender: " + gender;
        final int size = 250;
        final String fileType = "png";
        final String fileName = "barcode.png";

        try {
            String directory = contextPath + "transfer/";
            FileUtil fileUtil = new FileUtil();
            fileUtil.makeDir(directory);
            fileUtil.makeDir(stcontextPath + "transfer/");
            fileUtil.makeDir(ServletActionContext.getRequest().getContextPath() + "/transfer/");
            File file = new File(directory + fileName);

            Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            // Now with zxing version 3.2.1 you could change border size (white border size to just 1)
            hintMap.put(EncodeHintType.MARGIN, 1);
            /* default = 4 */
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(codeText, BarcodeFormat.QR_CODE, size, size, hintMap);
            int width = byteMatrix.getWidth();
            BufferedImage image = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);
            image.createGraphics();

            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, width);
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < width; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
            ImageIO.write(image, fileType, file);
            print(directory + fileName);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("\n\nYou have successfully created QR Code.");
        return "Done";
    }

    /* 
    This method takes the text to be encoded, the width and height of the QR Code, 
    and returns the QR Code in the form of a byte array.
     */
    private byte[] getQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();

        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();
        return pngData;
    }

    public void print() {
        //Image image = Image.getInstance("file");
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(new Printable() {
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex != 0) {
                    return NO_SUCH_PAGE;
                }
                //graphics.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
                return PAGE_EXISTS;
            }
        });
        try {
            printJob.print();
        } catch (PrinterException e1) {
            e1.printStackTrace();
        }
    }

    public void print(String fileName) {
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream("Image.pdf"));
            document.open();
            System.out.println("Document open.....");

            Image image = Image.getInstance(fileName);
            document.add(image);
            System.out.println("Image added....");
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
