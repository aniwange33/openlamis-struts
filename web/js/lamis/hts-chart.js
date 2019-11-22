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

    $.getJSON("HtsSummary_chart.action?" + params, function (json) {
        var tested = json.indicator[0].value1;
        var positive = json.indicator[0].value2;
        var initiated = json.indicator[0].value3;
        var perPositive = 0;
        var perInitiated = 0;

        if (tested > 0) {
            perPositive = (positive / tested) * 100;
            perInitiated = (initiated / positive) * 100;
        }

        $("#totalTested").html(tested);
        $("#totalPositive").html(positive);
        $("#totalInitiated").html(initiated);

        var data = '<div class="progress" style="height: 4px;">'
                + '<div class="progress-bar progress-bar-striped progress-bar-animated" role="progressbar" style="width: ' + perPositive + '%;" aria-valuenow="' + perPositive + '" aria-valuemin="0" aria-valuemax="100"></div>'
                + '</div>';
        $("#positive").html(data);
        data = '<div class="progress" style="height: 4px;">'
                + '<div class="progress-bar progress-bar-striped progress-bar-animated" role="progressbar" style="width: ' + perInitiated + '%;" aria-valuenow="' + perInitiated + '" aria-valuemin="0" aria-valuemax="100"></div>'
                + '</div>';
        $("#initiated").html(data);

    });
    
    url = "HtsTested_chart.action?" + params;
    $.getJSON(url, function (json) {
        setChart1(json);
        setChart9(json);
    });

    $.getJSON("HtsArtInitiated_chart.action?" + params, function (json) {
        setChart2(json);
    });

    $.getJSON("HtsTestByGender_chart.action?" + params, function (json) {
        console.log(json);
        setChart3(json);
    });

    $.getJSON("HtsPositiveByGender_chart?" + params, function (json) {
        setChart4(json);
    });


    $.getJSON("HtsSetting_chart.action?" + params, function (json) {
        setChart5(json);
    });

    $.getJSON("HtsReferral_chart.action?" + params, function (json) {
        console.log(json);
        setChart6(json);
        $("#loader").html('');
    }).fail(function(err){
        console.log(err);
    });

}

function getSum(total, num) {
    return total + num;
}

function getFacility() {
    var url;

    if ($("#stateId").val().length > 0 && $("#stateId").val() !== "") {
        url = "FacilityId_retrieve.action?q=1&stateId=" + $("#stateId").val();

        if (($("#lgaId").val().length > 0 && $("#lgaId").val() !== "")) {
            url = "FacilityId_retrieve.action?q=1&stateId=" + $("#stateId").val() + "&lgaId=" + $("#lgaId").val();
        }
        $.ajax({
            url: url,
            //  dataType: "json",
            data: {state: $("#stateId").val(), lga: $("#lgaId").val()},
            success: function (facilityMap) {
                console.log("Facility", facilityMap);
                var options = "<option value = '" + '' + "'>" + '' + "</option>";
                $.each(facilityMap, function (key, value) {
                    options += "<option value = '" + key + "'>" + value + "</option>";
                }) //end each
                $("#facilityId").html(options);
            },
            error: function (err) {
                console.warn(err);
            }
        }); 
    }
}

function setChart1(json) {
    $('#container1').highcharts({
        chart: { 
            type: 'column',
            options3d: {
                enabled: true,
            }
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
        exporting: {
            enabled: true
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
            },
            series: {
            borderWidth: 0,
            dataLabels: {
                enabled: true,
                format: '{point.y}'
            }
        }
        },
        series: json.series
    });

}

function setChart2(json) {
    $('#container2').highcharts({
        chart: {
            type: 'column',
            options3d: {
            enabled: true,
                }
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
            },
            series: {
            borderWidth: 0,
            dataLabels: {
                enabled: true,
                format: '{point.y}'
            }
        }
        },
        series: json.series
    });
}

