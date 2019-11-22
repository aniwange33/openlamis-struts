/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.controller;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.ModelDriven;
import org.fhi360.lamis.model.Facility;
import org.fhi360.lamis.dao.hibernate.FacilityDAO;
import org.fhi360.lamis.dao.hibernate.UserDAO;
import org.fhi360.lamis.model.Lga;
import org.fhi360.lamis.dao.jdbc.FacilityJDBC;
import org.fhi360.lamis.resource.EntityWebserviceInvocator;
import org.fhi360.lamis.service.parser.json.FacilityJsonParser;
import org.fhi360.lamis.utility.PropertyAccessor;
import org.fhi360.lamis.utility.PatientNumberNormalizer;

public class FacilityAction extends ActionSupport implements ModelDriven, Preparable {

    private Long lgaId;
    private Lga lga;
    private Long facilityId;
    private Facility facility;
    private Integer padHospitalNum;
    private Integer dayDqa;
    private Integer dqaStatus;
    private boolean paddingStatus;
    private String smsPrinter;
    private Set<Facility> facilities = new HashSet<>(0);

    private HttpServletRequest request;
    private HttpSession session;

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
    }

    @Override
    public Object getModel() {
        lga = new Lga();
        facility = new Facility();
        return facility;
    }

    // Save new facility to database
    public String saveFacility() {
        try {
            if (request.getParameterMap().containsKey("padHospitalNum")) {
                facility.setPadHospitalNum(1);
            } else {
                facility.setPadHospitalNum(0);
            }

            if (session.getAttribute("userGroup") != null) {
                if (((String) request.getSession().getAttribute("userGroup")).equals("Administrator")) {
                    lga.setLgaId(Long.parseLong(request.getParameter("lgaId")));
                    facility.setLga(lga);
                    try {
                        if (ServletActionContext.getServletContext().getInitParameter("instanceApp").equalsIgnoreCase("local")) {
                            String jsonString = FacilityJsonParser.objectJson(facility);
                            Long generatedID = EntityWebserviceInvocator.saveOrModifyEntity(jsonString, false);
                            if (generatedID != 0L) {
                                facility.setFacilityId(generatedID);
                                FacilityJDBC.save(facility);
                                setAsDefault(facility.getFacilityId());
                            }
                        } else {
                            Long id = FacilityDAO.save(facility);
                            setAsDefault(id);
                            FacilityJDBC.updateExchange(id);
                        }
                        return SUCCESS;
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        return ERROR;
                    }
                } else {
                    return ERROR;
                }
            } else {
                return ERROR;

            }
        } catch (Exception e) {
            return ERROR;
        }
    }

    // Update facility in database
    public String updateFacility() {
        try {
            if (request.getParameterMap().containsKey("padHospitalNum")) {
                facility.setPadHospitalNum(1);
            } else {
                facility.setPadHospitalNum(0);
            }
            if (session.getAttribute("userGroup") != null) {
                if (((String) request.getSession().getAttribute("userGroup")).equals("Administrator")) {
                    lga.setLgaId(Long.parseLong(request.getParameter("lgaId")));
                    facility.setLga(lga);
                    facility.setFacilityId(Long.parseLong(request.getParameter("facilityId")));
                    try {
                        PatientNumberNormalizer.normalize(facility);
                        String jsonString = FacilityJsonParser.objectJson(facility);
                        if (ServletActionContext.getServletContext().getInitParameter("instanceApp").equalsIgnoreCase("local")) {
                            EntityWebserviceInvocator.saveOrModifyEntity(jsonString, true);
                        }
                        FacilityDAO.update(facility);
                        setAsDefault(Long.parseLong(request.getParameter("facilityId")));
                        return SUCCESS;
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        return ERROR;
                    }
                } else {
                    return ERROR;
                }
            } else {
                return ERROR;
            }
        } catch (Exception e) {
            return ERROR;
        }
    }

    // Delete facility from database
    public String deleteFacility() {
        try {
            if (session.getAttribute("userGroup") != null) {
                if (((String) request.getSession().getAttribute("userGroup")).equals("Administrator")) {
                    facilityId = Long.parseLong(request.getParameter("facilityId"));
                    FacilityDAO.delete(facilityId);
                    return SUCCESS;
                } else {
                    return ERROR;
                }
            } else {
                return ERROR;
            }
        } catch (Exception e) {
            return ERROR;
        }
    }

    private void setAsDefault(Long id) {
        try {
            if (request.getParameterMap().containsKey("defaultFacility")) {
                session.setAttribute("facilityId", id);
                session.setAttribute("facilityName", request.getParameter("name"));
                UserDAO.updateFacilityId((Long) request.getSession().getAttribute("userId"), id);
            }
        } catch (Exception e) {

        }

    }

    // Retrieve a facility in database
    public String findFacility() {
        try {
            facilityId = Long.parseLong(request.getParameter("facilityId"));
            facility = FacilityDAO.find(facilityId);
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String retrieveFacilities() {
        return SUCCESS;
    }

    public String getPaddingStatus() {
        try {
            paddingStatus = FacilityDAO.getPaddingStatus((Long) request.getSession().getAttribute("facilityId"));
        } catch (Exception e) {
            return ERROR;
        }

        return SUCCESS;
    }

    public String getFacilityDqaStatus() {
        try {
            dqaStatus = new PropertyAccessor().getDateLastDqa();
            dayDqa = FacilityDAO.getDayDqa((Long) request.getSession().getAttribute("facilityId"));
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String checkSMSPrinter() {
        try {
            smsPrinter = FacilityJDBC.getSMSPrinter(Long.parseLong(request.getParameter("facilityId")));
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String assignSMSPrinter() {
        try {
            FacilityJDBC.setSMSPrinter(Long.parseLong(request.getParameter("facilityId")), request.getParameter("smsPrinter"));
        } catch (Exception e) {
            return ERROR;
        }

        return SUCCESS;
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
     * @return the facility
     */
    public Facility getFacility() {
        return facility;
    }

    /**
     * @param facility the facility to set
     */
    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    /**
     * @return the facilities
     */
    public Set<Facility> getFacilities() {
        return facilities;
    }

    /**
     * @param facilities the facilities to set
     */
    public void setFacilities(Set<Facility> facilities) {
        this.facilities = facilities;
    }

    /**
     * @return the lgaId
     */
    public Long getLgaId() {
        return lgaId;
    }

    /**
     * @param lgaId the lgaId to set
     */
    public void setLgaId(Long lgaId) {
        this.lgaId = lgaId;
    }

    /**
     * @return the lga
     */
    public Lga getLga() {
        return lga;
    }

    /**
     * @param lga the lga to set
     */
    public void setLga(Lga lga) {
        this.lga = lga;
    }

    /**
     * @return the paddingStatus
     */
    public boolean isPaddingStatus() {
        return paddingStatus;
    }

    /**
     * @param paddingStatus the paddingStatus to set
     */
    public void setPaddingStatus(boolean paddingStatus) {
        this.paddingStatus = paddingStatus;
    }

    /**
     * @return the padHospitalNum
     */
    public Integer getPadHospitalNum() {
        return padHospitalNum;
    }

    /**
     * @param padHospitalNum the padHospitalNum to set
     */
    public void setPadHospitalNum(Integer padHospitalNum) {
        this.padHospitalNum = padHospitalNum;
    }

    /**
     * @return the dayDqa
     */
    public Integer getDayDqa() {
        return dayDqa;
    }

    /**
     * @param dayDqa the dayDqa to set
     */
    public void setDayDqa(Integer dayDqa) {
        this.dayDqa = dayDqa;
    }

    /**
     * @return the dqaStatus
     */
    public Integer getDqaStatus() {
        return dqaStatus;
    }

    /**
     * @param dqaStatus the dqaStatus to set
     */
    public void setDqaStatus(Integer dqaStatus) {
        this.dqaStatus = dqaStatus;
    }

    /**
     * @return the smsPrinter
     */
    public String getSmsPrinter() {
        return smsPrinter;
    }

    /**
     * @param smsPrinter the smsPrinter to set
     */
    public void setSmsPrinter(String smsPrinter) {
        this.smsPrinter = smsPrinter;
    }

}
