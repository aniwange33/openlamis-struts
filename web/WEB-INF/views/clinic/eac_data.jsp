<%-- 
    Document   : Appointment
    Created on : Feb 8, 2012, 1:15:46 PM
    Author     : AALOZIE
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sj" uri="/struts-jquery-tags"%>
<!DOCTYPE html>
<html>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>LAMIS 3.0</title>
        <jsp:include page="/WEB-INF/views/template/css.jsp" />
        <jsp:include page="/WEB-INF/views/template/javascript.jsp" />
        <script type="text/javascript" src="js/lamis/eac-common.js"></script>
        <script type="text/JavaScript">
            var obj = {};
            var lastSelectDate = "", date = ""; 
            var updateRecord = false;
            $(document).ready(function(){
                resetPage();
                initialize();
                reports();
var newAac =  sessionStorage.getItem("key");
if(newAac==1){
    $.ajax({
        url: "Patient_retrieve.action",
        dataType: "json",                    
        success: function(patientList) {
            // set patient id and number for which infor is to be entered
            $("#patientId").val(patientList[0].patientId);
            $("#hospitalNum").val(patientList[0].hospitalNum);
            $("#patientInfor").html(patientList[0].surname + " " + patientList[0].otherNames);

            date = dateSlice(patientList[0].dateLastViralLoad);
            console.log("Date Last VL:"+date);
            $("#dateLastViralLoad_View").html(date);
            $("#lastViralLoad_View").html(patientList[0].lastViralLoad);
            //dueViralLoad(patientList[0].dueViralLoad);
        }                    
    }); //end of ajax call
}else{  $.ajax({
                    url: "Eac_retrieve.action",
                    dataType: "json",                    
                    success: function(eacList) {
                        populateForm(eacList);
                    }                    
                }); //end of ajax call  
}
                
    $("#save_button").bind("click", function(event){
        if($("#userGroup").html() == "Data Analyst") {
            $("#lamisform").attr("action", "Error_message");
            return true;                        
        }
        else {
            if(validateForm()) {
                if(updateRecord) {
                    $("#lamisform").attr("action", "Eac_update"); 
                  
                } 
                else {
                    $("#lamisform").attr("action", "Eac_save");       
                  
                }
                return true;                        
            } 
            else {
                return false;
            } 
        } 
    });            
                $("#close_button").bind("click", function(event){
                  
                    window.location.href = "Clinic_center";
                });               
            });          
        </script>
    </head>

    <body>
        <jsp:include page="/WEB-INF/views/template/header.jsp" />
        <jsp:include page="/WEB-INF/views/template/nav_clinic.jsp" />
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="Home_page.action">Home</a></li>
                <li class="breadcrumb-item"><a href="Clinic_center">Clinic</a></li>
                <li class="breadcrumb-item active" aria-current="page">New EAC</li>
            </ol>
        </nav>
        <s:form id="lamisform" theme="css_xhtml">
            <div class="row">
                <div class="col-md-8 ml-auto mr-auto">
                    <div class="card">
                        <div class="card-body">
                            <div id="messageBar"></div>
                            <div class="row">
                                <div class="col-md-6 form-group">
                                    <label>Hospital No</label>
                                    <input name="hospitalNum" type="text" class="form-control" id="hospitalNum" readonly="readonly"/>
                                </div>
                                <div class="col-md-6 form-group">
                                    <br/>
                                    <span id="patientInfor"></span>
                                    <input name="patientId" type="hidden" id="patientId"/>
                                    <input name="eacId" type="hidden" id="eacId"/>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label class="form-label">Date of 1st EAC Session<span
                                                style="color:black">:</span></label>
                                        <input name="date1" type="text" class="form-control" id="date1" />
                                        <input name="dateEac1" type="hidden" class="form-control" id="dateEac1" /><span
                                            id="dateHelp1" class="errorspan"></span>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group"><label>Last Viral Load:</label>&nbsp;<span id="lastViralLoad_View" style="color:blue"></span>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label class="form-label">Date of 2nd EAC Session<span
                                                style="color:black">:</span></label>
                                        <input name="date2" type="text" class="form-control" id="date2" />
                                        <input name="dateEac2" type="hidden" class="form-control" id="dateEac2" /><span
                                            id="dateHelp2" class="errorspan"></span>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group"><label>Date of Last Viral Load:</label>&nbsp;<span id="dateLastViralLoad_View" style="color:blue"></span>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label class="form-label">Date of 3rd EAC Session</label>
                                        <input name="date3" type="text" class="form-control" id="date3" />
                                        <input name="dateEac3" type="hidden" class="form-control" id="dateEac3" /><span
                                            id="dateHelp3" class="errorspan"></span>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label>Date of Repeat VL Sample Collection</label>
                                        <input name="date4" type="text" class="form-control" id="date4" />
                                    </div>
                                </div>
                                <input name="dateSampleCollected" type="hidden" id="dateSampleCollected" />
                            </div>
                            <div id="userGroup" style="display: none">
                                <s:property value="#session.userGroup" />
                            </div>
                            <div class="pull-right">
                                <button id="save_button" class="btn btn-info">Save</button>
                                <button id="close_button" type="button" class="btn btn-default">Close</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </s:form>
        <jsp:include page="/WEB-INF/views/template/footer.jsp" />
    </body>

</html>