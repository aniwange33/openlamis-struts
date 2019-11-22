<%-- 
    Document   : Clinic
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
        <script type="text/javascript" src="js/lamis/lamis-common.js"></script>               
        <script type="text/javascript" src="js/lamis/clinic-common.js"></script>               
        <script type="text/javascript" src="js/lamis/report-common.js"></script>               
        <script type="text/JavaScript">
            var oiIds = "", adrIds = "", adhereIds = "", date = "", lastSelectDate = "";
            var updateRecord = false;
            $(document).ready(function(){ 
                resetPage();
                initialize();
                reports(); 
                  
                $.ajax({
                    url: "Clinic_retrieve.action",
                    dataType: "json",                     
                    success: function(clinicList) {
                        populateForm(clinicList);                                    
                    }                    
                }); 
   
                var lastSelected = -99;
                $("#adrgrid").jqGrid({
                    url: "Adr_grid_clinic.action",
                    datatype: "json",
                    mtype: "GET",
                    colNames: ["Description", "Severity"],
                    colModel: [
                        {name: "description", index: "description", width: "250"},
                        {name: "severity", index: "severity", width: "70", sortable:false, editable:true, edittype:"select", editoptions:{value:" : ;Grade 1:Grade 1;Grade 2:Grade 2;Grade 3:Grade 3;Grade 4:Grade 4"}},                        
                    ],
                    sortname: "adrId",
                    sortorder: "desc",
                    viewrecords: true,
                    imgpath: "themes/basic/images",
                    resizable: false,
                    height: 100,                     
                    jsonReader: {
                        root: "adrList",
                        page: "currpage", 
                        total: "totalpages",
                        records: "totalrecords", 
                        repeatitems: false,
                        id: "adrId"
                    },
                    onSelectRow: function(id) { 
                        if(id && id!=lastSelected) {
                            $("#adrgrid").jqGrid('saveRow', lastSelected,
                            {
                                successfunc: function(response) {
                                    return true;
                                },
                                url: "Adr_update.action"
                            })
                            lastSelected = id;
                        }
                        $("#adrgrid").jqGrid('editRow',id);
                        $("#adrId").val(id);
                        var data = $("#adrgrid").getRowData(id) 
                        $("#description").val(data.description);
                    }, //end of onSelectRow                     
                    loadComplete: function(data) {
                        for(i = 0; i < adrIds.length; i++) {
                            var values = adrIds[i].split(",");                          
                            $("#adrgrid").jqGrid('setCell', values[0], 'severity', 'Grade '+values[1]);
                        }
                    }                                     
                }); //end of jqGrid  
                 
                $("#oigrid").jqGrid({
                    url: "Oi_grid.action",
                    datatype: "json", 
                    mtype: "GET",
                    colNames: ["Description"],
                    colModel: [
                        {name: "description", index: "description", width: "250"},
                    ],
                    sortname: "oiId",
                    sortorder: "desc",
                    viewrecords: true,
                    imgpath: "themes/basic/images",
                    resizable: false,
                    multiselect: true,
                    height: 85,                    
                    jsonReader: {
                        root: "oiList",
                        page: "currpage",
                        total: "totalpages",
                        records: "totalrecords",
                        repeatitems: false,
                        id: "oiId"
                    },
                    loadComplete: function(data) {
                        $.each(oiIds, function(_, rowId){
                            $("#oigrid").setSelection(rowId, true);
                        })
                    }                    
                }); //end of jqGrid 
                
                $("#adheregrid").jqGrid({
                    url: "Adhere_grid.action",
                    datatype: "json",
                    mtype: "GET",
                    colNames: ["Description"],
                    colModel: [
                        {name: "description", index: "description", width: "250"},
                    ],
                    sortname: "adhereId",
                    sortorder: "desc",
                    viewrecords: true,
                    imgpath: "themes/basic/images",
                    resizable: false,
                    multiselect: true,
                    height: 85,                    
                    jsonReader: {
                        root: "adhereList",
                        page: "currpage",
                        total: "totalpages",
                        records: "totalrecords",
                        repeatitems: false,
                        id: "adhereId"
                    },
                    loadComplete: function(data) {
                        $.each(adhereIds, function(_, rowId){
                            $("#adheregrid").setSelection(rowId, true);
                        })
                    }
                }); //end of jqGrid 
                
               $("#adrScreened").bind("click", function() {
                    if($("#adrScreened").val() == "Yes") {
                        $("#adr_button").removeAttr("disabled");                        
                    }
                    else { 
                        $("#adr_button").attr("disabled", "disabled");                                                
                    }
                });
                 
                $("#oiScreened").bind("click", function() {
                    if($("#oiScreened").val() == "Yes") {
                        $("#oi_button").removeAttr("disabled");                        
                    }
                    else {
                        $("#oi_button").attr("disabled", "disabled");                                                
                    }
                });                
                $("#adherenceLevel").bind("click", function() {
                    if($("#adherenceLevel").val() == "Fair" || $("#adherenceLevel").val() == "Poor") {
                        $("#adhere_button").removeAttr("disabled");                        
                    }
                    else {
                        $("#adhere_button").attr("disabled", "disabled");                                                
                    }
                });
                
                $("#adr_button").bind("click", function(event){
                   $("#adrtable").toggle("slow");
                   return false;
                }); //show and hide grid

                $("#oi_button").bind("click", function(event){
                   $("#oitable").toggle("slow");
                   return false;
                }); //show and hide grid
                
                $("#adhere_button").bind("click", function(event){
                   $("#adheretable").toggle("slow");
                   return false;
                }); //show and hide grid
                
                $("#date1").bind("change", function(event){
                    checkDate();                
                });
                $("#date3").bind("change", function(event){
                    checkDate();
                });

                $("#close_button").bind("click", function(event){
                    $("#lamisform").attr("action", "Clinic_center");
                    return true;
                });
            });
             
            function checkDate() {
                if($("#date1").val().length != 0 && $("#date3").val().length != 0 ) {
                   if(parseInt(compare($("#date1").val(), $("#date3").val())) == -1) {
                       var message = "Date of next appointment cannot be ealier than date of visit";
                       $("#messageBar").html(message).slideDown('slow');                                                     
                    } 
                    else {
                       $("#messageBar").slideUp('slow');                 
                    }
                }
            }            
        </script>
    </head>

    <body>
      <jsp:include page="/WEB-INF/views/template/header.jsp" /> 
    <jsp:include page="/WEB-INF/views/template/nav_clinic.jsp" /> 
    <!-- MAIN CONTENT -->
        <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="Home_page.action">Home</a></li>
            <li class="breadcrumb-item"><a href="Clinic_page">Clinic</a></li>
            <li class="breadcrumb-item active" aria-current="page">New Clinic Visit</li>
            </ol>
            </nav>   
                <s:form id="lamisform" theme="css_xhtml">
                  <div class="row">
                <div class="col-md-8 ml-auto mr-auto">
                <div class="card">
                    <div class="card-body">
                        <div id="messageBar" class="alert alert-warning alert-dismissible fade show" role="alert">
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
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
                                <input name="clinicId" type="hidden" id="clinicId"/>
                                <input name="name" type="hidden" id="name"/>
                            </div>
                                </div>
                                <div class="row">
                               <div class="col-md-6">
                                <div class="form-group">
                                    <label>Date of Visit:</label>
                                    <input name="date1" type="text" class="form-control" id="date1" />
                                    <input name="dateVisit" type="hidden" id="dateVisit" />
                                </div>
                                </div>
                                <div class="col-md-6">
                                <div class="form-group">
                                    <label>Clinical Stage:</label>
                                    <select name="clinicStage" class="form-control select2" style="width: 100%" id="clinicStage">
                                        <option></option>
                                        <option>Stage I</option>
                                        <option>Stage II</option>
                                        <option>Stage III</option>
                                        <option>Stage IV</option>
                                    </select>
                                </div>
                            </div>
                            </div>
                            <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>Functional Status:</label>
                                    <select name="funcStatus" class="form-control select2" style="width: 100%" id="funcStatus">
                                         <option></option>
                                        <option>Working</option>
                                        <option>Ambulatory</option>
                                        <option>Bedridden</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>TB Status:</label>
                                    <select name="tbStatus" class="form-control select2" style="width: 100%" id="tbStatus">
                                         <option></option>
                                        <option>No sign or symptoms of TB</option>
                                        <option>TB suspected and referred for evaluation</option>
                                        <option>Currently on INH prophylaxis</option>
                                        <option>Currently on TB treatment</option>
                                        <option>TB positive not on TB drugs</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-3">
                                <div class="form-group">
                                    <label>Body Weight(kg):</label>
                                    <input name="bodyWeight" type="text" class="form-control" id="bodyWeight" />
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="form-group">
                                    <label>Height(m):</label>
                                    <input name="height" type="text" class="form-control" id="height" />
                                    <span id="bmi" style="color:red"></span>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>Blood Pressure(mmHg):</label>
                                    <div class="row">
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <input name="bp1" type="text" class="form-control" id="bp1" />
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <input name="bp2" type="text" class="form-control" id="bp2" />
                                            </div>
                                        </div>
                                    </div>
                                    <input name="bp" type="hidden" id="bp" />
                                </div>
                            </div>
                        </div>
                             <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="form-label">Pregnancy Status</label>
                                    <select name="pregnantStatus" style="width: 100%;" class="form-control select2" id="pregnantStatus">
                                        <option></option>
                                        <option value="1">Not Pregnant</option>
                                        <option value="2">Pregnant</option>
                                        <option value="3">Breastfeeding</option>
                                        <span id="pregHelp" class="errorspan"></span>
                                    </select>  
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>L.M.P:</label>
                                    <input name="date2" type="text" class="form-control" id="date2" disabled="true" />
                                    <input name="lmp" type="hidden" id="lmp" />
                                </div>
                            </div>
                        </div>
                       <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>Opportunistic Infections:</label>
                                    <select name="oiScreened" class="form-control select2" style="width: 100%" id="oiScreened">
                                        <option></option>
                                        <option value="No">No</option>
                                        <option value="Yes">Yes</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>Adverse Drug Reactions:</label>
                                    <select name="adrScreened" class="form-control select2" style="width: 100%" id="adrScreened">
                                        <option></option>
                                        <option value="No">No</option>
                                        <option value="Yes">Yes</option>
                                    </select>
                                </div>
                            </div>
                           </div>
                        <!-- oi and adr options -->
                           <div class="row">
                            <div class="col-md-6">
                                <div id="oitable" style="display:none;">
                                    <table id="oigrid" class="table stable"></table>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div id="adrtable" style="display:none;">
                                    <table id="adrgrid" class="table stable"></table>
                                </div>
                            </div>
                            </div>
                           <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>Level of Adherence:</label>  
                                    <select name="adherenceLevel" class="form-control select2" style="width: 100%" id="adherenceLevel">
                                        <option></option>
                                        <option value="Good">Good</option>
                                        <option value="Fair">Fair</option>
                                        <option value="Poor">Poor</option>
                                    </select>
                                </div>
                            </div>
                           <div class="col-md-6">
                                <div class="form-group">
                                    <label>Date of Next Appointment:</label>
                                    <input name="date3" type="text" class="form-control datepicker" id="date3"/>
                                    <input name="nextAppointment" type="hidden"  id="nextAppointment"/>
                                </div>
                            </div>
                            </div>
                            <div class="row">
                            <div class="col-md-6">
                                <div id="adheretable" style="display: none">
                                    <table id="adheregrid"></table>                                       
                                </div>
                            </div>
                                </div>
                               <input name="oiIds" type="hidden" id="oiIds"/>
                               <input name="adrId" type="hidden" id="adrId"/>
                               <input name="adhereIds" type="hidden" id="adhereIds"/>
                               <input name="description" type="hidden" id="description"/>
                               <input name="screener" type="hidden" value="1" id="screener"/>
                            </div>
                            <table>
                           <tr>
                                <td><label>Clinical Notes:</label></td>
                            </tr>
                           <tr>
                                <td></td>
                                <td colspan="3"><textarea name="notes" rows="7" cols="65" id="notes"></textarea></td>                            
                               <td><input name="notes" type="hidden" id="notes"/></td>
                           </tr>
                           <div>     
                          <input name="gender" type="hidden" id="gender"/>
                          <input name="dateBirth" type="hidden" id="dateBirth"/>                              
                          <input name="currentStatus" type="hidden" id="currentStatus"/>
                          <input name="dateCurrentStatus" type="hidden" id="dateCurrentStatus"/>                             
                          <input name="dateLastCd4" type="hidden" id="dateLastCd4"/>
                          <input name="dateLastClinic" type="hidden" id="dateLastClinic"/>
                          <input name="dateNextClinic" type="hidden" id="dateNextClinic"/>
                          <input name="commence" type="hidden" id="commence"/>
                           </div>
                        </table>
                        <hr>
                        <div id="userGroup" style="display: none"><s:property value="#session.userGroup"/></div>
                        <div class="pull-right">
                            <button id="save_button" class="btn btn-fill btn-info">Save</button>
                            <button id="close_button"  class="btn btn-fill btn-default">Close</button>
                        </div>  
                </div>
                    </s:form>
                </div>
            </div>
        </div>
        <div id="footer">
            <jsp:include page="/WEB-INF/views/template/footer.jsp" />
        </div>
    </body>
</html>
