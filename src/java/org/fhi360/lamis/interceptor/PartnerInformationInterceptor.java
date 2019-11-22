/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.interceptor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.builder.AncListBuilder;

import org.fhi360.lamis.utility.builder.PartnerInformationListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class PartnerInformationInterceptor extends AbstractInterceptor {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public String intercept(ActionInvocation invocation) throws Exception {
        String result = invocation.invoke();
        findPartnerInformation();
        return result;
    }

    private void findPartnerInformation() {
        request = ServletActionContext.getRequest();
        session = ServletActionContext.getRequest().getSession();
        query = "SELECT * FROM partnerinformation WHERE facility_id = ? AND patient_id = ? AND date_visit = (SELECT MAX(date_visit) FROM  partnerinformation WHERE facility_id = ? AND patient_id = ?)";
        try {
            jdbcTemplate.query(query, (resultSet) -> {
                new PartnerInformationListBuilder().buildPartnerinformationList(resultSet);
                return null;
            }, (Long) session.getAttribute("facilityId"),
                    Long.parseLong(request.getParameter("patientId")),
                    (Long) session.getAttribute("facilityId"),
                    Long.parseLong(request.getParameter("patientId"))
            );

        } catch (Exception exception) {

        }
    }
}
