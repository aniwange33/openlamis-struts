/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

function initialize() {
    addDatepicker("#date1");
    addDatepicker("#date2");
    addDatepicker("#date3");
    addDatepicker("#date4");
    addDatepicker("#date5");
    addDatepicker("#date6");
    addDatepicker("#date7");
    addDatepicker("#date12");
    addDatepicker("#date11");

    $("#rapidTestResult").attr("disabled", true);
    $("#date12").attr("disabled", true);
    $("#date11").attr("disabled", true);
    $("#arvType").attr("disabled", true);
    $("#arvTiming").attr("disabled", true);
    $("#date2").attr("disabled", true);
    $("#ageNvpInitiated").attr("disabled", true); 
    $("#date3").attr("disabled", true);
    $("#ageCotrimInitiated").attr("disabled", true);
    
//    Disable ARV Params

    $("#arv").change(function(event) {
        if ($("#arv").val() === "Yes") {
            $("#arvType").attr("disabled", false);
            $("#arvTiming").attr("disabled", false);
            $("#date2").attr("disabled", false);
            $("#ageNvpInitiated").attr("disabled", false);
        }
        else {
            $("#arvType").attr("disabled", true);
            $("#arvTiming").attr("disabled", true);
            $("#date2").attr("disabled", true);
            $("#ageNvpInitiated").attr("disabled", true);
           
        }
    });
    
    
    $("#cotrim").change(function(event) {
        if ($("#cotrim").val() === "Yes") {
            $("#date3").attr("disabled", false);
            $("#ageCotrimInitiated").attr("disabled", false);
        }
        else {
            $("#date3").attr("disabled", true);
            $("#ageCotrimInitiated").attr("disabled", true);
        }
    });

    $("#rapidTest").change(function(event) {
        if ($("#rapidTest").val() === "Yes") {
            $("#rapidTestResult").attr("disabled", false);
            $("#date12").attr("disabled", false);
        }
        else {
            $("#rapidTestResult").attr("disabled", true);
            $("#date12").attr("disabled", true);
        }
    });
    
    $("#infantOutcome").change(function(event) {
        if ($("#infantOutcome").val() === "1 - HIV Positive Linked to ART") {
            $("#date11").attr("disabled", false);
        }
        else {
            $("#date11").attr("disabled", true);
        }
    });
    
    $("#date1").bind("change", function(event){
        isAFutureDate("#date1", "Date of Visit", true);
        if ($("#date1").val().length !== 0 && $("#dateBirth").val().length !== 0) {
            var dateString = $("#date1").val().slice(3,5)+"/"+$("#date1").val().slice(0,2)+"/"+$("#date1").val().slice(6);

            var second = 1000, minute = second*60, hour = minute*60, day = hour*24, week = day*7;
            var weeks = Math.round((new Date(dateString).getTime() - new Date($("#dateBirth").val()).getTime()) / week); 
            //if (weeks == 0) weeks = 1;
            $("#ageVisit").val(weeks); 
        }		
    });
    
    $("#date2").bind("change", function(event){
        isAFutureDate("#date2", "Date NVP Initiated", false);
        if ($("#date2").val().length !== 0 && $("#dateBirth").val().length !== 0) {
            var ageInWeeks = calculateWeeks($("#dateBirth").val(), $("#date2").val());
            $("#ageNvpInitiated").val(ageInWeeks); 
        }		
    });
    
    $("#date3").bind("change", function(event){
        isAFutureDate("#date3", "Date Cotrim Initiated", true);
        if ($("#date3").val().length !== 0 && $("#dateBirth").val().length !== 0) {
            var ageInWeeks = calculateWeeks($("#dateBirth").val(), $("#date3").val());
            $("#ageCotrimInitiated").val(ageInWeeks); 
        }		
    });
    
    $("#date4").bind("change", function(event){
        isAFutureDate("#date4", "Date of PCR Sample Collection", true);
    });
    
    $("#date5").bind("change", function(event){
        isAFutureDate("#date5", "Date PCR Sample Sent", true);
        isEarlierThan("#date5", "#date4", "Date PCR Result sent", "Date of PCR Sample Collection", true);
        
    });
    
    $("#date6").bind("change", function(event){
        isAFutureDate("#date6", "Date PCR Result Received", true);
        isEarlierThan("#date6", "#date4", "Date PCR Result received", "Date of PCR Sample Collection", true);
        isEarlierThan("#date6", "#date5", "Date PCR Result received", "Date of PCR Sample sent", true);
    });
    
    $("#date7").bind("change", function(event){
        isEarlierThan("#date7", "#date1", "Date of Next Visit", "Date of Viist", true);
    });
    
    $("#date11").bind("change", function(event){
        isAFutureDate("#date11", "Date Linked to ART", true);
    });
    
    $("#date12").bind("change", function(event){
        isAFutureDate("#date12", "Date Rapid Antibody Test", true);
    });
    
	
$("#save_button").bind("click", function(event){
    if($("#userGroup").html() == "Data Analyst") {
        $("#lamisform").attr("action", "Error_message");
        return true;                        
    }
    else {
        if(validateForm()) {
            if(updateRecord) {
                $("#lamisform").attr("action", "Childfollowup_update");                
            } 
            else {
                $("#lamisform").attr("action", "Childfollowup_save");                
            }
            return true;                        
        } 
        else {
            return false;
        }
    }
});   
				
$("#delete_button").bind("click", function(event){
    if($("#userGroup").html() === "Data Analyst") {
            $("#lamisform").attr("action", "Error_message");
        }
        else {
            $("#lamisform").attr("action", "Childfollowup_delete");
        }
        return true;
    });

    $("#close_button").bind("click", function(event){
        $("#lamisform").attr("action", "Childfollowup_search");
        return true;
    });  	
}

