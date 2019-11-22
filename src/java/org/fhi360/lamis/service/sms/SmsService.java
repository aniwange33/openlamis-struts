/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.service.sms;

import org.fhi360.lamis.utility.PropertyAccessor;
import org.fhi360.lamis.dao.hibernate.SentDAO;
import java.util.Date;
import java.io.File;

import org.fhi360.lamis.dao.hibernate.UnsentDAO;
import org.fhi360.lamis.model.Sent;
import org.fhi360.lamis.utility.Scrambler;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.time.DateUtils;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.StringUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class SmsService {

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private Scrambler scrambler;
    private SmsMessageSender smsMessageSender;
    private Map<String, Object> map;

    public SmsService() {
        this.scrambler = new Scrambler();
        this.smsMessageSender = new SmsMessageSender();
        this.map = new HashMap<>();
    }

    public void init() {
        //This method is called by the SmsListener each time the application started
        //The method initializes the activity_tracker property file 
        try {
            File file = new File("activity_tracker.properties");
            if (!file.exists()) {
                map.put("dateLastDqa", new SimpleDateFormat("MMM yyyy").format(new Date()));
                map.put("dateLastAsyncTask", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                map.put("dateLastSms", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                map.put("appointmentMessages", "0");
                map.put("dailyMessages", "0");
                map.put("specificMessages", "0");
                new PropertyAccessor().setActivityTrackerProperties(map);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public synchronized void runSchedule() {
        //This method is called by the SmsJobScheduler class to start sending sms via the selected gateway (modem or internet)
        try {
            File file = new File("activity_tracker.properties");
            if (!file.exists()) {
                map.put("dateLastDqa", new SimpleDateFormat("MMM yyyy").format(new Date()));
                map.put("dateLastAsyncTask", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                map.put("dateLastSms", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                map.put("appointmentMessages", "0");
                map.put("dailyMessages", "0");
                map.put("specificMessages", "0");
                new PropertyAccessor().setActivityTrackerProperties(map);
            } else {
                map = new PropertyAccessor().getActivityTrackerProperties();
                Date dateLastSms = new SimpleDateFormat("yyyy-MM-dd").parse((String) map.get("dateLastSms"));
                if (!DateUtils.isSameDay(new Date(), dateLastSms)) {
                    map.put("dateLastSms", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                    map.put("appointmentMessages", "0");
                    map.put("dailyMessages", "0");
                    map.put("specificMessages", "0");
                    new PropertyAccessor().setActivityTrackerProperties(map);
                }
            }

            file = new File("activity_tracker.properties");
            if (file.exists()) {
                map = new PropertyAccessor().getActivityTrackerProperties();
                String appointmentMessages = (String) map.get("appointmentMessages");
                String dailyMessages = (String) map.get("dailyMessages");
                String specificMessages = (String) map.get("specificMessages");

                smsFailedMessages();  // send previous failed messages first
                if (appointmentMessages.equals("0")) {
                    smsAppointmentMessages();     // send sms for due appointments
                }
                if (dailyMessages.equals("0")) {
                    smsDailyMessages();   // send sms for daily messages
                }
                if (specificMessages.equals("0")) {
                    smsSpecificMessages();     // send sms for on specific dates
                }
                map.put("dateLastSms", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                new PropertyAccessor().setActivityTrackerProperties(map);
            } else {
                System.out.println("Activity properties file not found .....");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void smsAppointmentMessages() {
        //Send clients appointment messages
        final String[] phone = {""};
        final String[] message = {""};
        int due = getDaysToAppointment();
        Date expire = DateUtils.addDays(new Date(), due);
        String query = "SELECT message1, message2, message3, message4, recipients FROM message WHERE message_type = 1";
        jdbcTemplate.query(query, messages -> {
            //String query = "SELECT DISTINCT phone, send_message FROM patient WHERE send_message != 0";              
            String query1 = "SELECT DISTINCT phone, send_message FROM patient "
                    + "WHERE (DATEDIFF(DAY, CURDATE(), date_next_clinic) = " + due
                    + " OR DATEDIFF(DAY, CURDATE(), date_next_refill) = " + due + ") AND send_message != 0";
            if (messages.getString("recipients").equalsIgnoreCase("ART Only")) {
                query1 = query1 + " AND current_status IN ('ART Start', 'ART Restart', 'ART Transfer In')";
            } else {
                if (messages.getString("recipients").equalsIgnoreCase("Pre-ART Only")) {
                    query1 = query1 + " AND current_status IN ('HIV+ non ART', 'Pre-ART Transfer In')";
                } else {
                    if (messages.getString("recipients").equalsIgnoreCase("Defaulter Only")) {
                        query1 = query1 + " AND current_status IN ('Lost to Follow Up')";
                    }
                }
            }
            jdbcTemplate.query(query1, resultSet -> {
                while (resultSet.next()) {
                    phone[0] = scrambler.unscrambleNumbers(resultSet.getString("phone").trim());
                    if (isValidPhone(phone[0])) {
                        if (resultSet.getInt("send_message") == 2) {
                            message[0] = messages.getString("message2");
                        }
                        if (resultSet.getInt("send_message") == 3) {
                            message[0] = messages.getString("message3");
                        }
                        if (resultSet.getInt("send_message") == 4) {
                            message[0] = messages.getString("message4");
                        }
                        if (message[0].equals("")) {
                            message[0] = messages.getString("message1");
                        }

                        String messageStatus = smsMessageSender.send(message[0], phone[0]);
                        if (messageStatus.equals("SENT")) {
                            logSentSms(scrambler.scrambleNumbers(phone[0]), message[0]);
                        } else if ("FAILED".equals(messageStatus) || "UNSENT".equals(messageStatus)) {
                            logUnsentSms(scrambler.scrambleNumbers(phone[0]), message[0], expire);
                        }
                    }
                }
                return null;
            });
            map.put("appointmentMessages", "1");
        });
    }

    private void smsDailyMessages() {
        //Send daily messages if any has been setup
        final String phone[] = {""};
        final String message[] = {""};
        Date expire = new Date();
        String query = "SELECT message1, message2, message3, message4, recipients FROM message WHERE message_type = 2";
        jdbcTemplate.query(query, rs -> {
            String query1 = "SELECT DISTINCT phone, send_message FROM patient WHERE send_message != 0";
            if (rs.getString("recipients").equalsIgnoreCase("ART Only")) {
                query1 = query1 + " AND current_status IN ('ART Start', 'ART Restart', 'ART Transfer In')";
            } else {
                if (rs.getString("recipients").equalsIgnoreCase("Pre-ART Only")) {
                    query1 = query1 + " AND current_status IN ('HIV+ non ART', 'Pre-ART Transfer In')";
                } else {
                    if (rs.getString("recipients").equalsIgnoreCase("Defaulter Only")) {
                        query1 = query1 + " AND current_status IN ('Lost to Follow Up')";
                    }
                }
            }
            jdbcTemplate.query(query1, resultSet -> {
                while (resultSet.next()) {
                    phone[0] = scrambler.unscrambleNumbers(resultSet.getString("phone").trim());
                    if (isValidPhone(phone[0])) {
                        if (resultSet.getInt("send_message") == 2) {
                            message[0] = rs.getString("message2");
                        }
                        if (resultSet.getInt("send_message") == 3) {
                            message[0] = rs.getString("message3");
                        }
                        if (resultSet.getInt("send_message") == 4) {
                            message[0] = rs.getString("message4");
                        }
                        if (message.equals("")) {
                            message[0] = rs.getString("message1");
                        }

                        String messageStatus = smsMessageSender.send(message[0], phone[0]);
                        if (messageStatus.equals("SENT")) {
                            logSentSms(scrambler.scrambleNumbers(phone[0]), message[0]);
                        } else if ("FAILED".equals(messageStatus) || "UNSENT".equals(messageStatus)) {
                            logUnsentSms(scrambler.scrambleNumbers(phone[0]), message[0], expire);
                        }
                    }
                }
                return null;
            });
            map.put("dailyMessages", "1");
        });
    }

    private void smsSpecificMessages() {
        //Send messages setup to be sent on a specific date
        final String phone[] = {""};
        final String message[] = {""};
        Date expire = new Date();
        String query = "SELECT message1, message2, message3, message4, recipients FROM message WHERE date_to_send = CURDATE() AND message_type = 3";
        jdbcTemplate.query(query, messages -> {
            String query1 = "SELECT DISTINCT phone, send_message FROM patient WHERE send_message != 0";
            if (messages.getString("recipients").equalsIgnoreCase("ART Only")) {
                query1 = query1 + " AND current_status IN ('ART Start', 'ART Restart', 'ART Transfer In')";
            } else {
                if (messages.getString("recipients").equalsIgnoreCase("Pre-ART Only")) {
                    query1 = query1 + " AND current_status IN ('HIV+ non ART', 'Pre-ART Transfer In')";
                } else {
                    if (messages.getString("recipients").equalsIgnoreCase("Defaulter Only")) {
                        query1 = query1 + " AND current_status IN ('Lost to Follow Up')";
                    }
                }
            }
            jdbcTemplate.query(query1, resultSet -> {
                while (resultSet.next()) {
                    phone[0] = scrambler.unscrambleNumbers(resultSet.getString("phone").trim());
                    if (isValidPhone(phone[0])) {
                        if (resultSet.getInt("send_message") == 2) {
                            message[0] = messages.getString("message2");
                        }
                        if (resultSet.getInt("send_message") == 3) {
                            message[0] = messages.getString("message3");
                        }
                        if (resultSet.getInt("send_message") == 4) {
                            message[0] = messages.getString("message4");
                        }
                        if (message.equals("")) {
                            message[0] = messages.getString("message1");
                        }

                        String messageStatus = smsMessageSender.send(message[0], phone[0]);
                        if (messageStatus.equals("SENT")) {
                            logSentSms(scrambler.scrambleNumbers(phone[0]), message[0]);
                        } else if ("FAILED".equals(messageStatus) || "UNSENT".equals(messageStatus)) {
                            logUnsentSms(scrambler.scrambleNumbers(phone[0]), message[0], expire);
                        }
                    }
                }
                return null;
            });

            map.put("specificMessages", "1");
            return null;
        });
    }

    private void smsFailedMessages() {
        //Check for unsent messages and resend
        String query = "SELECT unsent_id, phone, message FROM unsent WHERE expire >= CURDATE()";
        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                String phone = scrambler.unscrambleNumbers(resultSet.getString("phone").trim());
                String message = resultSet.getString("message");

                String messageStatus = smsMessageSender.send(message, phone);
                if ("SENT".equals(messageStatus)) {
                    logSentSms(scrambler.scrambleNumbers(phone), message);
                    UnsentDAO.delete(resultSet.getLong("unsent_id"));
                }
            }
            return null;
        });
    }

    public void logSentSms(String phone, String message) {
        //Log sms that has been sent
        Sent sent = new Sent();
        sent.setPhone(phone);
        sent.setMessage(message);
        sent.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
        SentDAO.save(sent);
    }

    private void logUnsentSms(String phone, String message, Date expire) {
        //Log unsent sms
        String query = "SELECT unsent_id FROM unsent WHERE phone = '" + phone + "' AND message = '" + message + "' AND expire = '" + expire + "'";
        jdbcTemplate.query(query, resultSet -> {
            String query1 = "INSERT INTO unsent(phone, message, expire) VALUES('" + phone + "','" + message + "','" + expire + "')";
            executeUpdate(query);
        });
    }

    private int getDaysToAppointment() {
        String query = "SELECT days_to_appointment FROM message WHERE message_type = 1";
        Integer days = jdbcTemplate.queryForObject(query, Integer.class);
        return days != null ? days : 0;
    }

    private void executeUpdate(String query) {
        transactionTemplate.execute(ts -> {
            jdbcTemplate.execute(query);
            return null;
        });
    }

    private boolean isValidPhone(String phone) {
        if (!StringUtil.isInteger(phone)) {
            return false;
        }
        if (phone.length() != 11) {
            return false;
        }
        return true;
    }
}

//update patient set send_message = 1 where date_started is not null and current_status IN ('HIV+ non ART', 'ART Start', 'ART Restart', 'ART Transfer In', 'Pre-ART Transfer In')
//update patient set send_message = 1 where state in ('Rivers', 'Lagos', 'Akwa Ibom') AND current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') 
