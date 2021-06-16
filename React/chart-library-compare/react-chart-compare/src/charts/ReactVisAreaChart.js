import React, { Component } from "react";
import {
  YAxis,
  XAxis,
  HorizontalGridLines,
  VerticalGridLines,
  XYPlot,
  AreaSeries,
  Crosshair
} from "react-vis";
import styles from "./styles";
import "react-vis/dist/style.css";
import { simpleData } from "../util";
import moment from "moment";

export class ReactVisArea extends Component {
  state = {
    data: [
      simpleData().map(({ x, y }) => ({ x: new Date(x).getTime(), y })),
      simpleData().map(({ x, y }) => ({ x: new Date(x).getTime(), y }))
    ],
    crosshairValues: []
  };

  _onMouseLeave = () => {
    this.setState({ crosshairValues: [] });
  };
  _onNearestX = ({ x, y }, e) => {
    this.setState({ crosshairValues: this.state.data.map((d) => d[e.index]) });
  };

  render() {
    const { data, crosshairValues } = this.state;

    return (
      <div style={styles.container}>
        <h3 style={styles.title}>React-vis Stacked Area Chart</h3>
        <XYPlot
          height={400}
          width={800}
          margin={{ right: 32, bottom: 60 }}
          stackedBy="y"
          domain={[0, 200]}
        >
          <VerticalGridLines />
          <HorizontalGridLines />
          <XAxis
            tickTotal={10}
            tickFormat={function tickFormat(d) {
              return moment(new Date(d)).format("mm:ss");
            }}
          />
          <YAxis />
          <AreaSeries
            data={data[0]}
            onNearestX={this._onNearestX}
            color="steelblue"
            opacity={0.5}
          />
          <AreaSeries data={data[1]} color="slateblue" opacity={0.5} />
          <Crosshair values={crosshairValues} />
        </XYPlot>
      </div>
    );
  }
}
