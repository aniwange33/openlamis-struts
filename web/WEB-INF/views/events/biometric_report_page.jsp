<%-- 
    Document   : fingerprint_duplicate_page
    Created on : Mar 23, 2019, 7:09:30 PM
    Author     : User10
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sj" uri="/struts-jquery-tags"%>

<!DOCTYPE html>
<html>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>LAMIS 2.6</title>
   <jsp:include page="/WEB-INF/views/template/css.jsp" />
    <jsp:include page="/WEB-INF/views/template/javascript.jsp" />    
    <script type="text/javascript" src="js/lamis/lamis-common.js"></script>                  
    <script type="text/javascript" src="js/lamis/report-common.js"></script>             
    <script type="text/javascript">
      var _this = this;
        $(document).ready(function (e) {
            $("#from, #to").mask("99/99/9999");
            $("#from, #to").datepicker({
                dateFormat: "dd/mm/yy",
                changeMonth: true,
                changeYear: true,
                yearRange: "-10:+0",
                constrainInput: true,
                buttonImageOnly: true,
                buttonImage: "/images/calendar.gif"
            });

            $("#ok_button").attr("disabled", true);

            $('#ok_button').on('click', function () {
                $("#ok_button").attr("disabled", true);
                $('#lamisform').submit();
                setTimeout(function () {
                    $("#ok_button").attr("disabled", false);
                });
            });


            $('#from, #to').on('change', function () {
                validateForm();
            });

            function validateForm() {
                var from = $('#from').val();
                var to = $('#to').val();
                var mFrom = moment(from, 'DD/MM/YYYY');
                var mTo = moment(to, 'DD/MM/YYYY');
                if ((from && to && mFrom.isValid() && mTo.isValid()) && (mFrom.isBefore(mTo) || mFrom.isSame(mTo))) {
                    $("#ok_button").attr("disabled", false);
                } else {
                    $("#ok_button").attr("disabled", true);
                }
            }
        });
    </script>
    
    <body>
         <jsp:include page="/WEB-INF/views/template/header.jsp"/> 
        <jsp:include page="/WEB-INF/views/template/nav_report.jsp"/> 
        <div class="mt-5"></div>
        <div class="content mr-auto ml-auto">
            <!-- MAIN CONTENT -->
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="Home_page">Home</a></li>
                <li class="breadcrumb-item"><a href="Patient_query_criteria.action">Report</a></li>
                <li class="breadcrumb-item active">Biometric Report</li>
            </ol>
            <s:form id="lamisform" theme="css_xhtml" action="biometric_reports.action">                        
                <div class="row">
                    <div class="col-md-10 ml-auto mr-auto">
                        <div class="card">
                            <div class="card-body">
                                <div id="messageBar"></div>
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label>From:</label>
                                            <input name="from" type="text" style="width: 100%;" class="form-control" id="from"/>
                                            <span id="stateHelp" class="errorspan"></span>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label>To:</label>
                                            <input name="to" type="text" style="width: 100%;" class="form-control" id="to"/>
                                            <span id="lgaHelp" class="errorspan"></span>
                                        </div>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label>Report:</label>
                                            <select name="report" style="width: 100%;" class="form-control" id="report">
                                                <option value="enrolled">Enrolled Patients</option>
                                                <option value="not-enrolled">Patients not enrolled</option>
                                                <option value="duplicate">Duplicate Patients</option>
                                            </select>
                                            <span id="stateHelp" class="errorspan"></span>
                                        </div>
                                    </div>
       
                                </div>
                                <div class="pull-right">
                                    <button class="btn btn-info" type="button" id="ok_button">Generate</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div> 
            </s:form>
            <div id="userGroup" style="display: none"><s:property value="#session.userGroup"/></div>     
            <jsp:include page="/WEB-INF/views/template/footer.jsp" />
    </body>
</html>
