/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.controller.chart;

import org.fhi360.lamis.utility.ChartUtil;
import org.fhi360.lamis.utility.DateUtil;
import com.opensymphony.xwork2.ActionSupport;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.struts2.ServletActionContext;

public class SyncPharmChartAction extends ActionSupport {
    private List<String> categories;
    private List<Map<String, Object>> series;
    private String title;
    private String subtitle;
    private String titleForYAxis;
    private String query;
    
    public String getChartData() {
        ChartUtil chartUtil = new ChartUtil();
        
        categories = new ArrayList<String>();
        series = new ArrayList<Map<String, Object>>();
        
        List<Integer> data = new ArrayList<Integer>();
    
        long facilityId = Long.parseLong(ServletActionContext.getRequest().getParameter("facilityId"));
        
        Date startDate = DateUtil.addMonth(new Date(), -3); 
        Date endDate = DateUtil.addMonth(new Date(), -1);
        
        chartUtil.executeUpdate("DROP TABLE IF EXISTS pm");
        
        query = "CREATE TEMPORARY TABLE pm AS SELECT DISTINCT facility_id, patient_id, date_visit, time_stamp FROM pharmacy WHERE facility_id = " + facilityId + " AND time_stamp >= DATEADD('MONTH', -8, CURDATE()) AND time_stamp <= DATEADD('MONTH', -1, CURDATE())";
        chartUtil.executeUpdate(query);
                
        int monthsBetween = DateUtil.monthsBetweenIgnoreDays(startDate, endDate);
        for (int i = 0; i <= monthsBetween; i++) {					
            Map<String, Object> period = chartUtil.getPeriod(startDate, i);
            int year = (Integer) period.get("year");
            int month = (Integer) period.get("month");        
            String periodLabel = (String) period.get("periodLabel");
            int total = 0;

            query = "SELECT COUNT(*) AS count FROM pm WHERE MONTH(time_stamp) = " + month + "  AND YEAR(time_stamp) = " + year; 
            total = chartUtil.getCount(query);
            data.add(total);
                        
            categories.add(periodLabel);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "Pharmacy");
        map.put("data", data);
        series.add(map);
        
        setCategories(categories);
        setTitleForYAxis("Number");
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
