<nav class="navbar navbar-expand-md navbar-dark bg-white">
<!--    <a class="navbar-brand" href="#" id="dash-label">Case Management</a>-->
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
            <!-- <li class="nav-item">
                <a href="/Specimen_search.action" class="nav-link text-center">
                    <i class="now-ui-icons design_app"></i>
                    Clinic
                </a>
            </li> -->
            <!-- <li class="nav-item">
                <a href="/Status_search.action" class="nav-link text-center">
                    <i class="now-ui-icons business_badge"></i>
                    <span  data-i18n="drug-dispensing">Client Status Update</span>
                </a>
            </li> -->
            <li class="nav-item">
                <a href="Status_search" class="nav-link text-center">
                    <i class="now-ui-icons business_badge"></i>
                    <span  data-i18n="Status_search">Client Status Update</span>
                </a>
            </li>
<!--            <li class="nav-item">
                <a href="Client_tracker.action" class="nav-link text-center">
                    <i class="now-ui-icons business_globe"></i>
                    <span data-i18n="Client_tracker">Client Tracking</span>
                </a>
            </li>-->
            <li class="nav-item">
                <a href="Appointment_search.action" class="nav-link text-center">
                    <i class="now-ui-icons business_globe"></i>
                    <span data-i18n=Appointment_search">Appointment Scheduling</span>
                </a>
            </li>
            <li class="nav-item">
                <a href="Assign_client" class="nav-link text-center">
                    <i class="now-ui-icons business_badge"></i>
                    Assign Case Managers
                </a>
            </li>
            <li class="nav-item">
                <a href="Reassign_client" class="nav-link text-center">
                    <i class="now-ui-icons business_badge"></i>
                    Re-Assign Case Managers
                </a>
            </li>

            <li class="nav-item dropdown pull-right">
                <a class="nav-link dropdown-toggle text-center" href="#" id="navbarDropdownMenuLink"
                   data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <i class="now-ui-icons education_paper"></i>
                    Reports
                </a>
                <div class="dropdown-menu dropdown-menu-right" aria-labelledby="navbarDropdownMenuLink">
                    <a class="dropdown-item" href="Client_list.action">Case Managers Clients List</a>
                    <a class="dropdown-item" id="Unassigned_list" >List of Clients not assigned to a Case Manager</a>
                    <a class="dropdown-item" href="Client_appointment.action">Case Manager Client Appointment List</a>
                    <a class="dropdown-item" href="Client_defaulter.action">Case Managers Client Defaulters List</a>
                    <a class="dropdown-item" href="Client_cd4.action">Case Managers Clients Due for CD4 Count Test </a>
                    <a class="dropdown-item" href="Client_viral_load.action">Case Managers Clients due for Viral Load Test</a>
                </div>
            </li>
        </ul>
    </div>
</nav>
<div class="mt-5"></div>
<div class="content col-12 mr-auto ml-auto">