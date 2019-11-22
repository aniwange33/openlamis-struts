/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.resource;

import com.neurotec.biometrics.*;
import com.neurotec.biometrics.client.NBiometricClient;
import com.neurotec.devices.NDevice;
import com.neurotec.devices.NDeviceManager;
import com.neurotec.devices.NDeviceManager.DeviceCollection;
import com.neurotec.devices.NDeviceType;
import com.neurotec.devices.NFScanner;
import com.neurotec.io.NBuffer;
import com.neurotec.lang.NCore;
import com.neurotec.lang.NObject;
import com.neurotec.licensing.NLicense;
import com.neurotec.licensing.NLicenseManager;
import org.apache.commons.lang3.StringUtils;
import org.fhi360.lamis.resource.model.BiometricResult;
import org.fhi360.lamis.resource.model.Device;
import org.fhi360.lamis.utility.Scrambler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

import static org.apache.commons.lang3.SystemUtils.FILE_SEPARATOR;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.PropertyAccessor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author User10
 */
@Path("/api/fingerprint")
public class FingerprintResource {

    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private static final Logger LOG = LoggerFactory.getLogger(FingerprintResource.class.getName());
    private static final Set<String> LICENSES = new HashSet<>();
    private NDeviceManager deviceManager;
    private NBiometricClient client;

    @Path("/readers")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Device> getReaders() {
        List<Device> devices = new ArrayList<>();
        for (NDevice device : getDevices()) {
            Device d = new Device();
            d.setName(device.getDisplayName());
            d.setId(device.getId());
            devices.add(d);
        }
        return devices;
    }

