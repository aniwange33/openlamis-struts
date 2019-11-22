/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.resource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import javax.xml.bind.JAXBException;

import org.fhi360.lamis.dao.hibernate.FacilityDAO;
import org.fhi360.lamis.dao.jdbc.FacilityJDBC;

import org.fhi360.lamis.model.Facility;
import org.fhi360.lamis.model.dto.mobile.AssessmentDTO;
import org.fhi360.lamis.model.dto.mobile.AssessmentList;
import org.fhi360.lamis.model.dto.mobile.ClinicDTO;
import org.fhi360.lamis.model.dto.mobile.ClinicList;
import org.fhi360.lamis.model.dto.mobile.HtsDTO;
import org.fhi360.lamis.model.dto.mobile.HtsList;
import org.fhi360.lamis.model.dto.mobile.IndexContactDTO;
import org.fhi360.lamis.model.dto.mobile.IndexContactList;
import org.fhi360.lamis.model.dto.mobile.PatientDTO;
import org.fhi360.lamis.model.dto.mobile.PatientList;
import org.fhi360.lamis.service.ApplicationUpdatesService;
import org.fhi360.lamis.service.parser.json.FacilityJsonParser;
import org.fhi360.lamis.utility.DatabaseHandler;
import org.fhi360.lamis.utility.PatientNumberNormalizer;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * REST Web Service
 *
 * @author user1
 */
