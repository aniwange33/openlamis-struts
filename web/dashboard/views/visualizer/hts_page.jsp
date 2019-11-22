<%-- 
    Document   : Patient
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
        <title>LAMIS 2.6</title>
        <link type="image/png" rel="icon" href="images/favicon.png" />
        <link type="text/css" rel="stylesheet" href="js/highchart/bootstrap.min.css" />
        <link type="text/css" rel="stylesheet" href="css/lamis.css" />
        <link type="text/css" rel="stylesheet" href="css/jquery-ui-1.8.18.custom.css" />
        <link type="text/css" rel="stylesheet" href="css/toastr.min.css" />
        <link type="text/css" rel="stylesheet" href="js/highchart/daterangepicker.css" />
        <!-- Font Awesome -->
        <link rel="stylesheet" href="js/highchart/font-awesome/css/font-awesome.min.css">

        <script type="text/javascript" src="js/lamis/lamis-common.js"></script>               
        <script type="text/javascript" src="js/lamis/report-common.js"></script>                        
        <script type="text/javascript" src="js/jquery-1.7.1.min.js"></script>       
        <script type="text/javascript" src="js/jquery-ui-1.8.18.custom.min.js"></script>       
        <script type="text/javascript" src="js/jquery.maskedinput-1.3.min.js"></script>  
        <script type="text/javascript" src="js/toastr.min.js"></script>
        <script type="text/javascript" src="js/json.min.js"></script>
        <script type="text/javascript" src="js/highchart/highcharts.js"></script>     
        <script type="text/javascript" src="js/highchart/highcharts-3d.js"></script>
        <script type="text/javascript" src="js/highchart/modules/exporting.js"></script>  
        <script type="text/javascript" src="js/highchart/moment.min.js"></script>
        <script type="text/javascript" src="js/highchart/daterangepicker.js"></script>
        <script type="text/javascript" src="js/highchart/moment.min.js"></script>
        <script type="text/javascript" src="js/highchart/daterangepicker.js"></script>
        <script type="text/javascript" src="js/lamis/hts-chart.js"></script> 
        <script type="text/javascript" src="js/lamis/hts-chart-month.js"></script>  
        <style>
            i.fa{
                font-size: 36px;
                color: #FFFFFF;
            }
            .card-title{
                font-size: 15px;
                color: #fffff;
                font-weight: bold;
            }

            .ninety{
                background-color: #FF6600;
                color: #FFFFFF;
            }
            .bgInfo{
                background-color: rgb(42, 87, 136);
            }
            .bgGrey{
                background-color: #001aa3\9;
            }
        </style>

        <style>
            i.fa{
                color: #FFFFFF;
            }
            .form-control{
                height: calc(1.5em + 0.5rem + 1px);
                line-height: inherit;
            }
            .bg-flat-color-1 {
                background: #5c6bc0;
            }

            .bg-flat-color-2 {
                background: #FF6600;
            }
            .bgInfo{
                background-color: rgb(42, 87, 136);
            }
        </style>
    </head>

    <body>
        <div id="page">           
            <jsp:include page="/WEB-INF/views/template/menu.jsp" />  

            <div id="mainPanel">                
                <jsp:include page="/WEB-INF/views/template/nav_visualizer.jsp" />                 
                <div id="rightPanelScroll">
                    <table width="100%" border="0">
                            <tr>
                                <td>
                                    <span>
                                        <img src="images/charts.png" style="margin-bottom: -5px;" /> &nbsp;
                                        <span class="top" style="margin-bottom: 1px; font-family: sans-serif; font-size: 1.1em;"><strong>Visualizer >> HTS Incidence Chart</strong></span>
                                    </span>
                                    <hr style="line-height: 2px"/>
                                </td>
                            </tr>
                            <tr>
                                <td class="topheaders">HTS Incidence Chart</td>
                            </tr>
                        </table>
                    <p></p>
                    <table width="100%">
                        <tbody>
                            <tr>
                                <td>IP</td>
                                <td>
                                    <select name="ipId" id="ipId" class="inputboxes" style="width: 100%;">
                                        <option value="1">FHI360</option>
                                    </select>
                                </td>
                                <td>State</td>
                                <td>
                                    <select name="stateId" id="stateId" class="inputboxes" style="width: 100%;">
                                        <option value="0">Select</option>
                                    </select>
                                </td>
                                <td>
                                    LGA
                                </td>
                                <td>
                                    <select name="lgaId" id="lgaId" class="inputboxes" style="width: 100%;">
                                        <option value="0">Select</option>
                                    </select>
                                </td>
                                <td>
                                    Facility
                                </td>
                                <td>
                                    <select name="facilityId" id="facilityId" class="inputboxes"
                                            style="width: 100%;">
                                        <option value="0">Select</option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="4">Reporting Period</td>
                            </tr>
                            <tr>
                                <td>Month</td>
                                <td>
                                    <select name="reportingMonthBegin" style="width: 100%;" class="inputboxes" id="reportingMonthBegin">
                                        <option value="0">Select Month</option>
                                        <option value="1">January</option>
                                        <option value="2">February</option>
                                        <option value="3">March</option>
                                        <option value="4">April</option>
                                        <option value="5">May</option>
                                        <option value="6">June</option>
                                        <option value="7">July</option>
                                        <option value="8">August</option>
                                        <option value="9">September</option>
                                        <option value="10">October</option>
                                        <option value="11">November</option>
                                        <option value="12">December</option>
                                    </select>
                                </td>
                                <td>Year</td>
                                <td>
                                    <select name="reportingYearBegin" style="width: 100%;" class="inputboxes" id="reportingYearBegin">
                                        <option value="0">Select Year</option>
                                    </select><span id="periodHelp1" style="color:red"></span>
                                </td>
                                <td>Month</td>
                                <td>
                                    <select name="reportingMonthEnd" style="width: 100%;" class="inputboxes" id="reportingMonthEnd">
                                        <option value="0">Select Month</option>
                                        <option value="1">January</option>
                                        <option value="2">February</option>
                                        <option value="3">March</option>
                                        <option value="4">April</option>
                                        <option value="5">May</option>
                                        <option value="6">June</option>
                                        <option value="7">July</option>
                                        <option value="8">August</option>
                                        <option value="9">September</option>
                                        <option value="10">October</option>
                                        <option value="11">November</option>
                                        <option value="12">December</option>
                                    </select>
                                </td>
                                <td>Year</td>
                                <td>
                                    <select name="reportingYearEnd" style="width: 100%;" class="inputboxes" id="reportingYearEnd">
                                        <option value="0">Select Year</option>
                                    </select><span id="periodHelp2" style="color:red"></span>
                                </td>
                            </tr>
                            <tr>
                                <td>Date Range</td>
                                <td>
                                    <div id="reportrange" class="form-control" style="background: #fff; cursor: pointer; padding: 5px 10px; border: 1px solid #ccc; width: 80%">
                                        <span></span> 
                                    </div>
                                </td>
                                <td colspan="4">
                                    <input type="hidden" name="reportingDateBegin" id="reportingDateBegin" class="form-control">
                                    <input type="hidden" name="reportingDateEnd" id="reportingDateEnd" class="form-control">
                                    <button id="ok_button" class="btn btn-primary">Generate</button>   
                                </td>
                            </tr>
                        </tbody>
                    </table> 
                    <hr></hr> 
                    <div id="loader"></div>            
                    <!-- Summary Section -->

                    <div class="row">
                        <div class="col-xl-4 col-md-4 mb-2">
                            <div class="card border-left border-5 border-secondary bgInfo">
                                <div class="card-body">
                                    <div class="row no-gutters align-items-center">
                                        <div class="col mr-2">
                                            <h6 style="color: #fff; font-weight: bold">Total Tested</h6>
                                            <div id="totalTested" class="h5 mb-0 font-weight-bold text-gray-800">0</div>
                                        </div>
                                        <div class="col-auto">
                                            <i class="fa fa-users fa-2x text-gray-light"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Total Positive -->
                        <div class="col-xl-4 col-md-4 mb-2">
                            <div class="card border-left border-5 border-secondary bg-flat-color-2">
                                <div class="card-body">
                                    <div class="row no-gutters align-items-center">
                                        <div class="col mr-2">
                                            <h6 style="color: #fff; font-weight: bold">Total Positive</h6>
                                            <div class="row no-gutters align-items-center">
                                                <div class="col-auto">
                                                    <div id="totalPositive" class="h5 mb-0 mr-3 font-weight-bold text-gray-800">0</div>
                                                </div>
                                                <div class="col" id="positive">
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-auto">
                                            <i class="fa fa-user-plus fa-2x text-gray-300"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Total Enrolled on ART -->
                        <div class="col-xl-4 col-md-4 mb-2">
                            <div class="card" style="background-color: #99CC33;">
                                <div class="card-body">
                                    <div class="row no-gutters align-items-center">
                                        <div class="col mr-2">
                                            <h6 style="color: #fff; font-weight: bold">Total Initiated on ART</h6>
                                            <div class="row no-gutters align-items-center">
                                                <div class="col-auto">
                                                    <div id="totalInitiated" class="h5 mb-0 mr-3 font-weight-bold text-gray-800">0</div>
                                                </div>
                                                <div class="col" id="initiated">
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-auto">
                                            <i class="fa fa-user-circle-o fa-2x text-gray-300"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Chart section -->
                    <div id="chart1"> 
                        <div id="container1"></div>
                    </div>
                    <div id="chart2"> 
                        <div id="container2"></div>
                    </div>
                    <div id="chart3"> 
                        <div id="container3"></div>
                    </div>
                    <div id="chart4"> 
                        <div id="container4"></div>
                    </div>                    
                    <div id="chart5"> 
                        <div id="container5"></div>
                    </div>                    
                    <div id="chart6"> 
                        <div id="container6"></div>
                    </div>       
                </div>
            </div>
        </div>
        <div id="footer"> 
            <div id="user_group1" style="display: none"><s:property value="#session.userGroup"/></div>     
            <jsp:include page="/WEB-INF/views/template/footer.jsp" />
        </div>

        <script type="text/JavaScript">

            for(i = new Date().getFullYear(); i > 1900; i--) {
            $("#reportingYearBegin").append($("<option/>").val(i).html(i));
            $("#reportingYearEnd").append($("<option/>").val(i).html(i));
            }


            $(document).ready(function(){          

            $.ajax({
            url: "StateId_retrieve.action",
            dataType: "json",
            success: function(stateMap) {
            var options = "<option value = '0'>" + 'Select' + "</option>";
            $.each(stateMap, function(key, value) {
            options += "<option value = '" + key + "'>" + value + "</option>";
            }); //end each
            $("#stateId").html(options);                        
            }                    
            }); //end of ajax call
            $("#stateId").change(function(event){
            $.ajax({
            url: "LgaId_retrieve.action",
            dataType: "json",
            data: {stateId: $("#stateId").val()},
            success: function(lgaMap) {
            var options = "<option value = '0'>" + 'Select' + "</option>";
            $.each(lgaMap, function(key, value) {
            options += "<option value = '" + key + "'>" + value + "</option>";
            }); //end each
            $("#lgaId").html(options);                        
            }                    
            }); //end of ajax call
            }); 

            $("#lgaId").change(function(event){
            $.ajax({
            url: "Facility_retrieve.action",
            dataType: "json",
            data: {stateId: $("#stateId").val(), lgaId: $("#lgaId").val()},

            success: function(facilityMap) {
            var options = "<option value = '0'>" + '' + "</option>";
            $.each(facilityMap, function(key, value) {
            options += "<option value = '" + key + "'>" + value + "</option>";
            }) //end each
            $("#facilityId").html(options);                        
            }                    
            }); //end of ajax call
            }); 


            toastr.options = {
            "closeButton": false,
            "debug": false,
            "newestOnTop": false,
            "progressBar": false,
            "positionClass": "toast-bottom-right",
            "preventDuplicates": false,
            "showDuration": "30000",
            "hideDuration": "1000",
            "timeOut": "30000",
            "extendedTimeOut": "3000",
            "showEasing": "swing",
            "hideEasing": "linear",
            "showMethod": "fadeIn",
            "hideMethod": "fadeOut",
            }

            var start = moment().subtract(6, 'days');
            var end = moment();

            var date_diff_indays = function(date1, date2) {
            dt1 = new Date(date1);
            dt2 = new Date(date2);
            return Math.floor((Date.UTC(dt2.getFullYear(), dt2.getMonth(), dt2.getDate()) - Date.UTC(dt1.getFullYear(), dt1.getMonth(), dt1.getDate()) ) /(1000 * 60 * 60 * 24));
            }

            function cb(start, end) {
            // $('#reportrange span').html(start.format('MMMM D, YYYY') + ' - ' + end.format('MMMM D, YYYY'));
            $('#reportrange span').html(start.format('D/M/YYYY') + ' - ' + end.format('D/M/YYYY'));
            $('#reportingDateBegin').val(start.format('YYYY-M-D')); $('#reportingDateEnd').val(end.format('YYYY-M-D'));

            if (date_diff_indays(start, end) > 7){
            toastr.info("You cannot select more than a week", "Notifications"); 
            }

            }


            $('#reportrange').daterangepicker({
            // "singleDatePicker": true,
            "showISOWeekNumbers": true,
            startDate: start,
            endDate: end,
            ranges: {
            'Today': [moment(), moment()],
            'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
            'This Week': [moment().startOf('isoWeek'), moment().endOf('isoWeek')],
            'Last 7 Days': [moment().subtract(6, 'days'), moment()],
            //   'Last 30 Days': [moment().subtract(29, 'days'), moment()],
            //   'This Month': [moment().startOf('month'), moment().endOf('month')],
            //   'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
            }
            }, cb);

            cb(start, end);

            $("#ok_button").bind("click", function() {
            event.preventDefault();
            //\\if (date_diff_indays(start, end) > 7){
            // toastr.info("You cannot select more than a week", "Notifications"); 
            // }
            if ($("#reportingMonthBegin").val() != 0 && $("#reportingYearBegin").val() != 0 && 
            $("#reportingMonthEnd").val() != 0 && $("#reportingMonthEnd").val() != 0){
            generateByMonth();
            } else{
            generate();             
            }

            return false;
            });

            generate();

            });


        </script> 

    </body>
</html>

