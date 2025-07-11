# Curve
用于创建包含插值方法的Curve 对象的抽象基类
```js
// 创建一个 curve
Curve()
```

## 方法
* getPoint(t: float), 返回曲线上的点

# CurvePath
CurvePath 仅仅是一个已连接的曲线的数组，但保留了曲线 的 API
## 属性
* curves: Array, 数据数组
* autoClose: boolean, 是否自动闭合路径

## 方法
* add(curve: Curve), 添加一条曲线到 curves 数组中
* closePath(), 添加一条 lineCurve 用于 闭合路径
* getCurveLengths(): Array, 返回曲线段的长度的数组

# Path
继承于 CurvePath, 定义了二维路径，提供了一些类似于 2D canvas api 的方法来创建二维路径
```js
const path = new THREE.Path();

path.lineTo( 0, 0.8 );
path.quadraticCurveTo( 0, 1, 0.2, 1 );
path.lineTo( 1, 1 );

const points = path.getPoints();

const geometry = new THREE.BufferGeometry().setFromPoints( points );
const material = new THREE.LineBasicMaterial( { color: 0xffffff } );

const line = new THREE.Line( geometry, material );
scene.add( line );

// 构造函数
// points 可以是 Vector2 数组，第一个点定义了偏移量，接下来的点作为 LineCurves 被添加到 curves 数组中
Path(points?: Array)
```

## 属性
* currentPoint, 路径当前的偏移量，任何新加入的 curve 将从这里开始

## 方法
* absarc(x, y, radius, startAngle, endAngle, clockwise: boolean), 添加一条绝对定位的 ellipse curve 到路径中，x,y 是弧线的绝对中心，radius 是弧线的半径，startAngle, endAngle 以弧度表示，clockwise, 是否顺时针， 默认值为 false
* absellipse(x, y, xRadius, yRadius, startAngle, endAngle, clockwise, rotation), 添加一条绝对定位的 EllipseCurve 到路径中, rotation 是椭圆弧线从 x axis 正方向逆时针旋转的角度，弧度，默认值为 0
* arc(x,y, radius, startAngle, endAngle, clockwise), 添加一条 EllipseCurve 到路径中，相对于 .currentPoint
* bezierCurveTo(cp1X, cp1Y, cp2X, cp2Y, x, y), 添加一条贝塞尔曲线，相对于 .currentPoint. 并且 更新 .currentPoint 到 x,y
* ellipse()
* lineTo(x, y), 在当前路径上，从 .currentPoint 连接一条直线到 x,y
* moveTo(x,y), 将 .currentPoint 移动到 x,y
* quadraticCurveTo(cpX, cpY, x, y)
* setFromPoints(vector2s: Array), 点被作为 line curves 添加到数组中

# Shape
Shape 继承自 Path, 使用路径以及可选的孔洞定义一个二维形状平面
```js
const heartShape = new THREE.Shape();

heartShape.moveTo( 25, 25 );
heartShape.bezierCurveTo( 25, 25, 20, 0, 0, 0 );
heartShape.bezierCurveTo( - 30, 0, - 30, 35, - 30, 35 );
heartShape.bezierCurveTo( - 30, 55, - 10, 77, 25, 95 );
heartShape.bezierCurveTo( 60, 77, 80, 55, 80, 35 );
heartShape.bezierCurveTo( 80, 35, 80, 0, 50, 0 );
heartShape.bezierCurveTo( 35, 0, 25, 25, 25, 25 );

const extrudeSettings = { depth: 8, bevelEnabled: true, bevelSegments: 2, steps: 2, bevelSize: 1, bevelThickness: 1 };

const geometry = new THREE.ExtrudeGeometry( heartShape, extrudeSettings );

const mesh = new THREE.Mesh( geometry, new THREE.MeshPhongMaterial() );
```
CurvePath 的继承类都是通过离散的点来构建 line, 从而形成 shape 等

# EllipseCurve
继承自 Curve, 创建一个形状为椭圆的曲线，如果 xRadius, yRadius 相同，将会成为一个圆
```js
EllipseCurve(x, y, xRaius, yRadius, startAngle, endAngle, clockwise, rotation)
```
# LineCurve
继承自 Curve, 创建一个线段
```js
// v1 是起点，v2 是终点
LineCurve(v1: Vector2, v2: Vector2)
```

## CubicBezierCurve
继承自 Curve, 创建一条平滑的二维三次贝塞尔曲线，由起点，终点和两个控制点所定义
```js
// v0 起点，v1 是第一个控制点，v2 第二个控制点，v3 终点
CubicBezierCurve(v0, v1, v2 v3)

const curve = new THREE.CubicBezierCurve(
	new THREE.Vector2( -10, 0 ),
	new THREE.Vector2( -5, 15 ),
	new THREE.Vector2( 20, 15 ),
	new THREE.Vector2( 10, 0 )
);

const points = curve.getPoints( 50 );
const geometry = new THREE.BufferGeometry().setFromPoints( points );

const material = new THREE.LineBasicMaterial( { color: 0xff0000 } );

// Create the final object to add to the scene
const curveObject = new THREE.Line( geometry, material );
```
## QuadraticBezierCurve
继承自 Curve, 创建一条平滑的二维二次贝塞尔曲线，由起点，终点和一个控制点所定义
```js
QuadraticBezierCurve(v0, v1, v2)

const curve = new THREE.QuadraticBezierCurve(
	new THREE.Vector2( -10, 0 ),
	new THREE.Vector2( 20, 15 ),
	new THREE.Vector2( 10, 0 )
);

const points = curve.getPoints( 50 );
const geometry = new THREE.BufferGeometry().setFromPoints( points );

const material = new THREE.LineBasicMaterial( { color: 0xff0000 } );

//Create the final object to add to the scene
const curveObject = new THREE.Line( geometry, material );
```

## SplineCurve
继承自 Curve, 从一系列的点中，创建一个平滑的二维样条曲线
```js
SplineCurve(points: Array)// points 是 Vector2 array

// Create a sine-like wave
const curve = new THREE.SplineCurve( [
	new THREE.Vector2( -10, 0 ),
	new THREE.Vector2( -5, 5 ),
	new THREE.Vector2( 0, 0 ),
	new THREE.Vector2( 5, -5 ),
	new THREE.Vector2( 10, 0 )
] );

const points = curve.getPoints( 50 );
const geometry = new THREE.BufferGeometry().setFromPoints( points );

const material = new THREE.LineBasicMaterial( { color: 0xff0000 } );

// Create the final object to add to the scene
const splineObject = new THREE.Line( geometry, material );
```

其它的类似于 LineCurve3, CubicBezierCurve3, QuadraticBezierCurve3 就是创建对应的三维 曲线