//Validate dates

function createChild(){
    var childRecord = {};
    if($("#userGroup").html() === "Data Analyst") {
        $("#lamisform").attr("action", "Error_message");
        return true;                        
    }
    else {
        if(validateChildForm()) {
            $("#dialog").dialog("close");
            
            var childRecord = {children: [], motherDTO : {}};
            var willing = "No";

            //Build the data...
            
            if($("#willing").val() === "Yes"){
                willing = "Yes";
                childRecord.motherDTO.willing = willing;
                console.log($("#motherPatientId").val());
                childRecord.motherDTO.patientId = $("#motherPatientId").val();
            }else if($("#willing").val() === "No" || $("#willing").val() === ""){
                willing = "No";
                childRecord.motherDTO.willing = willing;
                childRecord.motherDTO.address = $("#address").val();
                childRecord.motherDTO.motherUniqueId = $("#motherUniqueId").val();
                childRecord.motherDTO.inFacility = "No";
                childRecord.motherDTO.dateConfirmedHiv = $("#dateConfirmedHiv").val();
                childRecord.motherDTO.motherSurname = $("#motherSurname").val();
                childRecord.motherDTO.motherOtherNames = $("#motherOtherNames").val();
                childRecord.motherDTO.dateStarted = $("#dateStarted").val();
                childRecord.motherDTO.phone = $("#phone").val();
                
            }
                var selid = $("#childInfoGrid").getGridParam("selrow");
                $("#childInfoGrid").saveRow(selid, false, 'clientArray'); 

                var ids = $("#childInfoGrid").getDataIDs();
                for (var i = 0; i < ids.length; i++) {                       
                    var child = $("#childInfoGrid").getRowData(ids[i]);
                    var date = child.dateBirth;
                    child.dateBirth = date.slice(3,5)+"/"+date.slice(0,2)+"/"+date.slice(6); 
                    childRecord.children[i] = child;
                }
            $.postJSON("Child_save.action", childRecord, function(status) { 
                window.location.href = "Childfollowup_search.action";
            }); 
        }
    }    
}

