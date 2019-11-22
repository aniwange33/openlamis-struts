<%-- 
    Document   : Pharmacy
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
        <script type="text/javascript" src="assets/jqueryui/jqgrid/plugins/ui.multiselect.js"></script> 
        <script type="text/javascript" src="js/lamis/pharmacy-common.js"></script>
        <script type="text/javascript" src="js/lamis/prescribed_drugs.js"></script>
        <script>
            // $.jgrid.defaults.width = 780;
            $.jgrid.defaults.responsive = true;
            $.jgrid.defaults.styleUI = 'Bootstrap';
        </script>
        <script type="text/JavaScript">
            var gridNum = 5;
            var enablePadding = true;
            var gridData;
            $(document).keypress(function(e){
                if(e.which == '13') {
                    e.preventDefault();
                }
            })
            $(document).ready(function(){
               
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
            getPrescribedDrugs();

            $(".search").on("keyup", function() {
                var value = $(this).val();
                 $("#grid").setGridParam({url: "Patient_grid_search.action?q=1&value="+value, page:1}).trigger("reloadGrid");
            });
 
            $("#grid").jqGrid({
            url: "Patient_grid.action",
            datatype: "json",
            mtype: "GET",
            colNames: ["Hospital No", "Name", "Gender", "ART Status", "Address","Action"],
            colModel: [
            {name: "hospitalNum", index: "hospitalNum", width: "150"},
            {name: "name", index: "name", width: "215"},
            {name: "gender", index: "gender", width: "100"},
            {name: "currentStatus", index: "currentStatus", width: "180"},
            {name: "address", index: "address", width: "345"}, 
            {name: "patientId", index: "patientId", width: "100", formatter:function (cellvalue, options, rowObject){
                return "<button class='btn btn-info btn-sm' onclick='newButton()' data-toggle='tooltip' data-placement='left' title='New'>New </button>";
            //return '<div class="nav-item dropdown"><a class="nav-link dropdown-toggle btn btn-info" href="#" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Actions</a><div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdown09"><a class="dropdown-item" data-locale="en"><span class="flag-icon flag-icon-it"> </span>  English</a><a class="dropdown-item" data-locale="fr"><span class="flag-icon flag-icon-fr"> </span>  French</a></div></div>';
            }} 
            ],
            pager: '#pager',
            rowNum: 100,
            sortname: "patientId",
            sortorder: "desc",
            viewrecords: true,
            imgpath: "themes/basic/images",
            resizable: false,                    
            height: 250,                   
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
            $("#detail").setGridParam({url: "Pharmacy_grid.action?q=1&patientId="+id, page:1}).trigger("reloadGrid");
            }
            } 
            else {
            $("#detail").setGridParam({url: "Pharmacy_grid.action?q=1&patientId="+id, page:1}).trigger("reloadGrid");
            }
            $("#patientId").val(id);
            var data = $("#grid").getRowData(id);
            $("#hospitalNum").val(data.hospitalNum);
            $("#name").val(data.name);
            													 
            },               
            ondblClickRow: function(id) {
            $("#lamisform").attr("action", "Pharmacy_new");
            $("#lamisform").submit();
            }
            }); //end of master jqGrid                 


            $("#detail").jqGrid({
            datatype: "json",
            mtype: "GET",
            colNames: ["Date of Refill", "Drug Dispensed", "Refill Period", "Next Refill", "Actions",""],
            colModel: [
            {name: "dateVisit", index: "date1", width: "200", formatter: "date", formatoptions: {srcformat: "m/d/Y", newformat: "d/m/Y"}},
            {name: "description", index: "description", width: "300"},
            {name: "duration", index: "duration", width: "260"},
            {name: "nextAppointment", index: "nextAppointment", width: "200", formatter: "date", formatoptions: {srcformat: "m/d/Y", newformat: "d/m/Y"}},
            {name: "pharmacyId", index: "pharmacyId", width: "130",  classes: "table_dropdown", formatter:function (cellValue, options, rowObject){
             return '<div id="table-btn" class="dropdown" style="postion: absolute; color: #000;"><button  class="btn btn-sm btn-info dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Action</button><div class="dropdown-menu dropdown-menu-right"><a class="dropdown-item" href="#" onclick="editButton(' + cellValue + ');">Edit</a><a class="dropdown-item" href="#" onclick="deleteButton(' + cellValue + ');">Delete</a></div></div>';
            }},
            {name: "dateVisit", index: "dateVisit", width: "100", hidden: true},
            ],
            rowNum: -1,
            sortname: "pharmacyId",
            sortorder: "desc",
            viewrecords: true,
            imgpath: "themes/basic/images",
            resizable: false,                   
            height: 200,                    
            jsonReader: {
            root: "pharmacyList",
            page: "currpage",
            //total: "totalpages",
            records: "totalrecords",
            repeatitems: false,
            id: "pharmacyId"	 
            },
            onSelectRow: function(id) {
            if(id != null) {
            var selectedRow = $("#detail").getGridParam('selrow');
            if(selectedRow != null) {
            var data = $("#detail").getRowData(selectedRow);
            //console.log(data);
            $("#dateVisit").val(data.dateVisit);
            }
            $("#pharmacyId").val(id);
           
            }
            },                   					

            }); //end of detail jqGrid                 

            $.ajax({
            url: "Patient_retrieve_detail.action",
            dataType: "json",
            success: function(patientMap) {
            if(!$.isEmptyObject(patientMap)) {
            $("#patientId").val(patientMap.patientId);
            $("#hospitalNum").val(patientMap.hospitalNum);
            $("#name").val(patientMap.name);
            //$("#new_button").removeAttr("disabled");                            
            }
            },
            complete: function() {
            $("#detail").setGridParam({url: "Pharmacy_grid.action?q=1&patientId="+$("#patientId").val(), page:1}).trigger("reloadGrid");                        
            }                   
            });

            $("#new_button").click(function(event){
                $("#lamisform").attr("action", "Pharmacy_new");
                return true;                
            });

            $("#close_button").click(function(event){
            $("#lamisform").attr("action", "Pharmacy_search");
            return true;
            });

            // activate the toolbar searching
            var timer;
            $("#search_cells").on("keyup", function() {
            var self = this;
            if(timer) { clearTimeout(timer); }
            timer = setTimeout(function(){
            //timer = null;
            $("#grid").jqGrid('filterInput', self.value);
            },0);
            });
            
            });
            function newButton(){
                $("#lamisform").attr("action", "Pharmacy_new");
                return true; 
            }
