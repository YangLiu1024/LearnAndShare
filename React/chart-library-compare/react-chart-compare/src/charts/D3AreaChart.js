import React, { Component } from "react";
import { generateData } from "../util";
import xStyles from "./styles";
const {
  area,
  select,
  axisBottom,
  axisLeft,
  line,
  curveBasis,
  scaleUtc,
  extent,
  max,
  stack,
  scaleLinear
} = require("d3");

const config = {
  width: xStyles.width,
  height: xStyles.height,
  margin: {
    top: 40,
    right: 40,
    bottom: 40,
    left: 40
  },
  color: {
    blue1: "rgba(25, 100, 126,1)",
    blue2: "rgba(55, 130, 156,1)",
    blue3: "rgba(67, 207, 214, 1)",
    bg: "rgb(241, 244, 251)"
  }
};

const styles = {
  svg: {
    background: config.color.bg,
    borderRadius: 4,
    position: "relative",
    top: 0
  },
  title: xStyles.title,
  container: xStyles.container
};

export class D3AreaChart extends Component {
  constructor() {
    super();
    this.state = {
      data: generateData()
    };
    this.xScale = undefined;
    this.yScale = undefined;
  }

  drawLine() {
    this.xScale = scaleUtc()
      .domain(extent(this.state.dataset, ({ x }) => x))
      .nice()
      .rangeRound([config.margin.left, config.width - config.margin.right]);

    this.yScale = scaleLinear()
      .domain(extent(this.state.dataset, ({ y }) => y))
      .nice()
      .rangeRound([config.height - config.margin.top, config.margin.bottom]);

    let lineMaker = line()
      .x(d => this.xScale(d.x))
      .y(d => this.yScale(d.y))
      .curve(curveBasis);

    return (
      <path
        className="line"
        d={lineMaker(this.state.dataset)}
        stroke="white"
        fill="none"
      />
    );
  }

  drawStackedArea() {
    const stackData = stack().keys(["point1", "point2"]);
    const series = stackData(this.state.data);
    this.xScale = scaleUtc()
      .domain(extent(this.state.data, ({ date }) => date))
      .rangeRound([config.margin.left, config.width - config.margin.right])
      .nice();

    this.yScale = scaleLinear()
      .domain([0, max(series, s => max(s, d => d[1]))])
      .rangeRound([config.height - config.margin.bottom, config.margin.top])
      .nice();

    let areaMaker = area()
      .x(d => this.xScale(d.data.date))
      .y0(d => this.yScale(d[0]))
      .y1(d => this.yScale(d[1]))
      .curve(curveBasis);

    return (
      <>
        <path
          className="line"
          d={areaMaker(series[0])}
          fill={config.color.blue2}
          stroke={config.color.blue3}
        />
        <path
          className="line"
          d={areaMaker(series[1])}
          fill={config.color.blue1}
          stroke={config.color.blue3}
        />
      </>
    );
  }
  drawAxes() {
    let xAxis = axisBottom(this.xScale);
    let yAxis = axisLeft(this.yScale);
    const xAxisRef = axis => {
      axis && xAxis(select(axis));
    };
    const yAxisRef = axis => {
      axis && yAxis(select(axis));
    };
    const xTransform = `translate(0, ${config.height - config.margin.bottom})`;
    const yTransform = `translate(${config.margin.left}, 0)`;
    return (
      <>
        <g transform={xTransform} ref={xAxisRef} />
        <g ref={yAxisRef} transform={yTransform} />
      </>
    );
  }
  render() {
    return (
      <div style={styles.container}>
        <h3 style={styles.title}>D3 in React</h3>
        <svg style={styles.svg} width={config.width} height={config.height}>
          {this.drawStackedArea()}
          {this.drawAxes()}
        </svg>
      </div>
    );
  }
}