function validateChildForm() {
    var validate = true;
    $("#date8").datepicker("option", "altField", "#dateBirth");    
    $("#date8").datepicker("option", "altFormat", "mm/dd/yy");
    
    $("#date9").datepicker("option", "altField", "#dateConfirmedHiv");    
    $("#date9").datepicker("option", "altFormat", "mm/dd/yy");
    
    $("#date10").datepicker("option", "altField", "#dateStarted");    
    $("#date10").datepicker("option", "altFormat", "mm/dd/yy");
    
    $("#date11").datepicker("option", "altField", "#dateLinkedToArt");    
    $("#date11").datepicker("option", "altFormat", "mm/dd/yy");
    
    $("#date12").datepicker("option", "altField", "#dateRapidTest");    
    $("#date12").datepicker("option", "altFormat", "mm/dd/yy");

//    // check if hospital number is entered
//    if($("#hospitalNumber").val().length === 0) {
//        $("#hospitalNumHelp").html(" *");
//        validate = false;
//    }
//    else {
//        $("#hospitalNumHelp").html("");
//    }
    
     // check if patientId is entered
    if($("#willing").val() === "Yes"){
        if($("#motherPatientId").val().length === 0) {
           $("#motherPatientIdHelp").html(" *");
           validate = false;
       }
       else {
           $("#motherPatientIdHelp").html("");
       }
    }else if($("#willing").val() === "No"){
        //Validate the basic motehr information..
        //Check for surname
        if($("#motherSurname").val().length === 0) {
            $("#motherSurnameHelp").html(" *");
            validate = false;
        }
        else {
            $("#motherSurnameHelp").html("");
        }
    }
    

    

//    //Check for gender
//    if($("#gender").val().length === "-- select --") {
//        $("#genderHelp").html(" *");
//        validate = false;
//    }
//    else {
//        $("#genderHelp").html("");
//    }
//
//    // check if date of birth is entered
//    if($("#dateBirth").val().length === 0){
//        $("#dateBirthHelp").html(" *");
//        validate = false;
//    }
//    else {
//        $("#dateBirthHelp").html("");
//    }
    return validate;	
}

function addDatepicker(id) {
    $(id).mask("99/99/9999");
    $(id).datepicker({
        dateFormat: "dd/mm/yy",
        changeMonth: true,
        changeYear: true,
        yearRange: "-100:+0",
        constrainInput: true,
        buttonImageOnly: true,
        buttonImage: "/images/calendar.gif"
    });
}

