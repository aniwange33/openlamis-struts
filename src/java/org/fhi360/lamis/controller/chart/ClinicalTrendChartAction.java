/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.controller.chart;

import static com.opensymphony.xwork2.Action.SUCCESS;
import com.opensymphony.xwork2.ActionSupport;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.JDBCUtil;
import org.fhi360.lamis.utility.StringUtil;

public class ClinicalTrendChartAction extends ActionSupport {

    private List<String> categories;
    private List<Map<String, Object>> series;
    private String title;
    private String subtitle;
    private String titleForYAxis;
    private String query;

    private JDBCUtil jdbcUtil;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public String chartData() {
        categories = new ArrayList<String>();
        series = new ArrayList<Map<String, Object>>();

        List<Integer> data = new ArrayList<Integer>();

        long patientId = Long.parseLong(ServletActionContext.getRequest().getParameter("patientId"));

        query = "SELECT DISTINCT labtest_id, date_reported, resultab, resultpc FROM laboratory WHERE patient_id = " + patientId + "  AND labtest_id = 16 ORDER BY date_reported ASC LIMIT 12";
        try {
            jdbcUtil = new JDBCUtil();
            preparedStatement = jdbcUtil.getStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String resultab = resultSet.getObject("resultab") == null ? "" : resultSet.getString("resultab");
                String resultpc = resultSet.getObject("resultpc") == null ? "" : resultSet.getString("resultpc");
                System.out.println(resultab);

                if (resultab.isEmpty()) {
                    try {
                        data.add(Integer.valueOf(resultpc));
                    } catch (Exception e) {
                        data.add(0);
                    }

                } else {
                    try {
                        data.add(Integer.valueOf(resultab));
                    } catch (Exception e) {
                        data.add(0);
                    }

                }

                String periodLabel = resultSet.getObject("date_reported") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_reported"), "MM/dd/yyyy");
                categories.add(periodLabel);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "VL");
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
