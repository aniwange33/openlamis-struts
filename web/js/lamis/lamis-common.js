/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
function resetPage() {    
    $(".inputboxes").bind("keypress", function(event){
        if(event.which==13){
            event.preventDefault();
            return false;                        
        }
    });
    $("#messageBar").hide();

    String.prototype.capitalise = function() {
        return this.charAt(0).toUpperCase()+this.slice(1).toLowerCase();
    }     
}

function isAFutureDate(dateObject, stringValue, clear){
    if(stringValue.length === 0) stringValue = "The date entered";
    if($(dateObject).val().length !== 0){
        var todayFormatted = formatDate(new Date());
        if(parseInt(compare($(dateObject).val(), todayFormatted)) === -1) {
            var message = stringValue+" cannot be later than today please correct!";
            if(clear){
                $("#messageBar").html(message).slideDown('slow'); 
                $(dateObject).val("");
            }else{
                alert(message);
            }
        }else{
            $("#messageBar").slideUp('slow');
            return true;
        }
    }
    return false;
}

function isLaterThan(dateObject, dateObj2, stringValue, stringValue2, clear){
    if(stringValue.length === 0) stringValue = "The date entered";
    if($(dateObject).val().length !== 0){
        var upperDate = $(dateObj2).val();
        if(parseInt(compare($(dateObject).val(), upperDate)) === -1) {
            var message = stringValue+" cannot be later than " + stringValue2 +" please correct!";
            if(clear){
                $("#messageBar").html(message).slideDown('slow'); 
                $(dateObject).val("");
            }else{
                alert(message);
            }
        }else{
            $("#messageBar").slideUp('slow');
            return true;
        }
    }
    return false;
}

function isEarlierThan(dateObject, dateObj2, stringValue, stringValue2, clear){
    if(stringValue.length === 0) stringValue = "The date entered";
    if($(dateObject).val().length !== 0){
        var upperDate = $(dateObj2).val();
        if(parseInt(compare(upperDate, $(dateObject).val())) === -1) {
            console.log("Is Earlier : ",parseInt(compare(upperDate, $(dateObject).val())));
            var message = stringValue + " cannot be earlier than " + stringValue2 + " please correct!";
            if(clear){
                $("#messageBar").html(message).slideDown('slow'); 
                $(dateObject).val("");
            }else{
                alert(message);
            }
        }else{
            $("#messageBar").slideUp('slow');
            return true;
        }
    }
    return false;
}

function getStatus() {
    var reasons = { '' : '',
        'ART Restart': 'ART Restart', 
        'ART Transfer Out': 'ART Transfer Out', 
        'Pre-ART Transfer Out': 'Pre-ART Transfer Out',
        'Lost to Follow Up' : 'Lost to Follow Up',
        'Stopped Treatment' : 'Stopped Treatment',
        'Died (confirmed)' : 'Died (confirmed)',
        'Previously undocumented patient transfer (confirmed)' : 'Previously undocumented patient transfer (confirmed)',
        'Traced patient (unable to locate)' : 'Traced patient (unable to locate)',
        'Did not attempt to trace patient' : 'Did not attempt to trace patient',
    };
    return reasons;
}

function resetButtons() {
    $("#save_button").html("Save");
    $("#close_button").html("Close");
    $("#close_button").attr("data-button-state", "close");
    $("#delete_button").attr("disabled", true);
}

function initButtonsForModify() {
    $("#save_button").html("Modify");
    $("#close_button").html("Close");
    $("#close_button").attr("data-button-state", "close");
    $("#delete_button").attr("disabled", false);
}

function checkCd4(date) {
    var minutes = 1000*60;
    var hours = minutes*60;
    var days = hours*24;
    var dateLastCd4 = new Date(date.slice(6), date.slice(0,2), date.slice(3,5));
    var today = new Date().clearTime();
    var diff = Math.round((today - dateLastCd4)/days);
    if(diff > 90) {
        var message = "Patient is due for CD4 Count investigation";
        $("#messageBar").html(message).slideDown('slow');     
    } 
    else {
        $("#messageBar").slideUp('slow');                 
    }
}

function dueViralLoad(due) {
    if(due == "1") {
        var message = "Patient is due for viral load test investigation";
        $("#messageBar").html(message).slideDown('slow');     
    } 
    else {
        $("#messageBar").slideUp('slow');                 
    }
}

function zerorize(hospitalNumber) {
    //remove special some characters from hospital number 
    hospitalNumber = hospitalNumber.replace("'", "");        
    hospitalNumber = hospitalNumber.replace("&", "");        
    hospitalNumber = hospitalNumber.replace("%", "");        
    hospitalNumber = hospitalNumber.replace("?", "");        
    hospitalNumber = hospitalNumber.replace(",", "");        
    hospitalNumber = hospitalNumber.replace(" ", "");        
    var zeros = "";
    var MAX_LENGTH = 7; 
    if(hospitalNumber.length < MAX_LENGTH  && enablePadding == true) {
        for(i = 0; i < MAX_LENGTH-hospitalNumber.length; i++) {
            zeros = zeros + "0";  
        }
    }
    return (zeros+hospitalNumber).toUpperCase();
}

function formatDate(date) {
  var monthNames = [
    "01", "02", "03",
    "04", "05", "06", "07",
    "08", "09", "10",
    "11", "12"
  ];

  var day = date.getDate();
  var monthIndex = date.getMonth();
  var year = date.getFullYear();

  return day + '/' + monthNames[monthIndex] + '/' + year;
}