function populateForm(childfollowupList) {
    if($.isEmptyObject(childfollowupList)) {
        updateRecord = false;
        resetButtons();

        $("#childfollowupId").val("");
        $("#date1").val("");
        $("#ageVisit").val("");
        $("#date2").val("");
        $("#ageNvpInitiated").val("");
        $("#date3").val("");
        $("#ageCotrimInitiated").val("");
        $("#bodyWeight").val("");
        $("#height").val("");
        $("#feeding").val("");
        $("#arv").val("");
        $("#arvType").val("");
        $("#arvTiming").val("");
        $("#cotrim").val("");
        $("#date4").val("");
        $("#reasonPcr").val("");
        $("#date5").val("");
        $("#date6").val("");
        $("#pcrResult").val("");
        $("#rapidTest").val("");
        $("#rapidTestResult").val("");
        $("#caregiverGivenResult").val("");
        $("#childOutcome").val("");
        $("#referred").val("");
        $("#date7").val("");
        $("#date11").val("");
        $("#childOutcome").val("");
        $("#infantOutcome").val("");
        $("#date12").val("");
    }
    else {
        updateRecord = true;
        initButtonsForModify();

        $("#childId").val(childfollowupList[0].childId);
        $("#childfollowupId").val(childfollowupList[0].childfollowupId);
        date = childfollowupList[0].dateVisit;
	$("#date1").val(dateSlice(date));
        $("#ageVisit").val(childfollowupList[0].ageVisit);
        date = childfollowupList[0].dateNvpInitiated;
	if(date !== "") $("#date2").val(dateSlice(date));		
        $("#ageNvpInitiated").val(childfollowupList[0].ageNvpInitiated);
        date = childfollowupList[0].dateCotrimInitiated;
        if(date !== "") $("#date3").val(dateSlice(date));	
        $("#ageCotrimInitiated").val(childfollowupList[0].ageCotrimInitiated);
        $("#bodyWeight").val(childfollowupList[0].bodyWeight);	
        $("#height").val(childfollowupList[0].height);
        $("#feeding").val(childfollowupList[0].feeding);
        $("#arv").val(childfollowupList[0].arv);
        if(childfollowupList[0].arv !== "Yes"){
            $("#arvType").attr("disabled", true);
            $("#arvTiming").attr("disabled", true);
            $("#date2").attr("disabled", true);
            $("#ageNvpInitiated").attr("disabled", true);
        }else{
            $("#arvType").attr("disabled", false);
            $("#arvTiming").attr("disabled", false);
            $("#date2").attr("disabled", false);
            $("#ageNvpInitiated").attr("disabled", false);
        }
        $("#cotrim").val(childfollowupList[0].cotrim);
        if(childfollowupList[0].cotrim === "Yes"){
            $("#date3").attr("disabled", false);
            $("#ageCotrimInitiated").attr("disabled", false);
            $("#ageCotrimInitiated").val(childfollowupList[0].ageCotrimInitiated);
            date = childfollowupList[0].dateCotrimInitiated;
            if(date !== "") $("#date3").val(dateSlice(date));
        }else{
            $("#date3").attr("disabled", true);
        }
        date = childfollowupList[0].dateSampleCollected;
        if(date !== "") $("#date4").val(dateSlice(date));
        $("#reasonPcr").val(childfollowupList[0].reasonPcr);
        date = childfollowupList[0].dateSampleSent;
        if(date !== "") $("#date5").val(dateSlice(date));
        date = childfollowupList[0].datePcrResult;
        if(date !== "") $("#date6").val(dateSlice(date));
        $("#pcrResult").val(childfollowupList[0].pcrResult);
        $("#rapidTest").val(childfollowupList[0].rapidTest);
        $("#rapidTestResult").val(childfollowupList[0].rapidTestResult);
        if ($("#rapidTestResult").val().length !== 0) {
            $("#rapidTestResult").attr("disabled", false);
            $("#date12").attr("disabled", false);
            date = childfollowupList[0].dateRapidTest;
            $("#date12").val(dateSlice(date));
        }
        $("#caregiverGivenResult").val(childfollowupList[0].caregiverGivenResult);
        $("#arvType").val(childfollowupList[0].arvType);
        $("#arvTiming").val(childfollowupList[0].arvTiming);
        $("#childOutcome").val(childfollowupList[0].childOutcome);
        $("#infantOutcome").val(childfollowupList[0].infantOutcome);
        if (childfollowupList[0].infantOutcome === "1 - HIV Positive Linked to ART") {
            $("#date11").attr("disabled", false);
        }
        else {
            $("#date11").attr("disabled", true);
        }
        $("#referred").val(childfollowupList[0].referred);
        date = childfollowupList[0].dateNextVisit;
        $("#date7").val(date.slice(3,5)+"/"+date.slice(0,2)+"/"+date.slice(6));
        if(childfollowupList[0].dateLinkedToArt !== ""){
            date = childfollowupList[0].dateLinkedToArt;
            $("#date11").val(date.slice(3,5)+"/"+date.slice(0,2)+"/"+date.slice(6));
        }
    } 

    $.ajax({
        url: "Child_retrieve.action",
        dataType: "json",                    
        success: function(childList) {
            // set child id and hospital number
            console.log(childList[0]);
            $("#childId").val(childList[0].childId);
            $("#motherId").val(childList[0].motherId);
            $("#hospitalNum").val(childList[0].hospitalNum);
            $("#dateBirth").val(childList[0].dateBirth);
            $("#childInfor").html(childList[0].surname + " " + childList[0].otherNames);
            //$("#motherId").val(childList[0].motherId);
            //$("#patientInfor").html(childList[0].nameMother + " (Mother)");
            
            retrieveMotherInfo(childList[0].motherId);
	}	
    }); //end of ajax call 	

}

