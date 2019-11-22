<%-- 
    Document   : Clini
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
        <script type="text/javascript" src="js/lamis/anc-common.js"></script> 
        <script type="text/JavaScript">
            var gridNum = 4;
            var enablePadding = true;
            $(document).keypress(function(e){
                if(e.which == '13') {
                    e.preventDefault();
                }
            })
            $(document).ready(function(){
         
            $(".search").on("keyup", function() {
                var value = $(this).val();//
                 $("#grid").setGridParam({url: "Patient_grid_search.action?q=1&female=1&value="+value, page:1}).trigger("reloadGrid");
            });

            $.ajax({
            url: "Padding_status.action",
            dataType: "json",
            success: function(statusMap) {
            enablePadding = statusMap.paddingStatus;
            }
            });
            resetPage();
            initialize();
            reports();

            $("#grid").jqGrid({
            url: "Patient_grid.action?female",
            datatype: "json",
            mtype: "GET",
            colNames: ["Hospital No", "Name", "ART Status", "Address", "Date Enrolled Into PMTCT","Action", ""],
            colModel: [
            {name: "hospitalNum", index: "hospitalNum", width: "110"},
            {name: "name", index: "name", width: "287"},
            {name: "currentStatus", index: "currentStatus", width: "150"},
            {name: "address", index: "address", width: "250"},  
            {name: "dateEnrolledPmtct", index: "dateEnrolledPmtct", width: "200"},
            {name: "patientId", index: "patientId", width: "120", formatter:function (cellValue, options, rowObject){
            return "<button class='btn btn-info btn-sm' onclick='newButton("+cellValue+")' data-toggle='tooltip' data-placement='left' title='New'>New </button>";
            }}, 
            {name: "motherId", index: "motherId", width: "100", hidden: true},
            ],
            pager: $('#pager'),
            rowNum: 100,
            sortname: "hospitalNum",
            sortorder: "desc",
            viewrecords: true,
            imgpath: "themes/basic/images",
            resizable: false,
            height: 200,
            //postData: {gender: "female"},                    
            jsonReader: {
            root: "patientList",
            page: "currpage",
            total: "totalpages",
            records: "totalrecords",
            repeatitems: false,
            id: "patientId"
            },
            onSelectRow: function(id) {					
            if(id == null) {
            id = 0;
            if ($("#detail").getRecords() > 0) {
            $("#detail").setGridParam({url: "Anc_grid.action?q=1&patientId="+id, page:1}).trigger("reloadGrid");
            }
            } 
            else {
            $("#detail").setGridParam({url: "Anc_grid.action?q=1&patientId="+id, page:1}).trigger("reloadGrid");
            }

            $("#patientId").val(id);
            var data = $("#grid").getRowData(id)
            $("#hospitalNum").val(data.hospitalNum);
            $("#name").val(data.name);
            //            $("#new_button").removeAttr("disabled");
            //            $("#new_button").html("New");
            },
            ondblClickRow: function(id) {
            $("#lamisform").attr("action", "Anc_new");
            $("#lamisform").submit();
            }					
            }); //end of master jqGrid                 

            $("#detail").jqGrid({
            datatype: "json",
            mtype: "GET",
            colNames: ["Date of Visit", "Source of Referral", "LMP", "EDD","Actions"],
            colModel: [
            {name: "dateVisit", index: "dateVisit", width: "170", formatter: "date", formatoptions: {srcformat: "m/d/Y", newformat: "d/m/Y"}},
            {name: "sourceReferral", index: "sourceReferral", width: "280"},
            {name: "lmp", index: "lmp", width: "193", formatter: "date", formatoptions: {srcformat: "m/d/Y", newformat: "d/m/Y"}},
            {name: "edd", index: "edd", width: "193", formatter: "date", formatoptions: {srcformat: "m/d/Y", newformat: "d/m/Y"}},
            {name: "patientId", index: "patientId", width: "280", formatter:function (cellValue, options, rowObject){
            return '<div id="table-btn" class="dropdown" style="postion: absolute; color: #000;"><button  class="btn btn-sm btn-info dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Action</button><div class="dropdown-menu dropdown-menu-right"><a class="dropdown-item" href="#" onclick="viewButton(' + cellValue + ');">View</a><a class="dropdown-item" href="#" onclick="editButton(' + cellValue + ');">Edit</a><a class="dropdown-item" href="#" onclick="deleteButton(' + cellValue + ');">Delete</a></div></div>';
            }}
            ],
            rowNum: -1,
            sortname: "ancId",
            sortorder: "desc",
            viewrecords: true,
            imgpath: "themes/basic/images",
            resizable: false,                    
            height: 150,                    
            jsonReader: {
            root: "ancList",
            page: "currpage",
            //total: "totalpages",
            records: "totalrecords",
            repeatitems: false,
            id: "ancId"
            },
            onSelectRow: function(id) {
            if(id != null) {
            var selectedRow = $("#detail").getGridParam('selrow');
            if(selectedRow != null) {
            var data = $("#detail").getRowData(selectedRow);
            var date = data.dateVisit;
            $("#dateVisit").val(date.slice(3,5)+"/"+date.slice(0,2)+"/"+date.slice(6));
            }
            $("#ancId").val(id);
            $("#new_button").html("View");
            }
            },                   
            ondblClickRow: function(id) {
            var selectedRow = $("#detail").getGridParam('selrow');
            if(selectedRow != null) {
            var data = $("#detail").getRowData(selectedRow);
            var date = data.dateVisit;
            $("#dateVisit").val(date.slice(3,5)+"/"+date.slice(0,2)+"/"+date.slice(6));
            }
            $("#ancId").val(id);
            $("#lamisform").attr("action", "Anc_find");
            $("#lamisform").submit();
            }
            }); //end of detail jqGrid                 

            $.ajax({
            url: "Patient_retrieve_detail.action",
            dataType: "json",
            success: function(patientMap) {
            if(!$.isEmptyObject(patientMap)) {
            $("#patientId").val(patientMap.patientId);
            $("#hospitalNum").val(patientMap.hospitalNum);
            $("#name").val(patientMap.name);
            //  $("#new_button").removeAttr("disabled");                            
            }
            },
            complete: function() {
            $("#detail").setGridParam({url: "Anc_grid.action?q=1&patientId="+$("#patientId").val(), page:1}).trigger("reloadGrid");                        
            }                   
            });

            //            $("#new_button").click(function(event){
            //                $("#lamisform").attr("action", "Anc_new");
            //                return true;
            ////            if($("#new_button").html() === "New"){
            ////            $("#lamisform").attr("action", "Anc_new");
            ////            return true;
            ////            }else if($("#new_button").html() === "View"){
            ////            $("#lamisform").attr("action", "Anc_find");
            ////            $("#lamisform").submit();
            ////            return true;
            ////            }  											
            //            });
            //            $("#view_button").click(function(event){
            //                $("#lamisform").attr("action", "Anc_find");
            //                $("#lamisform").submit();
            //                return true;  											
            //            });
            //            
            //            $("#close_button").click(function(event){
            //            $("#lamisform").attr("action", "Pmtct_page");
            //            return true;
            //            });
                $("#new_patient").bind("click", function(event){
                    $("#lamisform").attr("action", "Patient_new");
                    return true;									
                });
            });

            function viewButton(data){
                $("#patientId").val(data);
                $("#lamisform").attr("action", "Anc_find");
                $("#lamisform").submit();
                return true;  	
            }
            function editButton(data){
                $("#patientId").val(data);
                $("#lamisform").attr("action", "Anc_find");
                $("#lamisform").submit();
                return true;  	
            }
            function newButton(data1){
                $("#patientId").val(data1);
                var data = $("#detail").getRowData(data1);
                $("#hospitalNum").val(data.hospitalNum);
                $("#motherId").val(data.motherId);
                $("#name").val(data.name);
                $("#lamisform").attr("action", "Anc_new");
                return true;
            }
            function deleteButton(data){
                console.log("delete process..");
                $("#patientId").val(data);
                var data = $("#detail").getRowData(data);
                $.confirm({
                    title: 'Confirm!',
                    content: 'Are you sure you want to delete?',
                    buttons: {
                        confirm: function () {
                            if($("#userGroup").html() == "Data Analyst") {
                                $("#lamisform").attr("action", "Error_message");
                                $("#lamisform").submit();
                            }
                            else {
                                 $("#lamisform").attr("action", "Anc_delete");
                                $("#lamisform").submit();
                            }
                            //window.location.replace("/Pharmacy_search");
                            return true;
                        },
                        cancel: function () {
                           console.log("cancel");
                        }
                    }
                });
            }
        </script>
    </head>

    <body>
        <jsp:include page="/WEB-INF/views/template/header.jsp" /> 
        <jsp:include page="/WEB-INF/views/template/nav_pmtct.jsp" /> 
        <!-- MAIN CONTENT -->
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="Home_page">Home</a></li>
            <li class="breadcrumb-item"><a href="Anc_search">PMTCT</a></li>
            <li class="breadcrumb-item active">First ANC Visit</li>
        </ol>
        <s:form id="lamisform" theme="css_xhtml">
            <input name="ancId" type="hidden" id="ancId" />
            <input name="patientId" type="hidden" id="patientId" />
            <input name="dateVisit" type="hidden" id="dateVisit"/>
            <input name="name" type="hidden" id="name" />
            <input name="hospitalNum" type="hidden" id="hospitalNum" />
            <input name="motherId" type="hidden" id="motherId" />
            
            <div class="row">
                <div class="col-md-12 ml-auto mr-auto">
                    <div class="card">
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-6">
                                    <button class="btn btn-info" id="new_patient"><strog>Enroll Client</strog></button>
                                </div>
                                <div class="col-md-6">
                                    <div class="input-group no-border col-md-6 pull-right">
                                    <input type="text" class="form-control search smalltext" placeholder="Search...">
                                    <div class="input-group-append">
                                        <div class="input-group-text">
                                            <i class="now-ui-icons ui-1_zoom-bold"></i>
                                        </div>
                                    </div>
                                </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-12 mr-auto ml-auto">                                    
                                    <div class="table-responsive">
                                        <table id="grid" class="table table-bordered table-striped table-hover"></table>
                                        <div id="pager" style="text-align:center;"></div>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-12 mr-auto ml-auto">
                                    <div class="table-responsive">
                                        <table id="detail" class="table table-bordered table-striped table-hover"></table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div> 
            </div>                                    
        </s:form>
        <!-- END MAIN CONTENT-->
        <div id="user_group" style="display: none">Clinician</div>
        <div id="userGroup" style="display: none"><s:property value="#session.userGroup"/></div>     
        <jsp:include page="/WEB-INF/views/template/footer.jsp" />
    </body>
</html>
