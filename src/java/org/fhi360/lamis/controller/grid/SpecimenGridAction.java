/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.controller.grid;

import org.fhi360.lamis.service.parser.json.LabFileParser;
import java.util.*;
import com.opensymphony.xwork2.ActionSupport;

import org.fhi360.lamis.utility.builder.SpecimenListBuilder;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class SpecimenGridAction extends ActionSupport {

    private Integer page;
    private Integer rows;
    private String sidx;
    private String sord;
    private Integer totalpages;
    private Integer currpage;
    private Integer totalrecords;

    private String query;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private ArrayList<Map<String, String>> specimenList = new ArrayList<>();

    public String specimenGrid() {
        Map<String, Object> pagerParams = new PaginationUtil().paginateGrid(getPage().intValue(), getRows().intValue(), "specimen");
        int start = (Integer) pagerParams.get("start");
        int numberOfRows = getRows().intValue();
        HttpServletRequest request = ServletActionContext.getRequest();
        long facilityId = (Long) request.getSession().getAttribute("facilityId");
        try {
            if (request.getParameterMap().containsKey("labno")) {
                String labno = request.getParameter("labno");
                query = "SELECT specimen.specimen_id, specimen.specimen_type, specimen.labno, specimen.barcode, specimen.facility_id, specimen.state_id, specimen.lga_id, specimen.treatment_unit_id, specimen.date_received, specimen.date_collected, specimen.date_assay, specimen.date_reported, specimen.date_dispatched, specimen.quality_cntrl, "
                        + " specimen.hospital_num, specimen.result, specimen.reason_no_test, specimen.surname, specimen.other_names, specimen.gender, specimen.date_birth, specimen.age, specimen.age_unit, specimen.address, specimen.phone, specimen.time_stamp, facility.name FROM specimen "
                        + " JOIN facility ON specimen.treatment_unit_id = facility.facility_id WHERE specimen.facility_id = " + facilityId + " AND specimen.labno LIKE '" + labno + "%' ORDER BY specimen.labno ASC LIMIT " + start + " , " + numberOfRows;
            } else {
                query = "SELECT specimen.specimen_id, specimen.specimen_type, specimen.labno, specimen.barcode, specimen.facility_id, specimen.state_id, specimen.lga_id, specimen.treatment_unit_id, specimen.date_received, specimen.date_collected, specimen.date_assay, specimen.date_reported, specimen.date_dispatched, specimen.quality_cntrl, "
                        + " specimen.hospital_num, specimen.result, specimen.reason_no_test, specimen.surname, specimen.other_names, specimen.gender, specimen.date_birth, specimen.age, specimen.age_unit, specimen.address, specimen.phone, specimen.time_stamp, facility.name FROM specimen "
                        + " JOIN facility ON specimen.treatment_unit_id = facility.facility_id WHERE specimen.facility_id = " + facilityId + " ORDER BY labno ASC LIMIT " + start + " , " + numberOfRows;
            }
            jdbcTemplate.query(query, resultSet -> {

                new SpecimenListBuilder().buildSpecimenList(resultSet);
                specimenList = new SpecimenListBuilder().retrieveSpecimenList();
                return null;
            });

        } catch (Exception exception) {

        }
        page = (Integer) pagerParams.get("page");
        currpage = (Integer) pagerParams.get("page");
        totalpages = (Integer) pagerParams.get("totalpages");
        totalrecords = (Integer) pagerParams.get("totalrecords");
        return SUCCESS;
    }

    public String resultProcessorGrid() {
        //setTotalpages(1);
        //setCurrpage(1);
        //setTotalrecords(20);
        HttpServletRequest request = ServletActionContext.getRequest();
        int fileUploaded = 0;
        if (request.getSession().getAttribute("fileUploaded") != null) {
            fileUploaded = (Integer) request.getSession().getAttribute("fileUploaded");
        }
        if (fileUploaded == 1) {
            try {
                new LabFileParser().parseFile((String) request.getSession().getAttribute("fileName"));
                if (request.getParameterMap().containsKey("discard")) {
                    executeUpdate("DELETE FROM labresult");
                }

                query = "SELECT specimen.specimen_id, specimen.labno, specimen.barcode, specimen.date_received, specimen.hospital_num, specimen.surname, specimen.other_names, labresult.result, facility.name FROM specimen "
                        + " JOIN labresult ON specimen.labno = labresult.labno JOIN facility ON specimen.treatment_unit_id = facility.facility_id WHERE specimen.labno = labresult.labno";

                jdbcTemplate.query(query, resultSet -> {

                    new SpecimenListBuilder().buildResultList(resultSet);
                    specimenList = new SpecimenListBuilder().retrieveSpecimenList();
                    return null;
                });
            } catch (Exception exception) {

            }
        }
        return SUCCESS;
    }

    public String resultDispatcherGrid() {
        //setTotalpages(1);
        //setCurrpage(1);
        //setTotalrecords(20);
        executeUpdate("DROP TABLE IF EXISTS dispatcher");
        long treatmentUnitId = Long.parseLong(ServletActionContext.getRequest().getParameter("treatmentUnitId"));
        if (ServletActionContext.getRequest().getParameterMap().containsKey("dispatched")) {
            query = "CREATE TEMPORARY TABLE dispatcher AS SELECT specimen.specimen_id, specimen.labno, specimen.barcode, specimen.date_received, specimen.date_reported, specimen.date_assay, specimen.hospital_num, specimen.surname, specimen.other_names, specimen.gender, specimen.result, facility.name, facility.phone2 FROM specimen "
                    + " JOIN facility ON specimen.treatment_unit_id = facility.facility_id WHERE specimen.result != '' AND specimen.result IS NOT NULL AND specimen.date_dispatched IS NOT NULL";
            if (treatmentUnitId != 0) {
                query = "CREATE TEMPORARY TABLE dispatcher AS SELECT specimen.specimen_id, specimen.labno, specimen.barcode, specimen.date_received, specimen.date_reported, specimen.date_assay, specimen.hospital_num, specimen.surname, specimen.other_names, specimen.gender, specimen.result, facility.name, facility.phone2 FROM specimen "
                        + " JOIN facility ON specimen.treatment_unit_id = facility.facility_id WHERE specimen.treatment_unit_id = " + treatmentUnitId + " AND specimen.result != '' AND specimen.result IS NOT NULL AND specimen.date_dispatched IS NOT NULL";
            }
        } else {
            query = "CREATE TEMPORARY TABLE dispatcher AS SELECT specimen.specimen_id, specimen.labno, specimen.barcode, specimen.date_received, specimen.date_reported, specimen.date_assay, specimen.hospital_num, specimen.surname, specimen.other_names, specimen.gender, specimen.result, facility.name, facility.phone2 FROM specimen "
                    + " JOIN facility ON specimen.treatment_unit_id = facility.facility_id WHERE specimen.result != '' AND specimen.result IS NOT NULL AND specimen.date_dispatched IS NULL";
            if (treatmentUnitId != 0) {
                System.out.println("Select result not dispatched for specific site");
                query = "CREATE TEMPORARY TABLE dispatcher AS SELECT specimen.specimen_id, specimen.labno, specimen.barcode, specimen.date_received, specimen.date_reported, specimen.date_assay, specimen.hospital_num, specimen.surname, specimen.other_names, , specimen.gender, specimen.result, facility.name, facility.phone2 FROM specimen "
                        + " JOIN facility ON specimen.treatment_unit_id = facility.facility_id WHERE specimen.treatment_unit_id = " + treatmentUnitId + " AND specimen.result != '' AND specimen.result IS NOT NULL AND specimen.date_dispatched IS NULL";
            }
        }
        executeUpdate(query);
        try {
            query = "SELECT * FROM dispatcher";
            jdbcTemplate.query(query, resultSet -> {
                new SpecimenListBuilder().buildResultList(resultSet);
                specimenList = new SpecimenListBuilder().retrieveSpecimenList();
                return null;
            });
        } catch (Exception exception) {

        }
        return SUCCESS;
    }

    private void executeUpdate(String query) {
        try {
            transactionTemplate.execute((ts) -> {
                jdbcTemplate.execute(query);
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });

        } catch (Exception exception) {

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

    /**
     * @return the specimenList
     */
    public ArrayList<Map<String, String>> getSpecimenList() {
        return specimenList;
    }

    /**
     * @param specimenList the specimenList to set
     */
    public void setSpecimenList(ArrayList<Map<String, String>> specimenList) {
        this.specimenList = specimenList;
    }
}
