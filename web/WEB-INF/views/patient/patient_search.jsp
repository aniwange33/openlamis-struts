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
        <title>LAMIS 3.0</title>
        <jsp:include page="/WEB-INF/views/template/css.jsp" /> 
        <jsp:include page="/WEB-INF/views/template/javascript.jsp" />
        <script type="text/javascript" src="js/fingerprint.js"></script>	
        <script type="text/javascript" src="js/zebra.js"></script> 
        <link type="text/css" rel="stylesheet" href="css/zebra.css" />
        <script type="text/JavaScript">
            var gridNum = 1;             
            
            var enablePadding = true;
            $(document).keypress(function(e){
                if(e.which == '13') {
                    e.preventDefault();
                }
            }); 
            $(document).ready(function(){
                
            resetPage();
            initialize();  
            reports();

            $(".search").on("keyup", function() {
                var value = $(this).val();
                 $("#grid").setGridParam({url: "Patient_grid_search.action?q=1&value="+value, page:1}).trigger("reloadGrid");
            });
            
            $("#identify_button").bind("click", function(event){
                    var cmp = new FingerprintComponent();
                    var result = function(res) {
                        if(!!res && res.patientId) {
                            if(res.inFacility){
                                $("#patientId").val(res.patientId);
                                $("#lamisform").attr("action", "Patient_find");
                                $("#lamisform").submit();
                                return true;
                            }else {
                                $('#messageBar').text(res.name + ' not in this facility!').show();
                                return false;
                            }
                        }
                        else {
                            return true;
                        }
                    }
                    cmp.identify(result);
                    return false;
                });

            $.ajax({
                url: "Padding_status.action",
                dataType: "json",
                success: function(statusMap) {
                    enablePadding = statusMap.paddingStatus;
                }
            });
             
            function imageFormat( value, options, rowObject ){
                 if(value === 'true') {
                    return '<div style="text-align: center;"><img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAACqUlEQVQ4T6WTW0iTYRjH/99pc9MN576yltoBYenMXJgijZT0opslZCDqnRddhBRosItCRlAwSCGJIKPu1IIKrCspOuBEDI2gVMwzzXma25rbt9O374tvh3INuumF5+Z9nv/vfZ8Tgf88RIZ+iNWBIi5CxBkQKIz7RfwAgTHExJdocTn3atIBz1hzFiPv1Ov0xYfZwrwceU6WCCAQ9odWtlfdc+vzC+FouBfNrtcpyB/AIGtWK9TWOsPZEhqUguf5tM/RNI2wEAl+nBmd9Yf8VrQmIAnAEKuTgxloqKivjoVjCkEQMjIbMT7F+S8tECgh+OHb6ESUiLZJ6SQAT7QdZcdKLfnZ+QWRSCRDfK/sNozqsvj9uU9NcHgcjnnnog3tO/cTgH7NYK2xrjG4yylT6mg0Cj/nh7XoOmo0lZBSuPH1Dka23kOlUXGLW4vDuOxpTQAe5NrrTzfUeL1ecrLhDYqen0QgGMApdTn6y3vi4scrA+hbfhQPVyqUwprLMY4rXlMC0Jdrr62qq2lmL5AmrhIUReHa6k08PH4XDM3gncuOru/dEMhEbWS0TNh0bIzjagrQox40VJxoXP+5oRzVvwLDMPFXJZvnlnBpph2Q7SlNVOQ8SzvD6PIlU7CpO7RHWAuZTxfwOzymjG9/AwxTJiA7va4hh98RXA/ZYPEli2hT6iiCGWCrDlRHs3iFuCViutIOw6QJ2J8uFvyxoO+ze0IUY22wcMk2SjHdSjOtpK0qk7aEUJEKcU0EDv0l9vHB3TH3rMAJVtzi9gxSKq5JbiaOEp3ykuximV6ZR2noLMkV8/ChyBznDs8GFsRlsRcvwhmjTAPQAmChow+iVDBhn2gATeribF5wYpuYxgxph5PfBLANwCV5UrtASu1NmgT714lJ+wWAAyD8AhbJDCA03+U6AAAAAElFTkSuQmCC"/></div>';
                }
                return '<div style="text-align: center;"><img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAACrUlEQVQ4T21TbUhTYRR+3nv9aG7dud1NM6fDmZBKMjXsh30RLjKKICLICILAMCSlUgTpV0ahEFriIAoEQUiwAgVFJczqh5jOPrCiqYxp2dyW2/xgc943dmVXNzu/zjnPeZ7znvc9L0GU9es4NVmn5QAtAlAIgKXAOCjGBQbm0wvLi9spZHvQlyx/EKuQ16WcKAaXqYciIx2EIfDN2rE8a8f80Duse3xNpY6V2jBPEuhNVjzh87Irc66XgbhtEDyLELxOsY7ZzYPhNIA2A9/bu+EYmzSfWVi+EcJEgV4t18AX5tbnlJ1E4PMwBjqnYLq4L2K4wS4rTJeyEX/gKH50j+DPqKXprMNXS3p4WSrDcXNFd64g+O09Jq0KnOobRH+pCcZMrygyOc1F5GJzj2Cs5QWCTpeOvNRyDYZzJfWauAXQdT90Tz9KnUMiIQsJhm2u/CBITCyc/j2Y6Xlzn3RpOUvB5RIj654WaxLyjyGpuiX6ccTY0VyFVctb0Re0WRjvGJginRrOf+i8MU7wuiSSvOA4UmpaI0R+N1ViZWJYyjGKRIy+/hIgHRqlL//wXgVDBAm0raVFHDsEhMbRy+xSDSUMJkZ+BUi7RmnJzuONsvhNbJ4adpDDrJBIKpkRw0AA+PrJZSXPeGWDzsDXJ3FBEcgfskldZu5eE33DvedSzlKiF32Xj4Vt+m8rMfN8qkwWM7c/nQXDAInFJmQ9bMfPuqtY+rB5+9E5SoEpWxD+1WCuuEhtalWjimNr9Ensf28/Omlf3IDbE2ypcC9VS6v8mFe2qROYijSeBRPxQ7boAgXm3QKcKxvmmy7P1iqHS5rVqsY4ltRo5BTKXUB8zCYS2AA8a4BzlSAQFB5VuZduhzk7ejWrVOkAKihwAYD4ISgwSyh9RQXBfMvrtW4f6R9Xl/9Jy6U2GQAAAABJRU5ErkJggg=="/></div>';
            }
            
           

            $("#grid").jqGrid({
            url: "Patient_grid_search.action",
            datatype: "json",
            mtype: "GET",
            colNames: ["Hospital No", "Biometric", "Name", "Gender", "ART Status", "Address", "Date Modified", "Action"],
            colModel: [
            {name: "hospitalNum", index: "hospitalNum", width: "150"},
            {name: "biometric", index: "biometric", width: "100", formatter: imageFormat},
            {name: "name", index: "name", width: "220"},
            {name: "gender", index: "gender", width: "100"},
            {name: "currentStatus", index: "currentStatus", width: "150"},
            {name: "address", index: "address", width: "250"},                        
            {name: "timeStamp", index: "timeStamp", width: "150", formatter: "date", formatoptions: {srcformat: "m/d/Y", newformat: "d-M-Y"}
            },
            {name: "patientId", index: "patientId", width: "100",  classes: "table_dropdown", formatter:function (cellValue, options, rowObject){
              return '<div id="table-btn" class="dropdown" style="postion: absolute; color: #000;"><button  class="btn btn-sm btn-info dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Action</button><div class="dropdown-menu dropdown-menu-right"><a class="dropdown-item" href="#" onclick="editButton(' + cellValue + ');">Edit</a><a class="dropdown-item" href="#" onclick="deleteButton(' + cellValue +');">Delete</a></div></div>';
            }},

            ], 
            pager: $('#pager'),
            rowNum: 100,
            sortname: "timeStamp", 
            sortorder: "desc",
            viewrecords: true,
            //imgpath: "themes/basic/images",
            imgpath: "assets/jqueryui/jqgrid/img",
            resizable: false,
            height: 350,                    
            jsonReader: {
            root: "patientList",
            page: "currpage",
            total: "totalpages",
            records: "totalrecords",
            repeatitems: false,
            id: "patientId"
            },
            });         

        });
             
            function deleteButton(patId){
                $.confirm({
                    title: 'Confirm!',
                    content: 'Are you sure you want to delete?',
                    buttons: {
                        confirm: function () {
                            $("#patientId").val(patId);
                            var data = $("#grid").getRowData(patId);
                            $("#hospitalNum").val(data.hospitalNum);
                            $("#name").val(data.name);
                            if($("#userGroup").html() == "Data Analyst") {
                                $("#lamisform").attr("action", "Error_message");
                                $("#lamisform").submit();
                            }
                            else {
                             
                                  $.ajax({
                       url: encodeURI('Patient_delete?patientId=' + patId),
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
                            
                           
                        },
                        cancel: function () {
                            console.log("cancel");
                        }
                    }
                });
            }
            
            
            function editButton(id){  
                $("#patientId").val(id);
                var data = $("#grid").getRowData(id);
                $("#hospitalNum").val(data.hospitalNum);
                $("#name").val(data.name);
                $("#lamisform").attr("action", "Patient_find");
                $("#lamisform").submit();
            }
            
            
            function newButton(id){                
                $("#patientId").val(id);
                var data = $("#grid").getRowData(id);
                $("#hospitalNum").val(data.hospitalNum);
                $("#name").val(data.name);
                $("#lamisform").attr("action", "Patient_new");
                $("#lamisform").submit();
                //return true;
            }
           
        </script>
    </head>
    <body>
        <jsp:include page="/WEB-INF/views/template/header.jsp"/>
        <jsp:include page="/WEB-INF/views/template/nav_home.jsp"/> 
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="Home_page.action">Home</a></li>
                <li class="breadcrumb-item active" aria-current="page">Patient View</li>
            </ol>
        </nav>        
        <div class="row">
            <div class="col-md-12 ml-auto mr-auto">
                <div class="card">
                    <s:form id="lamisform" theme="css_xhtml">  
                        <input name="patientId" type="hidden" id="patientId"/> 
                        <input type="hidden" id="name"/>  
                        <div class="card-body">
                            <div id="messageBar"></div>                   
                            <div class="row">
                                <div class="col-12 ml-auto mr-auto">
                                    <div class="input-group no-border col-md-3 pull-right">
                                        <input type="text" class="form-control  search" placeholder="Search...">
                                        <div class="input-group-append">
                                            <div class="input-group-text">
                                                <i class="now-ui-icons ui-1_zoom-bold"></i>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-6">
                                        <div class="pull-left">
                                            <a href="Patient_new" class="btn btn-info">New</a>
                                            <button id="identify_button" class="btn btn-info">Identify Client</button>
                                        </div>
                                    </div>
                                    <div class="table-responsive">
                                        <table id="grid" class="table table-striped table-bordered center"></table>
                                        <div id="pager" style="text-align:center;"></div>
                                    </div> 
                                </div></div></div>                          
                            </s:form>
                    <div id="userGroup" style="display: none"><s:property value="#session.userGroup"/></div>
                </div>
            </div>
        </div>
        <jsp:include page="/WEB-INF/views/template/footer.jsp" />
    </body>
</html>
