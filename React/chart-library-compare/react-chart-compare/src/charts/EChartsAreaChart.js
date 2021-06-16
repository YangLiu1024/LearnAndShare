import React, {Component} from 'react';
import ReactECharts from 'echarts-for-react'; 
import styles from "./styles";
import { simpleData } from "../util";
import moment from "moment";

export class EChartsAreaChart extends Component {

    state = {
        data: [
            simpleData(),
            simpleData()
        ]
    }

    getOptions() {
        const {data} = this.state;
        return {
            title: {
                text: 'ECharts Stack Area Chart'
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'cross',
                    label: {
                        backgroundColor: '#6a7985'
                    }
                }
            },
            legend: {
                data: ['y', 'y1']
            },
            toolbox: {
                feature: {
                    saveAsImage: {}
                }
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis: [
                {
                    type: 'category',
                    boundaryGap: false,
                    data: data[0].map(i => moment(i.x).format("mm:ss"))
                }
            ],
            yAxis: [
                {
                    type: 'value'
                }
            ],
            series: [
                {
                    name: 'y',
                    type: 'line',
                    stack: '总量',
                    areaStyle: {},
                    emphasis: {
                        focus: 'series'
                    },
                    data: data[0].map(i => i.y)
                },
                {
                    name: 'y1',
                    type: 'line',
                    stack: '总量',
                    areaStyle: {},
                    emphasis: {
                        focus: 'series'
                    },
                    data: data[1].map(i => i.y)
                }
            ]
        }
    }

    render() {
        return (
            <div style={styles.container}>
                <ReactECharts option={this.getOptions()}/>
            </div>
        )
    }
}