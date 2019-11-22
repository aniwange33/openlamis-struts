/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.controller;

import org.fhi360.lamis.dao.hibernate.EidDAO;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.ModelDriven;
import org.fhi360.lamis.model.Specimen;
import org.fhi360.lamis.dao.hibernate.SpecimenDAO;
import org.fhi360.lamis.dao.jdbc.EidJDBC;
import org.fhi360.lamis.dao.jdbc.SpecimenJDBC;
import org.fhi360.lamis.model.Eid;
import org.fhi360.lamis.model.dto.SpecimenObjHandler;
import org.fhi360.lamis.service.DeleteService;
import org.fhi360.lamis.service.MonitorService;
import org.fhi360.lamis.service.SpecimenProcessorService;
import org.fhi360.lamis.service.barcode.BarcodeService;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.LabNumberNormalizer;
import org.fhi360.lamis.utility.Scrambler;
import org.fhi360.lamis.utility.builder.EidListBuilder;
import org.fhi360.lamis.utility.builder.SpecimenListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

public class SpecimenAction extends ActionSupport implements ModelDriven, Preparable {

    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private Long facilityId;
    private Long specimenId;
    private String labno;
    private Long userId;
    private Specimen specimen;
    private Scrambler scrambler;
    private String status;
    private Set<Specimen> specimens = new HashSet<>(0);

    private HttpServletRequest request;
    private HttpSession session;

