<nav class="navbar navbar-expand-md navbar-dark bg-white">
<!--    <a class="navbar-brand" href="/Clinic_page" id="dash-label">Clinic</a>-->
    <button class="navbar-toggler text-white" type="button"
            data-toggle="collapse" data-target="#navbarText"
            aria-controls="navbarSupportedContent" aria-expanded="false"
            aria-label="Toggle navigation">
        <span class="navbar-toggler-bar navbar-kebab"></span> 
        <span class="navbar-toggler-bar navbar-kebab"></span> 
        <span class="navbar-toggler-bar navbar-kebab"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarText">
        <ul class="navbar-nav">
            <li class="nav-item dropdown pull-right">
                <a class="nav-link dropdown-toggle text-center" href="#" id="navbarDropdownMenuLink"
                   data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <i class="now-ui-icons education_paper"></i>
                    <span data-i18n="reports"> Patient Reports</span>
                </a>
               
                <div class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
                    <a data-i18n="Patient_query_criteria.action" class="dropdown-item" id="Patient_query_criteria.action" href="Patient_query_criteria.action">Patients Information Query</a>
                    <a data-i18n="Reporting_month_patient.action?formId=1" class="dropdown-item" href="Reporting_month_patient.action?formId=1">ART Monthly Summary</a>
                    <a data-i18n="Reporting_month_patient.action?formId=2" class="dropdown-item" href="Reporting_month_addendum.action?formId=1">ART Monthly Summary Addendum</a>
                    <!-- <a data-i18n="Reporting_month_patient.action?formId=2" class="dropdown-item" id="Reporting_month_patient.action?formId=2" href="Reporting_month_patient.action?formId=2">Performance Indicator</a> -->
                </div>
            </li>
          
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle text-center" href="#" id="navbarDropdownMenuLink"
                   data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <i class="now-ui-icons education_paper"></i>
                     Clinic Reports
                </a>
                <div class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
                    <a class="dropdown-item" id="Viral_load.action" href="Viral_load.action">Patients Due for Viral Load Test</a>
                    <a class="dropdown-item" id="Viralload_supressed" href="Viralload_supressed" target="_blank">Patient With Current Viral Load &lt; 1000copies/ml</a>
                    <a class="dropdown-item" id="Viralload_unsupressed" href="Viralload_unsupressed" target="_blank">Patient with Current Viral Load &ge; 1000copies/ml</a>
                    <a class="dropdown-item" id="" href="Cd4_due" target="_blank">Patients Due for CD4 Test</a>
                    <a class="dropdown-item" id="" href="Cd4_baseline" target="_blank">Patients with Current CD4 < baseline Value</a> 
                    <a data-i18n="Current_care" class="dropdown-item" id="" href="Current_care" target="_blank">Currently on Care(ART & Pre-ART)</a>
                    <a data-i18n="Current_treatment" class="dropdown-item" id="" href="Current_treatment" target="_blank">Currently On Treatment(ART)</a>
                    <a data-i18n="Lost_unconfirmed" class="dropdown-item" id="" href="Lost_unconfirmed_pepfar" target="_blank">Lost to Follow Up Unconfirmed (PEPFAR)</a>
                    <a data-i18n="Lost_unconfirmed" class="dropdown-item" id="" href="Lost_unconfirmed_gon" target="_blank">Lost to Follow Up Unconfirmed (GON)</a><a data-i18n="Appointment_option" class="dropdown-item" href="Appointment_option">Clinic & Refill Appointment/Visits</a>
                    <a data-i18n="Defaulter_refill" class="dropdown-item" href="Defaulter_refill" target="_blank">Defaulters for ARV Appoinment</a>
                    <a class="dropdown-item" id="Reporting_period_care.action?formId=1" href="Reporting_period_care.action?formId=1">Care and Support Monthly Summary Form</a>
                </div>
            </li>
            
            
            <li class="nav-item dropdown pull-right">
                <a class="nav-link dropdown-toggle text-center" href="#" id="navbarDropdownMenuLink"
                   data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <i class="now-ui-icons education_paper"></i>
                    <span data-i18n="reports"> Pharmacy Reports</span>
                </a>
                <div class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
                    <s:a id="firstline"  class="dropdown-item">Patients on First line Regimen</s:a>
                    <s:a id="secondline" class="dropdown-item">Patients on Second line Regimen</s:a>
                    <s:a id="thirdline" class="dropdown-item">Patients on Third Line Regimen (Salvage)</s:a>
                    <s:a id="regimensummary" class="dropdown-item">Patient Per Regimen Report</s:a>
                    <a data-i18n="Reporting_month_pharmacy.action?formId=1" class="dropdown-item" href="Reporting_month_pharmacy.action?formId=1">ART Monthly Summary Form</a>
                    <s:a id="devolvedsummary" class="dropdown-item">ART Refill Devolvement Report</s:a>
                </div>
            </li>
            <li class="nav-item dropdown pull-right">
                <a class="nav-link dropdown-toggle text-center" href="#" id="navbarDropdownMenuLink"
                   data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <i class="now-ui-icons education_paper"></i>
                    Laboratory Reports
                </a>
                <div class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
                    <a href="Lab_query_option.action?formId=1" class="dropdown-item">Laboratory Result Query</a>
                    <a id="cd4due" href="Lab_query_option.action" class="dropdown-item">Patients due for CD4 Count Test</a>
                    <a id="cd4baseline" href="Lab_query_option.action" class="dropdown-item">Patients with current CD4 Count &le; baseline value</a>
                    <a href="Viral_load.action" class="dropdown-item">Patients due for Viral Load Test</a>
                    <a id="viralloadsupressed" href="Lab_query_option.action" class="dropdown-item">Patients with current Viral Load &lt; 1000 copies/ml</a>
                    <a id="viralloadunsupressed" href="Lab_query_option.action" class="dropdown-item">Patients with current Viral Load &ge; 1000 copies/ml</a>
                    <a href="Reporting_month_lab.action?formId=1" class="dropdown-item">LAB Monthly Summary</a>
                </div>
            </li>
             
            <!-- <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle text-center" href="#" id="navbarDropdownMenuLink"
                   data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <i class="now-ui-icons education_paper"></i>
                    PMTCT Reports
                </a>
                <div class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
                    <a class="dropdown-item" id="cd4due" href="/Reporting_month_pmtct?formId=1">PMTCT Monthly Summary</a>
                    <a class="dropdown-item" id="cd4baseline" href="/Reporting_period_pmtct?formId=1">NIGERIA QUAL Indicators</a>
                </div>
            </li> -->
        </ul>
    </div>
</nav>
<div class="mt-5"></div>
<div class="content col-12 mr-auto ml-auto">