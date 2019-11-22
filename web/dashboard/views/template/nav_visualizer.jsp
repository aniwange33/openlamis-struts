<%-- 
    Document   : nav_chart
    Created on : Mar 29, 2014, 7:41:49 PM
    Author     : aalozie
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<div id="leftPanel">
    <div id="navigation">
        <div style="float: left" class="navMenu">
            <div class="navTitle">D a t a &nbsp; V i s u a l i z e r</div>
            <div style="margin-bottom: 20px;">
                <s:a action="Dashboard_page">Dashboard</s:a>
                <s:a action="Chart_period2.action">Performance Chart</s:a>
                <s:a action="Htschart_page.action">HIV Incidence Dashboard</s:a>
                <s:a action="Htschart_dashboard.action">HIV Incidence Dashboard (Slide)</s:a>
            </div>
        </div>
    </div>
</div>
