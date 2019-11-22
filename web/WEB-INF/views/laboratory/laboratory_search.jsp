<%-- 
    Document   : Laboratory
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
        <script type="text/javascript" src="js/lamis/report-common.js"></script>
        <script type="text/JavaScript">
                var gridNum = 6;
               
                var enablePadding = true;
                 $(document).keypress(function(e){
                if(e.which == '13') {
                    e.preventDefault();
                }
            });
                $(document).ready(function(){
                    
                    $(".search").on("keyup", function() {
                        var value = $(this).val();
                         $("#grid").setGridParam({url: "Patient_grid_search.action?q=1&value="+value, page:1}).trigger("reloadGrid");
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
                url: "Patient_grid.action",
                datatype: "json",
                mtype: "GET",
                colNames: ["Hospital No", "Name", "Gender", "ART Status", "Address", "Action"],
                colModel: [
                {name: "hospitalNum", index: "hospitalNum", width: "130"},
                {name: "name", index: "name", width: "287"},
                {name: "gender", index: "gender", width: "100"},
                {name: "currentStatus", index: "currentStatus", width: "200"},
                {name: "address", index: "address", width: "300"},  
                {name: "patientId", index: "patientId", width: "100", formatter:function (cellValue, options, rowObject){
                    return "<button class='btn btn-info btn-sm' onclick='newButton("+cellValue+")' title='New'>New </button>";
                }}
                ],
                pager: $('#pager'),
                rowNum: 100,
                sortname: "patientId",
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
                $("#detail").setGridParam({url: "Laboratory_grid.action?q=1&patientId="+id, page:1}).trigger("reloadGrid");
                }
                } 
                else {
                $("#detail").setGridParam({url: "Laboratory_grid.action?q=1&patientId="+id, page:1}).trigger("reloadGrid");
                }
                $("#patientId").val(id);
                var data = $("#grid").getRowData(id);
                $("#hospitalNum").val(data.hospitalNum);
                $("#name").val(data.name);
             											 
                },               

                }); //end of master jqGrid                  

                $("#detail").jqGrid({
                datatype: "json",
                mtype: "GET",
                colNames: ["Sample Collected", "Result Reported", "Lab No", "Description", "Absolute", "Relative","Action", ""],
                colModel: [
                {name: "dateCollected", index: "dateCollected", width: "190", formatter: "date", formatoptions: {srcformat: "m/d/Y", newformat: "d/m/Y"}},
                {name: "dateReported", index: "date1", width: "190", formatter: "date", formatoptions: {srcformat: "m/d/Y", newformat: "d/m/Y"}},
                {name: "labno", index: "labno", width: "150"},
                {name: "description", index: "description", width: "200"},
                {name: "resultab", index: "resultab", width: "150"},
                {name: "resultpc", index: "resultpc", width: "140"},
                {name: "laboratoryId", index: "laboratoryId", width: "100",  classes: "table_dropdown", formatter:function (cellValue, options, rowObject){
                    return '<div id="table-btn" class="dropdown" style="postion: absolute; color: #000;"><button  class="btn btn-sm btn-info dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Action</button><div class="dropdown-menu dropdown-menu-right"><a class="dropdown-item" href="#" onclick="editButton(' + cellValue + ')">Edit</a><a class="dropdown-item" href="#" onclick="deleteButton(' + cellValue + ')">Delete</a></div></div>';
                }},
                {name: "dateReported", index: "dateReported", width: "100", hidden: true}
                ],
                rowNum: -1,
                sortname: "laboratoryId",
                sortorder: "desc",
                viewrecords: true,
                imgpath: "themes/basic/images",
                resizable: false,                    
                height: 150,                    
                jsonReader: {
                root: "laboratoryList",
                page: "currpage",
                //total: "totalpages",
                records: "totalrecords",
                repeatitems: false,
                id: "laboratoryId"
                },
                onSelectRow: function(id) {
                if(id != null) {
                var selectedRow = $("#detail").getGridParam('selrow');
                if(selectedRow != null) {
                var data = $("#detail").getRowData(selectedRow);
                $("#dateReported").val(data.dateReported);
                }
                $("#laboratoryId").val(id);
               // $("#new_button").html("View");
                }
                },                    
//                ondblClickRow: function(id) {
//                var selectedRow = $("#detail").getGridParam('selrow');
//                if(selectedRow != null) {
//                var data = $("#detail").getRowData(selectedRow);
//                $("#dateReported").val(data.dateReported);
//                }
//                $("#laboratoryId").val(id);
//                $("#lamisform").attr("action", "Laboratory_find");
//                $("#lamisform").submit();
//                }
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
                $("#detail").setGridParam({url: "Laboratory_grid.action?q=1&patientId="+$("#patientId").val(), page:1}).trigger("reloadGrid");                        
                }                   
                });

              
                $("#close_button").click(function(event){
                    $("#lamisform").attr("action", "Laboratory_page");
                    return true;
                    });
                });
                
                function viewButton(data){
                    $("#laboratoryId").val(data);
                   $("#lamisform").attr("action", "Laboratory_find");
                   $("#lamisform").submit();
                   return true;
                }
                
                function editButton(data){
                    $("#laboratoryId").val(data);
                     var data = $("#detail").getRowData(data);
                $("#dateReported").val(data.dateReported);
                
                   $("#lamisform").attr("action", "Laboratory_find");
                   $("#lamisform").submit();
                   return true;
                }
                function deleteButton(labId){
                    $("#laboratoryId").val(labId);
                    var data = $("#detail").getRowData(labId);
                    $("#laboratoryId").val(data.dateDevolved);
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
                                    url: encodeURI('Laboratory_delete?laboratoryId=' + labId + '&patientId='+$('#patientId').val()),
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
                
                function newButton(data){
                    $("#patientId").val(data);
                    var data2 = $("#grid").getRowData(data);
                    $("#hospitalNum").val(data2.hospitalNum);
                    $("#lamisform").attr("action", "Laboratory_new");
                    return true; 
                }
        </script>
    </head>

    <body>
        <jsp:include page="/WEB-INF/views/template/header.jsp" />
        <jsp:include page="/WEB-INF/views/template/nav_laboratory.jsp"/> 
        <!-- MAIN CONTENT -->
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="Home_page">Home</a></li>
            <li class="breadcrumb-item active">Laboratory Test</li>
        </ol>
        <s:form id="lamisform" theme="css_xhtml">
            <input name="laboratoryId" type="hidden" id="laboratoryId" />
            <input name="patientId" type="hidden" id="patientId" />
            <input name="dateReported" type="hidden" id="dateReported" />

            <div class="row">
                <div class="col-md-12 ml-auto mr-auto">
                    <div class="card">
                        <div class="card-body">
                            <div id="messageBar" class="alert alert-warning alert-dismissible fade show" role="alert">
                                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
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
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="table-responsive">
                                        <table id="grid"></table>
                                        <div id="pager" style="text-align:center;"></div>
                                    </div>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-12">
                                    <div class="table-responsive">
                                        <table id="detail" class="table table-striped"></table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </s:form>
        <div id="userGroup" style="display: none">
            <s:property value="#session.userGroup" />
        </div>
        <jsp:include page="/WEB-INF/views/template/footer.jsp" />
    </body>

</html>