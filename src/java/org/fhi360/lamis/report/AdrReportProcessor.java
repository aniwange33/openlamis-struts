/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.report;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class AdrReportProcessor {

    private String reportingMonth;
    private String reportingYear;
    private String patientId;
    private HashMap parameterMap;

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    public AdrReportProcessor() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public void adrReport() {

    }

    public HashMap getReportParameters() {
        parameterMap = new HashMap();
        //reportingMonth = DateUtil.getMonthNumber(request.getParameter("reportingMonth"));
        reportingYear = request.getParameter("reportingYear");
        return parameterMap;
    }
}
