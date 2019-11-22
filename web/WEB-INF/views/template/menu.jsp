<%-- 
    Document   : newjsp
    Created on : Mar 29, 2014, 4:23:30 PM
    Author     : aalozie
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<div id="header">
    <img id="headerBanner" src="images/lamis_logo.png">
    <script type="text/javascript">
        $(document).ready(function(){
            //$("#headerText").html("Welcome "+$("#userId").val());
			var userGroup = $("#user_group").html();
            var $pharm = $("#pharmacy");
            var $lab = $("#lab");
            var $admin = $("#admin");
            var $clinic = $("#clinic");
            var $case = $("#case");
            var $home = $("#home");
            if(userGroup === "Clinician"){
                disable($pharm);
                disable($lab);
                disable($admin);
                disable($case);
                disable($home);
            }else if(userGroup === "Pharmacist"){
                disable($clinic);
                disable($lab);
                disable($admin);
                disable($home);
            }else if(userGroup === "Laboratory Scientist"){
                disable($pharm);
                disable($clinic);
                disable($admin);
                disable($home);
            }
            
            function disable(obj){
                obj.css("pointer-events", "none"); 
                obj.css("opacity", "0.6");
            }
        });
    </script>
    <span id="headerText" style="font-size: 1.1em">Welcome, <s:property value="#session.fullname"/></span>
    
    <div id="menuArea">
        <label id="user_group" style="display: none" ><s:property value="#session.userGroup"/></label>
        <ul id="menu">
          <li id="home"><a href="Home_page">Home</a></li>
          <li id="clinic">
              <a href="" onclick="return false">Clinic</a>
              <ul>
                   <%--<li><a href="" onclick="return false">General Clinic - GOPD</a></li>--%>
                   <li><a href="Clinic_page">ART Clinic</a></li>
                   <li><a href="Pmtct_page">Ante-Natal Clinic</a></li>
                   <li id="case"><a href="Casemanagement_page">Case Management</a></li>
              </ul>              
          </li>
          <li id="pharmacy">
              <a href="" onclick="return false">Pharmacy</a>
              <ul>
                   <li><a href="Pharmacy_page">General Pharmacy</a></li>
              </ul> 
          </li>
          <li id="lab">
              <a href="" onclick="return false">Laboratory</a>
              <ul>
                   <li><a href="Laboratory_page">General Lab</a></li>
                   <li><a href="Specimen_page">Auto Lab (PCR)</a></li>
              </ul>              
          </li>
          <li id="admin">
              <a href="" onclick="return false">Administration</a>
              <ul>
                   <li><a href="Setup_page">Setup</a></li>
                   <li><a href="Maintenance_page">Data Maintenance</a></li>
                   <li><a href="Conversion_page">Data Conversion</a></li>
                   <li><a href="Event_page">Events Monitor</a></li>
              </ul>              
          </li>
          <li>
              <a href="" onclick="return false">Visualizer</a>
              <ul>
                   <li><a href="Dashboard_page">Dashboard</a></li>
                   <li><a href="Chart_period2">Performance Indicator</a></li>
              </ul>
          </li>
          <li><a href="User_logout">Logout</a></li>
        </ul>            
    </div>

</div> 
