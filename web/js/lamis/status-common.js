function initialize() {
    $("#date").mask("99/99/9999");
    $("#date").datepicker({
        dateFormat: "dd/mm/yy",
        changeMonth: true,
        changeYear: true,
        yearRange: "-100:+0",
        constrainInput: true,
        buttonImageOnly: true,
        buttonImage: "/images/calendar.gif"
    });

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

    $("#date").change(function () {
        findStatusByDate();
        lastSelectDate = $(this).val();
        enableControl();
    });

    function findStatusByDate() {
      
        if ($('#date').val().length != 0) {
            $("#date").datepicker("option", "altField", "#outcomeDate");
            $("#date").datepicker("option", "altFormat", "mm/dd/yy");

            $.ajax({
                url: "Status_find_date.action",
                dataType: "json",
                data: {patientId: $("#patientId").val(), dateCurrentStatus: $("#outcomeDate").val()},
                success: function (statusList) {

                    if (statusList.length > 0) {
                        console.log(statusList);
                        if (statusList[0].currentStatus === "Known Death")
                            $("#outcome").val("Died (Confirmed)");
                        else
                            $("#outcome").val(statusList[0].currentStatus);
                        $("#outcome").select2().trigger('change');

                        $("#currentStatus").val(statusList[0].currentStatus);
                        $("#currentStatus").select2().trigger('change');
                        $("#outcomeDate").val(statusList[0].dateCurrentStatus);
                        $("#dateCurrentStatus").val(statusList[0].dateCurrentStatus);
                        $("#historyId").val(statusList[0].historyId);
                    
                        $("#reasonInterrupt").val(statusList[0].reasonInterrupt);
                         $("#reasonInterrupt").select2().trigger('change');
                        $("#causeDeath").val(statusList[0].causeDeath);
                         $("#causeDeath").select2().trigger('change');
                        $("#agreedDate").val(statusList[0].agreedDate);
                  

                        updateRecord = true;
                        initButtonsForModify();
                        if (statusList[0].deletable == "1") {
                            $("#delete_button").removeAttr("disabled");
                            $("#save_button").removeAttr("disabled");
                        } else {
                            $("#delete_button").attr("disabled", "disabled");
                            $("#save_button").attr("disabled", "disabled");
                        }
                    } else {
                        var outcome = $("#outcome").val();
  
                        $("#currentStatus").val(outcome);
                        if (statusList[0].deletable == "1") {
                            $("#delete_button").removeAttr("disabled");
                            $("#save_button").removeAttr("disabled");
                        } else {
                            $("#delete_button").attr("disabled", "disabled");
                            $("#save_button").attr("disabled", "disabled");
                        }
                    }
                }
            });
        }
    }
    
    $("#outcome").change(function (event) {
        //resetButtons();
        $("#reasonInterrupt").val('');
        $("#causeDeath").val('');
        $("#agreedDate").val('');

        //findStatusByDate();

        $("#date_tr").mask("99/99/9999");
        $("#date_tr").datepicker({
            dateFormat: "dd/mm/yy",
            changeMonth: true,
            changeYear: true,
            yearRange: "-100:+0",
            constrainInput: true,
            buttonImageOnly: true,
            buttonImage: "/images/calendar.gif"
        });
        $("#date_ag").mask("99/99/9999");
        $("#date_ag").datepicker({
            dateFormat: "dd/mm/yy",
            changeMonth: true,
            changeYear: true,
            yearRange: "-100:+0",
            constrainInput: true,
            buttonImageOnly: true,
            buttonImage: "/images/calendar.gif"
        });

        $("#save_button").removeAttr("disabled");

        var outcome = $("#outcome").val();

        if (outcome == "Did Not Attempt to Trace Patient") {
            $("#save_button").attr("disabled", true);
        }
        enableControl();
        //$("#date").val('');

    });


    $("#save_button").bind("click", function (event) {
        if ($("#userGroup").html() == "Data Analyst") {
            $("#lamisform").attr("action", "Error_message");
            return true;
        } else {
            if (validateForm()) {
                if (outcome === "ART Restart" || outcome === "ART Transfer Out" || outcome === "Pre-ART Transfer Out" || outcome === "Lost to Follow Up" || outcome === "Stopped Treatment" || outcome === "Died (Confirmed)") {
                    $("#outcome").val('');
                }

                if (updateRecord) {
                    $("#lamisform").attr("action", "Status_update");
                } else {
                    $("#lamisform").attr("action", "Status_save");
                }
                return true;
            } else {
                return false;
            }
        }
    });

    $("#delete_button").bind("click", function (event) {
        date = $("#dateCurrentStatus").val();

        $("#dateCurrentStatus").val(date.slice(3, 5) + "/" + date.slice(0, 2) + "/" + date.slice(6));

        if ($("#userGroup").html() == "Data Analyst") {
            $("#lamisform").attr("action", "Error_message");
        } else {
            $("#lamisform").attr("action", "Status_delete");
        }
        return true;

    });

    $("#close_button").bind("click", function (event) {
        $("#lamisform").attr("action", "Status_search");
        return true;
    });
}

function validateForm() {
    var regex = /^\d{2}\/\d{2}\/\d{4}$/;
    var validate = true;

    $("#date").datepicker("option", "altField", "#outcomeDate");
    $("#date").datepicker("option", "altFormat", "mm/dd/yy");
    $("#date1").datepicker("option", "altField", "#dateTracked");
    $("#date1").datepicker("option", "altFormat", "mm/dd/yy");
    $("#date2").datepicker("option", "altField", "#agreedDate");
    $("#date2").datepicker("option", "altFormat", "mm/dd/yy");
    var outcome = $("#outcome").val();
    // check if new status is entered


    if ($("#outcome").val().length === 0) {
        $("#statusregHelp").html(" *");
        validate = false;
    } else {
        if (outcome == "ART Restart" || outcome == "ART Transfer Out" || outcome == "Pre-ART Transfer Out" || outcome == "Lost to Follow Up" || outcome == "Stopped Treatment" || outcome == "Died (Confirmed)") {
            if ($("#outcome").val() == "Died (Confirmed)") {
                $("#currentStatus").val("Known Death");
            } else {

                $("#currentStatus").val(outcome);
            }
            $("#dateCurrentStatus").val($("#outcomeDate").val());
            $("#outcome").val('');
        } else {

        }
        $("#statusregHelp").html("");
    }
    // check if date of visit is entered
    if (outcome === "ART Restart" || outcome === "ART Transfer Out" || outcome === "Pre-ART Transfer Out" || outcome === "Lost to Follow Up" || outcome === "Stopped Treatment" || outcome === "Died (Confirmed)") {
        if ($("#date").val().length === 0 || !regex.test($("#date").val())) {
            $("#dateHelp").html(" *");
            validate = false;
        } else {
            $("#dateHelp").html("");
        }
    } else {
        if ($("#date1").val().length === 0 || !regex.test($("#date1").val())) {
            $("#dateHelp").html(" *");
            validate = false;
        } else {
            $("#dateHelp").html("");
        }
    }
    return validate;
}
