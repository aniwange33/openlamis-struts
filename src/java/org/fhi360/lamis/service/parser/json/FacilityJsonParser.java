/**
 *
 * @author user1
 */
package org.fhi360.lamis.service.parser.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fhi360.lamis.model.Facility;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


public class FacilityJsonParser {
    
    
    public static String objectJson(Object obj) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        String jsonString = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            jsonString = objectMapper.writeValueAsString(obj);           
        } catch(Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        return jsonString;            
    }
    
    public static Facility jsonObject(String jsonString) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Facility facility = new Facility();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            facility = objectMapper.readValue(jsonString, Facility.class);
        } catch(Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        return facility;
    }
    
    public static String objectXml(Object object, JAXBContext context) throws JAXBException {
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter writer = new StringWriter();
        marshaller.marshal(object, writer);
        String xmlString = writer.toString();
        return xmlString;
    }
    
    public static Facility xmlObject(String xmlString, JAXBContext context) throws JAXBException {
        StringReader reader = new StringReader(xmlString);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Facility facility = (Facility) unmarshaller.unmarshal(reader);
        return facility;
    }
}
