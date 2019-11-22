/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
    
function initialize() {
    $("#date1").mask("99/99/9999");
    $("#date1").datepicker({
        dateFormat: "dd/mm/yy",
        changeMonth: true,
        changeYear: true,
        yearRange: "-100:+0",
        constrainInput: true,
        buttonImageOnly: true,
        buttonImage: "/images/calendar.gif"
    });
    $("#date2").mask("99/99/9999");
    $("#date2").datepicker({
        dateFormat: "dd/mm/yy",
        changeMonth: true,
        changeYear: true,
        yearRange: "-100:+0",
        constrainInput: true,
        buttonImageOnly: true,
        buttonImage: "/images/calendar.gif"
    });

    $("#date3").mask("99/99/9999");
    $("#date3").datepicker({
        dateFormat: "dd/mm/yy",
        changeMonth: true,
        changeYear: true,
        yearRange: "-100:+0",
        constrainInput: true,
        buttonImageOnly: true,
        buttonImage: "/images/calendar.gif"
    });

    $("#date4").mask("99/99/9999");
    $("#date4").datepicker({
        dateFormat: "dd/mm/yy",
        changeMonth: true,
        changeYear: true,
        yearRange: "-100:+0",
        constrainInput: true,
        buttonImageOnly: true,
        buttonImage: "/images/calendar.gif"
    });

    $("#date1").change(function(){
      
            findEacByDate();
        
    });

      
    $("#delete_button").bind("click", function(event){
        if($("#userGroup").html() == "Data Analyst") {
            $("#lamisform").attr("action", "Error_message");
        }
        else {
            $("#lamisform").attr("action", "Eac_delete");
        }
        return true;
    });
}


function findEacByDate() {
    $("#date1").datepicker("option", "altField", "#dateEac1");    
    $("#date1").datepicker("option", "altFormat", "mm/dd/yy");    
    $.ajax({
        url: "Eac_find_date.action",
        dataType: "json",
        data: {patientId: $("#patientId").val(), dateEac1: $("#dateEac1").val()},
        success: function(eacList) {
            populateForm(eacList);            
        }        
    });
}

function populateForm(eacList) {
    if($.isEmptyObject(eacList)) {
        updateRecord = false;
        resetButtons();
        $("#eacId").val("");
        $("#dateEac1").val("");
        $("#dateEac2").val("");
        $("#dateEac3").val("");
        $("#dateSampledCollected").val("");
       
    }
    else {
        updateRecord = true;
        initButtonsForModify();

        $("#eacId").val(eacList[0].eacId);
        $("#dateEac1").val(eacList[0].dateEac1);
        date = eacList[0].dateEac1;
        $("#date1").val(dateSlice(date));
        
        $("#dateEac2").val(eacList[0].dateEac2);
        date = eacList[0].dateEac2;
        $("#date2").val(dateSlice(date));
        
        $("#dateEac3").val(eacList[0].dateEac3);
        date = eacList[0].dateEac3;
        $("#date3").val(dateSlice(date));
        
        $("#dateSampleCollected").val(eacList[0].dateSampleCollected);
        date = eacList[0].dateSampleCollected;
        $("#date4").val(dateSlice(date));
        
        $("#dateLastViralLoad").val(eacList[0].dateLastViralLoad);
        $("#dateLastViralLoad_view").html(dateSlice(eacList[0].dateLastViralLoad));
        $("#lastViralLoad").val(eacList[0].lastViralLoad);
        $("#lastViralLoad_view").html(eacList[0].lastViralLoad);
    }    

    $.ajax({
        url: "Patient_retrieve.action",
        dataType: "json",                    
        success: function(patientList) {
            // set patient id and number for which infor is to be entered
            $("#patientId").val(patientList[0].patientId);
            $("#hospitalNum").val(patientList[0].hospitalNum);
            $("#patientInfor").html(patientList[0].surname + " " + patientList[0].otherNames);

            date = dateSlice(patientList[0].dateLastViralLoad);
            console.log("Date Last VL:"+date);
            $("#dateLastViralLoad_View").html(date);
            $("#lastViralLoad_View").html(patientList[0].lastViralLoad);
            //dueViralLoad(patientList[0].dueViralLoad);
        }                    
    }); //end of ajax call
}

function validateForm() {
    var regex = /^\d{2}\/\d{2}\/\d{4}$/;
    var validate = true;

    $("#date1").datepicker("option", "altField", "#dateEac1");    
    $("#date1").datepicker("option", "altFormat", "mm/dd/yy"); 
    
    $("#date2").datepicker("option", "altField", "#dateEac2");    
    $("#date2").datepicker("option", "altFormat", "mm/dd/yy");   
    
    $("#date3").datepicker("option", "altField", "#dateEac3");    
    $("#date3").datepicker("option", "altFormat", "mm/dd/yy");  
    
    $("#date4").datepicker("option", "altField", "#dateSampleCollected");    
    $("#date4").datepicker("option", "altFormat", "mm/dd/yy");    

    // check if date of visit is entered
    if($("#date1").val().length == 0 || !regex.test($("#date1").val())){
        $("#dateHelp").html(" *");
        validate = false;
    }
    else {
        $("#dateHelp").html("");
    }          
    return validate;
}                 
