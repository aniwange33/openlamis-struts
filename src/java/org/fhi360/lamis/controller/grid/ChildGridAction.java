/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.controller.grid;

import static com.opensymphony.xwork2.Action.SUCCESS;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.ActionSupport;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.Constants;

import org.fhi360.lamis.utility.PatientNumberNormalizer;
import org.fhi360.lamis.utility.builder.ChildListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class ChildGridAction extends ActionSupport implements Preparable {

    private Integer page;
    private Integer rows;
    private String sidx;
    private String sord;
    private Integer totalpages;
    private Integer currpage;
    private Integer totalrecords;

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private ArrayList<Map<String, String>> childList = new ArrayList<>();

    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
    }

    public String childGrid() {
        Map<String, Object> pagerParams = new PaginationUtil().paginateGrid(getPage().intValue(), getRows().intValue(), "child");
        int start = (Integer) pagerParams.get("start");
        int numberOfRows = getRows();
        try {

            if (request.getParameterMap().containsKey("deliveryId")) {
                System.out.println("Delivery ID: " + request.getParameter("deliveryId"));
                query = "SELECT child.child_id AS child_id, child.mother_id AS mother_id, child.delivery_id AS delivery_id, child.registration_status AS registration_status, child.arv AS arv, child.hepb AS hepb, child.hbv AS hbv, child.patient_id AS patient_id, child.facility_id AS facility_id, child.anc_id AS anc_id, child.reference_num AS reference_num, child.hospital_number AS hospital_number, child.surname AS surname, child.other_names AS other_names, child.date_birth AS date_birth, child.gender AS gender, child.body_weight AS body_weight, child.apgar_score AS apgar_score, child.status AS status, patient.surname AS surname_mother, patient.other_names AS other_names_mother, child.mother_id, motherinformation.in_facility AS in_facility FROM child JOIN patient ON child.patient_id = patient.patient_id JOIN motherinformation ON child.mother_id = motherinformation.motherinformation_id WHERE child.facility_id = ? AND child.delivery_id = ? ORDER BY patient.surname LIMIT " + start + " , " + numberOfRows;

                jdbcTemplate.query(query, resultSet -> {
                    new ChildListBuilder().buildChildList(resultSet, Constants.Pmtct.Child.WITH_MOTHER);
                    childList = new ChildListBuilder().retrieveChildList();
                    return null;
                }, (Long) session.getAttribute("facilityId"), Long.parseLong(request.getParameter("deliveryId")));

            } else {
                //Find all instances of a child
                query = "SELECT child.child_id, child.delivery_id, child.patient_id, child.facility_id, child.anc_id, child.registration_status AS registration_status, child.reference_num, child.arv AS arv, child.hepb AS hepb, child.hbv AS hbv, child.hospital_number, child.surname, child.other_names, child.date_birth, child.gender, child.body_weight, child.apgar_score, child.status, motherinformation.surname AS surname_mother, motherinformation.other_names AS other_names_mother, child.mother_id, motherinformation.in_facility AS in_facility FROM child JOIN motherinformation ON child.mother_id = motherinformation.motherinformation_id WHERE child.facility_id = ? ORDER BY motherinformation.surname LIMIT " + start + " , " + numberOfRows;
                System.out.println("Query is: " + query);
                System.out.println("Facility Id is: " + session.getAttribute("facilityId"));

                jdbcTemplate.query(query, resultSet -> {
                    new ChildListBuilder().buildChildList(resultSet, Constants.Pmtct.Child.WITH_MOTHER);
                    childList = new ChildListBuilder().retrieveChildList();
                    return null;
                }, (Long) session.getAttribute("facilityId"));

            }
        } catch (Exception exception) {
            exception.printStackTrace();

        }

        page = (Integer) pagerParams.get("page");
        currpage = (Integer) pagerParams.get("page");
        totalpages = (Integer) pagerParams.get("totalpages");
        totalrecords = (Integer) pagerParams.get("totalrecords");
        return SUCCESS;
    }

    public String childGridByNumber() {
        long facilityId = (Long) session.getAttribute("facilityId");
        String hospitalNum = request.getParameter("hospitalNum");

//        System.out.println("Hospital Number: "+hospitalNum);
        Map<String, Object> pagerParams = new PaginationUtil().paginateGrid(getPage(), getRows(), "child");
        int start = (Integer) pagerParams.get("start");
        int numberOfRows = getRows();
        try {

            if (hospitalNum == null || hospitalNum.isEmpty()) {
                query = "SELECT child.*, motherinformation.surname AS surname_mother, motherinformation.other_names as other_names_mother, motherinformation.in_facility FROM child JOIN motherinformation ON child.mother_id = motherinformation.motherinformation_id WHERE child.facility_id = " + facilityId + " ORDER BY child.surname ASC LIMIT " + start + " , " + numberOfRows;
            } else {
                query = "SELECT child.*, motherinformation.surname AS surname_mother, motherinformation.other_names as other_names_mother, motherinformation.in_facility FROM child JOIN motherinformation ON child.mother_id = motherinformation.motherinformation_id WHERE child.facility_id = " + facilityId + " AND TRIM(LEADING '0' FROM child.hospital_number) LIKE '" + PatientNumberNormalizer.unpadNumber(hospitalNum) + "%' ORDER BY child.surname ASC LIMIT " + start + " , " + numberOfRows;
            }
            jdbcTemplate.query(query, resultSet -> {
                new ChildListBuilder().buildChildListSorted(resultSet);
                childList = new ChildListBuilder().retrieveChildList();
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
     * @return the childList
     */
    public ArrayList<Map<String, String>> getChildList() {
        return childList;
    }

    /**
     * @param childList the childList to set
     */
    public void setChildList(ArrayList<Map<String, String>> childList) {
        this.childList = childList;
    }
}
