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

public class SpecimenReportListBuilder {

    private HttpSession session;
    private Boolean viewIdentifier;
    private Scrambler scrambler;

    private ArrayList<Map<String, Object>> specimenList = new ArrayList<>();
    private Map<String, Map<String, Object>> sortedMaps = new TreeMap<>();

    public SpecimenReportListBuilder() {
        this.session = ServletActionContext.getRequest().getSession();
        this.scrambler = new Scrambler();
        if (ServletActionContext.getRequest().getSession().getAttribute("viewIdentifier") != null) {
            this.viewIdentifier = (Boolean) session.getAttribute("viewIdentifier");
        }
    }

    public ArrayList<Map<String, Object>> buildList(ResultSet resultSet) throws SQLException {
        boolean specimenIdColumn = contains(resultSet, "specimen_id");
        boolean specimenTypeColumn = contains(resultSet, "specimen_type");
        boolean labnoColumn = contains(resultSet, "labno");
        boolean barcodeColumn = contains(resultSet, "barcode");
        boolean facilityIdColumn = contains(resultSet, "facility_id");
        boolean stateIdColumn = contains(resultSet, "state_id");
        boolean treatmentUnitIdColumn = contains(resultSet, "treatment_unit_id");
        boolean facilityNameColumn = contains(resultSet, "facility_name");
        boolean dateReceivedColumn = contains(resultSet, "date_received");
        boolean dateCollectedColumn = contains(resultSet, "date_collected");
        boolean dateAssayColumn = contains(resultSet, "date_assay");
        boolean dateReportedColumn = contains(resultSet, "date_reported");
        boolean dateDispatchedColumn = contains(resultSet, "date_dispatched");
        boolean qualityCntrlColumn = contains(resultSet, "quality_cntrl");
        boolean resultColumn = contains(resultSet, "result");
        boolean reasonNoTestColumn = contains(resultSet, "reason_no_test");
        boolean hospitalNumColumn = contains(resultSet, "hospital_num");
        boolean surnameColumn = contains(resultSet, "surname");
        boolean otherNamesColumn = contains(resultSet, "other_names");
        boolean genderColumn = contains(resultSet, "gender");
        boolean dateBirthColumn = contains(resultSet, "date_birth");
        boolean ageColumn = contains(resultSet, "age");
        boolean ageUnitColumn = contains(resultSet, "age_unit");
        boolean addressColumn = contains(resultSet, "address");
        boolean phoneColumn = contains(resultSet, "phone");
        boolean transitTimeColumn = contains(resultSet, "transit_time");
        boolean turnaroundTimeColumn = contains(resultSet, "turnaround_time");

        try {
            while (resultSet.next()) {
                String specimenId = "";
                if (specimenIdColumn) {
                    specimenId = Long.toString(resultSet.getLong("specimen_id"));
                }
                String specimenType = "";
                if (specimenTypeColumn) {
                    specimenType = resultSet.getString("specimen_type") == null ? "" : resultSet.getString("specimen_type");
                }
                String labno = "";
                if (labnoColumn) {
                    labno = resultSet.getString("labno") == null ? "" : resultSet.getString("labno");
                }
                String barcode = "";
                if (barcodeColumn) {
                    barcode = resultSet.getString("barcode") == null ? "" : resultSet.getString("barcode");
                }
                String facilityId = "";
                if (facilityIdColumn) {
                    facilityId = Long.toString(resultSet.getLong("facility_id"));
                }
                String facilityName = "";
                if (facilityNameColumn) {
                    facilityName = resultSet.getString("facility_name");
                }
                String stateId = "";
                if (stateIdColumn) {
                    stateId = Long.toString(resultSet.getLong("state_id"));
                }

                String treatmentUnitId = "";
                if (treatmentUnitIdColumn) {
                    treatmentUnitId = Long.toString(resultSet.getLong("treatment_unit_id"));
                }
                String dateReceived = "";
                if (dateReceivedColumn) {
                    dateReceived = resultSet.getObject("date_received") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_received"), "MM/dd/yyyy");
                }
                String dateCollected = "";
                if (dateCollectedColumn) {
                    dateCollected = resultSet.getObject("date_collected") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_collected"), "MM/dd/yyyy");
                }

                String dateAssay = "";
                if (dateAssayColumn) {
                    dateAssay = resultSet.getObject("date_assay") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_assay"), "MM/dd/yyyy");
                }

                String dateReported = "";
                if (dateReportedColumn) {
                    dateReported = resultSet.getObject("date_reported") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_reported"), "MM/dd/yyyy");
                }
                String dateDispatched = "";
                if (dateDispatchedColumn) {
                    dateDispatched = resultSet.getObject("date_dispatched") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_dispatched"), "MM/dd/yyyy");
                }
                String qualityCntrl = "";
                if (qualityCntrlColumn) {
                    qualityCntrl = resultSet.getInt("quality_cntrl") == 0 ? "" : Integer.toString(resultSet.getInt("quality_cntrl"));
                }
                String hospitalNum = "";
                if (hospitalNumColumn) {
                    hospitalNum = resultSet.getObject("hospital_num") == null ? "" : resultSet.getString("hospital_num");
                }
                String result = "";
                if (resultColumn) {
                    result = resultSet.getObject("result") == null ? "" : resultSet.getString("result");
                }
                String reasonNoTest = "";
                if (reasonNoTestColumn) {
                    reasonNoTest = resultSet.getObject("reason_no_test") == null ? "" : resultSet.getString("reason_no_test");
                }
                String surname = "";
                if (surnameColumn) {
                    surname = resultSet.getObject("surname") == null ? "" : resultSet.getString("surname");
                    surname = (viewIdentifier) ? scrambler.unscrambleCharacters(surname) : surname;
                    surname = StringUtils.upperCase(surname);
                }
                String otherNames = "";
                if (otherNamesColumn) {
                    otherNames = resultSet.getObject("other_names") == null ? "" : resultSet.getString("other_names");
                    otherNames = (viewIdentifier) ? scrambler.unscrambleCharacters(otherNames) : otherNames;
                    otherNames = StringUtils.capitalize(otherNames);
                }
                String gender = "";
                if (genderColumn) {
                    gender = resultSet.getObject("gender") == null ? "" : resultSet.getString("gender");
                }
                String dateBirth = "";
                if (dateBirthColumn) {
                    dateBirth = resultSet.getObject("date_birth") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_birth"), "MM/dd/yyyy");
                }
                String age = "";
                if (ageColumn) {
                    age = resultSet.getObject("age") == null ? "" : resultSet.getInt("age") == 0 ? "" : Integer.toString(resultSet.getInt("age"));
                }
                String ageUnit = "";
                if (ageUnitColumn) {
                    ageUnit = resultSet.getObject("age_unit") == null ? "" : resultSet.getString("age_unit");
                }
                String address = "";
                if (addressColumn) {
                    address = resultSet.getObject("address") == null ? "" : resultSet.getString("address");
                    address = (viewIdentifier) ? scrambler.unscrambleCharacters(address) : address;
                    address = StringUtils.capitalize(address);
                }
                String phone = "";
                if (phoneColumn) {
                    phone = resultSet.getObject("phone") == null ? "" : resultSet.getString("phone");
                    phone = (viewIdentifier) ? scrambler.unscrambleNumbers(phone) : phone;
                }

                String transitTime = "";
                String threshold1 = "";
                if (transitTimeColumn) {
                    if (resultSet.getInt("transit_time") != 0) {
                        transitTime = Integer.toString(resultSet.getInt("transit_time"));
                        threshold1 = (resultSet.getInt("transit_time") > 10) ? "Above" : "Below";
                    }
                }
                String turnaroundTime = "";
                String threshold2 = "";
                if (turnaroundTimeColumn) {
                    if (resultSet.getInt("turnaround_time") != 0) {
                        turnaroundTime = Integer.toString(resultSet.getInt("turnaround_time"));
                        threshold2 = (resultSet.getInt("turnaround_time") > 10) ? "Above" : "Below";
                    }
                }

                // create an array from object properties 
                Map<String, Object> map = new HashMap<>();
                map.put("specimenId", specimenId);
                map.put("specimenType", specimenType);
                map.put("labno", labno);
                map.put("barcode", barcode);
                map.put("facilityId", facilityId);
                map.put("facilityName", facilityName);
                map.put("stateId", stateId);
                map.put("treatmentUnitId", treatmentUnitId);
                map.put("dateReceived", dateReceived);
                map.put("dateCollected", dateCollected);
                map.put("dateAssay", dateAssay);
                map.put("dateReported", dateReported);
                map.put("dateDispatched", dateDispatched);
                map.put("qualityCntrl", qualityCntrl);
                map.put("result", result);
                map.put("reasonNoTest", reasonNoTest);
                map.put("hospitalNum", hospitalNum);
                map.put("surname", surname);
                map.put("otherNames", otherNames);
                map.put("name", surname + ' ' + otherNames);
                map.put("gender", gender);
                map.put("dateBirth", dateBirth);
                map.put("age", age);
                map.put("ageUnit", ageUnit);
                map.put("address", address);
                map.put("phone", phone);
                map.put("transitTime", transitTime);
                map.put("turnaroundTime", turnaroundTime);
                map.put("threshold1", threshold1);
                map.put("threshold2", threshold2);
                specimenList.add(map);
            }
        } catch (SQLException sqlException) {
            throw sqlException;
        }
        return specimenList;
    }

    private boolean contains(ResultSet rs, String column) {
        try {
            rs.findColumn(column);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private void addSortedMapsToList() {
        for (Map.Entry<String, Map<String, Object>> entry : sortedMaps.entrySet()) {
            specimenList.add(entry.getValue());
        }
        sortedMaps = new TreeMap<>();
    }
}
