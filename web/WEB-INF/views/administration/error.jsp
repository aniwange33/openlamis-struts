<%-- 
    Document   : Patient
    Created on : Feb 8, 2012, 1:15:46 PM
    Author     : AALOZIE
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>LAMIS 3.0</title>
        <jsp:include page="/WEB-INF/views/template/css.jsp" /> 
        <jsp:include page="/WEB-INF/views/template/javascript.jsp" /> 
        <script type="text/javascript" src="js/lamis/lamis-common.js"></script>               
        <script type="text/javascript" src="js/lamis/report-common.js"></script>                    
        <script type="text/JavaScript">
            $(document).ready(function(){
            resetPage();
            reports();
            })
        </script>
    </head>

    <body>
        <div id="preloader"></div>
        <div class="wrapper">
            <div class="main-panel">
                <!-- Navbar -->

                <nav class="navbar navbar-expand-lg sticky-top" style="background: #2a5788">
                    <a class="navbar-brand" href="#">
                        <image src="assets/img/lamis_logo.png" />
                    </a>

                    <button class="navbar-toggler text-white" type="button" data-toggle="collapse" data-target="#navigation"
                            aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                        <span class="navbar-toggler-bar navbar-kebab"></span>
                        <span class="navbar-toggler-bar navbar-kebab"></span>
                        <span class="navbar-toggler-bar navbar-kebab"></span>
                    </button>
                    <div class="collapse navbar-collapse justify-content-end" id="navigation">
                        <ul class="navbar-nav">
                            <li class="nav-item">
                                <a class="nav-link" href="Home_page">
                                    <p>
                                        <span class="d-md-block">Home</span>
                                    </p>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="#">
                                    <p>
                                        <span class="d-md-block">Welcome, </span>
                                    </p>
                                </a>
                            </li>
                            <li class="nav-item dropdown switch-locale">
                                <a class="nav-link dropdown-toggle switch-locale" href="#" data-toggle="dropdown"
                                   aria-haspopup="true" aria-expanded="false">
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
                            <!--                    <li class="nav-item dropdown switch-locale">
                                                    <a class="nav-link dropdown-toggle switch-locale" href="#" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                                        Language</a>
                                                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdown09">
                                                        <a class="dropdown-item" data-locale="en"><span class="flag-icon flag-icon-it"> </span>  English</a>
                                                        <a class="dropdown-item" data-locale="fr"><span class="flag-icon flag-icon-fr"> </span>  French</a>
                                                    </div>
                                                </li>-->
                        </ul>
                    </div>
                </nav>
                <ol class="breadcrumb">
                    <li class="breadcrumb-item"><a href="Home_page">Home</a></li>
                    <li class="breadcrumb-item active">Error</li>
                </ol>
                <!-- MAIN CONTENT -->
                <form id="lamisform" theme="css_xhtml">
                    <div class="row">
                        <div class="col-md-8 ml-auto mr-auto">
                            <div class="card" style="height:50rem;">
                                <div class="card-body">
                                    <div id="errormessage"><span style="font-family:arial; margin-left: 200px; font-size:20px; color:red">Access Denied, Contact Your Administrator</span></div>                                    
                                </div>
                            </div>
                        </div>
                </form>
                <!-- END MAIN CONTENT-->
            </div>
            <!-- END CONTENT-->
            <div class="card-footer" style="background-color:#666666;">
                <footer class="footer mt-0 mb-0 pt-0 pb-0 text-white text-center">
                    <div class="row">
                        <div class="col-md-1">
                            <a href="#" class="text-white" id="footerMessage2">LAMIS 3.0</a>
                        </div>
                        <div class="col-md-1">
                            <a href="#" class="text-white">About Us</a>
                        </div>
                        <div class="col-md-8">
                            <div id="footerMessage"> </div> 
                        </div>
                        <div class="col-md-2 copyright text-white">
                            &copy; <span id="copyright"></span>
                        </div>
                    </div>


                    <!-- <nav>
                        <ul>
                            <li>
                                <a href="#">
                                    LAMIS
                                </a>
                            </li>
                            <li>
                                <a href="#">
                                    About Us
                                </a>
                            </li>
                            <li> <a href="#" id="footerMessage"><s:property value="#session.facilityName"/> Database </a></li> 
                             <li> <a href="#" 
                        </ul>
                    </nav> -->


                </footer>
                <!-- END FOOTER -->
            </div>
            <!-- END CARD FOOTER -->
        </div>
        <!-- END MAIN MENU -->
    </div>
    <!-- END WRAPPER -->

    <div id="user_group1" style="display: none"></div>     
</body>
</html>
