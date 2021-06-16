import React, {Component} from 'react';
import HighChartsReact from 'highcharts-react-official'; 
import HighCharts from 'highcharts';
import styles from "./styles";
import { simpleData } from "../util";
import moment from "moment";

export class HighChartsAreaChart extends Component {
    state = {
        data: [
            simpleData(),
            simpleData()
        ]
    }
    getOption() {
        const {data} = this.state;

        return {
            chart: {
                type: 'area'
            },
            title: {
                text: 'HighCharts Stacked Area Chart'
            },
            subtitle: {
                // text: 'Source: Wikipedia.org'
            },
            xAxis: {
                categories: data[0].map(i => moment(i.x).format("mm:ss")),
                tickmarkPlacement: 'on',
                title: {
                    enabled: false
                }
            },
            yAxis: {
                title: {
                    text: 'count'
                },
                labels: {
                    formatter: function () {
                        return this.value / 10;
                    }
                }
            },
            tooltip: {
                split: true,
                // valueSuffix: ' millions'
            },
            plotOptions: {
                area: {
                    stacking: 'normal',
                    lineColor: '#666666',
                    lineWidth: 1,
                    marker: {
                        lineWidth: 1,
                        lineColor: '#666666'
                    }
                }
            },
            series: [{
                name: 'y',
                data: data[0].map(i => i.y)
            }, {
                name: 'y1',
                data: data[1].map(i => i.y)
            }]
        }
    }
    render() {
        return (
            <div style={styles.container}>
                <HighChartsReact highcharts={HighCharts} options={this.getOption()}/>
            </div>
        )
    }
}