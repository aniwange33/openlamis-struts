/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.utility.builder;

import org.fhi360.lamis.utility.Scrambler;
import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.apache.commons.lang3.StringUtils;
import org.fhi360.lamis.utility.DateUtil;

public class EidReportListBuilder {

    private HttpSession session;
    private Boolean viewIdentifier;
    private Scrambler scrambler;
    private ArrayList<Map<String, Object>> eidList = new ArrayList<>();

    public EidReportListBuilder() {
        this.session = ServletActionContext.getRequest().getSession();
        this.scrambler = new Scrambler();
        if (ServletActionContext.getRequest().getSession().getAttribute("viewIdentifier") != null) {
            this.viewIdentifier = (Boolean) session.getAttribute("viewIdentifier");
        }
    }

    public ArrayList<Map<String, Object>> buildList(ResultSet resultSet) throws SQLException {
        boolean eidIdColumn = contains(resultSet, "eid_id");
        boolean facilityIdColumn = contains(resultSet, "facility_id");
        boolean labnoColumn = contains(resultSet, "labno");
        boolean motherNameColumn = contains(resultSet, "mother_name");
        boolean motherAddressColumn = contains(resultSet, "mother_address");
        boolean motherPhoneColumn = contains(resultSet, "mother_phone");
        boolean senderNameColumn = contains(resultSet, "sender_name");
        boolean senderAddressColumn = contains(resultSet, "sender_address");
        boolean senderDesignationColumn = contains(resultSet, "sender_designation");
        boolean senderPhoneColumn = contains(resultSet, "sender_phone");
        boolean reasonPcrColumn = contains(resultSet, "reason_pcr");
        boolean rapidTestDoneColumn = contains(resultSet, "rapid_test_done");
        boolean dateRapidTestColumn = contains(resultSet, "date_rapid_test");
        boolean rapidTestResultColumn = contains(resultSet, "date_rapid_result");
        boolean motherArtReceivedColumn = contains(resultSet, "mother_art_received");
        boolean motherProphylaxReceivedColumn = contains(resultSet, "mother_prophylax_received");
        boolean childProphylaxReceivedColumn = contains(resultSet, "child_prophylax_received");
        boolean breastfedEverColumn = contains(resultSet, "breastfed_ever");
        boolean feedingMethodColumn = contains(resultSet, "feeding_method");
        boolean breastfedNowColumn = contains(resultSet, "breastfed_now");
        boolean feedingCessationAgeColumn = contains(resultSet, "feeding_cessation_age");
        boolean cotrimColumn = contains(resultSet, "cotrim");
        boolean nextAppointmentColumn = contains(resultSet, "next_appointment");

        try {
            while (resultSet.next()) {
                String eidId = "";
                if (eidIdColumn) {
                    eidId = Long.toString(resultSet.getLong("eid_id"));
                }
                String facilityId = "";
                if (facilityIdColumn) {
                    facilityId = Long.toString(resultSet.getLong("facility_id"));
                }
                String labno = "";
                if (labnoColumn) {
                    labno = resultSet.getObject("labno") == null ? "" : resultSet.getString("labno");
                }
                String motherName = "";
                if (motherNameColumn) {
                    motherName = resultSet.getObject("mother_name") == null ? "" : resultSet.getString("mother_name");
                    motherName = (viewIdentifier) ? scrambler.unscrambleCharacters(motherName) : motherName;
                    motherName = StringUtils.upperCase(motherName);
                }
                String motherAddress = "";
                if (motherAddressColumn) {
                    motherAddress = resultSet.getObject("mother_address") == null ? "" : resultSet.getString("mother_address");
                    motherAddress = (viewIdentifier) ? scrambler.unscrambleCharacters(motherAddress) : motherAddress;
                    motherAddress = StringUtils.capitalize(motherAddress);
                }
                String motherPhone = "";
                if (motherPhoneColumn) {
                    motherPhone = resultSet.getObject("mother_phone") == null ? "" : resultSet.getString("mother_phone");
                    motherPhone = (viewIdentifier) ? scrambler.unscrambleNumbers(motherPhone) : motherPhone;
                }
                String senderName = "";
                if (senderNameColumn) {
                    senderName = resultSet.getObject("sender_name") == null ? "" : resultSet.getString("sender_name");
                }
                String senderDesignation = "";
                if (senderDesignationColumn) {
                    senderDesignation = resultSet.getObject("sender_designation") == null ? "" : resultSet.getString("sender_designation");
                }
                String senderPhone = "";
                if (senderPhoneColumn) {
                    senderPhone = resultSet.getObject("sender_phone") == null ? "" : resultSet.getString("sender_phone");
                }
                String senderAddress = "";
                if (senderAddressColumn) {
                    senderAddress = resultSet.getObject("sender_address") == null ? "" : resultSet.getString("sender_address");
                }
                String reasonPcr = "";
                if (reasonPcrColumn) {
                    reasonPcr = resultSet.getObject("reason_pcr") == null ? "" : resultSet.getString("reason_pcr");
                }
                String rapidTestDone = "";
                if (rapidTestDoneColumn) {
                    rapidTestDone = resultSet.getObject("rapid_test_done") == null ? "" : resultSet.getString("rapid_test_done");
                }
                String dateRapidTest = "";
                if (dateRapidTestColumn) {
                    dateRapidTest = resultSet.getObject("date_rapid_test") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_rapid_test"), "MM/dd/yyyy");
                }
                String rapidTestResult = "";
                if (rapidTestResultColumn) {
                    rapidTestResult = resultSet.getObject("date_rapid_test") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_rapid_test"), "MM/dd/yyyy");
                }

                String motherArtReceived = "";
                if (motherArtReceivedColumn) {
                    motherArtReceived = resultSet.getObject("mother_art_received") == null ? "" : resultSet.getString("mother_art_received");
                }
                String motherProphylaxReceived = "";
                if (motherProphylaxReceivedColumn) {
                    motherProphylaxReceived = resultSet.getObject("mother_prophylax_received") == null ? "" : resultSet.getString("mother_prophylax_received");
                }
                String childProphylaxReceived = "";
                if (childProphylaxReceivedColumn) {
                    childProphylaxReceived = resultSet.getObject("child_prophylax_received") == null ? "" : resultSet.getString("child_prophylax_received");
                }
                String breastfedEver = "";
                if (breastfedEverColumn) {
                    breastfedEver = resultSet.getObject("breastfed_ever") == null ? "" : resultSet.getString("breastfed_ever");
                }
                String feedingMethod = "";
                if (feedingMethodColumn) {
                    feedingMethod = resultSet.getObject("feeding_method") == null ? "" : resultSet.getString("feeding_method");
                }
                String breastfedNow = "";
                if (breastfedNowColumn) {
                    breastfedNow = resultSet.getObject("breastfed_now") == null ? "" : resultSet.getString("breastfed_now");
                }
                String feedingCessationAge = "";
                if (feedingCessationAgeColumn) {
                    feedingCessationAge = resultSet.getObject("feeding_cessation_age") == null ? "" : Integer.toString(resultSet.getInt("feeding_cessation_age"));
                }
                String cotrim = "";
                if (cotrimColumn) {
                    cotrim = resultSet.getObject("cotrim") == null ? "" : resultSet.getString("cotrim");
                }
                String nextAppointment = "";
                if (nextAppointmentColumn) {
                    nextAppointment = resultSet.getObject("next_appointment") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("next_appointment"), "MM/dd/yyyy");
                }
                // create an array from object properties 
                Map<String, Object> map = new TreeMap<String, Object>();
                map.put("eidId", eidId);
                map.put("facilityId", facilityId);
                map.put("labno", labno);
                map.put("motherName", motherName);
                map.put("motherAddress", motherAddress);
                map.put("motherPhone", motherPhone);
                map.put("senderName", senderName);
                map.put("senderAddress", senderAddress);
                map.put("senderDesignation", senderDesignation);
                map.put("senderPhone", senderPhone);
                map.put("reasonPcr", reasonPcr);
                map.put("rapidTestDone", rapidTestDone);
                map.put("dateRapidTest", dateRapidTest);
                map.put("rapidTestResult", rapidTestResult);
                map.put("motherArtReceived", motherArtReceived);
                map.put("motherProphylaxReceived", motherProphylaxReceived);
                map.put("childProphylaxReceived", childProphylaxReceived);
                map.put("breastfedEver", breastfedEver);
                map.put("feedingMethod", feedingMethod);
                map.put("breastfedNow", breastfedNow);
                map.put("feedingCessationAge", feedingCessationAge);
                map.put("cotrim", cotrim);
                map.put("nextAppointment", nextAppointment);
                eidList.add(map);
            } //end while
        } catch (SQLException sqlException) {
            throw sqlException;
        }
        return eidList;
    }

    private boolean contains(ResultSet rs, String column) {
        try {
            rs.findColumn(column);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
