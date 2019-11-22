/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.indicator;

import org.fhi360.lamis.dao.hibernate.DhisvalueDAO;
import org.fhi360.lamis.dao.hibernate.IndicatorDAO;
import org.fhi360.lamis.model.Dhisvalue;
import org.fhi360.lamis.model.Indicatorvalue;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * @author user10
 */
public class IndicatorPersister {

    public IndicatorPersister() {

    }

    public void persist(long dataElementId, long categoryId, long stateId, long lgaId, long facilityId, int value, String reportDate) {
        try {
            Indicatorvalue indicator = new Indicatorvalue();

            indicator.setDataElementId(dataElementId);
            indicator.setCategoryId(categoryId);
            indicator.setStateId(stateId);
            indicator.setLgaId(lgaId);
            indicator.setFacilityId(facilityId);
            indicator.setValue(value);
            indicator.setReportDate(java.sql.Date.valueOf(reportDate));

            long indicatorId = getIndicatorId(dataElementId, categoryId, facilityId, reportDate, "");
            if (indicatorId == 0) {
                IndicatorDAO.save(indicator);
            } else {
                indicator.setIndicatorvalueId(indicatorId);
                IndicatorDAO.update(indicator);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void persistDhis(long dataElementId, long categoryId, long stateId, long lgaId, long facilityId, String period, int value, String reportingPeriod) {
        try {
            Dhisvalue indicator = new Dhisvalue();

            indicator.setDataElementId(dataElementId);
            indicator.setCategoryId(categoryId);
            indicator.setStateId(stateId);
            indicator.setLgaId(lgaId);
            indicator.setFacilityId(facilityId);

            if (reportingPeriod.equalsIgnoreCase("daily")) {
                indicator.setDataElementIdDhis(DhisCodeSetResolver.getCode("DATA ELEMENT DR", dataElementId));
            } else {
                indicator.setDataElementIdDhis(DhisCodeSetResolver.getCode("DATA ELEMENT WR", dataElementId));
            }

            indicator.setCategoryIdDhis(DhisCodeSetResolver.getCode("CATEGORY COMBO", categoryId));
            indicator.setFacilityIdDhis(DhisCodeSetResolver.getCode("FACILITY", facilityId));

            indicator.setPeriod(period);
            indicator.setValue(value);

            long indicatorId = getIndicatorId(dataElementId, categoryId, facilityId, period, "dhis");
            if (indicatorId == 0) {
                DhisvalueDAO.save(indicator);
            } else {
                indicator.setDhisvalueId(indicatorId);
                DhisvalueDAO.update(indicator);
            }
            System.out.println("Indicator persister update completed.");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Get indicatorvalueId
    private long getIndicatorId(long dataElementId, long categoryId, long facilityId, String reportDate, String table) {
        String query = "SELECT indicatorvalue_id AS id FROM indicatorvalue WHERE data_element_id = " + dataElementId +
                "  AND category_id = " + categoryId + " AND facility_id = " + facilityId + " AND report_date = '" + reportDate + "'";
        if (table.equalsIgnoreCase("dhis"))
            query = "SELECT dhisvalue_id AS id FROM dhisvalue WHERE data_element_id = " + dataElementId + "  AND category_id = " + categoryId + " AND facility_id = " + facilityId + " AND period = '" + reportDate + "'";
        long id = 0;
        List<Long> ids = ContextProvider.getBean(JdbcTemplate.class).queryForList(query, Long.class);
        if (!ids.isEmpty()) {
            return ids.get(0);
        }
        return id;
    }

}
