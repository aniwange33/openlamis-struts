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

public class RetentionChartAction extends ActionSupport {
    private Integer reportingMonthBegin;
    private Integer reportingYearBegin;
    private Integer reportingMonthEnd;
    private Integer reportingYearEnd;
    
    private List<String> categories;
    private List<Map<String, Object>> series;
    private String title;
    private String subtitle;
    private String titleForYAxis;
    
    public String getChartData() {
        ChartUtil chartUtil = new ChartUtil();
        int[][] cohort = new int[5][6];
        
        categories = new ArrayList<String>();
        series = new ArrayList<Map<String, Object>>();
        
        List<Integer> data1 = new ArrayList<Integer>();
        List<Integer> data2 = new ArrayList<Integer>();
        List<Integer> data3 = new ArrayList<Integer>();
        List<Integer> data4 = new ArrayList<Integer>();
    
        Map<String, Object> map;
        
        String status[] = {"ART Tranfer Out", "Stopped Treatment", "Lost to Follow Up", "Known Death", "Alive and on ART"};
        
        for(int i = 0; i <= 4; i++) {
            if (i==0) {
            map = new HashMap<String, Object>();
            categories.add("6 Mons");
            data1.add(cohort[i][0]);
            map.put("name", "ART Tranfer Out");
            map.put("data", data1);
            series.add(map);
                
            map.put("name", status[i]);
            data2.add(cohort[i][1]);
            map.put("data", data2);
            series.add(map);
            }

            map = new HashMap<String, Object>();
            categories.add("12 Mons");
            map.put("name", status[i]);
            data2.add(cohort[i][1]);
            map.put("data", data2);
            series.add(map);
            
            map = new HashMap<String, Object>();
            categories.add("18 Mons");
            map.put("name", status[i]);
            data1.add(cohort[i][3]);
            map.put("data", data3);
            series.add(map);
            
            map = new HashMap<String, Object>();
            categories.add("24 Mons");
            map.put("name", status[i]);
            data1.add(cohort[i][4]);
            map.put("data", data3);
            series.add(map);
        }  
               
        Date startDate = chartUtil.getDate(getReportingMonthBegin().intValue() -1, getReportingYearBegin().intValue()); // month is zero-based
        Date endDate = chartUtil.getDate(getReportingMonthEnd().intValue() -1, getReportingYearEnd().intValue());
        
        String periodStart = DateUtil.parseDateToString(startDate, "MMMMM yyyy");
        String periodEnd = DateUtil.parseDateToString(endDate, "MMMMM yyyy");
        title = "Currenty on ART Male vs Female ";
        subtitle = periodStart + " to " + periodEnd;
        setTitle(title);
        setSubtitle(subtitle);
        setCategories(categories);
        setTitleForYAxis("Number");
        setSeries(series);
             
        return SUCCESS;
    }
    
    /**
     * @return the reportingMonthBegin
     */
    public Integer getReportingMonthBegin() {
        return reportingMonthBegin;
    }

    /**
     * @param reportingMonthBegin the reportingMonthBegin to set
     */
    public void setReportingMonthBegin(Integer reportingMonthBegin) {
        this.reportingMonthBegin = reportingMonthBegin;
    }

    /**
     * @return the reportingYearBegin
     */
    public Integer getReportingYearBegin() {
        return reportingYearBegin;
    }

    /**
     * @param reportingYearBegin the reportingYearBegin to set
     */
    public void setReportingYearBegin(Integer reportingYearBegin) {
        this.reportingYearBegin = reportingYearBegin;
    }

    /**
     * @return the reportingMonthEnd
     */
    public Integer getReportingMonthEnd() {
        return reportingMonthEnd;
    }

    /**
     * @param reportingMonthEnd the reportingMonthEnd to set
     */
    public void setReportingMonthEnd(Integer reportingMonthEnd) {
        this.reportingMonthEnd = reportingMonthEnd;
    }

    /**
     * @return the reportingYearEnd
     */
    public Integer getReportingYearEnd() {
        return reportingYearEnd;
    }

    /**
     * @param reportingYearEnd the reportingYearEnd to set
     */
    public void setReportingYearEnd(Integer reportingYearEnd) {
        this.reportingYearEnd = reportingYearEnd;
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
