function generate() {
    $("#loader").html('<img id="loader_image" src="images/loader_small.gif" />');

    $(function (H) {
        H.wrap(H.Chart.prototype, 'showResetZoom', function (proceed) {
        });
    }(Highcharts));

    Highcharts.theme = {

        colors: ['#FF6600', '#2a5788', '#FFCC00', '#DDDF00', '#24CBE5', '#64E572',
            '#FF9655', '#FFF263', '#6AF9C4'],
        chart: {
            backgroundColor: {
                linearGradient: [0, 0, 500, 500],
                stops: [
                    [0, 'rgb(255, 255, 255)'],
                    [1, 'rgb(240, 240, 255)']
                ]
            },
        },
        title: {
            style: {
                color: '#000',
                font: 'bold 16px "Trebuchet MS", Verdana, sans-serif'
            }
        },
        subtitle: {
            style: {
                color: '#666666',
                font: 'bold 12px "Trebuchet MS", Verdana, sans-serif'
            }
        },

        legend: {
            itemStyle: {
                font: '9pt Trebuchet MS, Verdana, sans-serif',
                color: 'black'
            },
            itemHoverStyle: {
                color: 'gray'
            }
        }
    };

    // Apply the theme
    Highcharts.setOptions(Highcharts.theme);
    
    var params = "ipId=" + $("#ipId").val() + "&stateId=" + $("#stateId").val() + "&lgaId=" + $("#lgaId").val() + "&facilityId=" + $("#facilityId").val() + "&reportingDateBegin=" + $("#reportingDateBegin").val() + "&reportingDateEnd=" + $("#reportingDateEnd").val();

    var url = "TreatmentSummary_chart.action?" + params;
    $.getJSON(url, function (json) {
        var everEnrolled = json.indicator[0].value1;
        var currentArt = json.indicator[0].value2;
        var NewlyEnrolled = json.indicator[0].value3;

        $("#everEnrolled").html(everEnrolled);
        $("#currentArt").html(currentArt);
        $("#newlyEnrolled").html(NewlyEnrolled);
    });
   
    url = "NewlyEnrolled_chart.action?" + params;
    $.getJSON(url, function (json) {
        setChart1(json);
    });

    url = "CurrentOnArt_chart.action?" + params;
    $.getJSON(url, function (json) {
        setChart2(json);
    });
    
    url = "EligibleViralload_chart.action?" + params;
    $.getJSON(url, function (json) {
        setChart3(json);
    });

    url = "ViralSuppressed_chart.action?" + params;
    $.getJSON(url, function (json) {
        setChart4(json);
    });

    url = "CurrentOnArtDmoc_chart.action?"+params;
    $.getJSON(url, function (json) {
        setChart5(json);
    });

    url = "CurrentOnArtDmocType_chart.action?"+params;
    $.getJSON(url, function (json) {
        setChart6(json);
    });

    url = "CurrentOnArtTldRegimen_chart.action?" + params;
    $.getJSON(url, function (json) {
        setChart7(json);
    });

    url = "CurrentOnArtTldRegimenCategory_chart?" + params;
    $.getJSON(url, function (json) {
        setChart8(json);
        $("#loader").html('');
    });    
    

}

function setChart1(json) {
    var chart = {
        renderTo: 'container1',
        type: 'column',
        margin: 75,
        zoomType: 'x',
        panning: true,
        panKey: 'shift',
        options3d: {
            enabled: true,
        }
    };
    var title = {
        text: json.title
    };
    var subtitle = {
        text: json.subtitle
    };
    
    var xAxis = {
        categories: json.categories
    };
    var yAxis = {
        min: 0,
        title: {
            text: json.titleForYAxis
        },
    };
    var tooltip = {
        headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
        pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' + '<td style="padding:0"><b>{point.y} </b></td></tr>',
        footerFormat: '</table>',
        shared: true,
        useHTML: true
    };
    
    var plotOptions = {
        column: {
            depth: 25,
            pointPadding: 0.2,
            borderWidth: 0
        }
    };

    var series = json.series;

    var json = {};
    json.chart = chart;
    json.title = title;
    json.subtitle = subtitle;
    json.xAxis = xAxis;
    json.yAxis = yAxis;
    json.tooltip = tooltip;
    json.series = series;
    json.plotOptions = plotOptions;
    var highchart = new Highcharts.Chart(json);

    function showValues() {
        $('#R10-value').html(highchart.options.chart.options3d.alpha);
        $('#R11-value').html(highchart.options.chart.options3d.beta);
    }

    // Activate the sliders
    $('#R10').on('change', function () {
        highchart.options.chart.options3d.alpha = this.value;
        showValues();
        highchart.redraw(false);
    });
    $('#R11').on('change', function () {
        highchart.options.chart.options3d.beta = this.value;
        showValues();
        highchart.redraw(false);
    });
    showValues();
}


