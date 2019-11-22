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
        <script type="text/javascript" src="js/json.min.js"></script>      
        <script type="text/javascript" src="js/popper.min.js"></script>               
        <script type="text/javascript" src="js/tippy.min.js"></script>
        <script type="text/javascript" src="js/lamis/childfollowup-common.js"></script>

        <script type="text/JavaScript">
            setTimeout(function () {
            $("#preloader").hide();
            $(".wrapper").show();
            }, 2000);
            
            var gridNum = 10;
            var enablePadding = true;
            var lastSelected = -99;
            var updateRecord = false;
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

            $("#dialog").dialog({
            title: "Register New Child",
            autoOpen: false,
            width: 850,
            resizable: false,
            buttons: [{text: "Save", click: createChild, class : 'btn btn-info'}, 
            {text: "Cancel", click: function(){$(this).dialog("close")}, class: 'btn btn-default'}]
            });

            $("#add_child").click(createChild);

            $("#grid").jqGrid({
            url: "Child_grid.action",
            datatype: "json",
            mtype: "GET",
            colNames: ["Child's Hospital No", "Mother's Name", "Child's Gender", "Mother In Facility","Action"],
            colModel: [
            {name: "hospitalNumber", index: "hospitalNumber", width: "180"},
            {name: "nameMother", index: "nameMother", width: "300"},
            {name: "gender", index: "gender", width: "150"},
            {name: "inFacility", index: "inFacility", width: "200"},
            //{name: "childId", index: "childId", width: "0", hidden: true},
            {name: "childId", index: "childId", width: "100", formatter:function (cellValue, options, rowObject){
            return "<button class='btn btn-info btn-sm' onclick='newButton("+cellValue+")' data-toggle='tooltip' data-placement='left' title='New'>New </button>";
            }}, 
            ],
            pager: $('#pager'),
            rowNum: 100,
            sortname: "childId",
            sortorder: "desc",
            viewrecords: true,
            imgpath: "themes/basic/images",
            resizable: false,
            height: 200,                    
            jsonReader: {
            root: "childList",
            page: "currpage",
            total: "totalpages",
            records: "totalrecords",
            repeatitems: false,
            id: "childId"
            },
            onSelectRow: function(id) {
            if(id == null) {
            id = 0;
            if ($("#detail").getRecords() > 0) {
            $("#detail").setGridParam({url: "Childfollowup_grid.action?q=1&childId="+id, page:1}).trigger("reloadGrid");
            }
            } 
            else {
            $("#detail").setGridParam({url: "Childfollowup_grid.action?q=1&childId="+id, page:1}).trigger("reloadGrid");
            }
            $("#childId").val(id);
            var data = $("#grid").getRowData(id)
            $("#cHospitalNum").val(data.hospitalNumber);
            $("#nameMother").val(data.nameMother);
            $("#patientId").val(data.patientId);
            $("#new_button").removeAttr("disabled");
            $("#new_button").html("New");													 
            },
            ondblClickRow: function(id) {
            $("#lamisform").attr("action", "Childfollowup_new");
            $("#lamisform").submit();
            }						
            }); //end of master jqGrid                 

            $("#detail").jqGrid({
            datatype: "json",
            mtype: "GET",
            colNames: ["Date of Visit", "Feeding at Present", "Outcome", "Rapid Test Result", "On Cotrim","Action"],
            colModel: [
            {name: "dateVisit", index: "dateVisit", width: "150", formatter: "date", formatoptions: {srcformat: "m/d/Y", newformat: "d/m/Y"}},
            {name: "feeding", index: "feeding", width: "200"},
            {name: "childOutcome", index: "childOutcome", width: "150"},
            {name: "rapidTestResult", index: "rapidTestResult", width: "150"},
            {name: "cotrim", index: "cotrim", width: "158"},
            {name: "childfollowupId", index: "childfollowupId", width: "120",  classes: "table_dropdown", formatter:function (cellValue, options, rowObject){
            return '<div id="table-btn" class="dropdown" style="postion: absolute; color: #000;"><button  class="btn btn-sm btn-info dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Action</button><div class="dropdown-menu dropdown-menu-right"><a class="dropdown-item" href="#" onclick="editButton(' + cellValue + ');">Edit</a><a class="dropdown-item" href="#" onclick="deleteButton(' + cellValue + ');">Delete</a></div></div>';
            }},
            ],
            rowNum: -1,
            sortname: "childfollowupId",
            sortorder: "desc",
            viewrecords: true,
            imgpath: "themes/basic/images",
            resizable: false,                    
            height: 150,                    
            jsonReader: {
            root: "childfollowupList",
            page: "currpage",
            //total: "totalpages",
            records: "totalrecords",
            repeatitems: false,
            id: "childfollowupId"
            }, 
            afterInsertRow: function(id) {
            var content = $("#detail").getCell(id, "feeding");
            $("#detail").setCell(id, "feeding", content.substring(content.indexOf("-") + 2), '', '');

            content = $("#detail").getCell(id, "childOutcome");
            $("#detail").setCell(id, "childOutcome", content.substring(content.indexOf("-") + 2), '', '');  
            },
            onSelectRow: function(id) {
            if(id != null) {
            var selectedRow = $("#detail").getGridParam('selrow');
            if(selectedRow != null) {
            var data = $("#detail").getRowData(selectedRow);
            var date = data.dateVisit;
            $("#dateVisit").val(date.slice(3,5)+"/"+date.slice(0,2)+"/"+date.slice(6));
            }
            $("#childfollowupId").val(id);
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
            $("#childfollowupId").val(id);
            $("#lamisform").attr("action", "Childfollowup_find");
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
            $("#new_button").removeAttr("disabled");                            
            }
            },
            complete: function() {
            $("#detail").setGridParam({url: "Childfollowup_grid.action?q=1&patientId="+$("#patientId").val(), page:1}).trigger("reloadGrid");                        
            }                   
            });

            $("#new_button").bind("click", function(event){
            if($("#new_button").html() === "New"){
            $("#lamisform").attr("action", "Childfollowup_new");
            return true;  
            }else if($("#new_button").html() === "View"){
            $("#lamisform").attr("action", "Childfollowup_find");
            $("#lamisform").submit();
            return true;
            } 										   
            });

            $("#new_patient").bind("click", function(event){
            $("#dialog").dialog("open");
            return false;      									
            });

            //            $("#close_button").bind("click", function(event){
            //                $("#lamisform").attr("action", "Pmtct_page");
            //                    return true;
            //                });
            });

            function newButton(childId){
                console.log("New Button Clicked");
//                $("#childId").val(data);
                $("#lamisform").attr("action", "Childfollowup_new");
                return true;  
            }
            
            function editButton(childfollowupId){
            if(childfollowupId != null) {
            var selectedRow = $("#detail").getRowData(childfollowupId);
            console.log(selectedRow);
            if(selectedRow != null) {
            //var data = $("#detail").getRowData(selectedRow);
            var date = selectedRow.dateVisit;
            console.log(date);
            $("#dateVisit").val(date.slice(3,5)+"/"+date.slice(0,2)+"/"+date.slice(6));
            }
            $("#childfollowupId").val(childfollowupId);
            }
            $("#lamisform").attr("action", "Childfollowup_find");
            $("#lamisform").submit();
            return true;
            }

