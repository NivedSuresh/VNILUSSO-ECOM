(function ($) {
    "use strict";

    /*Sale statistics Chart*/
    if ($('#myChart').length) {

        var ctx = document.getElementById('myChart').getContext('2d');

        var chart = new Chart(ctx, {
            // The type of chart we want to create
            type: 'line',
            
            // The data for our dataset
            data: {
                labels: keys,
                datasets: [{
                        label: 'Sales',
                        tension: 0.3,
                        fill: true,
                        backgroundColor: 'rgba(44, 120, 220, 0.2)',
                        borderColor: 'rgba(44, 120, 220)',
                        data: sales
                    }
                ]
            },
            options: {
                plugins: {
                legend: {
                    labels: {
                    usePointStyle: true,
                    },
                }
                }
            }
        });
    } //End if

    /*Sale statistics Chart*/
    if ($('#myChart2').length) {
        console.log(categories, salesPerCategory);
        var ctx = document.getElementById("myChart2");

        // Define an array of dynamic colors
        var dynamicColors = [
            'rgba(44, 120, 220, 0.7)',
            'rgba(220, 44, 120, 0.7)',
            'rgba(120, 220, 44, 0.7)',
            'rgba(255, 0, 0, 0.7)',
            'rgba(0, 255, 0, 0.7)',
            'rgba(0, 0, 255, 0.7)'
            // You can add more colors as needed
        ];

        // Create an array to store background colors for your data
        var backgroundColors = [];
        for (var i = 0; i < categories.length; i++) {
            backgroundColors.push(dynamicColors[i % dynamicColors.length]);
        }

        var myChart = new Chart(ctx, {
            type: 'pie',
            data: {
                labels: categories,
                datasets: [{
                    label: 'Sales',
                    tension: 0.3,
                    fill: true,
                    backgroundColor: backgroundColors,
                    borderColor: 'rgba(44, 120, 220)',
                    data: salesPerCategory
                }]
            },
            options: {
                plugins: {
                    legend: {
                        labels: {
                            usePointStyle: true
                        }
                    }
                }
            }
        });
    }
    //end if

    if ($('#myChart3').length) {
        console.log(categories, salesPerCategory)
        var ctx = document.getElementById("myChart3");
        var myChart = new Chart(ctx, {

            type: 'bar',

            // The data for our dataset
            data: {
                labels: orderStatus,
                datasets: [{
                    label: 'Count',
                    tension: 0.3,
                    fill: true,
                    backgroundColor: 'rgba(44, 120, 220, 0.7)',
                    borderColor: 'rgba(44, 120, 220)',
                    data: orderStatusCount
                }]
            },
            options: {
                plugins: {
                    legend: {
                        labels: {
                            usePointStyle: true,
                        },
                    }
                }
            }
        });
    }
    
})(jQuery);