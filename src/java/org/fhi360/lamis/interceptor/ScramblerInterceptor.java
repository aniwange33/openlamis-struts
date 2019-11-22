/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.fhi360.lamis.utility.Scrambler;
import org.fhi360.lamis.controller.PatientAction;

public class ScramblerInterceptor extends AbstractInterceptor {
    
    @Override        
    public String intercept(ActionInvocation invocation) throws Exception {
        Scrambler scrambler = new Scrambler();
        Object action = invocation.getAction();
        if(action instanceof PatientAction) {
            String surname = ((PatientAction) action).getPatient().getSurname();
            String otherNames = ((PatientAction) action).getPatient().getOtherNames();
            String address = ((PatientAction) action).getPatient().getAddress();
            String phone = ((PatientAction) action).getPatient().getPhone();
            String nextKin = ((PatientAction) action).getPatient().getNextKin();
            String addressKin = ((PatientAction) action).getPatient().getAddressKin();
            String phoneKin = ((PatientAction) action).getPatient().getPhoneKin();
            ((PatientAction) action).getPatient().setSurname(scrambler.scrambleCharacters(surname));
            ((PatientAction) action).getPatient().setOtherNames(scrambler.scrambleCharacters(otherNames));
            ((PatientAction) action).getPatient().setAddress(scrambler.scrambleCharacters(address));
            ((PatientAction) action).getPatient().setPhone(scrambler.scrambleNumbers(phone));
            ((PatientAction) action).getPatient().setNextKin(scrambler.scrambleCharacters(nextKin));
            ((PatientAction) action).getPatient().setAddressKin(scrambler.scrambleCharacters(addressKin));
            ((PatientAction) action).getPatient().setPhoneKin(scrambler.scrambleNumbers(phoneKin));
        }
        return invocation.invoke();
    }
}
