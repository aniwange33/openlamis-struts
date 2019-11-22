/**
 *
 * @author AALOZIE
 */

package org.fhi360.lamis.controller.report;

import java.util.*;
import com.opensymphony.xwork2.ActionSupport;
import org.fhi360.lamis.report.ChroniccareSummaryProcessor;

public class ChroniccareReportAction extends ActionSupport {
    private ArrayList<Map<String, Object>> reportList = new ArrayList<>();
    private HashMap parameterMap = new HashMap();
    
    public String chroniccareSummary() {
        Map<String, Object> map = new HashMap<>();
        map.put("stub", "");
	reportList.add(map);
        parameterMap = new ChroniccareSummaryProcessor().getReportParameters(); 
        return SUCCESS; 
    }
               
    /**
     * @return the reportList
     */
    public ArrayList<Map<String, Object>> getReportList() {
        return reportList;
    }

    /**
     * @param reportList the reportList to set
     */
    public void setReportList(ArrayList<Map<String, Object>> reportList) {
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

}