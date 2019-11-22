/**
 *
 * @author aalozie
 */

package org.fhi360.lamis.service.barcode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import org.fhi360.lamis.utility.FileUtil;


/**
 *
 * @author user1
 */
public class BarcodeService {
    public static String generate(String barcode) {
        String status = "Barcode printing failed";
        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
        String stcontextPath = ServletActionContext.getServletContext().getInitParameter("stcontextPath");
        
        final int dpi = 150;
        final String fileName = "barcode.png";
        
        //Create the barcode bean and configure the barcode generator
        Code39Bean bean = new Code39Bean();
        bean.setModuleWidth(UnitConv.in2mm(1.0f / dpi)); //makes the narrow bar width exactly one pixel
        bean.setWideFactor(3);
        bean.doQuietZone(false);

        try {
            String directory = contextPath+"transfer/";
            
            FileUtil fileUtil = new FileUtil();
            fileUtil.makeDir(directory);
            fileUtil.makeDir(stcontextPath+"transfer/");
            fileUtil.makeDir(ServletActionContext.getRequest().getContextPath()+"/transfer/");

            OutputStream outputStream = new FileOutputStream(new File(directory+fileName));            
            //Set up the canvas provider for monochrome PNG output 
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(outputStream, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
            bean.generateBarcode(canvas, barcode);   //Generate the barcode          
            canvas.finish();
            outputStream.close();
            
            //for servlets in the stand alone (webapps) context, copy file to the transfer folder in webapps 
            fileUtil.copyFile(fileName, contextPath+"transfer/", stcontextPath+"transfer/");                
            //for servlets in the default(root) context, copy file to the transfer folder in root 
            if(!contextPath.equalsIgnoreCase(ServletActionContext.getRequest().getContextPath())) fileUtil.copyFile(fileName, contextPath+"transfer/", ServletActionContext.getRequest().getContextPath()+"/transfer/");                    
            print(directory+fileName);
            status = "Barcode generated and printed";
        } 
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return status;
    }
    
    public static void print(String fileName) {
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream("Image.pdf"));
            document.open();

            Image image = Image.getInstance(fileName);
            document.add(image);
            document.close();
        } 
        catch(Exception e){
          e.printStackTrace();
        }        
    }

}
