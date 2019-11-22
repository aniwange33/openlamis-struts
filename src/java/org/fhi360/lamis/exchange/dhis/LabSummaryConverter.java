/**
 *
 * @author AALOZIE
 */

package org.fhi360.lamis.exchange.dhis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.utility.DateUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class LabSummaryConverter {
    //private static final Log log = LogFactory.getLog(ArtSummaryProcessor.class);
    private int reportingMonth;
    private int reportingYear;
    private String reportingDateBegin;
    private String reportingDateEnd;
    private int [][] value = new int[18][6];

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
     private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    private long facilityId;
    private String facility;
    private String state;
    private String lga;

    public LabSummaryConverter() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();        
    }
    
    public String convertXml() {
        reportingMonth = DateUtil.getMonth(request.getParameter("reportingMonth"));
        reportingYear = Integer.parseInt(request.getParameter("reportingYear"));
        reportingDateBegin = dateFormat.format(DateUtil.getFirstDateOfMonth(reportingYear, reportingMonth));
        reportingDateEnd = dateFormat.format(DateUtil.getLastDateOfMonth(reportingYear, reportingMonth));
        initScriptId();
        
        String[] ids =  request.getParameter("facilityIds").split(",");
        for(String id : ids) {
            facilityId = Long.parseLong(id);
            process();
        }
        
        return "";

    }
    
    private void process() {
        
    }
    
    private void save() {
        for(int i = 0; i < 18; i++) {
            int scriptId = value[i][0];
            int dataValue = value[i][1];
            query = "UPDATE indicator SET state = '" + state + "', lga = '" + lga + "', facility = '" + facility + "', value = '" + dataValue + "' WHERE script_id = '" + scriptId;
            executeUpdate(query);
        }            
    }
    
    private void initScriptId() {
        value[0][0] = 1;          
        value[1][0] = 2;          
        value[2][0] = 3;          
        value[3][0] = 4;          
        value[4][0] = 5;          
        value[5][0] = 6;          
    }
    
    private void clearValue() {
        value[0][1] = 0;          
        value[1][1] = 0;          
        value[2][1] = 0;          
        value[3][1] = 0;          
        value[4][1] = 0;          
        value[5][1] = 0;                  
    }
    
    private void executeUpdate(String query) {
           try {
            transactionTemplate.execute((ts) -> {
                jdbcTemplate.execute(query);
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });
        }
        catch (Exception exception) {
          
        }        
    }        
    
}