//            $("#delete_button").click(function(event){
//        if($("#userGroup").html() == "Data Analyst") {
//            $("#lamisform").attr("action", "Error_message");
//        }
//        else {
//            $("#lamisform").attr("action", "Pharmacy_delete");
//        }
//        return true;
//    });
            
            function viewButton(pharmId){
                $("#pharmacyId").val(pharmId);
                var data = $("#detail").getRowData(pharmId);
                $("#dateVisit").val(data.dateVisit);
                $("#lamisform").attr("action", "Pharmacy_find");
                $("#lamisform").submit();
                return true;  	
            }
            
            function editButton(pharmId){
                $("#pharmacyId").val(pharmId);
                var data = $("#detail").getRowData(pharmId);
                $("#dateVisit").val(data.dateVisit);
                $("#lamisform").attr("action", "Pharmacy_find");
                $("#lamisform").submit();
                return true;  	
            }
            
            function newButton(patId){
                $("#patientId").val(patId);
                $("#lamisform").attr("action", "Pharmacy_new");
                $("#lamisform").submit();
                return true;
            }
           function deleteButton(pharmId){
                console.log("delete process..");
                $("#pharmacyId").val(pharmId);
                var data = $("#detail").getRowData(pharmId);
                $("#dateVisit").val(data.dateVisit);
                $("#nextAppointment").val(data.nextAppointment);
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
                                $.ajax({
                                    url: encodeURI('Pharmacy_delete?pharmacyId=' + pharmId + '&patientId='+$('#patientId').val()),
                                    dataType: 'json',
                                    header: {
                                        'Content-Type': 'application/json'
                                    },
                                    success: function (e) {
                                        window.location.reload();
                                    }
                                });
                            }
                            window.location.reload();
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
        <jsp:include page="/WEB-INF/views/template/nav_pharmacy.jsp" />
        <!-- MAIN CONTENT -->
        <s:form id="lamisform" theme="css_xhtml">
            <input name="hospitalNum" type="hidden" id="hospitalNum" />
            <input name="pharmacyId" type="hidden" id="pharmacyId" />
            <input name="patientId" type="hidden" id="patientId" />
            <input name="dateVisit" type="hidden" id="dateVisit" />
            <input name="nextAppointment" type="hidden" id="nextAppointment" />
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="Home_page">Home</a></li>
                <li class="breadcrumb-item"><a href="Pharmacy_search">Pharmacy</a></li>
                <li class="breadcrumb-item active">Drug Dispensing</li>
            </ol>
            <div class="row">
                <div class="col-md-12 ml-auto mr-auto">
                    <div class="card">
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="input-group no-border col-md-3 pull-right">
                                        <input type="text" class="form-control  search" placeholder="Search...">
                                        <div class="input-group-append">
                                            <div class="input-group-text">
                                                <i class="now-ui-icons ui-1_zoom-bold"></i>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="table-responsive">
                                        <table id="grid" class="table table-bordered table-striped table-hover"></table>
                                        <div id="pager" style="text-align:center;"></div>
                                    </div>
                                </div></div>
                            <div class="row" style="display:none;">
                                <div class="col-md-12 mr-auto ml-auto">
                                    <div class="btn-group pull-right">
                                        <button id="new_button" disabled="true" class="btn btn-info">New</button>
                                        <button id="close_button" class="btn btn-default">Close</button>
                                    </div> 
                                </div></div>
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
        <div id="user_group" style="display: none">Pharmacist</div>
        <div id="user_group1" style="display: none"><s:property value="#session.userGroup"/></div>     
        <jsp:include page="/WEB-INF/views/template/footer.jsp" />
    </body>
</html>

