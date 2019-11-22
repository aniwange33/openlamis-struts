<%-- 
    Document   : Facility
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
        <link type="text/css" rel="stylesheet" href="css/lamis.css" />
        <link type="text/css" rel="stylesheet" href="css/jquery-ui-1.8.18.custom.css" />
        
        <script type="text/javascript" src="js/lamis/lamis-common.js"></script>               
        <script type="text/javascript" src="js/lamis/report-common.js"></script>               
        <script type="text/javascript" src="js/jquery-1.7.1.min.js"></script>               
        <script type="text/javascript" src="js/jquery-ui-1.8.18.custom.min.js"></script>       
        <script type="text/javascript" src="js/jquery.maskedinput-1.3.min.js"></script>       
        <script type="text/javascript" src="js/json.min.js"></script>
        <script type="text/javascript" src="js/highcharts.js"></script>               
        <script type="text/javascript" src="js/modules/exporting.js"></script>               
        <script type="text/JavaScript">
            $(document).ready(function(){
                resetPage();
                reports();
				
		$("#ok_button").bind("click", function(event){
                    if(validateForm()) {
                        $("#ok_button").attr("disabled", true);
                        event.preventDefault();
			$("#loader").html('<img id="loader_image" src="images/loader_small.gif" />');					
                        $.getJSON("Performance_chart.action", {reportingMonthBegin: $('#reportingMonthBegin').val(), reportingYearBegin: $('#reportingYearBegin').val(), reportingMonthEnd: $('#reportingMonthEnd').val(), reportingYearEnd: $('#reportingYearEnd').val(), indicatorId: $('#indicatorId').val()}, function(json) {
                            $("#loader").html('');
                            $("#container").css({minWidth: '400px', height: '400px', margin: '0 auto'});
                            setChart(json);    			
                            $("#ok_button").attr("disabled", false);
                        });   
                    }
                    return false;
                });
                                
                $("#cancel_button").bind("click", function(event){
                    $("#lamisform").attr("action", "Visualizer_page");
                    return true;
                });				
            }); 
			
            function setChart(json) {
                $('#container').highcharts({
                    chart: {
                        type: 'column'
                    },
                    title: {
                        text: json.title
                    },
                    subtitle: {
                        text: json.subtitle
                    },
                    xAxis: {
                        categories: json.categories
                    },
                    yAxis: {
                        min: 0,
                        title: {
                            text: json.titleForYAxis
                        }
                    },
                    tooltip: {
                        headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                        pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' + '<td style="padding:0"><b>{point.y} </b></td></tr>',
                        footerFormat: '</table>',
                        shared: true,
                        useHTML: true
                    },
                    plotOptions: {
                        column: {
                            pointPadding: 0.2,
                            borderWidth: 0
                        }
                    },
                    series: json.series
                });
            }

            function validateForm() {
                var validate = true;

                // check for valid input is entered
                if($("#indicatorId").val() == 0){
                    $("#indicatorHelp").html(" *");
                    validate = false;
                }
                else {
                    $("#indicatorHelp").html("");                    
                }
                if($("#reportingMonthBegin").val().length == 0){
                    $("#periodHelpBegin").html(" *");
                    validate = false;
                }
                else {
                    $("#periodHelpBegin").html("");                    
                }
                if($("#reportingYearBegin").val().length == 0){
                    $("#periodHelpBegin").html(" *");
                    validate = false;
                }
                else {
                    $("#periodHelpBegin").html("");                    
                }
                if($("#reportingMonthEnd").val().length == 0){
                    $("#periodHelpEnd").html(" *");
                    validate = false;
                }
                else {
                    $("#periodHelpEnd").html("");                    
                }
                if($("#reportingYearEnd").val().length == 0){
                    $("#periodHelpEnd").html(" *");
                    validate = false;
                }
                else {
                    $("#periodHelpEnd").html("");                    
                }

                return validate;
            }                                         
        </script>
    </head>

    <body>
        <div id="page">
            <jsp:include page="/WEB-INF/views/template/menu.jsp" />  
                      
            <div id="mainPanel">
                <jsp:include page="/WEB-INF/views/template/nav_visualizer.jsp" />  

                <div id="rightPanel">
                    <s:form id="lamisform">
                        <table width="100%" border="0">
                            <tr>
                                <td>
                                    <span>
                                        <img src="images/charts.png" style="margin-bottom: -5px;" /> &nbsp;
                                        <span class="top" style="margin-bottom: 1px; font-family: sans-serif; font-size: 1.1em;"><strong>Visualizer >> Performance Chart</strong></span>
                                    </span>
                                    <hr style="line-height: 2px"/>
                                </td>
                            </tr>
                            <tr>
                                <td class="topheaders">Performance Chart</td>
                            </tr>
                        </table>
                        <p></p>
                        <table width="99%" border="0" class="space" cellpadding="3">
                            <tr>
                                <td><label>Indicator:</label></td>
                                <td>
                                    <select name="indicatorId" style="width: 500px;" class="inputboxes" id="indicatorId">
                                        <option value="0"></option>
                                        <option value="1">Proportion of clinic visits during the reporting period that had a documentation of TB status</option>
                                        <option value="2">Proportion of clinic visits during the reporting period that had a documentation of functional status</option>
                                        <option value="3">Proportion of clinic visits during the reporting period that had a documentation of weight</option>
                                        <option value="4">Proportion of clinic visits during the of the reporting month that had documentation of OI status in ART patients</option>
                                        <option value="5">Proportion of clinic visits during the of the reporting month that had documentation of OI status in non-ART patients</option>
                                        <option value="6">Proportion of clinic visits of current ART patients within the reporting period that had a documentation of ADR status</option>
                                        <option value="7">Proportion of pharmacy visits of current ART patients within the reporting period that had a documentation of ADR status</option>
                                        <option value="8">Proportion of current ART patients who had at least one documented clinical visit in the last 6 months</option>        
                                        <option value="9">Proportion of current ART patients with at least one CD4 count test done in the last 6 months</option>
                                        <option value="10">Proportion of current ART patients at least at one time a minimum set of standard haematology tests done in the last 6 months</option>
                                        <option value="11">Proportion of current ART patients with at least at one time a minimum set of standard chemistry tests done in the last 6 months</option>
                                        <option value="12">Proportion of current ART patients who had a clinical staging done at the last clinical visit prior to the reporting date</option>
                                        <option value="13">Proportion of current ART patients reporting an ADR with severity grade 3 or 4 in the reporting month</option>
                                        <option value="14">Proportion of current ART patients who picked up their drugs within 7 days of the appointment day within the last 3 months</option>
                                        <option value="15">Proportion of current ART patients who have not had a refill 3 months after the last refill who have had their status correctly updated</option>
                                        <option value="16">Proportion of patients newly initiated on ART <= 5 years old with documented eligibility criteria</option>
                                        <option value="17">Proportion of patients newly initiated on ART > 5 years old with documented eligibility criteria</option>
                                        <option value="18">Proportion of patients newly initiated on ART who had at least one clinical staging done prior to ART commencement</option>
                                        <option value="19">Proportion of patients newly initiated on ART with at least one CD4 count test done before ART commencement</option>
                                        <option value="20">Proportion of patients newly initiated on ART with at least at one time a minimum set of standard chemistry tests done before ART commencement</option>
                                        <option value="21">Proportion of patients newly initiated on ART with at least at one time a minimum set of standard haematology tests done before ART commencement</option>
                                        <option value="22">Proportion of HIV positive patients <= 5 years initiating cotrimoxazole prophylaxis in the last 6 months</option>
                                        <option value="23">Proportion of HIV positive patients > 5 years initiating cotrimoxazole prophylaxis in the last 6 months</option>
                                    </select><span id="indicatorHelp" style="color:red"></span>			
                                </td>
                                <td colspan="2"></td> 
                            </tr>
                            <tr></tr>
                            <tr>
                                <td><label>Reporting Period:</label></td>
                                <td> 
                                    <select name="reportingMonthBegin" style="width: 100px;" class="inputboxes" id="reportingMonthBegin"/>
                                       <option value="0"></option>
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
                                    </select>&nbsp;
                                    <select name="reportingYearBegin" style="width: 75px;" class="inputboxes" id="reportingYearBegin"/>
                                       <option></option>
                                    </select><span id="periodHelpBegin" style="color:red"></span>
                                    &nbsp; to &nbsp;
                                    <select name="reportingMonthEnd" style="width: 100px;" class="inputboxes" id="reportingMonthEnd"/>
                                       <option value="0"></option>
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
                                    </select>&nbsp;
                                    <select name="reportingYearEnd" style="width: 75px;" class="inputboxes" id="reportingYearEnd"/>
                                       <option></option>
                                    </select><span id="periodHelpEnd" style="color:red"></span>
                                </td>
                                <td colspan="2"></td> 
                            </tr>
                            <tr>
                                <td>
                                    <span id="loader"></span>
                                </td>
                                <td colspan="3"></td> 
                            </tr>
                        </table>
                        <p></p>
                        <hr></hr>
                        <div>
                            <div id="container"></div>                                                    
                        </div>                        						                       
                        <div id="buttons" style="width: 200px">
                            <button id="ok_button">Generate</button> &nbsp;<button id="cancel_button">Close</button>
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
