/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class ChartUtil {

    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    public ChartUtil() {
    }

    public String getReportingPeriodAsString(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        DateFormat dateFormat = new SimpleDateFormat("MMMMM yyyy");
        return dateFormat.format(cal.getTime());
    }

    public Date getDate(int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        return cal.getTime();
    }

    public Map<String, Object> getPeriod(Date date, int increment) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, increment);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        DateFormat dateFormat = new SimpleDateFormat("MMM ''yy");
        String periodLabel = dateFormat.format(cal.getTime());

        Map<String, Object> map = new HashMap<>();
        map.put("year", year);
        map.put("month", month + 1);
        map.put("periodLabel", periodLabel);
        return map;
    }

    public Map<String, Object> getDayPeriod(Date date, int increment) {

        Calendar cal = Calendar.getInstance();
        // cal.setTime(DateUtil.formatDate(date, "dd/MM/yyyy"));
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, increment);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DateFormat dateFormat = new SimpleDateFormat("MMM ''dd");
        String periodLabel = dateFormat.format(cal.getTime());

        Map<String, Object> map = new HashMap<>();
        map.put("year", year);
        map.put("month", month);
        map.put("day", day + 1);
        map.put("periodLabel", periodLabel);
        return map;
    }

    public double getPercentage(String query) {
        double percentage[] = {0.0};
        jdbcTemplate.query(query, resultSet -> {
            int numerator = resultSet.getInt("numerator");
            int denominator = resultSet.getInt("denominator");
            percentage[0] = numerator > 0 ? 100.0 * numerator / denominator : 0.0;
        });
        return Math.round(percentage[0] * 10.0) / 10.0; // round percentage to 1 decimal point
    }

    public double getPercentage(int numerator, int denominator) {
        double percentage = numerator > 0 ? 100.0 * numerator / denominator : 0.0;
        return Math.round(percentage * 10.0) / 10.0; // round percentage to 1 decimal point
    }

    public int getCount(String query) {
        int count[] = {0};
        jdbcTemplate.query(query, resultSet -> {
            count[0] = resultSet.getInt("count");
        });
        return count[0];
    }

    public void executeUpdate(String query) {
        transactionTemplate.execute(ts -> {
            jdbcTemplate.execute(query);
            return null;
        });
    }

}
