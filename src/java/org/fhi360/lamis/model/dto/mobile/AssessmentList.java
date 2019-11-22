/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.model.dto.mobile;

import java.util.List;


/**
 *
 * @author user10
 */
public class AssessmentList {

    private List<AssessmentDTO> assessment;

    public List<AssessmentDTO> getAssessment() {
        return assessment;
    }

    public void setAssessment(List<AssessmentDTO> assessment) {
        this.assessment = assessment;
    }

}