function setChart2(json) {
    var chart = {
        renderTo: 'container2',
        type: 'column',
        margin: 75,
        zoomType: 'x',
        panning: true,
        panKey: 'shift',
      
    };
    var title = {
        text: json.title
    };
    var subtitle = {
        text: json.subtitle
    };
    var xAxis = {
        categories: json.categories
    };
    var yAxis = {
        min: 0,
        title: {
            text: json.titleForYAxis
        },
    };
    var tooltip = {
        headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
        pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' + '<td style="padding:0"><b>{point.y} </b></td></tr>',
        footerFormat: '</table>',
        shared: true,
        useHTML: true
    };
    var plotOptions = {
        column: {
            pointPadding: 0.2,
            borderWidth: 0
        }
    };

    var series = json.series;

    var json = {};
    json.chart = chart;
    json.title = title;
    json.subtitle = subtitle;
    json.xAxis = xAxis;
    json.yAxis = yAxis;
    json.tooltip = tooltip;
    json.series = series;
    json.plotOptions = plotOptions;
    var highchart = new Highcharts.Chart(json);

    function showValues() {
        $('#R10-value').html(highchart.options.chart.options3d.alpha);
        $('#R11-value').html(highchart.options.chart.options3d.beta);
    }

    // Activate the sliders
    $('#R10').on('change', function () {
        highchart.options.chart.options3d.alpha = this.value;
        showValues();
        highchart.redraw(false);
    });
    $('#R11').on('change', function () {
        highchart.options.chart.options3d.beta = this.value;
        showValues();
        highchart.redraw(false);
    });
    showValues();
}

function setChart3(json) {
    console.log(json);
    var v1 = json.series[0].data;
    var v2 = json.series[1].data;
    var v3 = json.series[2].data;


    var chart = {
        renderTo: 'container3',
        type: 'column',
        margin: 75,
        zoomType: 'x',
        panning: true,
        panKey: 'shift',
        options3d: {
            enabled: true,
                    }
    };
    var title = {
        text: json.title
    };
    var subtitle = {
        text: json.subtitle
    };
    var plotOptions = {
        column: {
            depth: 25
        }
    };

    var xAxis = {
        categories: json.categories
    };
    var yAxis = {
        min: 0,
        title: {
            text: json.titleForYAxis
        },
    };
    var tooltip = {
        headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
        pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' + '<td style="padding:0"><b>{point.y} </b></td></tr>',
        footerFormat: '</table>',
        shared: true,
        useHTML: true
    };
    var series = [{
            type: 'column',
            name: json.series[0].name,
            data: v1
        }, {
            type: 'column',
            name: json.series[1].name,
            data: v2
        }, {
            type: 'spline',
            name: json.series[2].name,
            data: v3
        },
        {
        plotOptions: {
           pie: {
               allowPointSelect: true,
               cursor: 'pointer',
               depth:35,
               dataLabels: {
                   enabled: true,
                   format: '{pont.name} <b>{point.percentage: .if}%</b>'
               }
           }  
        },
        data: [{
            name: 'Current On ART',
            y: 13,
            color: Highcharts.getOptions().colors[0] // Jane's color
        }, {
            name: 'DMOC',
            y: 23,
            color: Highcharts.getOptions().colors[1] // John's color
        }],
        center: [720, 90],
        size: 120,
        showInLegend: false,
        dataLabels: {
            enabled: false
        }
    }]


    var json = {};
    json.chart = chart;
    json.title = title;
    json.subtitle = subtitle;
    json.xAxis = xAxis;
    json.yAxis = yAxis;
    json.tooltip = tooltip;
    json.series = series;
    json.plotOptions = plotOptions;
    var highchart = new Highcharts.Chart(json);

    function showValues() {
        $('#R24-value').html(highchart.options.chart.options3d.alpha);
        $('#R25-value').html(highchart.options.chart.options3d.beta);
    }

    // Activate the sliders
    $('#R24').on('change', function () {
        highchart.options.chart.options3d.alpha = this.value;
        showValues();
        highchart.redraw(false);
    });
    $('#R25').on('change', function () {
        highchart.options.chart.options3d.beta = this.value;
        showValues();
        highchart.redraw(false);
    });
    showValues();
}


