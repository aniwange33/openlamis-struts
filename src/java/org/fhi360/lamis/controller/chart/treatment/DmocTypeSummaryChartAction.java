/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.controller.chart.treatment;

import static com.opensymphony.xwork2.Action.SUCCESS;
import com.opensymphony.xwork2.ActionSupport;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.chart.treatment.DmocTypeSummaryChartService;
import org.fhi360.lamis.utility.ChartUtil;
import org.fhi360.lamis.utility.DateUtil;

/**
 *
 * @author user10
 */
public class DmocTypeSummaryChartAction  extends ActionSupport {
    
    private static final String DATA_ELEMENT_IDS = "31,32,33,34";   
    
    private List<String> categories;
    private List<Map<String, Object>> series;
    private String title;
    private String subtitle;
    private String titleForYAxis;
   
    private DmocTypeSummaryChartService currentOnArtDmocTypeChartService;
    public String chartData() {
        
        ChartUtil chartUtil = new ChartUtil();
        HttpServletRequest request = ServletActionContext.getRequest();
        
       currentOnArtDmocTypeChartService = new DmocTypeSummaryChartService();
                
        categories = new ArrayList<String>();
        series = new ArrayList<Map<String, Object>>();
 
        
        // structures to hold data for the chart        
        List<Object> data = new ArrayList<Object>();
        List<Object> container1 = new ArrayList<Object>();
        List<Object> container2 = new ArrayList<Object>();
        List<Object> container3 = new ArrayList<Object>();
        Map<String, Object> container4 = new HashMap<String, Object>(); // to be selected by default on the interface

         
        container1.add("MMS");
        container2.add("MMM");
        container3.add("CARC");
        container4.put("name", "CPARP");
        
        
        long ipId = Long.parseLong(request.getParameter("ipId"));
        long stateId = Long.parseLong(request.getParameter("stateId"));
        long lgaId = Long.parseLong(request.getParameter("lgaId"));
        long facilityId = Long.parseLong(request.getParameter("facilityId"));

        Date today = new Date();
        Date reportingDateBegin = (!request.getParameter("reportingDateBegin").isEmpty()) ? DateUtil.parseStringToDate(request.getParameter("reportingDateBegin"), "yyyy-MM-dd") : today;
        Date reportingDateEnd = (!request.getParameter("reportingDateEnd").isEmpty()) ? DateUtil.parseStringToDate(request.getParameter("reportingDateEnd"), "yyyy-MM-dd") : today;

        Map values = currentOnArtDmocTypeChartService.chartData(ipId, stateId, lgaId, facilityId,  DATA_ELEMENT_IDS, reportingDateBegin, reportingDateEnd);


        if(values != null && !values.isEmpty()) {
            container1.add((Integer) values.get("value1"));
            container2.add((Integer) values.get("value2"));
            container3.add((Integer) values.get("value3"));
            container4.put("y", (Integer) values.get("value4"));
            container4.put("y", 0);
            container4.put("sliced", true);
            container4.put("selected", true);
        }
        else {
                container1.add(0);
                container2.add(0);
                container3.add(0);
                container4.put("y", 0);           
        }
        
        data.add(container1);
        data.add(container2);
        data.add(container3);
        data.add(container4);
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", "pie");
        map.put("name", "DMOC By Type");
        map.put("data", data);
        series.add(map);
        title = "DMOC Type";

        return SUCCESS;
    }
           
    /**
     * @return the categories
     */
    public List<String> getCategories() {
        return categories;
    }

    /**
     * @param categories the categories to set
     */
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    /**
     * @return the series
     */
    public List<Map<String, Object>> getSeries() {
        return series;
    }

    /**
     * @param series the series to set
     */
    public void setSeries(List<Map<String, Object>> series) {
        this.series = series;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the subtitle
     */
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * @param subtitle the subtitle to set
     */
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    /**
     * @return the titleForYAxis
     */
    public String getTitleForYAxis() {
        return titleForYAxis;
    }

    /**
     * @param titleForYAxis the titleForYAxis to set
     */
    public void setTitleForYAxis(String titleForYAxis) {
        this.titleForYAxis = titleForYAxis;
    }
    
}
