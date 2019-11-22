/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.controller.grid;

import static com.opensymphony.xwork2.Action.SUCCESS;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import java.util.ArrayList;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.dao.hibernate.PrescriptionDAO;
import org.fhi360.lamis.model.Prescription;
import org.fhi360.lamis.model.dto.PrescriptionDTO;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.Constants;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.builder.PharmacyListBuilder;
import org.fhi360.lamis.utility.builder.PrescriptionListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author user10
 */
public class PrescriptionGridAction extends ActionSupport implements Preparable {

    private Integer page;
    private Integer rows;
    private String sidx;
    private String sord;
    private Integer totalpages;
    private Integer currpage;
    private Integer totalrecords;
    private String regimentypeId;
    private String regimenId;
    private Long facilityId;
    private Long patientId;
    ;
    private Long userId;
    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private ArrayList<Map<String, String>> regimenList = new ArrayList<>();
    private List<String> drugList = new ArrayList<>();
    private List<String> labtestList = new ArrayList<>();
    private List<Map<String, String>> prescriptionList = new ArrayList<>();
    private ArrayList<Map<String, String>> dispenserList = new ArrayList<>();
    private ArrayList<Map<String, String>> prescribedList = new ArrayList<>();

    //For Lab Test
    private List<String> labtestPrescriptionList = new ArrayList<>();
    private ArrayList<Map<String, String>> labresultList = new ArrayList<>();
    private ArrayList<Map<String, String>> labPrescribedList = new ArrayList<>();

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
        userId = (Long) session.getAttribute("userId");
    }

    public String getRegimenForSelectedType() {
        try {
            if (request.getParameterMap().containsKey("regimentypeId")) {
                regimentypeId = request.getParameter("regimentypeId");
                query = "SELECT * FROM regimen WHERE regimentype_id = ? ORDER BY description";

                if (regimentypeId != null && !"".equals(regimentypeId)) {

                    jdbcTemplate.query(query, resultSet -> {
                        try {
                            new PharmacyListBuilder().buildRegimenList(resultSet, "");
                            regimenList = new PharmacyListBuilder().retrieveRegimenList();

                        } catch (Exception ex) {
                            Logger.getLogger(PrescriptionGridAction.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        return null;
                    }, Long.parseLong(regimentypeId));

                } else {
                    regimenList = new ArrayList<>();
                }
            } else if (request.getParameterMap().containsKey("regimen_ids")) {
                regimenId = request.getParameter("regimen_ids");

                if (regimenId != null && !"".equals(regimenId)) {
                    query = "SELECT * FROM regimen WHERE regimen_id IN (" + regimenId + ") ORDER BY description";
                    jdbcTemplate.query(query, resultSet -> {
                        try {
                            new PharmacyListBuilder().buildRegimenList(resultSet, "");
                            regimenList = new PharmacyListBuilder().retrieveRegimenList();
                        } catch (Exception ex) {
                            Logger.getLogger(PrescriptionGridAction.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        return null;

                    });

                } else {
                    regimenList = new ArrayList<>();
                }
            }
        } catch (Exception exception) {

        }
        return SUCCESS;
    }

    public String saveSelectedPrescriptions() {
        String[] labtest_ids = null;
        String[] regimen_ids = null;
        String dateVisit = "";
        try {
            facilityId = (Long) session.getAttribute("facilityId");
            if (request.getParameterMap().containsKey("regimen_map") && !request.getParameter("regimen_map").equals("")) {
                patientId = Long.valueOf(request.getParameter("patient_id"));
                regimen_ids = request.getParameter("regimen_map").split(",");
                dateVisit = request.getParameter("dateVisit");
            }
            if (request.getParameterMap().containsKey("labtest_ids") && !request.getParameter("labtest_ids").equals("")) {
                patientId = Long.valueOf(request.getParameter("patient_id"));
                labtest_ids = request.getParameter("labtest_ids").split(",");
                dateVisit = request.getParameter("dateVisit");
            }
            String prescriptionType;
            List<Prescription> prescriptions = new ArrayList<>();

            //Save the Selected drugs...
            if (regimen_ids != null) {
                for (int i = 0; i < regimen_ids.length; i += 3) {
                    prescriptionType = "drug";
                    Prescription p = new Prescription();
                    //Check If that prescription exists for the given date and update if yes...
                    PrescriptionDTO prescription = getPrescriptionsByDate(facilityId, patientId, dateVisit, "drug", Integer.parseInt(regimen_ids[i + 1]));
                    if (prescription != null) {
                        p.setPrescriptionId(prescription.getPrescriptionId());
                    }
                    p.setPatientId(patientId);
                    p.setFacilityId(facilityId);
                    p.setPrescriptionType(prescriptionType);
                    p.setRegimenTypeId(Long.parseLong(regimen_ids[i]));
                    p.setRegimenId(Long.parseLong(regimen_ids[i + 1]));
                    p.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    p.setUserId(userId);
                    p.setDuration(Integer.parseInt(regimen_ids[i + 2]));
                    if (prescription == null) {
                        p.setStatus(Constants.Prescription.PRESCRIBED);
                    } else {
                        p.setStatus(prescription.getStatus());
                    }
                    p.setDateVisit(DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy"));
                    System.out.println("P in Drug is: " + p);
                    prescriptions.add(p);
                }
            }
            if (labtest_ids != null) {
                for (String labtest_id : labtest_ids) {
                    prescriptionType = "labtest";
                    Prescription p = new Prescription();
                    PrescriptionDTO prescription = getPrescriptionsByDate(facilityId, patientId, dateVisit, "labtest", Integer.parseInt(labtest_id));
                    if (prescription != null) {
                        p.setPrescriptionId(prescription.getPrescriptionId());
                    }
                    p.setPatientId(patientId);
                    p.setFacilityId(facilityId);
                    p.setPrescriptionType(prescriptionType);
                    p.setLabtestId(Long.parseLong(labtest_id));
                    p.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    p.setUserId(userId);
                    p.setDateVisit(DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy"));
                    if (prescription == null) {
                        p.setStatus(Constants.Prescription.PRESCRIBED);
                    } else {
                        p.setStatus(prescription.getStatus());
                    }
                    System.out.println("P in LAB is: " + p);
                    prescriptions.add(p);
                }
            }
            if (regimen_ids != null || labtest_ids != null) {
                System.out.println(prescriptions);
                //Save the Prescriptions...
                PrescriptionDAO.saveBatch(prescriptions);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return SUCCESS;
    }

    public String getDrugsByDate() {
        try {
            String prescriptionType = "drug";
            facilityId = (Long) session.getAttribute("facilityId");
            if (request.getParameter("patientId") != null) {
                patientId = Long.valueOf(request.getParameter("patientId"));
                String dateVisit = request.getParameter("dateVisit");

                query = "SELECT * FROM prescription WHERE facility_id = ? AND patient_id = ? AND date_visit = ? AND prescription_type = ?";

                jdbcTemplate.query(query, rs -> {
                    new PrescriptionListBuilder().buildDrugList(rs);
                    drugList = new PrescriptionListBuilder().retrieveDrugList();
                    return null;
                }, facilityId, patientId, DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy"), prescriptionType);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return SUCCESS;
    }

    //Create an object to hold the prescription and then return the object here...
    public PrescriptionDTO getPrescriptionsByDate(Long facilityId, Long patientId, String dateVisit, String type, Integer idTofind) {
        PrescriptionDTO prescription[] = {null};
        try {
            facilityId = (Long) session.getAttribute("facilityId");
            if (patientId != null) {
                query = "SELECT * FROM prescription WHERE facility_id = ? AND patient_id = ? AND date_visit = ? AND prescription_type = ?";
                if (type.equals("drug")) {
                    query += " AND regimen_id = ?";
                } else if (type.equals("labtest")) {
                    query += " AND labtest_id = ?";
                }

                jdbcTemplate.query(query, rs -> {
                    while (rs.next()) {
                        Long prescription_id = rs.getLong("prescription_id");
                        Long patient_id = rs.getLong("patient_id");
                        Long facility_id = rs.getLong("facility_id");
                        String prescription_type = rs.getString("prescription_type");
                        Long labtest_id = rs.getLong("labtest_id");
                        Long regimentype_id = rs.getLong("regimentype_id");
                        Long regimen_id = rs.getLong("regimen_id");
                        Date date_visit = rs.getDate("date_visit");
                        Integer duration = rs.getInt("duration");
                        Integer status = rs.getInt("status");

                        prescription[0] = new PrescriptionDTO(prescription_id, facility_id, patient_id, prescription_type, labtest_id, status, regimentype_id, regimen_id, duration, date_visit);
                    }
                    return null;
                }, facilityId, patientId, DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy"), type, idTofind);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return prescription[0];
    }

    public String getLabTestsByDate() {
        try {
            facilityId = (Long) session.getAttribute("facilityId");
            String prescriptionType = "labtest";
            if (request.getParameter("patientId") != null) {
                patientId = Long.valueOf(request.getParameter("patientId"));
                String dateVisit = request.getParameter("dateVisit");

                query = "SELECT * FROM prescription WHERE facility_id = ? AND patient_id = ? AND date_visit = ? AND prescription_type = ?";

                jdbcTemplate.query(query, rs -> {
                    while (rs.next()) {
                        new PrescriptionListBuilder().buildLabTestList(rs);
                        labtestList = new PrescriptionListBuilder().retrieveLabTestList();

                    }
                    return null;
                }, facilityId, patientId, DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy"), prescriptionType);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return SUCCESS;
    }

    public String retrieveDrugPrescriptionByPatientId() {
        ArrayList<Integer> regimen_ids = new ArrayList<>();
        try {
            facilityId = (Long) session.getAttribute("facilityId");
            String patient_id = request.getParameter("patientId");
            String prescriptionType = "drug";

            String query = "SELECT regimentype_id, regimen_id, duration FROM prescription WHERE patient_id = ? AND facility_id = ? AND prescription_type = ? AND status = ?";
            boolean done[] = {false};
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String regimen_id = Long.toString(resultSet.getInt("regimen_id"));  //Double.toString(double) if it is a double
                    String regimenTypeId = Long.toString(resultSet.getInt("regimentype_id"));
                    String duration = Long.toString(resultSet.getInt("duration"));
                    regimen_ids.add(Integer.parseInt(regimen_id));
                    Map<String, String> prescriptionMap = new HashMap<>();
                    prescriptionMap.put("regimenTypeId", regimenTypeId);
                    prescriptionMap.put("regimenId", regimen_id);
                    prescriptionMap.put("duration", duration);
                    prescriptionList.add(prescriptionMap);
                }
                done[0] = buildPrescriptionDispenser(regimen_ids.toString());
                return null;
            }, Long.parseLong(patient_id), facilityId, prescriptionType, Constants.Prescription.PRESCRIBED);
            // loop through resultSet for each row and put into Map
            if (done[0]) {
                return SUCCESS;
            }
        } catch (Exception exception) {

            return ERROR;
        }
        return ERROR;
    }

    private boolean buildPrescriptionDispenser(String regimen_ids) {

        boolean done[] = {false};
        try {

            String query = "SELECT drug.name, drug.strength, drug.morning, drug.afternoon, drug.evening, regimendrug.regimendrug_id, regimendrug.regimen_id, regimendrug.drug_id, regimen.regimentype_id "
                    + " FROM drug JOIN regimendrug ON regimendrug.drug_id = drug.drug_id JOIN regimen ON regimendrug.regimen_id = regimen.regimen_id WHERE regimendrug.regimen_id IN (" + regimen_ids.replace("[", "").replace("]", "") + ")";
            jdbcTemplate.query(query, resultSet -> {
                //loop through resultSet for each row and put into Map
                while (resultSet.next()) {
                    String regimentype_id = Long.toString(resultSet.getLong("regimentype_id"));
                    String regimendrugId = Long.toString(resultSet.getLong("regimendrug_id"));
                    String regimen_id = Long.toString(resultSet.getLong("regimen_id"));
                    String drugId = Long.toString(resultSet.getLong("drug_id"));
                    String description = resultSet.getString("name") + " " + resultSet.getString("strength");
                    String morning = Double.toString(resultSet.getDouble("morning"));
                    String afternoon = Double.toString(resultSet.getDouble("afternoon"));
                    String evening = Double.toString(resultSet.getDouble("evening"));
                    String duration = "0";
                    String quantity = "0.0";

                    Map<String, String> map = new HashMap<>();
                    map.put("regimentypeId", regimentype_id);
                    map.put("regimendrugId", regimendrugId);
                    map.put("regimenId", regimen_id);
                    map.put("drugId", drugId);
                    map.put("description", description);
                    map.put("morning", morning);
                    map.put("afternoon", afternoon);
                    map.put("evening", evening);
                    map.put("duration", duration);
                    map.put("quantity", quantity);
                    dispenserList.add(map);
                    prescribedList.add(map);
                }

                session.setAttribute("fromPrescription", "true");
                session.setAttribute("dispenserList", dispenserList);
                session.setAttribute("prescribedList", prescribedList);

                done[0] = true;
                return null;
            });

        } catch (Exception exception) {

            done[0] = false;
        }

        return done[0];
    }

    public String retrieveLabTestPrescriptionByPatientId() {
        ArrayList<Integer> labtest_ids = new ArrayList<>();
        try {
            facilityId = (Long) session.getAttribute("facilityId");
            String patient_id = request.getParameter("patientId");
            String prescriptionType = "labtest";

            String query = "SELECT labtest_id FROM prescription WHERE patient_id = ? AND facility_id = ? AND prescription_type = ? AND status = ?";

            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String labTestId = Long.toString(resultSet.getInt("labtest_id"));
                    labtest_ids.add(Integer.parseInt(labTestId));
                    labtestPrescriptionList.add(labTestId);
                    buildLabTests(labtest_ids.toString());
                }
                return null;
            }, Long.parseLong(patient_id), facilityId, prescriptionType, Constants.Prescription.PRESCRIBED);

        } catch (Exception exception) {

            exception.printStackTrace();
        }
        return SUCCESS;
    }

    private void buildLabTests(String labtest_ids) {
        try {

            query = "SELECT labtest_id, description, measureab, measurepc FROM labtest WHERE labtest_id IN (" + labtest_ids.replace("[", "").replace("]", "") + ")";

            jdbcTemplate.query(query, resultSet -> {
                // loop through resultSet for each row and put into Map
                while (resultSet.next()) {
                    String labtestId = Long.toString(resultSet.getInt("labtest_id"));
                    String description = resultSet.getString("description");
                    String resultab = "";
                    String measureab = resultSet.getString("measureab");
                    String resultpc = "";
                    String measurepc = resultSet.getString("measurepc");
                    String comment = "";

                    Map<String, String> map = new HashMap<>();
                    map.put("labtestId", labtestId);
                    map.put("description", description);
                    map.put("resultab", resultab);
                    map.put("measureab", measureab);
                    map.put("resultpc", resultpc);
                    map.put("measurepc", measurepc);
                    map.put("comment", comment);
                    labresultList.add(map);
                    labPrescribedList.add(map);
                }

                session.setAttribute("fromLabTest", "true");
                session.setAttribute("labresultList", labresultList);
                session.setAttribute("labPrescribedList", labPrescribedList);
                return null;
            });
        } catch (Exception exception) {

            exception.printStackTrace();
        }
    }

    /**
     * @return the page
     */
    public Integer getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     * @return the limit
     */
    public Integer getRows() {
        return rows;
    }

    /**
     * @param limit the limit to set
     */
    public void setRows(Integer rows) {
        this.rows = rows;
    }

    /**
     * @return the sidx
     */
    public String getSidx() {
        return sidx;
    }

    /**
     * @param sidx the sidx to set
     */
    public void setSidx(String sidx) {
        this.sidx = sidx;
    }

    /**
     * @return the sord
     */
    public String getSord() {
        return sord;
    }

    /**
     * @param sord the sord to set
     */
    public void setSord(String sord) {
        this.sord = sord;
    }

    /**
     * @return the totalpages
     */
    public Integer getTotalpages() {
        return totalpages;
    }

    /**
     * @param totalpages the totalpages to set
     */
    public void setTotalpages(Integer totalpages) {
        this.totalpages = totalpages;
    }

    /**
     * @return the currpage
     */
    public Integer getCurrpage() {
        return currpage;
    }

    /**
     * @param currpage the currpage to set
     */
    public void setCurrpage(Integer currpage) {
        this.currpage = currpage;
    }

    /**
     * @return the totalrecords
     */
    public Integer getTotalrecords() {
        return totalrecords;
    }

    /**
     * @param totalrecords the totalrecords to set
     */
    public void setTotalrecords(Integer totalrecords) {
        this.totalrecords = totalrecords;
    }

    public String getRegimentypeId() {
        return regimentypeId;
    }

    public void setRegimentypeId(String regimentypeId) {
        this.regimentypeId = regimentypeId;
    }

    public ArrayList<Map<String, String>> getRegimenList() {
        return regimenList;
    }

    public void setRegimenList(ArrayList<Map<String, String>> regimenList) {
        this.regimenList = regimenList;
    }

    public List<String> getDrugList() {
        return drugList;
    }

    public void setDrugList(List<String> drugList) {
        this.drugList = drugList;
    }

    public List<String> getLabtestList() {
        return labtestList;
    }

    public void setLabtestList(List<String> labtestList) {
        this.labtestList = labtestList;
    }

    public List<Map<String, String>> getPrescriptionList() {
        return prescriptionList;
    }

    public void setPrescriptionList(List<Map<String, String>> prescriptionList) {
        this.prescriptionList = prescriptionList;
    }

    public ArrayList<Map<String, String>> getDispenserList() {
        return dispenserList;
    }

    public void setDispenserList(ArrayList<Map<String, String>> dispenserList) {
        this.dispenserList = dispenserList;
    }

    public ArrayList<Map<String, String>> getPrescribedList() {
        return prescribedList;
    }

    public void setPrescribedList(ArrayList<Map<String, String>> prescribedList) {
        this.prescribedList = prescribedList;
    }

    public List<String> getLabtestPrescriptionList() {
        return labtestPrescriptionList;
    }

    public void setLabtestPrescriptionList(List<String> labtestPrescriptionList) {
        this.labtestPrescriptionList = labtestPrescriptionList;
    }

    public ArrayList<Map<String, String>> getLabresultList() {
        return labresultList;
    }

    public void setLabresultList(ArrayList<Map<String, String>> labresultList) {
        this.labresultList = labresultList;
    }

    public ArrayList<Map<String, String>> getLabPrescribedList() {
        return labPrescribedList;
    }

    public void setLabPrescribedList(ArrayList<Map<String, String>> labPrescribedList) {
        this.labPrescribedList = labPrescribedList;
    }

}
