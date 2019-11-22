/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.controller.grid;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.ActionSupport;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.builder.LaboratoryListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

public class LabresultGridAction extends ActionSupport implements Preparable {

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
    private JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private ArrayList<Map<String, String>> labresultList = new ArrayList<>();
    private ArrayList<Map<String, String>> labtestList = new ArrayList<>();

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
    }

    public String labresultGrid() {

        try {
            String labtestId = request.getParameter("labtestId");
            String labtestId1 = "";
            boolean test = true;
            if (session.getAttribute("labresultList") != null) {
                labresultList = (ArrayList) session.getAttribute("labresultList");
            }
            for (Iterator<Map<String, String>> iterator = labresultList.iterator(); iterator.hasNext();) {
                Map map = iterator.next();
                labtestId1 = (String) map.get("labtestId");

                if (labtestId.equals(labtestId1)) {
                    iterator.remove();
                    test = false;
                }
            }
            if (test) {
                test(labtestId);
            }
            System.out.println(labresultList);
            session.setAttribute("labresultList", labresultList);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return SUCCESS;
    }

    //Get the Lab Test for the Prescription
    public String laboratoryGrid() {

        try {
            Long facilityId = (Long) session.getAttribute("facilityId");

            query = "SELECT laboratory.laboratory_id, laboratory.patient_id, laboratory.facility_id, laboratory.date_collected, laboratory.date_reported, laboratory.labno, laboratory.resultab, laboratory.resultpc, laboratory.comment, laboratory.labtest_id, labtest.description "
                    + " FROM laboratory JOIN labtest ON laboratory.labtest_id = labtest.labtest_id WHERE laboratory.facility_id = ? AND laboratory.patient_id = ? ORDER BY laboratory.date_reported DESC";
            jdbcTemplate.query(query, resultSet -> {
                new LaboratoryListBuilder().buildLaboratoryList(resultSet);
                labtestList = new LaboratoryListBuilder().retrieveLaboratoryList();
                return null;
            }, (Long) session.getAttribute("facilityId"), Long.parseLong(request.getParameter("patientId")));
        } catch (Exception exception) {

        }
        return SUCCESS;
    }

    private void test(String id) {
        try {

            query = "SELECT labtest_id, description, measureab, measurepc FROM labtest WHERE labtest_id = " + Long.parseLong(id);
            jdbcTemplate.query(query, resultSet -> {

                while (resultSet.next()) {
                    String labtestId = Long.toString(resultSet.getInt("labtest_id"));
                    System.out.println("labtestId" + labtestId);
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
                }
                return null;

            });
        } catch (Exception exception) {
            System.out.println("err " + exception.getMessage());
        }
    }

    public String updateLabresultList() {
        // retrieve lab result information to be saved from request parameters 
        String labtestId = request.getParameter("id");
        String resultab = request.getParameter("resultab");
        String resultpc = request.getParameter("resultpc");
        String comment = request.getParameter("comment");

        // retrieve the defaulter list stored as an attribute in session object
        if (session.getAttribute("labresultList") != null) {
            labresultList = (ArrayList) session.getAttribute("labresultList");
        }

        // find the target labresult and update with values of request parameters
        for (int i = 0; i < labresultList.size(); i++) {
            String id = (String) labresultList.get(i).get("labtestId"); // retrieve labtest id from list
            if (id.equals(labtestId)) {
                labresultList.get(i).remove("resultab");
                labresultList.get(i).put("resultab", resultab);
                labresultList.get(i).remove("resultpc");
                labresultList.get(i).put("resultpc", resultpc);
                labresultList.get(i).remove("comment");
                labresultList.get(i).put("comment", comment);
            }
        }
        // set labresultList as a session-scoped attribute
        session.setAttribute("labresultList", labresultList);
        return SUCCESS;
    }

    public String labresultGridRetrieve() {
        setTotalpages(1);
        setCurrpage(1);
        setTotalrecords(20);

        // retrieve the labresult record store in session attribute
        if (session.getAttribute("labresultList") != null) {
            labresultList = (ArrayList) session.getAttribute("labresultList");
        }
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
     * @return the labresultList
     */
    public ArrayList<Map<String, String>> getLabresultList() {
        return labresultList;
    }

    /**
     * @param labresultList the labresultList to set
     */
    public void setLabresultList(ArrayList<Map<String, String>> labresultList) {
        this.labresultList = labresultList;
    }

    public ArrayList<Map<String, String>> getLabtestList() {
        return labtestList;
    }

    public void setLabtestList(ArrayList<Map<String, String>> labtestList) {
        this.labtestList = labtestList;
    }

}
