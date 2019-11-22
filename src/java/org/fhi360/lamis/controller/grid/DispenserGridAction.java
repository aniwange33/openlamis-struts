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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class DispenserGridAction extends ActionSupport implements Preparable {

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

    private ArrayList<Map<String, String>> dispenserList = new ArrayList<>();

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
    }

    public String dispenserGrid() {
        //setTotalpages(1);
        //setCurrpage(1);
        //setTotalrecords(20);
        String regimentypeId = request.getParameter("regimentypeId");
        String regimenId = request.getParameter("regimenId");
        System.out.println("......" + regimentypeId + "....." + regimenId);
        String regimentypeId1 = "";
        String regimenId1 = "";
        String s = "1,2,3,4,14";
        boolean dispense = true;
        if (session.getAttribute("dispenserList") != null) {
            dispenserList = (ArrayList) session.getAttribute("dispenserList");
//            System.out.println("Dispenser List in Dispenser Grid is "+dispenserList);
        }

        //check if drug has already been dispensed and remove
        for (Iterator<Map<String, String>> iterator = dispenserList.iterator(); iterator.hasNext();) {
            Map map = iterator.next();
            regimentypeId1 = (String) map.get("regimentypeId");
            regimenId1 = (String) map.get("regimenId");
            //If drug is the same or ARV remove from list
            if (regimenId.equals(regimenId1) || (s.indexOf(regimentypeId) != -1 && s.indexOf(regimentypeId1) != -1)) {
                iterator.remove();
                dispense = false;
            }
            //If ARV but different regimen dispense
            if (s.indexOf(regimentypeId) != -1 && !regimenId.equals(regimenId1)) {
                dispense = true;
            }
        }
        if (dispense) {
            dispenseDrug(regimenId);
        }
        session.setAttribute("dispenserList", dispenserList);
        return SUCCESS;
    }

    public String dispenserGridRetrieve() {
        //setTotalpages(1);
        //setCurrpage(1);
        //setTotalrecords(20);

        System.out.println("Got Here!");
        // retrieve the record store in session attribute
        if (session.getAttribute("dispenserList") != null) {
            dispenserList = (ArrayList) session.getAttribute("dispenserList");
//            System.out.println("Dispenser List in Dispenser Grid Retrieve is "+dispenserList);
        }
        return SUCCESS;
    }

    private void dispenseDrug(String id) {
        try {
            //obtain a JDBC connect and execute query

            query = "SELECT drug.name, drug.strength, drug.morning, drug.afternoon, drug.evening, regimendrug.regimendrug_id, regimendrug.regimen_id, regimendrug.drug_id, regimen.regimentype_id "
                    + " FROM drug JOIN regimendrug ON regimendrug.drug_id = drug.drug_id JOIN regimen ON regimendrug.regimen_id = regimen.regimen_id WHERE regimendrug.regimen_id = ?";

            jdbcTemplate.query(query, resultSet -> {
                //loop through resultSet for each row and put into Map
                while (resultSet.next()) {
                    String regimentypeId = Long.toString(resultSet.getLong("regimentype_id"));
                    String regimendrugId = Long.toString(resultSet.getLong("regimendrug_id"));
                    String regimenId = Long.toString(resultSet.getLong("regimen_id"));
                    String drugId = Long.toString(resultSet.getLong("drug_id"));
                    String description = resultSet.getString("name") + " " + resultSet.getString("strength");
                    String morning = Double.toString(resultSet.getDouble("morning"));
                    String afternoon = Double.toString(resultSet.getDouble("afternoon"));
                    String evening = Double.toString(resultSet.getDouble("evening"));
                    String duration = "0";
                    String quantity = "0.0";

                    Map<String, String> map = new HashMap<String, String>();
                    map.put("regimentypeId", regimentypeId);
                    map.put("regimendrugId", regimendrugId);
                    map.put("regimenId", regimenId);
                    map.put("drugId", drugId);
                    map.put("description", description);
                    map.put("morning", morning);
                    map.put("afternoon", afternoon);
                    map.put("evening", evening);
                    map.put("duration", duration);
                    map.put("quantity", quantity);
                    dispenserList.add(map);
                }
                return null;
            }, Long.parseLong(id));
        } catch (Exception exception) {

        }
    }

    public String updateDispenserList() {
        // retrieve pharmacy information to be saved from request parameters 
        String regimendrugId = request.getParameter("id");
        String morning = request.getParameter("morning");
        String afternoon = request.getParameter("afternoon");
        String evening = request.getParameter("evening");
        String duration = request.getParameter("duration");
        Double q = Double.parseDouble(request.getParameter("morning")) + Double.parseDouble(request.getParameter("afternoon")) + Double.parseDouble(request.getParameter("evening"));
        String quantity = Double.toString(Integer.parseInt(request.getParameter("duration")) * q);

        // retrieve the list stored as an attribute in session object
        if (session.getAttribute("dispenserList") != null) {
            dispenserList = (ArrayList) session.getAttribute("dispenserList");
        }

        // find the target drug and update with values of request parameters
        for (int i = 0; i < dispenserList.size(); i++) {
            String id = (String) dispenserList.get(i).get("regimendrugId"); // retrieve regimen id from list
            if (id.equals(regimendrugId)) {
                dispenserList.get(i).remove("morning");
                dispenserList.get(i).put("morning", morning);
                dispenserList.get(i).remove("afternoon");
                dispenserList.get(i).put("afternoon", afternoon);
                dispenserList.get(i).remove("evening");
                dispenserList.get(i).put("evening", evening);
                dispenserList.get(i).remove("duration");
                dispenserList.get(i).put("duration", duration);
                dispenserList.get(i).remove("quantity");
                dispenserList.get(i).put("quantity", quantity);
            }
        }
        // set dispenserList as a session-scoped attribute
        session.setAttribute("dispenserList", dispenserList);
        return SUCCESS;
    }

    public String refillPeriod() {
        // retrieve the list stored as an attribute in session object
        if (session.getAttribute("dispenserList") != null) {
            dispenserList = (ArrayList) session.getAttribute("dispenserList");
        }

        if (request.getParameterMap().containsKey("refill")) {
            // find the target drug and update duration only regimentype ids 1,2,3,4,14
            String s = "1,2,3,4,14";
            for (int i = 0; i < dispenserList.size(); i++) {
                String id = (String) dispenserList.get(i).get("regimentypeId"); // retrieve regimentype id from list
                if (s.indexOf(id) != -1) {
                    Double morn = Double.parseDouble(dispenserList.get(i).get("morning"));
                    Double after = Double.parseDouble(dispenserList.get(i).get("afternoon"));
                    Double even = Double.parseDouble(dispenserList.get(i).get("evening"));
                    String quantity = Double.toString((morn + after + even) * Integer.parseInt(request.getParameter("refill")));
                    dispenserList.get(i).remove("duration");
                    dispenserList.get(i).put("duration", request.getParameter("refill"));
                    dispenserList.get(i).remove("quantity");
                    dispenserList.get(i).put("quantity", quantity);
                }
            }
        } else if (session.getAttribute("fromPrescription") == "true" && request.getParameterMap().containsKey("regimen_durations")) {
            String regimen_durations = request.getParameter("regimen_durations");
            String[] regimen_durs = regimen_durations.split(",");
            List<String> regimenTypes = new ArrayList<>();
            Map<String, String> durations = new HashMap<>();
            for (int i = 0; i < regimen_durs.length; i += 3) {
                regimenTypes.add(regimen_durs[i]);
                durations.put(regimen_durs[i], regimen_durs[i + 2]);
            }
            for (int i = 0; i < dispenserList.size(); i++) {
                String id = (String) dispenserList.get(i).get("regimentypeId"); // retrieve regimentype id from list
                if (regimenTypes.indexOf(id) != -1) {
                    Double morn = Double.parseDouble(dispenserList.get(i).get("morning"));
                    Double after = Double.parseDouble(dispenserList.get(i).get("afternoon"));
                    Double even = Double.parseDouble(dispenserList.get(i).get("evening"));
                    String quantity = Double.toString((morn + after + even) * Integer.parseInt(durations.get(id).toString()));
                    dispenserList.get(i).remove("duration");
                    dispenserList.get(i).put("duration", durations.get(id));
                    dispenserList.get(i).remove("quantity");
                    dispenserList.get(i).put("quantity", quantity);
                }
            }
        }
        // set dispenserList as a session-scoped attribute
        session.setAttribute("dispenserList", dispenserList);
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
     * @return the dispenserList
     */
    public ArrayList<Map<String, String>> getDispenserList() {
        return dispenserList;
    }

    /**
     * @param dispenserList the dispenserList to set
     */
    public void setDispenserList(ArrayList<Map<String, String>> dispenserList) {
        this.dispenserList = dispenserList;
    }
}
