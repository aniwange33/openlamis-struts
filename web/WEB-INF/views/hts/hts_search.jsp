<%-- 
    Document   : Commence
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
        <script type="text/javascript" src="js/jquery.maskedinput-1.3.min.js"></script>
        <script type="text/javascript" src="js/lamis/lamis-common.js"></script>               
        <script type="text/javascript" src="js/lamis/search-common.js"></script>               
        <script type="text/javascript" src="js/lamis/report-common.js"></script>    
        <script type="text/JavaScript">
             $(document).keypress(function(e){
                if(e.which == '13') {
                    e.preventDefault();
                }
            });
            $(document).ready(function(){  
            $(".search").on("keyup", function() {
            var value = $(this).val().toLowerCase();
            $("#grid tr").filter(function() {
            $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
            });
            });
                $("#grid").jqGrid({
                    url: "Hts_grid.action",
                    datatype: "json", 
                    mtype: "GET",
                    colNames: ["Client Code", "Client Name", "Gender", "Date Tested", "HIV Test Result",  "Referred to ART", "Referred to TB", "Referred to STI", "Action",""],
                    colModel: [
                        {name: "clientCode", index: "clientCode", width: "100"},
                        {name: "name", index: "name", width: "200"},
                        {name: "gender", index: "gender", width: "100"},
                        {name: "dateVisit", index: "date1", width: "100", formatter: "date", formatoptions: {srcformat: "m/d/Y", newformat: "d/m/Y"}},                        
                        {name: "hivTestResult", index: "hivTestResult", width: "120"},                        
                        {name: "artReferred", index: "artReferred", width: "125"},                        
                        {name: "tbReferred", index: "tbReferred", width: "125"},                        
                        {name: "stiReferred", index: "stiReferred", width: "138"},
                        {name: "htsId", index: "htsId", width: "100",  classes: "table_dropdown", formatter:function (cellValue, options, rowObject){
                        return '<div id="table-btn" class="dropdown" style="postion: absolute; color: #000;"><button  class="btn btn-sm btn-info dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Action</button><div class="dropdown-menu dropdown-menu-right"><a class="dropdown-item" href="#" onclick="editButton(' + cellValue + ');">Edit</a><a class="dropdown-item" href="#" onclick="deleteButton(' + cellValue +');">Delete</a></div></div>';
                      }}, 
                        {name: "dateVisit", index: "dateVisit", width: "138", hidden: true},  
                    ], 
                    
                   pager: $('#pager'),
                    rowNum: 100, 
                    sortname: "htsId",
                    sortorder: "desc",
                    viewrecords: true,
                    imgpath: "themes/basic/images",
                    resizable: false,
                    height: 350,                    
                    jsonReader: {
                        root: "htsList",
                        page: "currpage",
                        total: "totalpages",
                        records: "totalrecords",
                        repeatitems: false,
                        id: "htsId"
                    },  
                    
              
                    afterInsertRow: function(id, data) {
                        //$(this).jqGrid('setRowData', id, false, {fontFamily: 'Arial, Helvetica, sans-serif'});
                        if(data.hivTestResult == "Positive") {
                            $(this).jqGrid('setRowData', id, false, {color: 'red'});
                        }                        
                    },
                    onSelectRow: function(id) {
                        $("#htsId").val(id);
                        var data = $("#grid").getRowData(id);
                        $("#clientCode").val(data.clientCode);
                        $("#name").val(data.name);
                    },               
                    ondblClickRow: function(id) {
                        $("#htsId").val(id);
                        var data = $("#grid").getRowData(id);
                        $("#clientCode").val(data.clientCode);
                        $("#name").val(data.name);
                        $("#dateVisit").val(data.dateVisit);
                        $("#lamisform").attr("action", "Hts_find");                            
                        $("#lamisform").submit();
                    }
                }); //end of jqGrid  
                
                $("#assessment_button").bind("click", function(event){
                    $("#lamisform").attr("action", "Assessment_new");
                    return true; 
                });
                
                 $("#register_button").bind("click", function(event){
                    $("#lamisform").attr("action", "Enroll_client");
                    return true; 
                });
                
                $("#close_button").bind("click", function(event){
                    $("#lamisform").attr("action", "Hts_page");
                    return true;
                });
            });
            
           function deleteButton(htsId){
                $.confirm({
                    title: 'Confirm!',
                    content: 'Are you sure you want to delete?',
                    buttons: {
                        confirm: function () {
                            $("#patientId").val(htsId);
                            var data = $("#grid").getRowData(patId);
                            $("#hospitalNum").val(data.hospitalNum);
                            $("#name").val(data.name);
                            if($("#userGroup").html() == "Data Analyst") {
                                $("#lamisform").attr("action", "Error_message");
                                $("#lamisform").submit();
                            }
                            else {
                                $("#lamisform").attr("action", "Patient_delete");
                                $("#lamisform").submit();
                            }
                            window.location.href="Hts_search";
                            return true;
                        },
                        cancel: function () {
                            console.log("cancel");
                        }
                    }
                });
            }
            
//        $("#close_button").click( function(event){
//        console.log("we are here");
//         //$("#lamisform").attr("action", "Patient_search");
//        return true;
//    }); 
            function editButton(htsId){                
                $("#patientId").val(htsId);
                var data = $("#grid").getRowData(htsId);
                $("#hospitalNum").val(data.hospitalNum);
                $("#name").val(data.name);
                $("#lamisform").attr("action", "Hts_find");
                $("#lamisform").submit();
                //return true;
            }
            
        </script>
    </head>
    
    <body>
        <jsp:include page="/WEB-INF/views/template/header.jsp" />  
        <jsp:include page="/WEB-INF/views/template/nav_hts.jsp" />  
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="Home_page.action">Home</a></li>
                <li class="breadcrumb-item active" aria-current="page">Hiv Counseling and Testing</li>
            </ol>
        </nav>          
        <div class="row">
            <div class="col-md-12 ml-auto mr-auto">
                <div class="card">
                    <s:form id="lamisform" theme="css_xhtml">
                        <input name="clientCode" id="clientCode" type="hidden">
                        <input name="htsId" id="htsId" type="hidden">
                        <input name="dateVisit" id="dateVisit" type="hidden">
                        <div class="card-body">
                            <div id="messageBar"></div>                        
                            <div class="row">
                                <div class="col-12 ml-auto mr-auto">
                                    <div class="input-group no-border col-md-3 pull-right">
                                        <input type="text" class="form-control search" placeholder="search...">
                                        <div class="input-group-append">
                                            <div class="input-group-text">
                                                <i class="now-ui-icons ui-1_zoom-bold"></i>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-6">
                                        <div class="pull-left">
                                             <button id="assessment_button" class="btn btn-info">Risk Assessment</button>
                                        
                                             <button id="register_button" class="btn btn-info" disabled="true">Enroll Client</button>
                                        </div>
                                    </div>
                                </div>
                            </div>                                    
                            <div class="table-responsive">
                                <table id="grid" class="table table-striped table-bordered center"></table>
                                <div id="pager" style="text-align:center;"></div>
                                <p></p>
                            </div> 
                        </div>
                    </s:form>
                </div>
            </div>
        </div>
        <jsp:include page="/WEB-INF/views/template/footer.jsp" />
    </body>
</html>
