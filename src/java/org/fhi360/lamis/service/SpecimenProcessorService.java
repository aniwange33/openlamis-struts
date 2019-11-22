/**
 *
 * @author user1
 */
package org.fhi360.lamis.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.fhi360.lamis.service.sms.ModemGatewayService;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.Scrambler;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class SpecimenProcessorService {

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private static ModemGatewayService smsGatewayService;

    public static synchronized void saveResult() {
        String query = "SELECT labno, result, date_assay FROM labresult";  //labresult is a temp table created during labfile parsing in the class LabFileParser

        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                String labno = resultSet.getString("labno"); //LabNumberNormalizer.normalize(resultSet.getString("labno"));
                String result = resultSet.getString("result");
                java.sql.Date dateAssay = resultSet.getDate("date_assay");
                executeUpdate("UPDATE specimen SET result = '" + result + "', "
                        + "date_assay = '" + dateAssay + "', date_reported = "
                        + "CURDATE() WHERE labno = '" + labno.trim().toUpperCase() + "'");
            }
            return null;
        });
    }

    public static synchronized void dispatchResult() {
        smsGatewayService = new ModemGatewayService();
        Scrambler scrambler = new Scrambler();
        String query = "SELECT * FROM dispatcher ORDER BY name";  //dispatcher temp table is created in the class SpecimenGridManager when results are retrieved for dispatch 
        try {
            if (!smsGatewayService.isStarted()) {
                smsGatewayService.startSmsService();
            }

            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String hospitalNum = resultSet.getString("hospital_num");
                    String surname = scrambler.unscrambleCharacters(resultSet.getString("surname"));
                    String otherNames = scrambler.unscrambleCharacters(resultSet.getString("other_names"));
                    String name = surname + " " + otherNames;
                    String gender = resultSet.getString("gender");
                    String labno = resultSet.getString("labno");
                    String result = resultSet.getString("result");
                    String facilityName = resultSet.getString("name");
                    String phone = resultSet.getString("phone2");
                    String dateAssay = (resultSet.getDate("date_assay") == null) ? "" : DateUtil.parseDateToString(resultSet.getDate("date_assay"), "MM/dd/yyyy");
                    String message = "81Patient ID: " + hospitalNum + ", Name: " + name + ", Gender: " + gender + ", Ref No: " + labno + ", HIV-1 DNAPCR Result: " + result + ", Hospital: " + facilityName + ", Assay Date: " + dateAssay;

                    if (phone != null || !phone.equals("")) {
                        try {
                            String messageStatus = smsGatewayService.sendSms(phone, message);
                            if (messageStatus.equals("SENT")) {
                                executeUpdate("UPDATE specimen SET date_dispatched = CURDATE() WHERE labno = '" + labno + "'");
                            } else if ("FAILED".equals(messageStatus) || "UNSENT".equals(messageStatus)) {
                                System.out.println("Result dispatched failed......");
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(SpecimenProcessorService.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (ServletActionContext.getRequest().getParameterMap().containsKey("forwardSms")) {
                        forwardSms(labno, message);
                    }
                }
                return null;
            });
        } catch (Exception ignored) {

        }
    }

    public static void sendSms(String phone, String message) {
        smsGatewayService = new ModemGatewayService();
        try {
            if (!smsGatewayService.isStarted()) {
                smsGatewayService.startSmsService();
            }
            if (phone != null || !phone.equals("")) {
                smsGatewayService.sendSms(phone, message);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static void forwardSms(String labno, String message) {
        String query = "SELECT sender_phone FROM eid WHERE labno = '" + labno + "'";
        jdbcTemplate.query(query, resultSet -> {
            String phone = resultSet.getString("sender_phone");
            if (phone != null || !phone.equals("")) {
                try {
                    smsGatewayService.sendSms(phone, message);
                } catch (Exception ex) {
                    Logger.getLogger(SpecimenProcessorService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private static void executeUpdate(String query) {
        transactionTemplate.execute(ts -> {
            jdbcTemplate.execute(query);
            return null;
        });
    }

}
