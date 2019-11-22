/**
 *
 * @author AALOZIE
 */

package org.fhi360.lamis.controller;

import com.opensymphony.xwork2.ActionSupport;
import org.fhi360.lamis.converter.ChroniccareDataConverter;
import org.fhi360.lamis.converter.ClinicDataConverter;
import org.fhi360.lamis.converter.DHISDataConverter;
import org.fhi360.lamis.converter.EidDataConverter;
import org.fhi360.lamis.converter.LabDataConverter;
import org.fhi360.lamis.converter.PatientDataConverter;
import org.fhi360.lamis.converter.PatientEncounterSummaryConverter;
import org.fhi360.lamis.converter.PatientOutcomeConverter;
import org.fhi360.lamis.converter.PatientStatusSummaryConverter;
import org.fhi360.lamis.converter.PharmacyDataConverter;
import org.fhi360.lamis.converter.TreatmentSummaryConverter;
import org.fhi360.lamis.converter.RetentionTrackerConverter;
import org.fhi360.lamis.converter.SummaryFormConverter;
import org.fhi360.lamis.converter.SyncAuditConverter;
import org.fhi360.lamis.converter.TxMlDataConverter;
import org.fhi360.lamis.converter.TreatmentTrackerConverter;
import org.fhi360.lamis.converter.ViralLoadSummaryConverter;

public class ConverterDispatchAction extends ActionSupport {
    private int recordType;
    private String status;
    private String fileName;
    
    public String dispatcher() {      
        try{
        switch(recordType) {
            case 1: setFileName(new PatientDataConverter().convertExcel()); 
                    break;
            case 2: setFileName(new ClinicDataConverter().convertExcel()); 
                    break;
            case 3: setFileName(new PharmacyDataConverter().convertExcel());  
                    break;
            case 4: setFileName(new LabDataConverter().convertExcel());
                    break;
            case 5: setFileName(new PatientStatusSummaryConverter().convertExcel()); 
                    break;
            case 6: setFileName(new SyncAuditConverter().convertExcel()); 
                    break;
            case 7: setFileName(new ChroniccareDataConverter().convertExcel()); 
                    break;
            case 8: setFileName(new EidDataConverter().convertExcel()); 
                    break;
            case 9: setFileName(new ViralLoadSummaryConverter().convertExcel()); 
                    break;
            case 10: setFileName(new PatientOutcomeConverter().convertExcel()); 
                    break;
            case 11: setFileName(new SummaryFormConverter().convertExcel()); 
                    break;
            case 12: setFileName(new PatientEncounterSummaryConverter().convertExcel()); 
                    break;
            case 13: setFileName(new TreatmentTrackerConverter().convertExcel()); 
                    break;
            case 14: setFileName(new RetentionTrackerConverter().convertExcel());
                    break;
            case 15: setFileName(new TreatmentSummaryConverter().convertExcel());
                    break;
            case 16: setFileName(new TxMlDataConverter().convertExcel());
                    break;
            case 20: setFileName(new DHISDataConverter().convertExcel());
                    break;
            default: break; 
        }
        }catch(Exception e){
            
        }
        return SUCCESS;        
    }
    
    /**
     * @return the recordTyp
     */
    public int getRecordType() {
        return recordType;
    }

    /**
     * @param recordTyp the recordTyp to set
     */
    public void setRecordType(int recordType) {
        this.recordType = recordType;
    }


    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}