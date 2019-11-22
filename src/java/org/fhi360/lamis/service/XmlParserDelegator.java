/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service;

import org.fhi360.lamis.service.parser.xml.*;

/**
 *
 * @author user1
 */
public class XmlParserDelegator {

    public XmlParserDelegator() {
    }

    public void delegate(String table, String fileName) {
        try {
            switch (table) {
                case "monitor":
                    MonitorXmlParser monitorXmlParser = new MonitorXmlParser();
                    monitorXmlParser.parseXml(fileName);
                    break;
                case "user":
                    UserXmlParser userXmlParser = new UserXmlParser();
                    userXmlParser.parseXml(fileName);
                    break;
                case "casemanager":
                    CasemanagerXmlParser caseManagerXmlParser = new CasemanagerXmlParser();
                    caseManagerXmlParser.parseXml(fileName);
                    break;
                case "communitypharm":
                    CommunitypharmXmlParser communitypharmXmlParser = new CommunitypharmXmlParser();
                    communitypharmXmlParser.parseXml(fileName);
                    break;
                case "patient":
                    PatientXmlParser patientXmlParser = new PatientXmlParser();
                    patientXmlParser.parseXml(fileName);
                    break;
                case "clinic":
                    ClinicXmlParser clinicXmlParser = new ClinicXmlParser();
                    clinicXmlParser.parseXml(fileName);
                    break;
                case "pharmacy":
                    PharmacyXmlParser pharmacyXmlParser = new PharmacyXmlParser();
                    pharmacyXmlParser.parseXml(fileName);
                    break;
                case "laboratory":
                    LaboratoryXmlParser laboratoryXmlParser = new LaboratoryXmlParser();
                    laboratoryXmlParser.parseXml(fileName);
                    break;
                case "adrhistory":
                    AdrXmlParser adrXmlParser = new AdrXmlParser();
                    adrXmlParser.parseXml(fileName);
                    break;
                case "oihistory":
                    OiXmlParser oiXmlParser = new OiXmlParser();
                    oiXmlParser.parseXml(fileName);
                    break;
                case "adherehistory":
                    AdhereXmlParser adhereXmlParser = new AdhereXmlParser();
                    adhereXmlParser.parseXml(fileName);
                    break;
                case "statushistory":
                    StatusXmlParser statusXmlParser = new StatusXmlParser();
                    statusXmlParser.parseXml(fileName);
                    break;
                case "regimenhistory":
                    RegimenXmlParser regimenXmlParser = new RegimenXmlParser();
                    regimenXmlParser.parseXml(fileName);
                    break;
                case "chroniccare":
                    ChroniccareXmlParser chroniccareXmlParser = new ChroniccareXmlParser();
                    chroniccareXmlParser.parseXml(fileName);
                    break;
                case "tbscreenhistory":
                    TbscreenhistoryXmlParser tbscreenhistoryXmlParser = new TbscreenhistoryXmlParser();
                    tbscreenhistoryXmlParser.parseXml(fileName);
                    break;
                case "dmscreenhistory":
                    DmscreenhistoryXmlParser dmscreenhistoryXmlParser = new DmscreenhistoryXmlParser();
                    dmscreenhistoryXmlParser.parseXml(fileName);
                    break;
                case "motherinformation":
                    MotherInformationXmlParser motherinformationXmlParser = new MotherInformationXmlParser();
                    motherinformationXmlParser.parseXml(fileName);
                    break;
                case "anc":
                    AncXmlParser ancXmlParser = new AncXmlParser();
                    ancXmlParser.parseXml(fileName);
                    break;
                case "delivery":
                    DeliveryXmlParser deliveryXmlParser = new DeliveryXmlParser();
                    deliveryXmlParser.parseXml(fileName);
                    break;
                case "child":
                    ChildXmlParser childXmlParser = new ChildXmlParser();
                    childXmlParser.parseXml(fileName);
                    break;
                case "maternalfollowup":
                    MaternalfollowupXmlParser maternalfollowupXmlParser = new MaternalfollowupXmlParser();
                    maternalfollowupXmlParser.parseXml(fileName);
                    break;
                case "childfollowup":
                    ChildfollowupXmlParser childfollowupXmlParser = new ChildfollowupXmlParser();
                    childfollowupXmlParser.parseXml(fileName);
                    break;
                case "partnerinformation":
                    PartnerinformationXmlParser partnerinformationXmlParser = new PartnerinformationXmlParser();
                    partnerinformationXmlParser.parseXml(fileName);
                    break;
                case "specimen":
                    SpecimenXmlParser specimenXmlParser = new SpecimenXmlParser();
                    specimenXmlParser.parseXml(fileName);
                    break;
                case "eid":
                    EidXmlParser eidXmlParser = new EidXmlParser();
                    eidXmlParser.parseXml(fileName);
                    break;
                case "labno":
                    LabnoXmlParser labnoXmlParser = new LabnoXmlParser();
                    labnoXmlParser.parseXml(fileName);
                    break;
                case "nigqual":
                    NigqualXmlParser nigqualXmlParser = new NigqualXmlParser();
                    nigqualXmlParser.parseXml(fileName);
                    break;
                case "devolve":
                    DevolveXmlParser devolveXmlParser = new DevolveXmlParser();
                    devolveXmlParser.parseXml(fileName);
                    break;
                case "patientcasemanager":
                    PatientcasemanagerXmlParser patientcasemanagerXmlParser = new PatientcasemanagerXmlParser();
                    patientcasemanagerXmlParser.parseXml(fileName);
                    break;
                case "eac":
                    EacXmlParser eacXmlParser = new EacXmlParser();
                    eacXmlParser.parseXml(fileName);
                    break;
                case "biometric":
                    BiometricXmlParser biometricXmlParser = new BiometricXmlParser();
                    biometricXmlParser.parseXml(fileName);
                    break;
                case "assessment":
                    AssessmentXmlParser assessmentXmlParser = new AssessmentXmlParser();
                    assessmentXmlParser.parseXml(fileName);
                    break;
                case "hts":
                    HtsXmlParser htsXmlParser = new HtsXmlParser();
                    htsXmlParser.parseXml(fileName);
                    break;
                case "indexcontact":
                    IndexcontactXmlParser indexcontactXmlParser = new IndexcontactXmlParser();
                    indexcontactXmlParser.parseXml(fileName);
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
