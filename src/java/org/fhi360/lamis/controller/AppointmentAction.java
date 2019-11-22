/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class AppointmentAction extends ActionSupport implements Preparable {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
    }
//setUploaded(0); 

    public String appointmentDate() {
        try {

            if (!request.getParameter("dateNextClinic").isEmpty() && !request.getParameter("dateNextRefill").isEmpty()) {
                query = "UPDATE patient SET date_next_clinic = ?, date_next_refill = ?, send_message = ?, time_stamp = NOW() WHERE facility_id = ? AND patient_id = ?";

                transactionTemplate.execute((ts) -> {
                    jdbcTemplate.update(query,
                             DateUtil.parseStringToSqlDate(request.getParameter("dateNextClinic"), "MM/dd/yyyy"),
                            DateUtil.parseStringToSqlDate(request.getParameter("dateNextRefill"), "MM/dd/yyyy"),
                            (request.getParameterMap().containsKey("enableSms") == true) ? Integer.parseInt(request.getParameter("sendMessage")) : 0,
                            (Long) session.getAttribute("facilityId"),
                            Long.parseLong(request.getParameter("patientId"))
                    );
                    return null; //To change body of generated lambdas, choose Tools | Templates.
                });

            } else {
                if (!request.getParameter("dateNextClinic").isEmpty()) {
                    query = "UPDATE patient SET date_next_clinic = ?, send_message = ?, time_stamp = NOW() WHERE facility_id = ? AND patient_id = ?";
                   
                  transactionTemplate.execute((ts) -> {
                    jdbcTemplate.update(query,
                             DateUtil.parseStringToSqlDate(request.getParameter("dateNextClinic"), "MM/dd/yyyy"),
              
                            (request.getParameterMap().containsKey("enableSms") == true) ? Integer.parseInt(request.getParameter("sendMessage")) : 0,
                            (Long) session.getAttribute("facilityId"),
                            Long.parseLong(request.getParameter("patientId"))
                    );
                    return null; //To change body of generated lambdas, choose Tools | Templates.
                });

                
                } else {
                    if (!request.getParameter("dateNextRefill").isEmpty()) {
                        query = "UPDATE patient SET date_next_refill = ?, send_message = ?, time_stamp = NOW() WHERE facility_id = ? AND patient_id = ?";
                       
                  transactionTemplate.execute((ts) -> {
                    jdbcTemplate.update(query,
                              DateUtil.parseStringToSqlDate(request.getParameter("dateNextRefill"), "MM/dd/yyyy"),
              
                            (request.getParameterMap().containsKey("enableSms") == true) ? Integer.parseInt(request.getParameter("sendMessage")) : 0,
                            (Long) session.getAttribute("facilityId"),
                            Long.parseLong(request.getParameter("patientId"))
                    );
                    return null; //To change body of generated lambdas, choose Tools | Templates.
                });
                    }
                }
            }
          
        } catch (Exception exception) {
             return ERROR;
        }
        return SUCCESS;
    }

}
