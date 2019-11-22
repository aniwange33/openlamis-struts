/**
 *
 * @author AALOZIE
 */

package org.fhi360.lamis.controller.report;

import static com.opensymphony.xwork2.Action.SUCCESS;
import java.util.*;
import com.opensymphony.xwork2.ActionSupport;
import org.fhi360.lamis.report.ArtAddendumSummaryProcessor;
import org.fhi360.lamis.report.NigeriaqualIndicatorProcessor;
import org.fhi360.lamis.report.PmtctAddendumSummaryProcessor;
import org.fhi360.lamis.report.PmtctSummaryProcessor;

public class PmtctReportAction extends ActionSupport {
    private ArrayList<Map<String, Object>> reportList = new ArrayList<Map<String, Object>>(); 
    private HashMap parameterMap = new HashMap();
    	
    public String pmtctSummary() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("stub", "");
        reportList.add(map);
        parameterMap = new PmtctSummaryProcessor().process(); 
        return SUCCESS; 
    }
	

    public String pmtctAddendumSummary() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("stub", "");
        reportList.add(map);
        parameterMap =  new PmtctAddendumSummaryProcessor().process(); 
        return SUCCESS; 
    }



    public String nigeriaqualIndicators() {
        reportList = new NigeriaqualIndicatorProcessor().process(); 
        parameterMap = new NigeriaqualIndicatorProcessor().getReportParameters(); 
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