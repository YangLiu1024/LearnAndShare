import React from 'react';
import {
  Chart,
  Area,
  Line,
  Tooltip,
} from 'bizcharts';
import styles from "./styles";
import { simpleData } from "../util";
import moment from "moment";

export function BizAreaChart () {
  
  const data = [
    simpleData().map(({x, y}) => ({ x:moment(x).format("hh:mm:ss"), y, category: 'y'})),
    simpleData().map(({x, y}) => ({ x:moment(x).format("hh:mm:ss"), y, category: 'y1'})),
  ];
  
//   const scale = {
//     value: {
//       nice: true,
//     },
//     year: {
//       type: 'linear',
//       tickInterval: 50,
//     },
//   };

  return (
      <div style={styles.container}>
            <Chart data={data} autoFit>
                <Tooltip shared />
                <Area adjust="stack" color="category" position="x*y" />
                <Line adjust="stack" color="category" position="x*y" />
            </Chart>
      </div>
  );
}