function setChart4(json) {
    console.log(json);

    var chart = {
        renderTo: 'container4',
        type: 'column',
        margin: 75,
        zoomType: 'x',
        panning: true,
        panKey: 'shift',
        options3d: {
            enabled: true,
        }
    };
    var title = {
        text: json.title
    };
    var subtitle = {
        text: json.subtitle
    };
    var plotOptions = {
        column: {
            depth: 25
        }
    };

    var xAxis = {
        categories: json.categories
    };
    var yAxis = {
        min: 0,
        title: {
            text: json.titleForYAxis
        },
    };
    var tooltip = {
        headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
        pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' + '<td style="padding:0"><b>{point.y} </b></td></tr>',
        footerFormat: '</table>',
        shared: true,
        useHTML: true
    };
    
    var series = json.series;
//    var series = [{
//            type: 'column',
//            name: json.series[0].name,
//            data: v1
//        }, {
//            type: 'column',
//            name: json.series[1].name,
//            data: v2
//        }, {
//            type: 'spline',
//            name: json.series[2].name,
//            data: v3
//        }];


    var json = {};
    json.chart = chart;
    json.title = title;
    json.subtitle = subtitle;
    json.xAxis = xAxis;
    json.yAxis = yAxis;
    json.tooltip = tooltip;
    json.series = series;
    json.plotOptions = plotOptions;
    var highchart = new Highcharts.Chart(json);

    function showValues() {
        $('#R2-value').html(highchart.options.chart.options3d.alpha);
        $('#R3-value').html(highchart.options.chart.options3d.beta);
    }

    // Activate the sliders
    $('#R2').on('change', function () {
        highchart.options.chart.options3d.alpha = this.value;
        showValues();
        highchart.redraw(false);
    });
    $('#R3').on('change', function () {
        highchart.options.chart.options3d.beta = this.value;
        showValues();
        highchart.redraw(false);
    });
    showValues();
}


function setChart5(json) {
    console.log(json);
    var v1 = json.series[0].data;
    var v2 = json.series[1].data;
    var v3 = json.series[2].data;


    var chart = {
        renderTo: 'container5',
        type: 'column',
        margin: 75,
        zoomType: 'x',
        panning: true,
        panKey: 'shift',
        options3d: {
            enabled: true,
        }
    };
    var title = {
        text: json.title
    };
    var subtitle = {
        text: json.subtitle
    };
    var plotOptions = {
        column: {
            depth: 25
        }
    };

    var xAxis = {
        categories: json.categories
    };
    var yAxis = {
        min: 0,
        title: {
            text: json.titleForYAxis
        },
    };
    var tooltip = {
        headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
        pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' + '<td style="padding:0"><b>{point.y} </b></td></tr>',
        footerFormat: '</table>',
        shared: true,
        useHTML: true
    };
    var series = [{
            type: 'column',
            name: json.series[0].name,
            data: v1
        }, {
            type: 'column',
            name: json.series[1].name,
            data: v2
        }, {
            type: 'spline',
            name: json.series[2].name,
            data: v3
        },
        {
        plotOptions: {
           pie: {
               allowPointSelect: true,
               cursor: 'pointer',
               depth:35,
               dataLabels: {
                   enabled: true,
                   format: '{pont.name} <b>{point.percentage: .if}%</b>'
               }
           }  
        },
        data: [{
            name: 'Current On ART',
            y: 13,
            color: Highcharts.getOptions().colors[0] // Jane's color
        }, {
            name: 'DMOC',
            y: 23,
            color: Highcharts.getOptions().colors[1] // John's color
        }],
        center: [720, 90],
        size: 120,
        showInLegend: false,
        dataLabels: {
            enabled: false
        }
    }]


    var json = {};
    json.chart = chart;
    json.title = title;
    json.subtitle = subtitle;
    json.xAxis = xAxis;
    json.yAxis = yAxis;
    json.tooltip = tooltip;
    json.series = series;
    json.plotOptions = plotOptions;
    var highchart = new Highcharts.Chart(json);

    function showValues() {
        $('#R24-value').html(highchart.options.chart.options3d.alpha);
        $('#R25-value').html(highchart.options.chart.options3d.beta);
    }

    // Activate the sliders
    $('#R24').on('change', function () {
        highchart.options.chart.options3d.alpha = this.value;
        showValues();
        highchart.redraw(false);
    });
    $('#R25').on('change', function () {
        highchart.options.chart.options3d.beta = this.value;
        showValues();
        highchart.redraw(false);
    });
    showValues();
}


