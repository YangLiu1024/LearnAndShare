import React from "react";
import { Chart, Tooltip, Axis, Legend, StackArea, Line } from "viser-react";
import styles from "./styles";
import { simpleData } from "../util";
import moment from "moment";
const scale = [
  {
    dataKey: "y",
    type: "linear",
    tickInterval: 50
  }
];

export class ViserArea extends React.Component {
  state = {
    data: [
      ...simpleData().map((d, i) => ({ ...d, type: "A" })),
      ...simpleData().map((d, i) => ({ ...d, type: "B" }))
    ]
  };
  render() {
    const { data } = this.state;
    return (
      <div style={styles.container}>
        <h3 style={styles.title}>Viser Stacked Area Chart</h3>
        <Chart forceFit height={400} data={data} scale={scale}>
          <Tooltip />
          <Axis
            dataKey="x"
            label={{ formatter: text => moment(text).format("mm:ss") }}
          />
          <Legend />
          <Line position="x*y" size={2} color="type" adjust="stack" />
          <StackArea position="x*y" color="type" />
        </Chart>
      </div>
    );
  }
}