function retrieveMotherInfo(motherId){
    
    $.ajax({
        url: "Mother_retrieve_child.action?motherId="+motherId,
        dataType: "json",                    
        success: function(motherList) {
            // set patient id and number for which infor is to be entered
            console.log(motherList);
            $("#motherId").val(motherList[0].motherId);
            $("#patientInfor").html(motherList[0].surname + " " + motherList[0].otherNames + " (Mother)"); 
        }                    
    }); //end of ajax call 
}

function getMonthDisplayName(month) {
    switch(month) {
        case "01" :return "Jan";
        case "02" :return "Feb";
        case "03" :return "Mar";
        case "04" :return "Apr";
        case "05" :return "May";
        case "06" :return "Jun";
        case "07" :return "Jul";
        case "08" :return "Aug";
        case "09" :return "Sep";
        case "10" :return "Oct";
        case "11" :return "Nov";
        case "12" :return "Dec";
        default :return undefined;
    }   
} 

function validateForm() {
    var regex = /^\d{2}\/\d{2}\/\d{4}$/;
    var validate = true;
    
    if($("#arv").val() !== "Yes"){
        $("#arvType").val("");
        $("#arvTiming").val("");
        $("#date2").val("");
        $("#ageNvpInitiated").val("");
    }
    
    if ($("#rapidTest").val() !== "Yes") {
        $("#rapidTestResult").val("");
        $("#date12").val("");
    }
    
    if ($("#infantOutcome").val() !== "1 - HIV Positive Linked to ART") {
        $("#date11").val("");
    }
    
    if($("#cotrim").val() !== "Yes"){
        $("#date3").val("");
        $("#ageCotrimInitiated").val("");
    }
    
    $("#date1").datepicker("option", "altField", "#dateVisit");    
    $("#date1").datepicker("option", "altFormat", "mm/dd/yy");
    $("#date2").datepicker("option", "altField", "#dateNvpInitiated");    
    $("#date2").datepicker("option", "altFormat", "mm/dd/yy");
    $("#date3").datepicker("option", "altField", "#dateCotrimInitiated");    
    $("#date3").datepicker("option", "altFormat", "mm/dd/yy");
    $("#date4").datepicker("option", "altField", "#dateSampleCollected");    
    $("#date4").datepicker("option", "altFormat", "mm/dd/yy");
    $("#date5").datepicker("option", "altField", "#dateSampleSent");    
    $("#date5").datepicker("option", "altFormat", "mm/dd/yy");
    $("#date6").datepicker("option", "altField", "#datePcrResult");    
    $("#date6").datepicker("option", "altFormat", "mm/dd/yy");
    $("#date7").datepicker("option", "altField", "#dateNextVisit");    
    $("#date7").datepicker("option", "altFormat", "mm/dd/yy");  
    $("#date12").datepicker("option", "altField", "#dateRapidTest");    
    $("#date12").datepicker("option", "altFormat", "mm/dd/yy");  
    $("#date11").datepicker("option", "altField", "#dateLinkedToArt");    
    $("#date11").datepicker("option", "altFormat", "mm/dd/yy");  
    
    if($("#dateVisit").val().length === 0 || !regex.test($("#dateVisit").val())){
        $("#dateVisitHelp").html(" *");
        validate = false;
    }
    else {
        $("dateVisitHelp").html("");
    }
    
    if($("#dateNextVisit").val().length === 0 || !regex.test($("#dateNextVisit").val())){
        $("#dateNextVisitHelp").html(" *");
        validate = false;
    }
    else {
        $("dateNextVisitHelp").html("");
    }
    
    return validate;	
}