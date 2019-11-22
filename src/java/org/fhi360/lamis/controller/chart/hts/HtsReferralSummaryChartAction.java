/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.controller.chart.hts;

import static com.opensymphony.xwork2.Action.SUCCESS;
import com.opensymphony.xwork2.ActionSupport;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.chart.hts.HtsReferralSummaryChartService;

import org.fhi360.lamis.utility.ChartUtil;
import org.fhi360.lamis.utility.DateUtil;

/**
 *
 * @author user10
 */
public class HtsReferralSummaryChartAction extends ActionSupport {

    private static final String DATA_ELEMENT_IDS = "112,113,114,115,116,117,118,119";

    private List<String> categories;
    private List<Map<String, Object>> series;
    private String title;
    private String subtitle;
    private String titleForYAxis;

    private HtsReferralSummaryChartService chartService;

    public String chartData() {

        ChartUtil chartUtil = new ChartUtil();
        HttpServletRequest request = ServletActionContext.getRequest();

        chartService = new HtsReferralSummaryChartService();

        categories = new ArrayList<String>();
        series = new ArrayList<Map<String, Object>>();

        List<Object> data = new ArrayList<Object>();
        List<Object> container1 = new ArrayList<Object>();
        List<Object> container2 = new ArrayList<Object>();
        List<Object> container3 = new ArrayList<Object>();
        List<Object> container4 = new ArrayList<Object>();
        List<Object> container5 = new ArrayList<Object>();
        List<Object> container6 = new ArrayList<Object>();
        List<Object> container7 = new ArrayList<Object>();
        List<Object> container8 = new ArrayList<Object>();
        
        container1.add("Self");
        container2.add("TB");
        container3.add("STI");
        container4.add("FP");
        container5.add("OPD");
        container6.add("Ward");
        container7.add("Blood Bank");
        container8.add("Others");
        
        long ipId = Long.parseLong(request.getParameter("ipId"));
        long stateId = Long.parseLong(request.getParameter("stateId"));
        long lgaId = Long.parseLong(request.getParameter("lgaId"));
        long facilityId = Long.parseLong(request.getParameter("facilityId"));

        try {
                int value1;
                int value2;
                int value3;
                int value4;
                int value5;
                int value6;
                int value7;
                int value8;

                Date today = new Date();
                Date reportingDateBegin = (!request.getParameter("reportingDateBegin").isEmpty()) ? DateUtil.parseStringToDate(request.getParameter("reportingDateBegin"), "yyyy-MM-dd") : today;
                Date reportingDateEnd = (!request.getParameter("reportingDateEnd").isEmpty()) ? DateUtil.parseStringToDate(request.getParameter("reportingDateEnd"), "yyyy-MM-dd") : today;

                Map values = chartService.chartData(ipId, stateId, lgaId, facilityId, DATA_ELEMENT_IDS, reportingDateBegin, reportingDateEnd);

                if(values != null && !values.isEmpty()) {      
                        value1 = values.get("value1") != null? (Integer) values.get("value1") : 0;
                        value2 = values.get("value2") != null? (Integer) values.get("value2") : 0;
                        value3 = values.get("value3") != null? (Integer) values.get("value3") : 0;
                        value4 = values.get("value4") != null? (Integer) values.get("value4") : 0;
                        value5 = values.get("value5") != null? (Integer) values.get("value5") : 0;
                        value6 = values.get("value6") != null? (Integer) values.get("value6") : 0;
                        value7 = values.get("value7") != null? (Integer) values.get("value7") : 0;
                        value8 = values.get("value8") != null? (Integer) values.get("value8") : 0;

                        container1.add(value1);
                        container2.add(value2);
                        container3.add(value3);
                        container4.add(value4); 
                        container5.add(value5);
                        container6.add(value6);
                        container7.add(value7);
                        container8.add(value8);
                }
                else {
                        container1.add(0);
                        container2.add(0);
                        container3.add(0);
                        container4.add(0);
                        container5.add(0);
                        container6.add(0);
                        container7.add(0);
                        container8.add(0);            
                }

                data.add(container1);
                data.add(container2);
                data.add(container3);
                data.add(container4);
                data.add(container5);
                data.add(container6);
                data.add(container7);
                data.add(container8);

                Map<String, Object> map = new HashMap<>();
                map.put("type", "pie");
                map.put("name", "Source of Referral");
                map.put("data", data);
                series.add(map);
                title = "Source of Referral";
        }
        catch (Exception ex) {
            ex.printStackTrace();
             return ERROR;
        }       

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