    private ArrayList<Map<String, String>> specimenList = new ArrayList<>();

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
        facilityId = (Long) session.getAttribute("facilityId");
        userId = (Long) session.getAttribute("userId");
    }

    @Override
    public Object getModel() {
        specimen = new Specimen();
        return specimen;
    }

    public String saveSpecimen() {
        try {
            scrambler = new Scrambler();
            if (request.getParameterMap().containsKey("qualityCntrl")) {
                specimen.setQualityCntrl(1);
            } else {
                specimen.setQualityCntrl(0);
            }
            specimen.setFacilityId(facilityId);
            specimen.setUserId(userId);
            specimen.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
            scrambleIdentifier();
            specimenId = SpecimenDAO.save(specimen);
            //LabNumberNormalizer.updateLabno();
            if (request.getParameter("specimenType").equalsIgnoreCase("DBS")) {
                Eid eid = getEidObject();
                eid.setFacilityId(facilityId);
                EidDAO.save(eid);
            }
            SpecimenObjHandler.store(specimen);
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String updateSpecimen() {
        try {
            scrambler = new Scrambler();
            if (request.getParameterMap().containsKey("qualityCntrl")) {
                specimen.setQualityCntrl(1);
            } else {
                specimen.setQualityCntrl(0);
            }
            specimen.setFacilityId(facilityId);
            specimen.setSpecimenId(Long.parseLong(request.getParameter("specimenId")));
            specimen.setUserId(userId);
            specimen.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
            specimen = SpecimenObjHandler.restore(specimen);
            scrambleIdentifier();
            SpecimenDAO.update(specimen);
            if (request.getParameter("specimenType").equalsIgnoreCase("DBS")) {
                Eid eid = getEidObject();
                eid.setEidId(Long.parseLong(request.getParameter("eidId")));
                eid.setFacilityId(facilityId);
                EidDAO.update(eid);
            }
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String deleteSpecimen() {
        try {
            String entityId = ServletActionContext.getRequest().getParameter("labno");
            MonitorService.logEntity(entityId, "specimen", 3);
            DeleteService deleteService = new DeleteService();
            if (facilityId > 0L) {
                deleteService.deleteSpecimen(facilityId, request.getParameter("labno"));
                return SUCCESS;
            } else {
                return ERROR;
            }
        } catch (Exception e) {
            return ERROR;
        }
    }

    public String findSpecimen() {

        try {
            specimenId = Long.parseLong(request.getParameter("specimenId"));
            specimen = SpecimenDAO.find(specimenId);
            SpecimenObjHandler.store(specimen);
            new SpecimenListBuilder().buildSpecimenList(specimen);
            specimenList = new SpecimenListBuilder().retrieveSpecimenList();

            String query = EidJDBC.findEidByLabno(request.getParameter("labno"));
            jdbcTemplate.query(query, resultSet -> {
                new EidListBuilder().buildEidList(resultSet);
                return null;
            });
            //new EidListBuilder().buildEidList(EidJDBC.findEidBySpecimenId(specimenId));  
        } catch (Exception exception) {
            exception.printStackTrace();
            return ERROR;
        }
        return SUCCESS;
    }

    public String findSpecimenByNumber() {
        try {
            String query = SpecimenJDBC.findSpecimenByNumber(request.getParameter("labno"));
            jdbcTemplate.query(query, resultSet -> {
                new SpecimenListBuilder().buildSpecimenList(resultSet);
                specimenList = new SpecimenListBuilder().retrieveSpecimenList();
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
            return ERROR;
        }
        return SUCCESS;
    }

    public String retrieveSpecimenList() {
        ArrayList<Map<String, String>> eidList = new ArrayList<>();
        specimenList = new SpecimenListBuilder().retrieveSpecimenList();
        eidList = new EidListBuilder().retrieveEidList();
        specimenList.addAll(eidList);
        return SUCCESS;
    }

    public String generateLabno() {
        setLabno(LabNumberNormalizer.getLabno());
        return SUCCESS;
    }

    public String printBarcode() {
        setStatus(BarcodeService.generate(request.getParameter("barcode")));
        return SUCCESS;
    }

    public String saveResult() {
        SpecimenProcessorService.saveResult();
        return SUCCESS;
    }

    public String dispatchResult() {
        SpecimenProcessorService.dispatchResult();
        return SUCCESS;
    }

    private Eid getEidObject() {

        Eid eid = new Eid();
        eid.setLabno(request.getParameter("labno"));
        eid.setMotherName(scrambler.scrambleCharacters(request.getParameter("motherName")));
        eid.setMotherAddress(scrambler.scrambleCharacters(request.getParameter("motherAddress")));
        eid.setMotherPhone(scrambler.scrambleNumbers(request.getParameter("motherPhone")));
        eid.setSenderName(request.getParameter("senderName"));
        eid.setSenderAddress(request.getParameter("senderAddress"));
        eid.setSenderDesignation(request.getParameter("senderDesignation"));
        eid.setSenderPhone(request.getParameter("senderPhone"));
        eid.setReasonPcr(request.getParameter("reasonPcr"));
        eid.setRapidTestDone(request.getParameter("rapidTestDone"));
        eid.setDateRapidTest(request.getParameter("dateRapidTest").equals("") ? null : DateUtil.parseStringToDate(request.getParameter("dateRapidTest"), "MM/dd/yyyy"));
        eid.setRapidTestResult(request.getParameter("rapidTestResult"));
        eid.setMotherArtReceived(request.getParameter("motherArtReceived"));
        eid.setMotherProphylaxReceived(request.getParameter("motherProphylaxReceived"));
        eid.setChildProphylaxReceived(request.getParameter("childProphylaxReceived"));
        eid.setBreastfedEver(request.getParameter("breastfedEver"));
        eid.setFeedingMethod(request.getParameter("feedingMethod"));
        eid.setBreastfedNow(request.getParameter("breastfedNow"));
        eid.setFeedingCessationAge((request.getParameter("feedingCessationAge").equals("")) ? 0 : Integer.parseInt(request.getParameter("feedingCessationAge")));
        eid.setCotrim(request.getParameter("cotrim"));
        eid.setNextAppointment(request.getParameter("nextAppointment").equals("") ? null : DateUtil.parseStringToDate(request.getParameter("nextAppointment"), "MM/dd/yyyy"));
        eid.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
        return eid;
    }

    public void scrambleIdentifier() {
        specimen.setSurname(scrambler.scrambleCharacters(specimen.getSurname()));
        specimen.setOtherNames(scrambler.scrambleCharacters(specimen.getOtherNames()));
        specimen.setAddress(scrambler.scrambleCharacters(specimen.getAddress()));
        specimen.setPhone(scrambler.scrambleNumbers(specimen.getPhone()));
    }

    /**
     * @return the facilityId
     */
    public Long getFacilityId() {
        return facilityId;
    }

    /**
     * @param facilityId the facilityId to set
     */
    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    /**
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return the specimenId
     */
    public Long getSpecimenId() {
        return specimenId;
    }

    /**
     * @param specimenId the specimenId to set
     */
    public void setSpecimenId(Long specimenId) {
        this.specimenId = specimenId;
    }

    /**
     * @return the specimen
     */
    public Specimen getSpecimen() {
        return specimen;
    }

    /**
     * @param specimen the specimen to set
     */
    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }

    /**
     * @return the specimens
     */
    public Set<Specimen> getSpecimens() {
        return specimens;
    }

    /**
     * @param specimens the specimens to set
     */
    public void setSpecimens(Set<Specimen> specimens) {
        this.specimens = specimens;
    }

    /**
     * @return the specimenList
     */
    public ArrayList<Map<String, String>> getSpecimenList() {
        return specimenList;
    }

    /**
     * @param specimenList the specimenList to set
     */
    public void setSpecimenListList(ArrayList<Map<String, String>> specimenList) {
        this.specimenList = specimenList;
    }

    /**
     * @return the labno
     */
    public String getLabno() {
        return labno;
    }

    /**
     * @param labno the labno to set
     */
    public void setLabno(String labno) {
        this.labno = labno;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