@Path("webservice")
public class WebserviceResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GenericserviceResource
     */
    public WebserviceResource() {
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    /**
     * Retrieves representation of an instance of
     * org.fhi360.lamis.resource.WebserviceResource
     *
     * @param facilityId
     * @param action
     * @return an instance of java.lang.String
     */
    //Get the lock status of the upload folder or lock the folder after upload of files
    @Path("/uploadfolder/{facilityId}/{action}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getText(@PathParam("facilityId") long facilityId, @PathParam("action") int action) {
        System.out.println("Folder status ...");
        if (action == 1) {
            return WebserviceRequestHandler.getUploadFolderStatus(facilityId);
        } else {
            WebserviceRequestHandler.lockUploadFolder(facilityId);
            return "locked";
        }

    }

    //Get the updates manifest
    @Path("/patient/{patientId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public @ResponseBody
    Map<String, ? extends Object> getPatient(@PathParam("patientId") String patientId) {
        System.out.println("badthing");
        String name = "oysic";
        Map<String, Object> map = new HashMap();
        map.put("name", "John");
        return map;
    }

    //Get the updates manifest
    @Path("/manifest/{dateManifest}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getToken(@PathParam("dateManifest") String dateManifest) {
        return ApplicationUpdatesService.getManifestToken(dateManifest);
    }

    //Get the updates manifest
    @Path("/updates/{filename}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public byte[] getUpdates(@PathParam("filename") String filename) {
        return ApplicationUpdatesService.getUpdates(filename);
    }

    /**
     * PUT method for updating or creating an instance of WebserviceResource
     *
     * @param stream
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(InputStream stream) {
    }

    /**
     * POST method for creating an instance of WebserviceResource
     *
     * @param stream
     * @param table
     * @param facilityId
     * @param content representation for the resource
     * @return an HTTP response with content of the created resource.
     */
    //Upload transaction data to the server
    @Path("/upload/{table}/{facilityId}")
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void postXml(InputStream stream, @PathParam("table") String table, @PathParam("facilityId") long facilityId) {
        //Init the Upload for this facility and date...(Insert or Update)
        Date uploadDate = new Date(new java.util.Date().getTime());
        Long facilityUploading = DatabaseHandler.getFacilityUploadForToday(facilityId, uploadDate);
        String facilityName = FacilityJDBC.getFacilityName(facilityId);
        Timestamp timeStamp = new java.sql.Timestamp(new java.util.Date().getTime());
        //Check if there is an upload initiated for this facility today, if not, initiate one...
        if (facilityUploading == 0L) {
            DatabaseHandler.executeUpdate("INSERT INTO synchistory "
                    + "(facility_id, facility_name, upload_date, tables_uploaded, num_files_uploaded, upload_completed, upload_time_stamp)"
                    + "VALUES "
                    + "('" + facilityId + "', '" + facilityName + "', '" + uploadDate + "', ' ',  0, 0, '" + timeStamp + "' )");
        }
        WebserviceRequestHandler.uploadStream(stream, table, facilityId);
    }

    //Download system files (not specific to any facility)from the server
    @Path("/download/{table}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public byte[] getXml(@PathParam("table") String table) {
        System.out.println(".....downloading tablet: " + table);
        byte[] content = WebserviceRequestHandler.getXml(table);
        return content;
    }

    //Download mobile records from the server
    @Path("/download/mobile/{table}/{facilityId}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public byte[] getXmlMobile(@PathParam("table") String table, @PathParam("facilityId") long facilityId) {
        System.out.println(".....downloading tablet: " + table);
        byte[] content = WebserviceRequestHandler.getJsonMobile(table, facilityId);
        return content;
    }

    //Download dhis indicator value 
    @Path("/download/{table}/{period}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public byte[] getDhisXml(@PathParam("table") String table, @PathParam("period") String period) {
        System.out.println(".........table:  " + table + "........period: " + period);
        byte[] content = WebserviceRequestHandler.getDhisXml(table, period);
        return content;
    }

    /**
     * POST method for creating an instance of WebserviceResource
     *
     * @param stream
     * @param table
     * @param facilityId
     * @param content representation for the resource
     * @return an HTTP response with content of the created resource.
     */
    //Upload transaction data and download files from the server
    @Path("/sync/{table}/{facilityId}")
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public byte[] syncXml(InputStream stream, @PathParam("table") String table, @PathParam("facilityId") long facilityId) {
        return WebserviceRequestHandler.syncStream(stream, table, facilityId);
    }

    /**
     * POST method for creating an instance of Facilityservice
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the created resource.
     */
    //Save a new facility in the server and return the generated facility id to the client
    @Path("/entity")
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public String postJson(String content) {
        Long generatedID = 0L;
        try {
            Facility facility = FacilityJsonParser.jsonObject(content); // Convert content to a facility object
            generatedID = FacilityDAO.save(facility);  // Save facility in the server
            FacilityJDBC.updateExchange(generatedID);  //Update exchange table on the server
        } catch (JAXBException | IOException exception) {
            exception.printStackTrace();
        }
        System.out.println("Saved ID" + generatedID);
        return generatedID.toString();
    }

    //Update an existing facility in the server
    //Normalize hospital numbers if hospital numbers for this facility are to be normalized
    @Path("/entity/update")
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void putJson(String content) {
        try {
            Facility facility = FacilityJsonParser.jsonObject(content);
            PatientNumberNormalizer.normalize(facility);  //Normalize hospital number if padHospitalNumber field is set
            FacilityDAO.update(facility);
        } catch (JAXBException | IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * *************************************************************************************************
     * *
     * This part of the webservice is used by the LAMIS mobile to exchange data
     * with the server
     *
     *
     * *************************************************************************************************
     * @param assessment
     * @return
     */
    
  //  Upload encounter to the server
    @Path("/mobile/sync/{table}")
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void syncJsonMobile(InputStream stream, @PathParam("table") String table) {
      WebserviceRequestHandlerMobile.syncStream(stream, table);
    }
    
    @Path("/mobile/sync/assessment")
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void syncAssessment(@RequestBody AssessmentList assessment) {
        List<AssessmentDTO> assessmentsDto = assessment.getAssessment();
         MobileResourceHandler.saveAssessment(assessmentsDto);
    }

    @Path("/mobile/sync/hts")
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void syncHts(@RequestBody HtsList hts) {
        List<HtsDTO> htsListDto = hts.getHts();
         MobileResourceHandler.saveHts(htsListDto);
    }

    @Path("/mobile/sync/indexcontact")
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void syncIndexcontact(@RequestBody IndexContactList indexcontact) {
        List<IndexContactDTO> indexcontactDto = indexcontact.getIndexcontact();
         MobileResourceHandler.saveIndexContact(indexcontactDto);

    }

    @Path("/mobile/sync/patient")
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void syncPatient(@RequestBody PatientList patient) {
        List<PatientDTO> patientDTO = patient.getPatient();
        // MobileResourceHandler.savePatients(patientDTO);

    }

    @Path("/mobile/sync/clinic")
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void syncClinic(@RequestBody ClinicList clinic) {
        List<ClinicDTO> clinicDTO = clinic.getClinic();
       //  MobileResourceHandler.saveClinic(clinicDTO);
    }

    //Download newly devolved patients from server
    @Path("/mobile/devolved/{communitypharmId}")
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public byte[] syncGetJsonMobile(InputStream stream, @PathParam("communitypharmId") int communitypharmId) {
        return WebserviceRequestHandlerMobile.getPatient(communitypharmId);
    }

    //Get CPARP mobile on a device
    @Path("/mobile/activate/{userName}/{pin}/{deviceId}/{accountUserName}/{accountPassword}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public byte[] getActivationData(@PathParam("userName") String ip, @PathParam("pin") String pin, @PathParam("deviceId") String deviceId, @PathParam("accountUserName") String username, @PathParam("accountPassword") String password) {
        return WebserviceRequestHandlerMobile.getActivationData(ip, pin, deviceId, username, password);
    }

    //Get CPARP user credentials
    @Path("/mobile/activate/{deviceId}/{pin}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public byte[] getCredentials(@PathParam("deviceId") String deviceId, @PathParam("pin") String pin) {
        return WebserviceRequestHandlerMobile.getCredentials(deviceId, pin);
    }

    //Initialise LAMIS lite on a device
    @Path("/mobile/initialize/{userName}/{facilityId}/{deviceId}/{accountUserName}/{accountPassword}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public byte[] getInitializationData(@PathParam("userName") String ip, @PathParam("facilityId") long facilityId, @PathParam("deviceId") String deviceId, @PathParam("accountUserName") String username, @PathParam("accountPassword") String password) {
        return WebserviceRequestHandlerMobile.getInitializationData(ip, facilityId, deviceId, username, password);
    }

    //Get LAMIS lite user credentials
    @Path("/mobile/initialize/{deviceId}/{facilityId}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public byte[] getCredentials(@PathParam("deviceId") String deviceId, @PathParam("facilityId") long facilityId) {
        return WebserviceRequestHandlerMobile.getCredentials(deviceId, facilityId);
    }

}

//@Consumes(MediaType.TEXT_PLAIN)
//@Consumes(MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_XML)
//"text/plain" 
//"application/json"
//"application/xml"
