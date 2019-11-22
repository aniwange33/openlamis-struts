/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service;

/**
 *
 * @author user1
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.PropertyAccessor;

public class ApplicationUpdatesService {
    private static String contextPath;

    //This method reads the version manifest on the server and compares the date of manifest sent by the client 
    //and tokenize all files names on the server manifest whose modification date is after the last clients date of manifest 
    public static String getManifestToken(String dateManifest) {
        contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");            
        
        String token = "";
        try {
            File file = new File(contextPath+"version/manifest.txt");
            if (file.exists()) {
                InputStream input = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(input);
                BufferedReader bufferedReader = new BufferedReader(reader);

                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    String split[] = line.split("#");
                    if (!line.contains("Manifest-Date")) {
                        String dateOfModification = split[2].trim();
                        //If the date of file modification is after the manifest date from the client add file name to manifest token
                        if ((DateUtil.parseStringToDate(dateOfModification, "yyyy-MM-dd")).after(DateUtil.parseStringToDate(dateManifest, "yyyy-MM-dd"))) {
                            token = token.isEmpty()? split[0].trim() : token+","+split[0].trim();
                        }               
                    }
                }
                input.close();
                reader.close();             
                bufferedReader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
        if(!token.isEmpty()) token = token+",manifest.txt";
        System.out.println("Manifest token ....."+token);
        return token;
    }
    
    public static  byte[] getUpdates(String filename) {
        Map<String, Object> map = new PropertyAccessor().getSystemProperties();
        contextPath = (String) map.get("contextPath");
        byte[] bytes = new byte[1024];
        try {            
            filename = filename.equalsIgnoreCase("manifest.txt")? contextPath+"version/"+filename : contextPath+"version/updates/"+filename;
            bytes = FileUtils.readFileToByteArray(new File(filename));
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }      
        return bytes;
    }
        
}
