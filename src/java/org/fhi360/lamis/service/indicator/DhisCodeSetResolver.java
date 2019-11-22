/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.indicator;

import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class DhisCodeSetResolver {

    public static String getCode(String codeSetNm, long lamisId) {
        String query = "SELECT DISTINCT dhis_id FROM dhiscodeset WHERE UPPER(code_set_nm) = '" +
                codeSetNm.toUpperCase() + "' AND lamis_id = " + lamisId;
        List<String> ids = ContextProvider.getBean(JdbcTemplate.class)
                .queryForList(query, String.class);
        if (!ids.isEmpty()) {
            return ids.get(0);
        }
        return "";
    }
}