function dateDiff(date1, date2, interval) {
    var second = 1000, minute = second*60, hour = minute*60, day = hour*24, week = day*7;
    date1 = new Date(parseDate(date1));
    date2 = new Date(parseDate(date2));
    var timediff = date2 - date1;
    if(isNaN(timediff)) return NaN;
    switch(interval) {
        case "years" :return date2.getFullYear() - date1.getFullYear();
        case "months" :return((date2.getFullYear() * 12 + date2.getMonth()) - (date1.getFullYear() * 12 + date1.getMonth()));
        case "weeks" :return Math.round(timediff/week);
        case "days" :return Math.round(timediff/day);
        case "hours" :return Math.round(timediff/hour);
        case "minutes" :return Math.round(timediff/minute);
        case "seconds" :return Math.round(timediff/second);
        default :return undefined;
    }   
}

function compare(date1, date2) {
    date1 = new Date(parseDate(date1));
    date2 = new Date(parseDate(date2));
    if(isNaN(date2 - date1)) return -1;
    if(date2 == date1) return 0;
    if(date2 < date1) return -1;
    return 1;
}

function calculateWeeks(lmp, visitDate, isGa, edd){
    if (lmp.length !== 0) {
        var lmpDate = new Date(lmp.slice(3,5)+"/"+lmp.slice(0,2)+"/"+lmp.slice(6));
        var dateVisit = new Date(visitDate.slice(3,5)+"/"+visitDate.slice(0,2)+"/"+visitDate.slice(6));
        lmpDate.setMonth(lmpDate.getMonth() + 9); dateVisit.setMonth(dateVisit.getMonth() + 9);// add nine months 
        lmpDate.setDate(lmpDate.getDate() + 7); dateVisit.setDate(dateVisit.getDate() + 7); // add seven days
        if(isGa){
            var day = lmpDate.getDate() < 10 ? "0" + lmpDate.getDate() : lmpDate.getDate();
            var month = (lmpDate.getMonth() + 1) < 10 ? "0" + (lmpDate.getMonth() + 1) : (lmpDate.getMonth() + 1);
            $(edd).val("" + day + "/" + month + "/" + lmpDate.getFullYear());
        }
        var second = 1000, minute = second*60, hour = minute*60, day = hour*24, week = day*7;
        var weeks = (dateVisit.getTime() - lmpDate.getTime()) / week; 
        var weekString = weeks.toString().substring(0,2);
        console.log("G.A is: "+weeks);
        if(!weekString.includes("."))
            return weekString.substring(0,2);
        else
            return weekString.substring(0,1);
    }
}

//Change date format to mm/dd/yy
function parseDate(dateStr) {
    console.log(dateStr);
    var dateParts = dateStr.split("/");
    if(dateParts.length != 3) return NaN;
    var year = dateParts[2];
    var month = dateParts[1];
    var day = dateParts[0];
    
    if(isNaN(day) || isNaN(month) || isNaN(year)) return NaN;
    
    var result = new Date(year, (month - 1), day);
    if(result == null) return NaN;
    if(result.getDate() != day) return NaN;
    if(result.getMonth() != (month - 1)) return NaN;
    if(result.getFullYear() != year) return NaN;
    return result;
}

function monthAsNumber(month) {
    var number = 0;
    month = month.toUpperCase();
    if(month.equals("JANUARY")) {
        number = 1;
    }
    if(month.equals("FEBRUARY")) {
        number = 2;
    }
    if(month.equals("MARCH")) {
        number = 3;
    }
    if(month.equals("APRIL")) {
        number = 4;
    }
    if(month.equals("MAY")) {
        number = 5;
    }
    if(month.equals("JUNE")) {
        number = 6;
    }
    if(month.equals("JULY")) {
        number = 7;
    }
    if(month.equals("AUGUST")) {
        number = 8;
    }
    if(month.equals("SEPTEMBER")) {
        number = 9;
    }
    if(month.equals("OCTOBER")) {
        number = 10;
    }
    if(month.equals("NOVEMBER")) {
        number = 11;
    }
    if(month.equals("DECEMBER")) {
        number = 12;
    }
    return number;
}

function dateSlice(date) {
    if(date != "") {
        date = date.slice(3,5)+"/"+date.slice(0,2)+"/"+date.slice(6);                        
    }
    return date;
}

function convertJson() {
    var grid = $("#grid");
    var data = JSON.stringify(grid.jqGrid('getGridParam', 'data'));
    $.ajax({
        url: 'process',
        type: 'post',
        data: data
    });
}

function printNotifier() {
     new $.Zebra_Dialog(
        'Barcode generated and printed', {
        'type': 'information',
        /*'custom_class': 'notifierclass',*/
        'buttons':  false,
        'modal': false,
        'position': ['right - 20', 'top + 40'],
        'auto_close': 5000,
        'show_close_button': false
    });
}

function dqaNotifier() {
   $.ajax({
        url: "Facility_dqastatus.action",
        dataType: "json",
        success: function(data) {
            var date = new Date();
            var day = date.getDate();
            var lowerlimit = data.dayDqa - 3;
            var upperlimit = data.dayDqa + 2;
            if (data.dqaStatus == 0 && day >= lowerlimit && day <= upperlimit) {
                new $.Zebra_Dialog(
                'Please run DQA for the current month', {
                'type': 'warning',
                /*'custom_class': 'notifierclass',*/
                'buttons':  false,
                'modal': false,
                'position': ['right - 20', 'top + 40'],
                'auto_close': 10000,
                'show_close_button': false
            });
           }   
        }        
    });    
}