    @Path("/save-duplicate/{patientId1}/{patientId2}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public boolean saveDuplicate(@PathParam("patientId1") Long patientId1,
            @PathParam("patientId2") Long patientId2,
            @Context HttpServletRequest request) {
        Long facilityId = (Long) request.getSession().getAttribute("facilityId");

        transactionTemplate.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus ts) {
                jdbcTemplate.update("insert into biometric_duplicate "
                        + "(biometric_duplicate_id, patient_id_1, patient_id_2, facility_id, duplicate_date) values(?,?,?,?,?)",
                        UUID.randomUUID().toString(), getUUID(patientId1), getUUID(patientId2), facilityId, new Date());
                return null;
            }
        });
        return true;
    }

    private String getUUID(Long patientId) {
        PreparedStatement statement;
        transactionTemplate.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus ts) {
                return null;
            }
        });

        jdbcTemplate.query(String.format("select id_uuid from patient where patient_id = %s", patientId), new ResultSetExtractor() {
            @Override
            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                String[] id = {null};
                while (rs.next()) {
                    id[0] = rs.getString("id_uuid");
                    if (id == null) {
                        id[0] = UUID.randomUUID().toString();
                        transactionTemplate.execute(new TransactionCallback() {
                            @Override
                            public Object doInTransaction(TransactionStatus ts) {
                                jdbcTemplate.update("update patient set id_uuid = ? where patient_id = ?",
                                        id[0], patientId);
                                return null;
                            }
                        });
                    }
                }
                return id[0];
            }
        });
        return null;
    }

    @Path("/find-duplicate/{hospitalNum}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public BiometricResult addDuplicate(@PathParam("hospitalNum") String hospitalNum,
            @Context HttpServletRequest request) {
        Long facilityId = (Long) request.getSession().getAttribute("facilityId");
        Long patientId = jdbcTemplate.queryForObject("select patient_id "
                + " from patient where hospital_num = ? and facility_id = ?",
                Long.class, hospitalNum, facilityId);
        BiometricResult result = getResult(facilityId, patientId);
        if (result != null) {
            result.setMessage("Patient identified");
        }
        return result;
    }

    @Path("/identify/{reader}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public BiometricResult identify(@PathParam("reader") String reader,
            @Context HttpServletRequest request) {
        Long currentFacilityId = (Long) request.getSession().getAttribute("facilityId");
        /*
        try {
            if(!NLicense.isComponentActivated("Biometrics.FingerExtraction")) {
                obtainLicenses();
            }
        } catch (IOException ex) {
            LOG.error("Error: {}", ex.getMessage());
        }
         */
        createClient();
        try {
            reader = URLDecoder.decode(reader, "UTF-8");
        } catch (UnsupportedEncodingException ex) {

        }
        NSubject subject = new NSubject();
        NFinger finger = new NFinger();
        finger.setPosition(NFPosition.UNKNOWN);
        subject.getFingers().add(finger);
        for (NDevice device : getDevices()) {
            if (device.getId().equals(reader)) {
                client.setFingerScanner((NFScanner) device);
            }
        }
        client.setFingerScanner((NFScanner) getDevices().get(0));
        if (client.getFingerScanner() == null) {
            return null;
        }

        NBiometricStatus status = client.capture(subject);
        if (status.equals(NBiometricStatus.OK)) {
            status = client.createTemplate(subject);
            if (status.equals(NBiometricStatus.OK)) {
                List<NSubject> galleries = jdbcTemplate.query("select biometric_id, template from biometric",
                        new ResultSetExtractor<List<NSubject>>() {
                    @Override
                    public List<NSubject> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        List<NSubject> s = new ArrayList<>();
                        while (rs.next()) {
                            byte[] template = rs.getBytes("template");
                            NSubject gallery = new NSubject();
                            gallery.setTemplateBuffer(new NBuffer(template));
                            gallery.setId(rs.getString("biometric_id"));
                            s.add(gallery);
                        }
                        return s;
                    }
                });
                NBiometricTask task = client.createTask(
                        EnumSet.of(NBiometricOperation.ENROLL), null);
                for (NSubject gallery : task.getSubjects()) {
                    task.getSubjects().add(gallery);
                }
                client.performTask(task);
                if (task.getStatus().equals(NBiometricStatus.OK)) {
                    status = client.identify(subject);
                    if (status.equals(NBiometricStatus.OK)) {
                        String id = subject.getMatchingResults().get(0).getId();
                        final BiometricResult[] result = new BiometricResult[1];
                        jdbcTemplate.query("select p.patient_id, p.facility_id, p.hospital_num from biometric b "
                                + " inner join patient p on p.id_uuid = b.patient_id where biometric_id = ?",
                                new RowCallbackHandler() {
                            @Override
                            public void processRow(ResultSet rs) throws SQLException {
                                Long facilityId = rs.getLong("facility_id");
                                String hospitalNum = rs.getString("hospital_num");
                                Long patientId = rs.getLong("patient_id");
                                result[0] = getResult(facilityId, patientId);
                                if (!currentFacilityId.equals(facilityId)) {
                                    result[0].setInFacility(false);
                                    result[0].setMessage("Patient identified");
                                    transactionTemplate.execute(new TransactionCallback() {
                                        @Override
                                        public Object doInTransaction(TransactionStatus ts) {
                                            jdbcTemplate.update("insert into biometric_transfer "
                                                    + "(biometric_transfer_id, hospital_num, transfer_facility_id, facility_id, patient_id, transfer_date)"
                                                    + " values(?,?,?,?,?,?)",
                                                    UUID.randomUUID().toString(), hospitalNum, facilityId,
                                                    currentFacilityId, getUUID(patientId), new Date());
                                            return null;
                                        }
                                    });
                                }
                            }
                        }, id);
                        return result[0];
                    }
                }
            }
        }
        return null;
    }

    @Path("/enrolled-fingers/{patient}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> enrolledFingers(
            @PathParam("patient") Long patientId, @Context HttpServletRequest request) {
        Long facilityId = (Long) request.getSession().getAttribute("facilityId");
        return jdbcTemplate.query("select template_type from biometric where facility_id = ? and patient_id = ?",
                new ResultSetExtractor<List<String>>() {
            @Override
            public List<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<String> fingers = new ArrayList<>();
                while (rs.next()) {
                    String finger = rs.getString("template_type");
                    switch (finger) {
                        case "LEFT_THUMB":
                            finger = "Left Thumb";
                            break;
                        case "RIGHT_THUMB":
                            finger = "Right Thumb";
                            break;
                        case "LEFT_INDEX_FINGER":
                            finger = "Left Index Finger";
                            break;
                        case "RIGHT_INDEX_FINGER":
                            finger = "Right Index Finger";
                            break;
                        case "RIGHT_MIDDLE_FINGER":
                            finger = "Right Middle Finger";
                            break;
                        case "LEFT_MIDDLE_FINGER":
                            finger = "Left Middle Finger";
                            break;
                    }
                    fingers.add(finger);
                }
                return fingers;
            }
        }, facilityId, getUUID(patientId));
    }

    @Path("/verify/{reader}/{patientId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)

    public BiometricResult verify(@PathParam("patientId") Long patientId,
            @PathParam("reader") String reader,
            @Context HttpServletRequest request) {
        createClient();
        try {
            reader = URLDecoder.decode(reader, "UTF-8");
        } catch (UnsupportedEncodingException ex) {

        }
        Long facilityId = (Long) request.getSession().getAttribute("facilityId");
        NSubject subject = new NSubject();
        subject.setId(getUUID(patientId));
        NFPosition position = NFPosition.UNKNOWN;
        NFinger finger = new NFinger();
        finger.setPosition(position);
        subject.getFingers().add(finger);

        for (NDevice device : getDevices()) {
            if (device.getId().equals(reader)) {
                client.setFingerScanner((NFScanner) device);
            }
        }
        if (client.getFingerScanner() == null) {
            return null;
        }

        NBiometricStatus status = client.capture(subject);
        if (status.equals(NBiometricStatus.OK)) {
            status = client.createTemplate(subject);
            if (status.equals(NBiometricStatus.OK)) {
                List<NSubject> subjects = jdbcTemplate.query(
                        "select biometric_id, template,"
                        + "template_type from biometric where patient_id = ? and facility_id = ?",
                        new ResultSetExtractor<List<NSubject>>() {
                    @Override
                    public List<NSubject> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        List<NSubject> s = new ArrayList<>();
                        while (rs.next()) {
                            byte[] template = rs.getBytes("template");
                            String id = rs.getString("biometric_id");
                            //FMRecord record = new FMRecord(new NBuffer(template), BDIFStandard.UNSPECIFIED);
                            //referenceSubject.setTemplate(record);
                            //referenceSubject.setTemplate(value);
                            NSubject referenceSubject = new NSubject();
                            referenceSubject.setId(id);
                            referenceSubject.setTemplateBuffer(new NBuffer(template));
                            s.add(referenceSubject);
                        }
                        return s;
                    }
                }, getUUID(patientId), facilityId);
                for (NSubject referenceSubject : subjects) {
                    status = client.verify(subject, referenceSubject);
                    if (status.equals(NBiometricStatus.OK)) {
                        BiometricResult result = getResult(facilityId, patientId);
                        if (result != null) {
                            result.setMessage("Patient Verified");
                        }
                        dispose(client);
                        return result;
                    }
                }
            }
        }
        return null;
    }

    @Path("/enrol/{reader}/{patientId}/{finger}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public BiometricResult enrol(@PathParam("patientId") Long patientId,
            @PathParam("reader") String reader,
            @PathParam("finger") String type,
            @Context HttpServletRequest request
    ) {

        createClient();
        try {
            reader = URLDecoder.decode(reader, "UTF-8");
        } catch (UnsupportedEncodingException ex) {

        }

        Long facilityId = (Long) request.getSession().getAttribute("facilityId");
        NSubject subject = new NSubject();
        NFPosition position = getFingerPosition(type);
        NFinger finger = new NFinger();
        if (position != null) {
            finger.setPosition(position);
        }
        subject.getFingers().add(finger);
        for (NDevice device : getDevices()) {
            if (device.getId().equals(reader)) {
                client.setFingerScanner((NFScanner) device);
            }
        }
        if (client.getFingerScanner() == null) {
            return null;
        }

        NBiometricStatus status = client.capture(subject);
        if (status.equals(NBiometricStatus.OK)) {
            status = client.createTemplate(subject);
            if (status.equals(NBiometricStatus.OK)) {
                byte[] template = subject.getTemplateBuffer().toByteArray();
                boolean update = false;
                String id = null;
                try {
                    id = jdbcTemplate.queryForObject("select biometric_id from biometric where facility_id = ? "
                            + "and patient_id = ? and template_type = ?",
                            String.class, facilityId, getUUID(patientId), position.name());
                } catch (Exception e) {
                }
                if (id != null) {
                    update = true;
                }
                if (!update) {
                    jdbcTemplate.query("select id_uuid, surname, other_names, address, phone, gender,"
                            + " hospital_num from patient where patient_id = ? and facility_id = ?",
                            new RowCallbackHandler() {
                        @Override
                        public void processRow(ResultSet rs) throws SQLException {
                            String surname = rs.getString("surname");
                            String patientUUID = rs.getString("id_uuid");
                            String otherNames = rs.getString("other_names");
                            String address = rs.getString("address");
                            String phone = rs.getString("phone");
                            String gender = rs.getString("gender");
                            String hospitalNum = rs.getString("hospital_num");
                            Date[] enrollmentDate = {new Date()};
                            try {
                                enrollmentDate[0] = jdbcTemplate.queryForObject("select enrollment_date from biometric where patient_id = ?",
                                        Date.class, getUUID(patientId));
                            } catch (Exception e) {
                            }
                            transactionTemplate.execute(new TransactionCallback() {
                                @Override
                                public Object doInTransaction(TransactionStatus ts) {
                                    jdbcTemplate.update("insert into biometric (biometric_id, facility_id, patient_id, template,"
                                            + "template_type, biometric_type, patient_name, patient_address, patient_phone, "
                                            + "patient_gender, hospital_num, enrollment_date, time_stamp) "
                                            + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                            UUID.randomUUID().toString(), facilityId, patientUUID, template, position.name(),
                                            "FINGERPRINT", StringUtils.trimToEmpty(otherNames) + " " + StringUtils.trimToEmpty(surname),
                                            address, phone, gender, hospitalNum, enrollmentDate[0], new Date());
                                    return null;
                                }
                            });
                        }

                    }, patientId, facilityId);
                    BiometricResult result = getResult(facilityId, patientId);
                    if (result != null) {
                        result.setMessage("Patient Enrolled");
                    }
                    return result;
                } else {
                    jdbcTemplate.update("update biometric set template = ?, time_stamp = ? where biometric_id = ?",
                            template, new Date(), id);
                    BiometricResult result = getResult(facilityId, patientId);
                    if (result != null) {
                        result.setMessage("Patient Enrolled");
                    }
                    return result;
                }
            }

        }
        return null;
    }

    private BiometricResult getResult(Long facilityId, Long patientId) {
        Scrambler scrambler = new Scrambler();
        return jdbcTemplate.query("select surname, other_names, address, phone, gender"
                + " from patient where patient_id = ? and facility_id = ?",
                new ResultSetExtractor<BiometricResult>() {
            @Override
            public BiometricResult extractData(ResultSet rs) throws SQLException, DataAccessException {
                BiometricResult result = new BiometricResult();
                while (rs.next()) {
                    String surname = rs.getString("surname");
                    String otherNames = rs.getString("other_names");
                    String address = rs.getString("address");
                    String phone = rs.getString("phone");
                    String gender = rs.getString("gender");
                    result.setFacilityId(facilityId);
                    result.setPatientId(patientId);
                    result.setPhone(scrambler.unscrambleCharacters(phone));
                    result.setAddress(scrambler.unscrambleCharacters(address));
                    result.setGender(gender);
                    result.setName(scrambler.unscrambleCharacters(StringUtils.trimToEmpty(otherNames))
                            + " " + scrambler.unscrambleCharacters(StringUtils.trimToEmpty(surname)));
                }
                return result;
            }
        }, patientId, facilityId);
    }

    private void obtainLicense(String component) {
        try {
            NLicense.obtainComponents("/local", 52358, component);
        } catch (IOException ex) {
            LOG.error("{}", ex.getMessage());
        }
    }

    private void releaseLicense(String component) {
        try {
            NLicense.releaseComponents(component);
        } catch (IOException ex) {
            LOG.error("{}", ex.getMessage());
        }
    }

    @PostConstruct
    public void initialize() {
        StringBuilder path = new StringBuilder();
        path.append("C:/LAMIS3/neurotec/license").append(FILE_SEPARATOR);
        LibraryManager.initLibraryPath();

        NLicenseManager.initialize();
        Map<String, Object> properties = new PropertyAccessor().getSystemProperties();
        boolean offline = false;
        if (properties.containsKey("offlineBiometric")
                && properties.get("offlineBiometric") == "1") {
            offline = true;
        }
        if (!offline) {
            try {
                URLConnection connection = new URL("http://google.com").openConnection();
                try {
                    LOG.info("Connecting...");
                    connection.connect();
                } catch (IOException ex) {
                    throw new RuntimeException(ex.getMessage());
                }
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(FingerprintResource.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            NLicense.add(String.join("\n", Files.readAllLines(
                    Paths.get(path.toString() + "client.txt"), Charset.defaultCharset())));
            NLicense.add(String.join("\n", Files.readAllLines(
                    Paths.get(path.toString() + "matcher.txt"), Charset.defaultCharset())));

        } catch (IOException ex) {
            LOG.error("{}", ex.getMessage());
        }
        obtainLicenses();

        initDeviceManager();

        createClient();
    }

    private void createClient() {
        client = new NBiometricClient();
        client.setMatchingThreshold(48);
        client.setFingersMatchingSpeed(NMatchingSpeed.LOW);
        client.setFingersTemplateSize(NTemplateSize.LARGE);
        client.initialize();
    }

    @PreDestroy
    public void cleanup() {
        dispose(deviceManager, client);
        for (String license : LICENSES) {
            releaseLicense(license);
        }
        NCore.shutdown();
    }

    private void obtainLicenses() {
        LOG.info("Obtaining licenses");
        for (String license : LICENSES) {
            obtainLicense(license);
        }
    }

    private void initDeviceManager() {
        deviceManager = new NDeviceManager();
        deviceManager.setDeviceTypes(EnumSet.of(NDeviceType.FINGER_SCANNER));
        deviceManager.setAutoPlug(true);
        deviceManager.initialize();
    }

    private DeviceCollection getDevices() {
        return deviceManager.getDevices();
    }

    private void dispose(NObject... objects) {
        for (NObject object : objects) {
            object.dispose();
        }
    }

    private NFPosition getFingerPosition(String type) {
        if (StringUtils.isEmpty(type)) {
            return null;
        }
        return NFPosition.valueOf(type);
    }

    static {
        LICENSES.add("Biometrics.FingerExtraction");
        LICENSES.add("Biometrics.Standards.FingerTemplates");
        LICENSES.add("Devices.FingerScanners");
        LICENSES.add("Biometrics.FingerMatching");
    }
}
