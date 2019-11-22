/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.exchange.radet;

/**
 *
 * @author user1
 */
import com.opensymphony.xwork2.ActionSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;

public class RadetAction extends ActionSupport {
    private Integer page;
    private Integer rows;
    private String sidx;
    private String sord;
    private Integer totalpages;
    private Integer currpage;
    private Integer totalrecords;

    private String status;
    private String fileName;
    
    private ArrayList<Map<String, String>> clientList = new ArrayList<>();
    private ArrayList<Map<String, String>> reportList = new ArrayList<>();
    private HashMap parameterMap = new HashMap();
    
    public String parseRadetFile() {
        new RadetService().clearClientList();
        
        HttpServletRequest request = ServletActionContext.getRequest();        
        int fileUploaded = 0;
        if(request.getSession().getAttribute("fileUploaded") != null) {
            fileUploaded = (Integer) request.getSession().getAttribute("fileUploaded");    
        }
        if(fileUploaded == 1) {
           // new RadetService().parseFile((String) request.getSession().getAttribute("fileName"));
        }       
        return SUCCESS;
    }
    
    public String buildClientList() {
        new RadetService().buildClientList();
        clientList = new RadetService().retrieveClientList();
        return SUCCESS;        
    }
    
    public String radetGrid() {
        clientList = new RadetService().retrieveClientList();
        return SUCCESS;        
    }
  
    public String radetReport() {
        String reportType = ServletActionContext.getRequest().getParameter("reportType");
        reportList = new RadetService().radetReport(reportType);
        if(reportType.equals("1")) parameterMap.put("reportTitle", "Clients with no ARV pickup"); 
        if(reportType.equals("2")) parameterMap.put("reportTitle", "Clients with no Regimen at ART start"); 
        if(reportType.equals("3")) parameterMap.put("reportTitle", "Clients who are Lost to Follow Up");         
        if(reportType.equals("4")) parameterMap.put("reportTitle", "Clients who are Due for Viral Load Test");         
        return SUCCESS;         
    }
    
    public String dispatcher() {       
        setFileName(new RadetConverter().convertExcel()); 
        return SUCCESS;        
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
     * @return the clientList
     */
    public ArrayList<Map<String, String>> getClientList() {
        return clientList;
    }

    /**
     * @param clientList the clientList to set
     */
    public void setClientList(ArrayList<Map<String, String>> clientList) {
        this.clientList = clientList;
    }

    /**
     * @return the reportList
     */
    public ArrayList<Map<String, String>> getReportList() {
        return reportList;
    }

    /**
     * @param reportList the reportList to set
     */
    public void setReportList(ArrayList<Map<String, String>> reportList) {
        this.reportList = reportList;
    }

    /**
     * @return the parameterMap
     */
    public HashMap getParameterMap() {
        return parameterMap;
    }

    /**
     * @param parameterMap the parameterMap to set
     */
    public void setParameterMap(HashMap parameterMap) {
        this.parameterMap = parameterMap;
    }


    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
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
