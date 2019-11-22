/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.controller.chart;

import static com.opensymphony.xwork2.Action.SUCCESS;
import com.opensymphony.xwork2.ActionSupport;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fhi360.lamis.service.chart.FacilitySyncChartService;
import org.fhi360.lamis.utility.Constants;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.ChartUtil;

/**
 *
 * @author user10
 */
public class FacilitySyncChartAction extends ActionSupport{
    
    private List<String> categories;
    private List<Map<String, Object>> series;
    private String title;
    private String subtitle;
    private String titleForYAxis;
    
    public String getChartData () {    
           categories = new ArrayList<String>();
           series = new ArrayList<Map<String, Object>>();

           FacilitySyncChartService facilitySyncChartService = new FacilitySyncChartService();
           ChartUtil chartUtil = new ChartUtil();

           List<Integer> data1 = new ArrayList<Integer>();
           List<Integer> data2 = new ArrayList<Integer>();
           List<Double> data3 = new ArrayList<Double>();

           //Date reportingDateBegin = new Date(); 
           Date reportingDateEnd = new Date();
           
           Date reportingDateBegin = new Date();
            
           String [] stateIds = Constants.StateIds.STATE_IDS.split(",");
           
           for(int i = 0; i< stateIds.length; i++ ) { 
               Map values = facilitySyncChartService.chartData(Integer.parseInt(stateIds[i]), reportingDateBegin, reportingDateEnd);
                int value1;
                int value2;
                double value3;

                if(values != null && !values.isEmpty()) {
                    value1 = (Integer) values.get("value1");
                    value2 = (Integer) values.get("value2");
                    value3 =  chartUtil.getPercentage(value2, value1); 
                }  
                else{
                    value1 = 0; 
                    value2 = 0;
                    value3 = 0;  
                }              
                data1.add(value1);
                data2.add(value2);
                data3.add(value3);
            
                String state = facilitySyncChartService.getStateById(Integer.parseInt(stateIds[i]));
                categories.add(state);
           } 

           Map<String, Object> map = new HashMap<String, Object>();
           map.put("name", "Expected Number of Facilities");
           map.put("data", data1);
           series.add(map);

           map = new HashMap<String, Object>();
            map.put("name", "Number of Facilities Uploaded");
            map.put("data", data2);
            series.add(map);

            map = new HashMap<String, Object>();
            map.put("name", "Upload Rate");
            map.put("data", data3);
            series.add(map);

            String periodStart = DateUtil.parseDateToString(reportingDateBegin, "dd MMMMM, yyyy");
            String periodEnd = DateUtil.parseDateToString(reportingDateEnd, "dd MMMMM, yyyy");
            title = "Facility LAMIS Data Daily Upload Rate";
            if(periodStart.equalsIgnoreCase(periodEnd)) {
                subtitle = periodStart;
            } else {
                subtitle = periodStart + " to " + periodEnd;                
            }
            setTitle(title);
            setSubtitle(subtitle);
            setCategories(categories);
            setTitleForYAxis("No. of Facilities");
            setSeries(series);
            
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
