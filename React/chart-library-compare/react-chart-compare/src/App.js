import React from "react";
import "./App.css";
import { D3AreaChart } from "./charts/D3AreaChart";
import { VictoryAreaChart } from "./charts/VictoryAreaChart";
import { RechartsAreaChart } from "./charts/RechartsAreaChart";
import { NivoAreaChart } from "./charts/NivoAreaChart";
import { ReactVisArea } from "./charts/ReactVisAreaChart";
import { ViserArea } from "./charts/ViserAreaChart";
import {EChartsAreaChart} from "./charts/EChartsAreaChart";
import { HighChartsAreaChart } from './charts/HighChartsAreaChart';
// import {PlotlyChartArea} from './charts/PlotlyChartArea';
// import { BizAreaChart } from './charts/BizAreaChart'
import StackedBar from './charts/ReactChartStackBar'
// import { ReactTimeSeriesChart } from './charts/ReactTimeSeriesAreaChart'
// import { ReactTimeSeriesCurrencyChart } from './charts/ReactTimeSeriesCurrencyChart'

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <h3>React Data-viz Libraries Comparison</h3>
      </header>
      <div className="App-main">
          <D3AreaChart />
          <VictoryAreaChart />
          <RechartsAreaChart />
          <NivoAreaChart />
          <ReactVisArea />
          <ViserArea />
          <EChartsAreaChart />
          <HighChartsAreaChart/>
          <StackedBar/>
          {/* <ReactTimeSeriesCurrencyChart /> */}
          {/* <ReactTimeSeriesChart></ReactTimeSeriesChart> */}
          {/* <BizAreaChart /> */}
          {/* <PlotlyChartArea/> */}
      </div>
    </div>
  );
}

export default App;
