
<%@ taglib prefix="s" uri="/struts-tags" %>
<div id="preloader"></div>
<div class="wrapper">
    <div class="main-panel">
        <nav class="navbar topbar sticky-top navbar-expand-lg col-12" style="background: #2a5788;">
            <a class="navbar-brand" href="#">
                <image src="assets/img/lamis_logo.png"/>
            </a>

            <button class="navbar-toggler text-white" type="button" data-toggle="collapse" data-target="#navigation" aria-controls="navbarSupportedContent" 
                    aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-bar navbar-kebab"></span>
                <span class="navbar-toggler-bar navbar-kebab"></span>
                <span class="navbar-toggler-bar navbar-kebab"></span>
            </button>
            <div class="collapse navbar-collapse justify-content-end" id="navigation">
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <a class="nav-link" href="Home_page"> Home</a>
                    </li>
                    <li class="nav-item dropdown switch-locale">
                        <a class="nav-link dropdown-toggle switch-locale" href="#" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            Visualizer</a>
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdown09">
                            <a class="dropdown-item" href="Treatment_dashboard">Treatment Dashboard</a>
                            <a class="dropdown-item" href="Hts_dashboard">HTS Dashboard</a>
                            <a class="dropdown-item" href="#">PMTCT Dashboard</a>
                        </div>
                    </li>
                    <li class="nav-item dropdown switch-locale">
                        <a class="nav-link dropdown-toggle switch-locale" href="#" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            Events Monitor</a>
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdown09">
                            <a class="dropdown-item" href="Event_pharmacy_page">Community Pharmacy Events</a>
                            <a class="dropdown-item" href="Analyzer_page">Data Synchronization Events</a>
                            <a class="dropdown-item" href="Validate_page">Data Profiling and Validation</a>
                            <a class="dropdown-item" href="Retention_tracker_page">Retention Tracker Report</a>
                            <a class="dropdown-item" href="Treatment_tracker_page">Treatment Tracker Report</a>
                        </div>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#">
                            <p>
                                <span class="d-md-block">Welcome, <s:property value="#session.fullname"/></span>
                            </p>
                        </a>
                    </li>
                    <li class="nav-item dropdown switch-locale">
                        <a class="nav-link dropdown-toggle switch-locale" href="#" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            Help</a>
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdown09">
                            <a class="dropdown-item" data-locale="en">LAMIS User Manual </a>
                            <a class="dropdown-item" data-locale="fr">About LAMIS</a>
                        </div>
                    </li>
                    <li class="nav-item dropdown pull-right">
                        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdownMenuLink" data-toggle="dropdown"
                           aria-haspopup="true" aria-expanded="false">
                            <i class="now-ui-icons users_single-02"></i>
                            <p>
                                <span class="d-lg-none d-md-block">Admin</span>
                            </p>
                        </a>
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="navbarDropdownMenuLink">
                            <a class="dropdown-item" href="#">Change Password</a>
                            <a class="dropdown-item" href="User_logout">Logout</a>
                        </div>
                    </li>
                </ul>
            </div>
        </nav>
