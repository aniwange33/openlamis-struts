/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.controller;

import com.opensymphony.xwork2.ActionSupport;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.PDFUtil;
import org.fhi360.lamis.utility.Scrambler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author User10
 */
public class FingerprintReportAction extends ActionSupport implements ServletRequestAware {

    Logger LOG = LoggerFactory.getLogger(FingerprintReportAction.class);
    private HttpServletRequest request;
    private static final DecimalFormat FORMATTER = new DecimalFormat("#,###");
    private final JdbcTemplate jdbcTemplate =ContextProvider.getBean(JdbcTemplate.class);
    private InputStream stream;

    public InputStream getStream() {
        return stream;
    }

    public String fingerprintReport() throws IOException {
        try{
        String report = request.getParameter("report");
        String title = "Biometric Enrollment Report";
        Long facilityId = (Long) request.getSession().getAttribute("facilityId");
        String query = "select distinct other_names, surname, unique_id, p.gender, age, age_unit, "
                + "p.address, p.phone, current_status from biometric b "
                + "inner join patient p on b.patient_id = p.id_uuid where "
                + "enrollment_date between ? and ? and p.facility_id = ?";
        switch (report) {
            case "not-enrolled":
                query = "select distinct other_names, surname, unique_id, p.gender, age, age_unit, "
                        + "p.address, p.phone, current_status from patient p "
                        + "left join biometric b on p.id_uuid = b.patient_id where "
                        + "p.facility_id = ? and b.biometric_id is null and current_status "
                        + "not in ('ART Transfer Out', 'Known Death', 'Pre-ART Transfer Out')";
                title = "Patient Without Biometric Enrollment";
                break;
            case "duplicate":
                query = "";
        }
        Scrambler scrambler = new Scrambler();
        List<Map<String, Object>> dataSource = jdbcTemplate.query(query, rs -> {
            List<Map<String, Object>> result = new ArrayList<>();
            int count = 1;
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("sn", FORMATTER.format(count++));
                String name = scrambler.unscrambleCharacters(rs.getString("other_names")) + " " + 
                        scrambler.unscrambleCharacters(rs.getString("surname"));
                map.put("name", name);
                map.put("unique_id", rs.getString("unique_id"));
                map.put("gender", rs.getString("gender"));
                map.put("age", rs.getInt("age") + " " + rs.getString("age_unit"));
                map.put("address", scrambler.unscrambleCharacters(rs.getString("address")));
                map.put("phone", scrambler.unscrambleNumbers(rs.getString("phone")));
                map.put("current_status", rs.getString("current_status"));
                result.add(map);
            }
            return result;
        }, getDateParameter("from"), getDateParameter("to"), facilityId);

        query = "select f.name as facility, s.name as state, l.name as lga from "
                + "facility f inner join lga l on f.lga_id = l.lga_id inner join state s "
                + "on f.state_id = s.state_id where f.facility_id = ?";
        Map<String, Object> parameters = new HashMap<>();
        jdbcTemplate.query(query, rs -> {
            while (rs.next()) {
                parameters.put("facility", rs.getString("facility"));
                parameters.put("state", rs.getString("state"));
                parameters.put("lga", rs.getString("lga"));
            }
            return null;
        }, facilityId);
        parameters.put("title", title);
        parameters.put("from", getDateParameter("from"));
        parameters.put("to", getDateParameter("to"));
        
        byte[] data = PDFUtil.generate("biometric_report", parameters, dataSource, true);
        stream = new ByteArrayInputStream(data);
        }catch(Exception e){
            e.printStackTrace();
        }
        return SUCCESS;
    }

    private Date getDateParameter(String date) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return format.parse(request.getParameter(date));
        } catch (ParseException ignored) {
        }
        return new Date();
    }

    @Override
    public void setServletRequest(HttpServletRequest hsr) {
        this.request = hsr;
    }
}
