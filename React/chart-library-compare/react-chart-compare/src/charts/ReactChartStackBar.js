import React from 'react';
import { Bar } from 'react-chartjs-2';
import styles from "./styles";
import { simpleData } from "../util";
import moment from "moment";

const options = {
  scales: {
    yAxes: [
      {
        stacked: true,
        ticks: {
          beginAtZero: true,
        },
      },
    ],
    xAxes: [
      {
        stacked: true,
      },
    ],
  },
};

function StackedBar() {
    const simple = [simpleData(), simpleData()];
    const data = {
        labels: simple[0].map(({x, _}) => moment(x).format("mm:ss")),
        datasets: [
          {
            label: 'react',
            data: simple[0].map(({_, y}) => y),
            backgroundColor: 'rgb(255, 99, 132)',
          },
          {
            label: 'chartjs',
            data: simple[1].map(({_, y}) => y),
            backgroundColor: 'rgb(54, 162, 235)',
          }
        ],
      };
    return (
        <>
           <div style={styles.container}>
          <Bar data={data} options={options} />
        </div>
        </>
      )
};

export default StackedBar;