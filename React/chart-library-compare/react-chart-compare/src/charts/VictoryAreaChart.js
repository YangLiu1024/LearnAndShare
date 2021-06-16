import React, { Component } from "react";
import {
  VictoryArea as Area,
  VictoryChart as Chart,
  VictoryTheme,
  VictoryStack as Stack,
  createContainer,
  VictoryAxis as Axis,
  VictoryLabel as Label
} from "victory";
import { simpleData } from "../util";
import moment from "moment";
import styles from "./styles";

export class VictoryAreaChart extends Component {
  constructor() {
    super();
    this.state = {
      data: simpleData()
    };
  }

  render() {
    const { data } = this.state;
    const VictoryZoomVoronoiContainer = createContainer("voronoi");
    return (
      <div style={styles.container}>
        <Chart
          height={styles.height}
          width={styles.width}
          theme={VictoryTheme.material}
          domainPadding={{ y: 40, x: 1 }}
          containerComponent={
            <VictoryZoomVoronoiContainer
              labels={({ datum }) => `${datum.x}, ${datum.y}`}
            />
          }
        >
          <Label
            text="Victory.js Stacked Area"
            x={400}
            y={20}
            textAnchor="middle"
            style={{ fontSize: 20 }}
          />
          <Stack>
            <Area
              style={{ data: { fill: "rgba(208, 82, 1, 1)" } }}
              data={data}
              name="area-1"
            />
            <Area
              style={{ data: { fill: "rgb(202, 124, 74)" } }}
              data={data}
              name="area-2"
            />
          </Stack>
          <Axis
            style={{ tickLabels: { angle: 0, fontSize: 8 } }}
            tickFormat={data => {
              const label = moment(data).format("hh:mm:ss");
              return label;
            }}
          />
          <Axis dependentAxis />
        </Chart>
      </div>
    );
  }
}