function setChart3(json) {
    $('#container3').highcharts({
        chart: {
            type: 'column',
            options3d: {
            enabled: true,
                }
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
            },
            series: {
            borderWidth: 0,
            dataLabels: {
                enabled: true,
                format: '{point.y}'
            }
        }
        },
        series: json.series
    });
}

function setChart4(json) {
    $('#container4').highcharts({
        chart: {
            type: 'column',
            options3d: {
            enabled: true,
                }
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
            },
            series: {
            borderWidth: 0,
            dataLabels: {
                enabled: true,
                format: '{point.y}'
            }
        }
        },
        series: json.series
    });
}

function setChart5(json) {
    $('#container5').highcharts({
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
            options3d: {
            enabled: true,
            }
            
        },
        title: {
            text: json.title
        },
        tooltip: {
            formatter: function () {
                return this.point.name + ':<b>' + Highcharts.numberFormat(this.percentage, 1) + '%</b>';
            }
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                innerSize: 100,
                depth: 45,
                dataLabels: {
                    enabled: true,
                    color: '#000000',
                    connectorColor: '#000000',
                    formatter: function () {
                        return this.point.name + ':<b>' + Highcharts.numberFormat(this.percentage, 1) + '%</b>';
                    }
                }
            },
        },
        series: json.series
    });

}

function setChart6(json) {
    $('#container6').highcharts({
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
            options3d: {
            enabled: true,
            }
            
        },
        title: {
            text: json.title
        },
        tooltip: {
            formatter: function () {
                return this.point.name + ':<b>' + Highcharts.numberFormat(this.percentage, 1) + '%</b>';
            }
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                innerSize: 100,
                depth: 45,
                dataLabels: {
                    enabled: true,
                    color: '#000000',
                    connectorColor: '#000000',
                    formatter: function () {
                        return this.point.name + ':<b>' + Highcharts.numberFormat(this.percentage, 1) + '%</b>';
                    }
                }
            },
        },
        series: json.series
    });

}


function setChart9(json) {
            $('#container9').highcharts({
            chart: {
            type: 'spline'
            },
            title: {
            text: 'Positivity Rate'
            },
            subtitle: {
            text: ''
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
            series: json.series.data3
            });
 }


function jsonmap(){
	var data = [
    ['ng-ri', 0],
    ['ng-kt', 1],
    ['ng-so', 2],
    ['ng-za', 3],
    ['ng-yo', 4],
    ['ng-ke', 5],
    ['ng-ad', 6],
    ['ng-bo', 7],
    ['ng-ak', 8],
    ['ng-ab', 9],
    ['ng-im', 10],
    ['ng-by', 11],
    ['ng-be', 12],
    ['ng-cr', 13],
    ['ng-ta', 14],
    ['ng-kw', 15],
    ['ng-la', 16],
    ['ng-ni', 17],
    ['ng-fc', 18],
    ['ng-og', 19],
    ['ng-on', 20],
    ['ng-ek', 21],
    ['ng-os', 22],
    ['ng-oy', 23],
    ['ng-an', 24],
    ['ng-ba', 25],
    ['ng-go', 26],
    ['ng-de', 27],
    ['ng-ed', 28],
    ['ng-en', 29],
    ['ng-eb', 30],
    ['ng-kd', 31],
    ['ng-ko', 32],
    ['ng-pl', 33],
    ['ng-na', 34],
    ['ng-ji', 35],
    ['ng-kn', 36]
];

// Create the chart
$('#container8').highcharts('Map', {
    chart: {
        map: 'countries/ng/ng-all'
    },

    title: {
        text: 'Highmaps basic demo'
    },

    subtitle: {
        text: 'Source map: <a href="http://code.highcharts.com/mapdata/countries/ng/ng-all.js">Nigeria</a>'
    },

    mapNavigation: {
        enabled: true,
        buttonOptions: {
            verticalAlign: 'bottom'
        }
    },

    colorAxis: {
        min: 0
    },

    series: [{
        data: data,
        name: 'Random data',
        states: {
            hover: {
                color: '#BADA55'
            }
        },
        dataLabels: {
            enabled: true,
            format: '{point.name}'
        }
    }]
});
}