function setChart6(json) {
    var chart = {
       renderTo: 'container6',
       plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false,
        margin: 75,
        zoomType: 'x',
        panning: true,
        panKey: 'shift',
        options3d: {
            enabled: true,
        }
    };
    var title = {
        text: json.title
    };
    var subtitle = {
        text: json.subtitle
    };
    var plotOptions = {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    color: '#000000',
                    connectorColor: '#000000',
                    formatter: function() {
                        return this.point.name + ':<b>' + Highcharts.numberFormat(this.percentage, 1) + '%</b>';
                    }
                }
            }
        };

    var xAxis = {
        categories: json.categories
    };
    var yAxis = {
        min: 0,
        title: {
            text: json.titleForYAxis
        },
    };
    var tooltip =  {
               formatter: function() {
                   return this.point.name + ':<b>' + Highcharts.numberFormat(this.percentage, 1) + '%</b>';                
               }
            };
            
    var series = json.series;


    var json = {};
    json.chart = chart;
    json.title = title;
    json.subtitle = subtitle;
    json.xAxis = xAxis;
    json.yAxis = yAxis;
    json.tooltip = tooltip;
    json.series = series;
    json.plotOptions = plotOptions;
    var highchart = new Highcharts.Chart(json);

    function showValues() {
        $('#R26-value').html(highchart.options.chart.options3d.alpha);
        $('#R27-value').html(highchart.options.chart.options3d.beta);
    }

    // Activate the sliders
    $('#R26').on('change', function () {
        highchart.options.chart.options3d.alpha = this.value;
        showValues();
        highchart.redraw(false);
    });
    $('#R27').on('change', function () {
        highchart.options.chart.options3d.beta = this.value;
        showValues();
        highchart.redraw(false);
    });
    showValues();
    }



function setChart7(json) {
    var chart = {
       renderTo: 'container7',
       plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false,
        margin: 75,
        zoomType: 'x',
        panning: true,
        panKey: 'shift',
        options3d: {
            enabled: true,
        }
    };
    var title = {
        text: json.title
    };
    var subtitle = {
        text: json.subtitle
    };
    var plotOptions = {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    color: '#000000',
                    connectorColor: '#000000',
                    formatter: function() {
                        return this.point.name + ':<b>' + Highcharts.numberFormat(this.percentage, 1) + '%</b>';
                    }
                }
            }
        };

    var xAxis = {
        categories: json.categories
    };
    var yAxis = {
        min: 0,
        title: {
            text: json.titleForYAxis
        },
    };
    var tooltip =  {
               formatter: function() {
                   return this.point.name + ':<b>' + Highcharts.numberFormat(this.percentage, 1) + '%</b>';                
               }
            };
            
    var series = json.series;


    var json = {};
    json.chart = chart;
    json.title = title;
    json.subtitle = subtitle;
    json.xAxis = xAxis;
    json.yAxis = yAxis;
    json.tooltip = tooltip;
    json.series = series;
    json.plotOptions = plotOptions;
    var highchart = new Highcharts.Chart(json);

    function showValues() {
        $('#R26-value').html(highchart.options.chart.options3d.alpha);
        $('#R27-value').html(highchart.options.chart.options3d.beta);
    }

    // Activate the sliders
    $('#R26').on('change', function () {
        highchart.options.chart.options3d.alpha = this.value;
        showValues();
        highchart.redraw(false);
    });
    $('#R27').on('change', function () {
        highchart.options.chart.options3d.beta = this.value;
        showValues();
        highchart.redraw(false);
    });
    showValues();
    }


