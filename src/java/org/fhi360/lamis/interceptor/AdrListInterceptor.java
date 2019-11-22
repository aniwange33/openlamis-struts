/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class AdrListInterceptor extends AbstractInterceptor {
    private int actionId;
    
    @Override    
    public String intercept(ActionInvocation invocation) throws Exception {
        String result = invocation.invoke();
        if(getActionId()== 1) {
            AdrList.initList("clinic");
        } 
        else {
            AdrList.initList("pharmacy");            
        }           
        return result;
    }    


    /**
     * @return the actionId
     */
    public int getActionId() {
        return actionId;
    }

    /**
     * @param actionId the actionId to set
     */
    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

}
