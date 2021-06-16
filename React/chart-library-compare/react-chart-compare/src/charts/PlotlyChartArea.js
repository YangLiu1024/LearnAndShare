import React from 'react';
import Plot from 'react-plotly.js';
import styles from "./styles";
import { simpleData } from "../util";
import moment from "moment";

export class PlotlyChartArea extends React.Component {
    state = {
        data : [
            simpleData(),
            simpleData()
        ]
    }
    render() {
        const {data} = this.state;
      return (
          <div style= {styles.container}>
                <Plot
          data={[
            {
                x: data[0].map(i => moment(i.x).format("mm:ss")),
                y: data[0].map(i => i.y),
                fill: 'tozeroy',
                fillcolor: '#ab63fa',
                hoveron: 'points+fills',
                line: {
                  color: '#ab63fa'
                },
                type: "scatter",
                text: "Points + Fills",
                hoverinfo: 'text'
            },
            {
                x: data[1].map(i => moment(i.x).format("mm:ss")),
                y: data[1].map(i => i.y),
                fill: 'tozeroy',
                fillcolor: '#e763fa',
                hoveron: 'points+fills',
                line: {
                  color: '#e763fa'
                },
                type: "scatter",
                text: "Points + Fills",
                hoverinfo: 'text'
            },
          ]}
          layout={ {title: 'Plotly Stacked Area Chart'} }
            />
          </div>

      );
    }
  }