function setChart8(json) {
    console.log(json);
    
    var chart = {
        renderTo: 'container8',
        type: 'bar',
        margin: 75,
        zoomType: 'x',
        panning: true,
        panKey: 'shift',
        options3d: {
            enabled: true,
        }
    };
    var title = {
        text: json.title
    };
    var subtitle = {
        text: json.subtitle
    };
    var xAxis = [{
            categories: json.categories,
            reversed: false,
            labels: {
                step: 1
            }
        }, {// mirror axis on right side
            opposite: true,
            reversed: false,
            categories: json.categories,
            linkedTo: 0,
            labels: {
                step: 1
            }
        }];
    var yAxis = {
        title: {
            text: null
        },
        labels: {
            formatter: function () {
                return Math.abs(this.value) + '%';
            }
        }
    };
    var tooltip = {
        formatter: function () {
            return '<b>' + this.series.name + ', age ' + this.point.category + '</b><br/>' +
                    'Population: ' + Highcharts.numberFormat(Math.abs(this.point.y), 0);
        }
    };
    var plotOptions = {
        series: {
            stacking: 'normal'
        }
    };

    var series = json.series;

    var json = {};
    json.chart = chart;
    json.title = title;
    json.subtitle = subtitle;
    json.xAxis = xAxis;
    json.yAxis = yAxis;
    json.tooltip = tooltip;
    json.series = series;
    json.plotOptions = plotOptions;
    var highchart = new Highcharts.Chart(json);

    function showValues() {
        $('#R22-value').html(highchart.options.chart.options3d.alpha);
        $('#R23-value').html(highchart.options.chart.options3d.beta);
    }

    // Activate the sliders
    $('#R22').on('change', function () {
        highchart.options.chart.options3d.alpha = this.value;
        showValues();
        highchart.redraw(false);
    });
    $('#R23').on('change', function () {
        highchart.options.chart.options3d.beta = this.value;
        showValues();
        highchart.redraw(false);
    });
    showValues();

}


//Donut chart
function setChart_1(json) {
    var chart = {
       renderTo: 'container7',
       plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false,
        margin: 75,
        zoomType: 'x',
        panning: true,
        panKey: 'shift',
        options3d: {
            enabled: true,
            alpha: 15,
            beta: 15,
            depth: 50,
            viewDistance: 25
        }
    };
    var title = {
        text: json.title
    };
    var subtitle = {
        text: json.subtitle
    };
    var plotOptions = {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    color: '#000000',
                    connectorColor: '#000000',
                    formatter: function() {
                        return this.point.name + ':<b>' + Highcharts.numberFormat(this.percentage, 1) + '%</b>';
                    }
                }
            }
        };

    var xAxis = {
        categories: json.categories
    };
    var yAxis = {
        min: 0,
        title: {
            text: json.titleForYAxis
        },
    };
    var tooltip =  {
               formatter: function() {
                   return this.point.name + ':<b>' + Highcharts.numberFormat(this.percentage, 1) + '%</b>';                
               }
            };
            
    var series = json.series;


    var json = {};
    json.chart = chart;
    json.title = title;
    json.subtitle = subtitle;
    json.xAxis = xAxis;
    json.yAxis = yAxis;
    json.tooltip = tooltip;
    json.series = series;
    json.plotOptions = plotOptions;
    var highchart = new Highcharts.Chart(json);

    function showValues() {
        $('#R26-value').html(highchart.options.chart.options3d.alpha);
        $('#R27-value').html(highchart.options.chart.options3d.beta);
    }

    // Activate the sliders
    $('#R26').on('change', function () {
        highchart.options.chart.options3d.alpha = this.value;
        showValues();
        highchart.redraw(false);
    });
    $('#R27').on('change', function () {
        highchart.options.chart.options3d.beta = this.value;
        showValues();
        highchart.redraw(false);
    });
    showValues();
    }

//Donut chart
function setChart_2(json) {
    var chart = {
       renderTo: 'container8',
       plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false,
        margin: 75,
        zoomType: 'x',
        panning: true,
        panKey: 'shift',
        options3d: {
            enabled: true,
            alpha: 15,
            beta: 15,
            depth: 50,
            viewDistance: 25
        }
    };
    var title = {
        text: json.title
    };
    var subtitle = {
        text: json.subtitle
    };
    var plotOptions = {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    color: '#000000',
                    connectorColor: '#000000',
                    formatter: function() {
                        return this.point.name + ':<b>' + Highcharts.numberFormat(this.percentage, 1) + '%</b>';
                    }
                }
            }
        };

    var xAxis = {
        categories: json.categories
    };
    var yAxis = {
        min: 0,
        title: {
            text: json.titleForYAxis
        },
    };
    var tooltip =  {
               formatter: function() {
                   return this.point.name + ':<b>' + Highcharts.numberFormat(this.percentage, 1) + '%</b>';                
               }
            };
            
    var series = json.series;


    var json = {};
    json.chart = chart;
    json.title = title;
    json.subtitle = subtitle;
    json.xAxis = xAxis;
    json.yAxis = yAxis;
    json.tooltip = tooltip;
    json.series = series;
    json.plotOptions = plotOptions;
    var highchart = new Highcharts.Chart(json);

    function showValues() {
        $('#R26-value').html(highchart.options.chart.options3d.alpha);
        $('#R27-value').html(highchart.options.chart.options3d.beta);
    }

    // Activate the sliders
    $('#R26').on('change', function () {
        highchart.options.chart.options3d.alpha = this.value;
        showValues();
        highchart.redraw(false);
    });
    $('#R27').on('change', function () {
        highchart.options.chart.options3d.beta = this.value;
        showValues();
        highchart.redraw(false);
    });
    showValues();
    }