//            function viewButton(childfollowupId){
//            if(childfollowupId != null) {
//            var selectedRow = $("#detail").getRowData(childId);
//            if(selectedRow != null) {
//            var data = $("#detail").getRowData(selectedRow);
//            var date = data.dateVisit;
//            $("#dateVisit").val(date.slice(3,5)+"/"+date.slice(0,2)+"/"+date.slice(6));
//            }
//            $("#childfollowupId").val(childfollowupId);
//            }
//
//            $("#lamisform").attr("action", "Childfollowup_find");
//            $("#lamisform").submit();
//            return true;
//            }

            function deleteButton(childfollowupId){
            $("#childfollowupId").val(childfollowupId);
            var data = $("#detail").getRowData(childfollowupId);
            $("#dateVisit").val(data.dateVisit);
            $.confirm({
            title: 'Confirm!',
            content: 'Are you sure you want to delete?',
            buttons: {
            confirm: function () {
            if($("#userGroup").html() === "Data Analyst") {
            $("#lamisform").attr("action", "Error_message");
            }
            else {
                 $.ajax({
                       url: encodeURI('Childfollowup_delete?childfollowupId=' + childfollowupId),
                                    dataType: 'json',
                                    header: {
                                        'Content-Type': 'application/json'
                                    },
                                    success: function (e) {
                                        window.location.reload();
                                    }
                                });
                                window.location.reload();
                            }
                          // window.location.replace("Status_search");
               
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
            <li class="breadcrumb-item active">Child Follow-up</li>
        </ol>
        <s:form id="lamisform" method="post" theme="css_xhtml">
            <div class="row">
                <div class="col-md-10 ml-auto mr-auto">
                    <div class="card">
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-4 form-group">
                                    <input name="cHospitalNum" type="hidden" class="form-control" id="cHospitalNum" />
                                    <input name="childfollowupId" type="hidden" id="childfollowupId" />
                                    <input name="childId" type="hidden" id="childId" />
                                    <input name="patientId" type="hidden" id="patientId" />
                                    <input name="motherId" type="hidden" id="motherId" />
                                    <input name="dateVisit" type="hidden" id="dateVisit"/>
                                    <input name="nameMother" type="hidden" class="form-control" id="nameMother" />
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="card-title">Child born outside this facility?
                                        <button type="button" class="btn btn-info" data-toggle="modal" data-target="#childModal">Register</button>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="card-title"><strong>Child List</strong></div>
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
                                        <table id="detail"></table>
                                    </div>
                                </div>
                            </div>
                            <!-- Modal -->
                            <div class="modal fade" id="childModal" tabindex="-1" role="dialog" aria-labelledby="childModalTitle" aria-hidden="true">
                                <div class="modal-dialog modal-notify modal-success modal-full-height modal-lg" role="document">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h5 class="modal-title" id="childModalTitle"><stron>Register New Child</strong></h5>
                                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                <span aria-hidden="true">&times;</span>
                                            </button>
                                        </div>
                                        <div class="modal-body">
                                            <div class="row">
                                                <div class="col-md-12">
                                                    <div class="table-responsive">
                                                        <table id="childInfoGrid" class="table table-striped table-bordered"></table>
                                                        <div id="childInfoPager" style="text-align:center;"></div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-md-12">
                                                    <div class="card-title"><strong>Mother/Caregiver Information</strong></div>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-md-5 form-group pull-right">
                                                    <label>Mother taking treatment in this facility?</label>
                                                    <button style='color:blue;' id="tip" data-tippy-content="This is used to determine if the woman is taking treatment 
                                                            in the facility!">?
                                                    </button>
                                                </div>
                                                <div class="col-md-2">
                                                    <select name="willing" style="width: 100%;" class="form-control" id="willing">
                                                        <option></option>
                                                        <option>No</option>
                                                        <option>Yes</option>
                                                    </select>
                                                    <span id="willingHelp" style="color:red"></span>
                                                </div>
                                            </div>
                                            <div class="row" id="outfac">
                                                <div class="col-md-6 form-group">
                                                    <label>Hospital Number:</label>
                                                    <input name="motherUniqueId" type="text" class="form-control" id="motherUniqueId"/>
                                                </div>
                                                <div class="col-md-6 form-group">
                                                    <label>Date Confirmed HIV:</label>
                                                    <input name="date9" type="text" class="form-control" id="date9"/>
                                                    <input name="dateConfirmedHiv" type="hidden" class="form-control" id="dateConfirmedHiv"/>
                                                </div>
                                            </div>
                                            <div class="row" id="outfac1">
                                                <div class="col-md-6 form-group">
                                                    <label>Mother's Surname:</label>
                                                    <input name="motherSurname" type="text" class="form-control" id="motherSurname"/>
                                                    <span id="motherSurnameHelp" class="errorspan"></span>
                                                </div>
                                                <div class="col-md-6 form-group">
                                                    <label>Mother's Other Names:</label>
                                                    <input name="motherOtherNames" type="text" class="form-control" id="motherOtherNames"/>
                                                </div>
                                            </div>
                                            <div class="row" id="outfac2">
                                                <div class="col-md-6 form-group">
                                                    <label>Date Started ARV/ART:</label>
                                                    <input name="date10" type="text" class="form-control" id="date10"/>
                                                    <input name="dateStarted" type="hidden" class="form-control" id="dateStarted"/>
                                                </div>
                                                <div class="col-md-6 form-group">
                                                    <label>Mother's Address:</label>
                                                    <input name="address" type="text" class="form-control" id="address"/>
                                                </div>
                                            </div>
                                            <div class="row" id="outfac3">
                                                <div class="col-md-6 form-group">
                                                    <label>Mother's Phone No.:</label>
                                                    <input name="phone" type="text" class="form-control" id="phone"/>
                                                </div>
                                            </div>
                                            <div class="row" id="infac">
                                                <div class="col-md-6 form-group">
                                                    <label>Mother's Hospital Number</label>
                                                    <input name="hospNumber" type="text" class="form-control" id="hospNumber"/>
                                                </div>
                                                <div class="col-md-6 form-group">
                                                    <br/>
                                                    <button  class="btn btn-info" id="search">Search</button>
                                                </div>
                                            </div>
                                            <div class="row" id="infac2">
                                                <div class="col-md-6 form-group">
                                                    <label>Mother's Name:</label>
                                                    <input name="motherName" type="text" class="form-control" id="motherName" readonly="readonly"/>
                                                </div>
                                                <div class="col-md-6 form-group">
                                                    <!--<input name="patientId" type="hidden" id="patientId"/>-->
                                                    <span id="patientIdHelp" class="errorspan"></span>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="modal-footer justify-content-end">
                                            <button type="button" id="add_child" class="btn btn-info waves-effect waves-light mr-3">Save changes</button>
                                            <button type="button" class="btn btn-secondary waves-effect waves-light" data-dismiss="modal">Close</button>
                                        </div>
                                    </div>
                                </div>
                                <!--Create the child-->
                                <script>
$.jgrid.defaults.responsive = true;
$.jgrid.defaults.styleUI = 'Bootstrap4';
$.jgrid.defaults.iconSet = "Octicons";
//                                var lastSelected = -99; 
$("#search").bind("click", function (event) {
    $("#motherName").val("");
    $("#patientId").val("");
    $.ajax({
        url: "Patient_find_number.action",
        dataType: "json",
        data: {hospitalNum: $("#hospNumber").val()},

        success: function (patientList) {
            if ($.isEmptyObject(patientList)) { // check if the return json object is empty ie no patient found
                var message = "Hospital number does not exist!";
                $("#messageBar").html(message).slideDown('slow');
            } else {
                if (patientList[0].gender !== "Female") {
                    var message = "This patient is not a female!";
                    $("#messageBar").html(message).slideDown('slow');
                } else {
                    $("#messageBar").slideUp('slow');
                    $("#motherName").val(patientList[0].name);
                    id = patientList[0].patientId;
                    $("#patientId").val(id);
                }
            }
        }
    }); //end of ajax call   
});

$("#motherUniqueId").bind("mouseleave", function (event) {
    var hospNumber = $("#motherUniqueId").val();
    if (hospNumber !== "")
        $("#motherUniqueId").val(zerorize(hospNumber));
});

//create the child grid
createChildGrid();
tippy('#tip');
$("#infac").attr("hidden", "hidden");
$("#infac2").attr("hidden", "hidden");
$("#date8").mask("99/99/9999");
$("#date8").datepicker({
    dateFormat: "dd/mm/yy",
    changeMonth: true,
    changeYear: true,
    yearRange: "-100:+0",
    constrainInput: true,
    buttonImageOnly: true,
    buttonImage: "/images/calendar.gif"
});

$("#date8").bind("change", function (event) {
    //TODO: Check
    var dated = $("#date8").val();
    if (dated.length !== 0) {
        var todayFormatted = formatDate(new Date());
        if (parseInt(compare($("#date8").val(), todayFormatted)) === -1) {
            var message = "Date of birth cannot be later than today please correct!";
            $("#messageBar").html(message).slideDown('slow');
            $("#date8").val("");
        } else {
            $("#messageBar").slideUp('slow');
        }
    }
});

$("#date9").mask("99/99/9999");
$("#date9").datepicker({
    dateFormat: "dd/mm/yy",
    changeMonth: true,
    changeYear: true,
    yearRange: "-100:+0",
    constrainInput: true,
    buttonImageOnly: true,
    buttonImage: "/images/calendar.gif"
});

$("#date9").bind("change", function (event) {
    //TODO: Check
    var dated = $("#date9").val();
    if (dated.length !== 0) {
        var todayFormatted = formatDate(new Date());
        if (parseInt(compare($("#date9").val(), todayFormatted)) === -1) {
            var message = "Date confirmed HIV cannot be later than today please correct!";
            $("#messageBar").html(message).slideDown('slow');
            $("#date9").val("");
        } else {
            $("#messageBar").slideUp('slow');
        }
    }
});

$("#date10").mask("99/99/9999");
$("#date10").datepicker({
    dateFormat: "dd/mm/yy",
    changeMonth: true,
    changeYear: true,
    yearRange: "-100:+0",
    constrainInput: true,
    buttonImageOnly: true,
    buttonImage: "/images/calendar.gif"
});

$("#date10").bind("change", function (event) {
    //TODO: Check
    var dated = $("#date10").val();
    if (dated.length !== 0) {
        var todayFormatted = formatDate(new Date());
        if (parseInt(compare($("#date10").val(), todayFormatted)) === -1) {
            var message = "Date ARV Started cannot be later than today please correct!";
            $("#messageBar").html(message).slideDown('slow');
            $("#date10").val("");
        } else {
            $("#messageBar").slideUp('slow');
        }
    }
});

$("#willing").change(function (event) {
    if ($("#willing").val() === "Yes") {
        $("#outfac").attr("hidden", "hidden");
        $("#outfac1").attr("hidden", "hidden");
        $("#outfac2").attr("hidden", "hidden");
        $("#outfac3").attr("hidden", "hidden");
        $("#infac").removeAttr("hidden");
        $("#infac2").removeAttr("hidden");
        $("#messageBar").slideUp('slow');
        $("#motherName").val("");
        $("#patientId").val("");
        //                                            createChild();
    } else {
        $("#outfac").removeAttr("hidden");
        $("#outfac1").removeAttr("hidden");
        $("#outfac2").removeAttr("hidden");
        $("#outfac3").removeAttr("hidden");
        $("#infac").attr("hidden", "hidden");
        $("#infac2").attr("hidden", "hidden");
        $("#messageBar").slideUp('slow');
        $("#motherName").val("");
        $("#patientId").val("");
    }
});

function createChildGrid() {
    //var date = $("#date1").val();
    //date = date.slice(3,5)+"/"+date.slice(0,2)+"/"+date.slice(6); 

    var postData = {deliveryId: 0};
    $.jgrid.gridUnload('#childInfoGrid');
    //$("#childInfoGrid").jqGrid('GridUnload');
    $("#childInfoGrid").jqGrid({
        url: "Child_grid.action",
        datatype: "json",
        mtype: "GET",
        colNames: ["Hospital No", "Surname", "Other Names", "Gender", "Weight (kg)", "Date of Birth", "Status At Registration", "", "", ""],
        colModel: [
            {name: "hospitalNumber", index: "hospitalNumber", width: "110", editable: true},
            {name: "surname", index: "surname", width: "100", editable: true},
            {name: "otherNames", index: "otherNames", width: "100", editable: true},
            {name: "gender", index: "gender", width: "110", editable: true, edittype: 'select', editoptions: {value: "-- select --:-- select --;Male:Male;Female:Female"}},
            {name: "bodyWeight", index: "bodyWeight", width: "110", editable: true},
            {name: "dateBirth", index: "dateBirth", width: "120", editable: true, formatter: "date", formatoptions: {srcformat: "m/d/Y", newformat: "d/m/Y"}},
            {name: "registrationStatus", index: "registrationStatus", width: "170", editable: true, edittype: 'select', editoptions: {value: "-- select --:-- select --;Transfer In:Transfer In;New:New"}},
            {name: "motherId", index: "motherId", width: "0", sortable: false, hidden: true},
            {name: "childId", index: "childId", width: "0", sortable: false, hidden: true},
            {name: "deliveryId", index: "deliveryId", width: "0", sortable: false, hidden: true}
        ],
        pager: $("#childInfoPager"),
        rowNum: 10,
        rowList: [10, 20],
        sortname: "childId",
        sortorder: "desc",
        viewrecords: true,
        imgpath: "themes/basic/images",
        resizable: false,
        height: 80,
        postData: postData,
        jsonReader: {
            root: "childList",
            page: "currpage",
            total: "totalpages",
            records: "totalrecords",
            repeatitems: false,
            id: "childId"
        },
        //                                            gridComplete: function()
        //                                            {
        //                                                $('#childInfoGrid').jqGrid('setGridWidth', '1000'); // max width for grid
        //                                            },
        onSelectRow: function (id) {
            if (id === null)
                id = 0;
            if (id && id !== lastSelected) {
                $("#childInfoGrid").saveRow(lastSelected, false, 'clientArray');
                $("#childInfoGrid").editRow(id, true);
                lastSelected = id;
                console.log("Selected Id: ", id);

                // add a datepicker to the textfields on the cells
                addDatepicker("#" + id + "_dateBirth");
            }
        } //end of onSelectRow 
    }); //end of jqGrid

    // construct the navigation          
    $("#childInfoGrid").navGrid('#childInfoPager', {
        edit: false, add: false, del: false, search: false
    })
            .navButtonAdd('#childInfoPager', {
                caption: "Add Child", buttonimg: "", title: "Add New Child", onClickButton: function () {
                    // retrieve last selected child's id
                    var selid = $("#childInfoGrid").getGridParam("selrow");
                    $("#childInfoGrid").saveRow(selid, false, 'clientArray');

                    var datarow = {hospitalNumber: "", surname: "", otherNames: "", gender: "", bodyWeight: "", dateBirth: "", registrationStatus: "", motherId: "", childId: "", deliveryId: ""};

                    var row_id = parseInt($("#deliveryId").attr("data-row-id")) - 1;
                    if (isNaN(row_id))
                        row_id = -1;
                    $("#deliveryId").attr("data-row-id", row_id);

                    var su = $("#childInfoGrid").addRowData("" + row_id + "", datarow, "first");
                    if (su) {
                        $("#childInfoGrid").editRow("" + row_id + "", true);
                        // add a datepicker to the textfields on the cells
                        addDatepicker("#" + row_id + "_dateBirth");
                    }
                    ;
                    $("#childInfoGrid").setGridParam({selrow: "" + row_id + ""});

                    $("#" + row_id + "_hospitalNumber").bind("mouseleave", function (event) {
                        var hospNumber = $("#" + row_id + "_hospitalNumber").val();
                        if (hospNumber !== "")
                            $("#" + row_id + "_hospitalNumber").val(zerorize(hospNumber));
                    });

                }, position: "last"
            })
            .navButtonAdd('#childInfoPager', {
                caption: "Remove Child", buttonimg: "", title: "Delete Child", onClickButton: function (id) {
                    // retrieve selected child's id
                    var selid = $("#childInfoGrid").getGridParam("selrow");

                    // a new entry
                    if (parseInt(selid) < 0) {
                        $("#childInfoGrid").delRowData(selid);

                        // an existing child
                    } else {
                        $("#childInfoGrid").restoreRow(selid); // undo changes
                        var child = $("#childInfoGrid").getRowData(selid);
                        if (child !== null) {
                            $.ajax({
                                url: "Child_delete.action?childId=" + child.childId,
                                dataType: "json",
                                success: function (data) {
                                    $("#childInfoGrid").delRowData(selid);
                                }
                            }); //end of ajax call 	      
                        }
                    }
                }, position: "last"
            });
}
                                </script>
                            </div>
                            <!--End Create Child-->
                            <div id="userGroup" style="display: none"><s:property value="#session.userGroup"/></div>                       
                        </div>
                    </div>
                </div>    
            </div>
        </s:form>
        <jsp:include page="/WEB-INF/views/template/footer.jsp" />
    </body>
</html>