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
        <!--        <link type="text/css" rel="stylesheet" href="css/jquery-ui-1.8.18.custom.css" />
                    <link type="text/css" rel="stylesheet" href="css/ui.jqgrid.css" />
                    <link type="text/css" rel="stylesheet" href="themes/basic/grid.css" />
                    <link type="text/css" rel="stylesheet" href="themes/jqModal.css" />-->

        <jsp:include page="/WEB-INF/views/template/javascript.jsp" />
        <script type="text/JavaScript">
            var gridNum = 4;
            var enablePadding = true;
            $(document).keypress(function(e){
                if(e.which == '13') {
                    e.preventDefault();
                }
            });
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
            colNames: ["Hospital No", "Name", "Gender", "ART Status", "Address","Action", ""],
            colModel: [
            {name: "hospitalNum", index: "hospitalNum", width: "130"},
            {name: "name", index: "name", width: "290"},
            {name: "gender", index: "gender", width: "100"},
            {name: "currentStatus", index: "currentStatus", width: "190"},
            {name: "address", index: "address", width: "290"},
            {name: "patientId", index: "patientId", width: "100", formatter:function (cellValue, options, rowObject){
            return "<button class='btn btn-info btn-sm' onclick='newButton("+cellValue+")' data-toggle='tooltip' data-placement='left' title='New'>New </button>";
            }}, 
            {name: "dateCurrentStatus", index: "dateCurrentStatus", width: "100", hidden: true},
            ],
            pager: $('#pager'),
            rowNum: 100,
            sortname: "hospitalNum",
            sortorder: "desc",
            viewrecords: true,
            imgpath: "themes/basic/images",
            resizable: false,
            height: 200,
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
            $("#detail").setGridParam({url: "Delivery_grid.action?q=1&patientId="+id, page:1}).trigger("reloadGrid");
            }
            } 
            else {
            $("#detail").setGridParam({url: "Delivery_grid.action?q=1&patientId="+id, page:1}).trigger("reloadGrid");
            }

            $("#patientId").val(id);
            var data = $("#grid").getRowData(id)
            
            $("#hospitalNum").val(data.hospitalNum);
            $("#name").val(data.name);
            // $("#new_button").removeAttr("disabled");
            // $("#new_button").html("New");													 
            },
            ondblClickRow: function(id) {
            $("#lamisform").attr("action", "Delivery_new");
            $("#lamisform").submit();
            }					
            }); //end of master jqGrid                 

            $("#detail").jqGrid({
            datatype: "json",
            mtype: "GET",
            colNames: ["Date of Delivery", "Mode of Delivery", "Maternal Outcome","Actions"],
            colModel: [
            {name: "dateDelivery", index: "dateDelivery", width: "205", formatter: "date", formatoptions: {srcformat: "m/d/Y", newformat: "d/m/Y"}},
            {name: "modeDelivery", index: "modeDelivery", width: "344"},
            {name: "maternalOutcome", index: "maternalOutcome", width: "280"},
            {name: "patientId", index: "patientId", width: "280",  classes: "table_dropdown", formatter:function (cellValue, options, rowObject){
         return '<div id="table-btn" class="dropdown" style="postion: absolute; color: #000;"><button  class="btn btn-sm btn-info dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Action</button><div class="dropdown-menu dropdown-menu-right"><a class="dropdown-item" href="#" onclick="editButton(' + cellValue + ');">Edit</a><a class="dropdown-item" href="#" onclick="deleteButton(' + cellValue + ');">Delete</a></div></div>';
        }}
            ],
            rowNum: -1,
            sortname: "deliveryId",
            sortorder: "desc",
            viewrecords: true,
            imgpath: "themes/basic/images",
            resizable: false,                    
            height: 150,                    
            jsonReader: {
            root: "deliveryList",
            page: "currpage",
            //total: "totalpages",
            records: "totalrecords",
            repeatitems: false,
            id: "deliveryId"},
            onSelectRow: function(id) {
            if(id != null) {
            var selectedRow = $("#detail").getGridParam('selrow');
            if(selectedRow != null) {
            var data = $("#detail").getRowData(selectedRow);
            var date = data.dateDelivery;
            $("#dateDelivery").val(date.slice(3,5)+"/"+date.slice(0,2)+"/"+date.slice(6));
            }
            $("#deliveryId").val(id);
            // $("#new_button").html("View");
            }
            },                   
            ondblClickRow: function(id) {
            var selectedRow = $("#detail").getGridParam('selrow');
            if(selectedRow != null) {
            var data = $("#detail").getRowData(selectedRow);
            var date = data.dateDelivery;
            $("#dateDelivery").val(date.slice(3,5)+"/"+date.slice(0,2)+"/"+date.slice(6));
            }
            $("#deliveryId").val(id);
            $("#lamisform").attr("action", "Delivery_find");
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
            //$("#new_button").removeAttr("disabled");                            
            }
            },
            complete: function() {
            $("#detail").setGridParam({url: "Delivery_grid.action?q=1&patientId="+$("#patientId").val(), page:1}).trigger("reloadGrid");                        
            }                   
            });

            $("#close_button").click(function(event){
            $("#lamisform").attr("action", "Pmtct_page");
            return true;
            });
            
                $("#new_patient").bind("click", function(event){
                    $("#lamisform").attr("action", "Patient_new");
                    return true;      									
                });
                
            });
            function editButton(data){
            $("#patientId").val(data);
            $("#lamisform").attr("action", "Delivery_find");
            $("#lamisform").submit();
            return true;  	
            }
            function newButton(data){
            $("#patientId").val(data);
            $("#lamisform").attr("action", "Delivery_new");
            return true;
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
            <li class="breadcrumb-item active">Labour & Delivery</li>
        </ol>
        <s:form id="lamisform" theme="css_xhtml">
            <input name="dateDelivery" type="hidden" id="dateDelivery" />
            <input name="hospitalNum" type="hidden" class="inputboxes" id="hospitalNum" />
            <input name="name" type="hidden" class="inputboxes" id="name" />
            <input name="deliveryId" type="hidden" id="deliveryId" />
            <input name="patientId" type="hidden" id="patientId" />
            <div class="row">
                <div class="col-md-12 ml-auto mr-auto">
                    <div class="card">
                        <div class="card-body">
                            <div id="messageBar"></div>
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="card-title">
                                        Cannot find patient record?
                                        <button class="btn btn-info" id="new_patient">Register</button>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-12 mr-auto ml-auto">
                                    <div class="input-group no-border col-3 pull-right">
                                    <input type="text" class="form-control search" placeholder="Search...">
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
                                </div>
                            </div>

                             <div class="row">
                                <div class="col-md-12 mr-auto ml-auto">
                                    <div class="table-responsive" style="height: 100%">
                                        <table id="detail" class="table table-bordered table-striped table-hover"></table>
                                    </div>
                                </div>
                            </div>
                        </div>
                        </div>
                    </div>
                </div>
            </div>
        </s:form>
        <div id="user_group" style="display: none">Clinician</div>
        <div id="userGroup" style="display: none">
            <s:property value="#session.userGroup" />
        </div>
        <jsp:include page="/WEB-INF/views/template/footer.jsp" />
    </body>
</html>