//Disggregated by sex and age
function setChart13(json) {
    console.log(json);
    
    var chart = {
        renderTo: 'container13',
        type: 'bar',
        margin: 75,
        zoomType: 'x',
        panning: true,
        panKey: 'shift',
        options3d: {
            enabled: true,
                   }
    };
    var title = {
        text: json.title
    };
    var subtitle = {
        text: json.subtitle
    };
    var xAxis = [{
            categories: json.categories,
            reversed: false,
            labels: {
                step: 1
            }
        }, {// mirror axis on right side
            opposite: true,
            reversed: false,
            categories: json.categories,
            linkedTo: 0,
            labels: {
                step: 1
            }
        }];
    var yAxis = {
        title: {
            text: null
        },
        labels: {
            formatter: function () {
                return Math.abs(this.value) + '%';
            }
        }
    };
    var tooltip = {
        formatter: function () {
            return '<b>' + this.series.name + ', age ' + this.point.category + '</b><br/>' +
                    'Population: ' + Highcharts.numberFormat(Math.abs(this.point.y), 0);
        }
    };
    var plotOptions = {
        series: {
            stacking: 'normal'
        }
    };

    var series = json.series;

    var json = {};
    json.chart = chart;
    json.title = title;
    json.subtitle = subtitle;
    json.xAxis = xAxis;
    json.yAxis = yAxis;
    json.tooltip = tooltip;
    json.series = series;
    json.plotOptions = plotOptions;
    var highchart = new Highcharts.Chart(json);

    function showValues() {
        $('#R12-value').html(highchart.options.chart.options3d.alpha);
        $('#R13-value').html(highchart.options.chart.options3d.beta);
    }

    // Activate the sliders
    $('#R12').on('change', function () {
        highchart.options.chart.options3d.alpha = this.value;
        showValues();
        highchart.redraw(false);
    });
    $('#R13').on('change', function () {
        highchart.options.chart.options3d.beta = this.value;
        showValues();
        highchart.redraw(false);
    });
    showValues();

}

//            $('#container14').highcharts({
//            chart: {
//                plotBackgroundColor: null,
//                plotBorderWidth: null,
//                plotShadow: false
//            },
//            title: {
//                text: json.title
//            },
//            tooltip: {
//               formatter: function() {
//                   return this.point.name + ':<b>' + Highcharts.numberFormat(this.percentage, 1) + '%</b>';                
//               }
//            },
//            plotOptions: {
//                pie: {
//                    allowPointSelect: true,
//                    cursor: 'pointer',
//                    dataLabels: {
//                        enabled: true,
//                        color: '#000000',
//                        connectorColor: '#000000',
//                        formatter: function() {
//                            return this.point.name + ':<b>' + Highcharts.numberFormat(this.percentage, 1) + '%</b>';
//                        }
//                    }
//                }
//            },
//            series: json.series
//        });


//    $('#container5').highcharts({
//        chart: {
//            type: 'column'
//        },
//        title: {
//            text: json.title
//        },
//        subtitle: {
//            text: json.subtitle
//        },
//        xAxis: {
//            categories: json.categories
//        },
//        yAxis: {
//            min: 0,
//            title: {
//                text: json.titleForYAxis
//            }
//        },
//        tooltip: {
//            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
//            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' + '<td style="padding:0"><b>{point.y} </b></td></tr>',
//            footerFormat: '</table>',
//            shared: true,
//            useHTML: true
//        },
//        plotOptions: {
//            column: {
//                pointPadding: 0.2,
//                borderWidth: 0
//            }
//        },
//        series: [{
//            type: 'column',
//            name: 'Viral Load Result',
//            data: json.series
//          },{
//                type: 'spline',
//                name: 'Virally Suppressed',
//                data: result,
//                marker: {
//                    lineWidth: 2,
//                    lineColor: Highcharts.getOptions().colors[3],
//                    fillColor: 'white'
//                }
//            },
//        ]
//    });
