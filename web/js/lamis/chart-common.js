/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


function getClinicalTrendChart() {
 ///$("#loader").html('<img id="loader_image" src="images/loader_small.gif" />');
   
    var data = "patientId=" + $("#patientId").val() ; //+ "&dataType=" + $("#dataType").val();
  
    var url = "ClinicalTrend_chart.action?" + data;
    $.getJSON(url, function (json) {

        //$("#container").css({height: '100%', width: '100%', position: "absolute", margin: '0 auto'});
        setClinicalTrend(json);
        //$("#loader").html('');
        //$('#loader-container').hide();
    });
 }
    

function setChart12(json) {
          var chart = {
              renderTo: 'container14',
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
        }
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
    
  function patientTrend() {
       var myChart = Highcharts.chart('container', {
    title: {
        text: 'Patient Trend Analysis'
    },

    subtitle: {
        text: 'Patients Vitals'
    },

    yAxis: {
        title: {
            text: 'Parameters'
        }
    },
    legend: {
        layout: 'vertical',
        align: 'right',
        verticalAlign: 'middle'
    },

    plotOptions: {
        series: {
            label: {
                connectorAllowed: false
            },
            pointStart: 2010
        }
    },

    series: [{
        name: 'Body Weight',
        data: [43934, 52503, 57177, 69658, 97031, 119931, 137133, 154175]
    }, {
        name: 'BMI/Height',
        data: [24916, 24064, 29742, 29851, 32490, 30282, 38121, 40434]
    }, {
        name: 'Blood Pressure',
        data: [11744, 17722, 16005, 19771, 20185, 24377, 32147, 39387]
    }, {
        name: 'Viral Load',
        data: [null, null, 7988, 12169, 15112, 22452, 34400, 34227]
    }, {
        name: 'WBC',
        data: [12908, 5948, 8105, 11248, 8989, 11816, 18274, 18111]
         }, 
    {
        name: 'Cretinine',
        data: [12908, 5948, 8105, 11248, 8989, 11816, 18274, 18111]
    }],

    responsive: {
        rules: [{
            condition: {
                maxWidth: 500
            },
            chartOptions: {
                legend: {
                    layout: 'horizontal',
                    align: 'center',
                    verticalAlign: 'bottom'
                }
            }
        }]
    }

});   
        
 }
 

function setClinicalTrend(json) {
            $('#container').highcharts({
            chart: {
            type: 'spline'
            },
            title: {
            text: json.title
            },
            subtitle: {
            text: json.subtitle
            },
            xAxis: {
            categories: json.categories
            },
            yAxis: {
            min: 0,
            title: {
            text: json.titleForYAxis
            }
            },
            colors: ['#FF0000'],
            tooltip: {
            headerFormat: '<span style="font-size:9px">{point.key}</span><table>',
            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' + '<td style="padding:0"><b>{point.y} </b></td></tr>',
            footerFormat: '</table>',
            shared: true,
            useHTML: true
            },
            plotOptions: {
            column: {
            pointPadding: 0.2,
            borderWidth: 0
            }
            },
            series: json.series
            });
 }
 

 
 function ColumnChart() {    
    $('#container1').highcharts({
        chart: {
            type: 'column'
        },
        title: {
            text: json.title
        },
        subtitle: {
            text: json.subtitle
        },
        xAxis: {
            categories: json.categories
        },
        yAxis: {
            min: 0,
            title: {
                text: json.titleForYAxis
            }
        },
        tooltip: {
            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' + '<td style="padding:0"><b>{point.y} </b></td></tr>',
            footerFormat: '</table>',
            shared: true,
            useHTML: true
        },
        plotOptions: {
            column: {
                pointPadding: 0.2,
                borderWidth: 0
            }
        },
        series: json.series
    });
}

