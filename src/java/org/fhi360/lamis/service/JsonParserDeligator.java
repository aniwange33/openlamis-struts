/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service;

import org.fhi360.lamis.service.parser.mobile.AppointmentJsonParser;
import org.fhi360.lamis.service.parser.mobile.AssessmentJsonParser;
import org.fhi360.lamis.service.parser.mobile.ClinicJsonParser;
import org.fhi360.lamis.service.parser.mobile.DrugtherapyJsonParser;
import org.fhi360.lamis.service.parser.mobile.EncounterJsonParser;
import org.fhi360.lamis.service.parser.mobile.HtsJsonParser;
import org.fhi360.lamis.service.parser.mobile.IndexcontactJsonParser;
import org.fhi360.lamis.service.parser.mobile.MhtcJsonParser;
import org.fhi360.lamis.service.parser.mobile.MonitorJsonParser;
import org.fhi360.lamis.service.parser.mobile.PatientJsonParser;

/**
 *
 * @author user10
 */
public class JsonParserDeligator {

    public JsonParserDeligator() {
    }
    
    public void delegate(String table, String content) {
        try {
            switch (table) {              
                case "monitor":
                    MonitorJsonParser monitorJsonParser = new MonitorJsonParser();                    
                    monitorJsonParser.parserJson(content);
                    break;
                case "patient":
                    PatientJsonParser patientJsonParser = new PatientJsonParser();                    
                    patientJsonParser.parserJson(table, content);
                    break;
                case "clinic":
                    ClinicJsonParser clinicJsonParser = new ClinicJsonParser();                    
                    clinicJsonParser.parserJson(table, content);
                    break;
                case "hts":
                    HtsJsonParser htsJsonParser = new HtsJsonParser();                    
                    htsJsonParser.parserJson(table, content);
                    break;
                case "indexcontact":
                    IndexcontactJsonParser indexcontactJsonParser = new IndexcontactJsonParser();
                    indexcontactJsonParser.parserJson(table, content);
                    break;
                case "assessment":
                    AssessmentJsonParser assessmentJsonParser = new AssessmentJsonParser();
                    assessmentJsonParser.parserJson(table, content);
                    break;
                case "encounter":
                    EncounterJsonParser encounterJsonParser = new EncounterJsonParser();
                    encounterJsonParser.parserJson(table, content);
                    break;
                case "drugtherapy":
                    DrugtherapyJsonParser drugtherapyJsonParser = new DrugtherapyJsonParser();
                    drugtherapyJsonParser.parserJson(table, content);
                    break;
                case "mhtc":
                    MhtcJsonParser mhtcJsonParser = new MhtcJsonParser();
                    mhtcJsonParser.parserJson(table, content);
                    break;
                case "appointment":
                    AppointmentJsonParser appointmentJsonParser = new AppointmentJsonParser();
                    appointmentJsonParser.parserJson(table, content);
                    break;
                default:
                    break;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }
}
