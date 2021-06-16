import React, { Component } from "react";
import { ResponsiveLine } from "@nivo/line";
import moment from "moment";
import styles from "./styles";
import { nivoData } from "../util";

export class NivoAreaChart extends Component {
  state = {
    data: nivoData()
  };
  customTooltip = ({ point }) => {
    return (
      <p style={styles.tooltip}>
        Time: <b>{point.data.xFormatted}</b>
        <br />
        Count: <b>{point.data.yFormatted}</b>
      </p>
    );
  };
  render() {
    return (
      // make sure parent container have a defined height when using
      <div style={styles.container}>
        <h3 style={styles.title}>Nivo Stacked Area Chart</h3>
        <ResponsiveLine
          animate
          data={this.state.data}
          margin={{ top: 24, right: 96, bottom: 72, left: 64 }}
          xFormat={d => moment(d).format("mm:ss")}
          xScale={{ type: "time", format: "native" }}
          yScale={{
            type: "linear",
            min: 0,
            max: 200,
            stacked: true
          }}
          curve="natural"
          axisBottom={{
            format: "%M:%S ",
            tickValues: "every second",
            orient: "bottom",
            legend: "time",
            legendOffset: 36,
            legendPosition: "middle"
          }}
          axisLeft={{
            legend: "count",
            legendOffset: -40,
            legendPosition: "middle"
          }}
          tooltip={this.customTooltip}
          colors={{ scheme: "purpleRed_green" }}
          lineWidth={1}
          pointSize={4}
          enableArea={true}
          useMesh={true}
          legends={[
            {
              anchor: "bottom-right",
              direction: "column",
              justify: false,
              translateX: 100,
              translateY: 0,
              itemsSpacing: 0,
              itemDirection: "left-to-right",
              itemWidth: 80,
              itemHeight: 20,
              itemOpacity: 0.75,
              symbolSize: 8
            }
          ]}
        />
      </div>
    );
  }
}
