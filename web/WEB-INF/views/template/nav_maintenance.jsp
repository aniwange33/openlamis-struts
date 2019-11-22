<style>
    .wrapper2 {
        position:relative;
        margin:0 auto;
        overflow:hidden;
        padding:5px;
        height:50px;
    }

    .list {
        position:absolute;
        left:0px;
        top:0px;
        min-width:3000px;
        margin-left:12px;
        margin-top:0px;
    }

    .list li{
        display:table-cell;
        position:relative;
        text-align:center;
        cursor:grab;
        cursor:-webkit-grab;
        color:#efefef;
        vertical-align:middle;
    }

    .scroller {
        text-align:center;
        cursor:pointer;
        display:none;
        padding:7px;
        padding-top:11px;
        white-space:no-wrap;
        vertical-align:middle;
        background-color:#fff;
    }

    .scroller-right{
        float:right;
    }

    .scroller-left {
        float:left;
    }
</style>
<nav class="col-md-12 navbar navbar-expand-md bg-white">
    <button class="navbar-toggler text-white" type="button"
            data-toggle="collapse" data-target="#navbarText"
            aria-controls="navbarSupportedContent" aria-expanded="false"
            aria-label="Toggle navigation">
        <span class="navbar-toggler-bar navbar-kebab"></span> 
        <span class="navbar-toggler-bar navbar-kebab"></span> 
        <span class="navbar-toggler-bar navbar-kebab"></span>
    </button>
    <div class="scroller scroller-left"><i class="fa fa-chevron-left"></i></div>
    <div class="wrapper2 collapse navbar-collapse" id="navbarText">
        <ul class="navbar-nav list">
            <li class="nav-item">
                <a class="nav-link" href="Export_page">Export Data</a> 
            </li>
            <li class="nav-item">
                <a class="nav-link" href="Import_page">Import Data</a> 
            </li>
            <li class="nav-item">
                <a class="nav-link" href="Upload_page">Upload Data to the Server</a> 
            </li>
            <li class="nav-item">
                <a class="nav-link" href="Sync_page">Download &amp; Synchronize Data</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="Updates_page">Check &amp; Download Updates</a> 
            </li>
            <li class="nav-item">
                <a class="nav-link" href="Radet_page">RADET Analyzer &amp; Data Update</a> 
            </li>
            <li class="nav-item">
                <a class="nav-link" href="Dqa_page">Data Quality Analyzer</a> 
            </li>
            <li class="nav-item">
                <a class="nav-link" href="Cleanup_page">Cleanup Database Records</a> 
            </li>
            <li class="nav-item">
                <a class="nav-link" href="Deduplicator_page">Remove Duplicate Numbers</a> 
            </li>
            <li class="nav-item">
                <a class="nav-link" href="Facility_switch">Switch Facility</a> 
            </li>
        </ul>
    </div>
    <div class="scroller scroller-right"><i class="fa fa-chevron-right"></i></div>
</nav>
<script>
    var hidWidth;
    var scrollBarWidths = 40;

    var widthOfList = function () {
        var itemsWidth = 0;
        $('.list li').each(function () {
            var itemWidth = $(this).outerWidth();
            itemsWidth += itemWidth;
        });
        return itemsWidth;
    };

    var widthOfHidden = function () {
        return $('.wrapper2').outerWidth() - widthOfList() - getLeftPosi() - scrollBarWidths;
    };

    var getLeftPosi = function () {
        return $('.list').position().left;
    };

    var reAdjust = function () {
        if ($('.wrapper2').outerWidth() < widthOfList()) {
            $('.scroller-right').show();
        } else
        {
            $('.scroller-right').hide();
        }

        if (getLeftPosi() < 0) {
            $('.scroller-left').show();
        } else
        {
            $('.item').animate({left: "-=" + getLeftPosi() + "px"}, 'slow');
            $('.scroller-left').hide();
        }
    };

    reAdjust();

    $(window).on('resize', function (e) {
        reAdjust();
    });

    $('.scroller-right').click(function () {

        $('.scroller-left').fadeIn('slow');
        $('.scroller-right').fadeOut('slow');

        $('.list').animate({left: "+=" + widthOfHidden() + "px"}, 'slow', function () {

        });
    });

    $('.scroller-left').click(function () {

        $('.scroller-right').fadeIn('slow');
        $('.scroller-left').fadeOut('slow');

        $('.list').animate({left: "-=" + getLeftPosi() + "px"}, 'slow', function () {

        });
    });
</script>

<div class="mt-5"></div>
<div class="content col-12 mr-auto ml-auto">