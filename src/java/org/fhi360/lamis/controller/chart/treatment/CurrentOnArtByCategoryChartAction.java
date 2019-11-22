/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.controller.chart.treatment;

import static com.opensymphony.xwork2.Action.SUCCESS;
import com.opensymphony.xwork2.ActionSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.chart.IndicatorSummaryByCategoryChartService;
import org.fhi360.lamis.utility.ChartUtil;
import org.fhi360.lamis.utility.Constants;
import org.fhi360.lamis.utility.DateUtil;

/**
 *
 * @author user10
 */
public class CurrentOnArtByCategoryChartAction extends ActionSupport {

    private static final String DATA_ELEMENT_IDS = "11";

    private List<String> categories;
    private List<Map<String, Object>> series;
    private String title;
    private String subtitle;
    private String titleForYAxis;

    private IndicatorSummaryByCategoryChartService chartService;

    public String chartData() {
        ChartUtil chartUtil = new ChartUtil();
        chartService = new IndicatorSummaryByCategoryChartService();

        HttpServletRequest request = ServletActionContext.getRequest();

        categories = new ArrayList<String>();
        series = new ArrayList<Map<String, Object>>();

        List<Integer> data1 = new ArrayList<Integer>();
        List<Integer> data2 = new ArrayList<Integer>();

        String[] age = {"<1", "1-4", "5-9", "10-14", "15-19", "20-24", "25-29", "30-34", "35-39", "40-44", "45-49", "50+"};
        ArrayList data3 = new ArrayList(12);
        data3.addAll(Arrays.asList(age));

        long ipId = Long.parseLong(request.getParameter("ipId"));
        long stateId = Long.parseLong(request.getParameter("stateId"));
        long lgaId = Long.parseLong(request.getParameter("lgaId"));
        long facilityId = Long.parseLong(request.getParameter("facilityId"));


        Date today = new Date();
        Date reportingDateBegin = (!request.getParameter("reportingDateBegin").isEmpty()) ? DateUtil.parseStringToDate(request.getParameter("reportingDateBegin"), "yyyy-MM-dd") : today;
        Date reportingDateEnd = (!request.getParameter("reportingDateEnd").isEmpty()) ? DateUtil.parseStringToDate(request.getParameter("reportingDateEnd"), "yyyy-MM-dd") : today;

        String[] categoryFemaleIds = Constants.CategoryIds.CATEGORY_IDS_FEMALE.split(",");
        String[] categoryMaleIds = Constants.CategoryIds.CATEGORY_IDS_MALE.split(",");

        int len = categoryFemaleIds.length;

        for (int i = 0; i < age.length; i++) {
            Map values = chartService.chartData(ipId, stateId, lgaId, facilityId, DATA_ELEMENT_IDS, Integer.parseInt(categoryFemaleIds[i]), Integer.parseInt(categoryMaleIds[i]), reportingDateBegin, reportingDateEnd);
            if(values != null && !values.isEmpty()) {
                data1.add((Integer) values.get("value1"));
                data2.add((Integer) values.get("value2"));
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("name", "Male");
        map.put("data", data1);
        series.add(map);

        map = new HashMap<>();
        map.put("name", "Female");
        map.put("data", data2);
        series.add(map);

        String periodStart = DateUtil.parseDateToString(reportingDateBegin, "MMMMM yyyy");
        String periodEnd = DateUtil.parseDateToString(reportingDateEnd, "MMMMM yyyy");
        title = "Current on ART Male vs Female ";
        subtitle = periodStart + " to " + periodEnd;
        setTitle(title);
        setSubtitle(subtitle);
        setCategories(data3);
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
