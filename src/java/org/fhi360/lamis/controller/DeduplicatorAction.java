/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.controller;

import static com.opensymphony.xwork2.Action.SUCCESS;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.converter.DeduplicatorDataConverter;
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.service.Deduplicator;
import org.fhi360.lamis.service.DeleteService;
import org.fhi360.lamis.service.MonitorService;

import java.io.File;
import org.apache.commons.io.FileUtils;
import java.io.IOException;

/**
 *
 * @author user10
 */
public class DeduplicatorAction {

    private Integer page;
    private Integer rows;
    private String sidx;
    private String sord;
    private Integer totalpages;
    private Integer currpage;
    private Integer totalrecords;
    private String fileName;
    private HttpServletRequest request;

    private File attachment;
    private String attachmentContentType;
    private String attachmentFileName;

    private ArrayList<Map<String, String>> duplicateList = new ArrayList<Map<String, String>>();

    public String duplicateGrid() {
        //setTotalpages(1);
        //setCurrpage(1);
        //setTotalrecords(20);
        new Deduplicator().duplicates();
        duplicateList = new Deduplicator().getDuplicates();
        return SUCCESS;
    }

    public String removeDuplicates() {
        long facilityId = (Long) ServletActionContext.getRequest().getSession().getAttribute("facilityId");
        String ids = ServletActionContext.getRequest().getParameter("patientIds");
        String patientIds[] = ids.split(",");
        for (int i = 0; i < patientIds.length; i++) {
            long patientId = Integer.parseInt(patientIds[i]);
            String hospitalNum = new PatientJDBC().getHospitalNum(patientId);
            System.out.println("Hospital...." + hospitalNum);
            new DeleteService().deletePatient(facilityId, patientId);
            MonitorService.logEntity(hospitalNum, "patient", 3);
        }
        return SUCCESS;
    }

    public String exportDuplicates() throws FileNotFoundException {
        this.setFileName(new DeduplicatorDataConverter().convertExcel());

        return SUCCESS;
    }

    public static List<Patient> readExcelData(String fileName) {
        long facilityId = (Long) ServletActionContext.getRequest().getSession().getAttribute("facilityId");
        
        List<Patient> patientList = new ArrayList<Patient>();

        try {
            //Create the input stream from the xlsx/xls file
            FileInputStream fis = new FileInputStream(fileName);

            //Create Workbook instance for xlsx/xls file input stream
            Workbook workbook = null;
            if (fileName.toLowerCase().endsWith("xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (fileName.toLowerCase().endsWith("xls")) {
                workbook = new HSSFWorkbook(fis);
            }

            Sheet sheet = workbook.getSheetAt(0);
            
            ArrayList<String> arrayList = null;

            //every sheet has rows, iterate over them
            Iterator<Row> rowIterator = sheet.iterator();
            
            int retainCounter = 0;
            while (rowIterator.hasNext()) {
                long patientId = 0;
                String hospitalNum = "";
                String uniqueId = "";
                int retainStatus = 0;

                //Get the row object
                Row row = rowIterator.next();

                if (row.getRowNum() == 0) {
                    Iterator<Cell> cellIterator = row.cellIterator();

                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        String retain = cell.getStringCellValue().trim();
                        if (cell.getColumnIndex() == 10) {
                            retainCounter = cell.getColumnIndex();
                        }
                    }
                } else if (row.getRowNum() > 0) {
                    //Every row has columns, get the column iterator and iterate over them
                    Iterator<Cell> cellIterator = row.cellIterator();
                    
                    
                    while (cellIterator.hasNext()) {
                        //Get the Cell object
                        Cell cell = cellIterator.next();
                        
                        if (cell.getColumnIndex() == 0){
                            patientId = (long)cell.getNumericCellValue();
                        }
                        if (cell.getColumnIndex() == 1){
                            hospitalNum = cell.getStringCellValue();
                        }
                        if (cell.getColumnIndex() == 2){
                            uniqueId = cell.getStringCellValue();
                        }
                        if (cell.getColumnIndex() == retainCounter) {
                            retainStatus = (int) cell.getNumericCellValue();
                        }
                        
                    } //end of cell iterator
                                            
                    if (retainStatus == 1) {
                        //update record
                        new Deduplicator().updateDuplicates(patientId, hospitalNum, uniqueId, facilityId);
                    } else if(retainStatus == 0){
                        //delete record
                        new DeleteService().deletePatient(facilityId, patientId);
                        MonitorService.logEntity(hospitalNum, "patient", 3);
                    }
                    
                    //Patient p = new Patient(, hospitalNum);
                    //patientList.add(p);

                } //end of rows iterator
            }
            //close file input stream
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return patientList;
    }

    public String execute() {
        try {
            String filePath = ServletActionContext.getServletContext().getRealPath("/").concat("deduplicate");

            System.out.println("Image Location:" + filePath);//see the server console for actual location  
            File fileToCreate = new File(filePath, attachmentFileName);
            FileUtils.copyFile(attachment, fileToCreate);//copying source file to new file  

            System.out.println("File uploaded successfully.");
            System.out.println("Reading excel file");
            readExcelData(fileToCreate.toString());

            return SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return SUCCESS;
    }

    public File getAttachment() {
        return attachment;
    }

    public void setAttachment(File attachment) {
        this.attachment = attachment;
    }

    public String getAttachmentContentType() {
        return attachmentContentType;
    }

    public void setAttachmentContentType(String attachmentContentType) {
        this.attachmentContentType = attachmentContentType;
    }

    public String getUserImageFileName() {
        return attachmentFileName;
    }

    public void setAttachmentFileName(String attachmentFileName) {
        this.attachmentFileName = attachmentFileName;
    }

    /**
     * @return the page
     */
    public Integer getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     * @return the limit
     */
    public Integer getRows() {
        return rows;
    }

    /**
     * @param limit the limit to set
     */
    public void setRows(Integer rows) {
        this.rows = rows;
    }

    /**
     * @return the sidx
     */
    public String getSidx() {
        return sidx;
    }

    /**
     * @param sidx the sidx to set
     */
    public void setSidx(String sidx) {
        this.sidx = sidx;
    }

    /**
     * @return the sord
     */
    public String getSord() {
        return sord;
    }

    /**
     * @param sord the sord to set
     */
    public void setSord(String sord) {
        this.sord = sord;
    }

    /**
     * @return the totalpages
     */
    public Integer getTotalpages() {
        return totalpages;
    }

    /**
     * @param totalpages the totalpages to set
     */
    public void setTotalpages(Integer totalpages) {
        this.totalpages = totalpages;
    }

    /**
     * @return the currpage
     */
    public Integer getCurrpage() {
        return currpage;
    }

    /**
     * @param currpage the currpage to set
     */
    public void setCurrpage(Integer currpage) {
        this.currpage = currpage;
    }

    /**
     * @return the totalrecords
     */
    public Integer getTotalrecords() {
        return totalrecords;
    }

    /**
     * @param totalrecords the totalrecords to set
     */
    public void setTotalrecords(Integer totalrecords) {
        this.totalrecords = totalrecords;
    }

    /**
     * @return the duplicateList
     */
    public ArrayList<Map<String, String>> getDuplicateList() {
        return duplicateList;
    }

    /**
     * @param duplicateList the duplicateList to set
     */
    public void setDuplicateList(ArrayList<Map<String, String>> duplicateList) {
        this.duplicateList = duplicateList;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
