/**
 *
 * @author aalozie
 */

package org.fhi360.lamis.controller;

import com.opensymphony.xwork2.ActionSupport;
import org.fhi360.lamis.utility.MiscService;

public class MiscServiceAction extends ActionSupport{
    public String miscService() {
        try{
        new MiscService().task();
        }catch(Exception E){
            
        }
        return SUCCESS;
    }  
}
