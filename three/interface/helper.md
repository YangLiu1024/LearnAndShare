# 辅助类
THREE.js 提供了多种辅助类
# ArrowHelper
同于模拟方向的3维箭头对象
```js
const dir = new THREE.Vector3( 1, 2, 0 );

//normalize the direction vector (convert to vector of length 1)
dir.normalize();

const origin = new THREE.Vector3( 0, 0, 0 );
const length = 1;
const hex = 0xffff00;

// dir 是箭头归一化方向，origin 是箭头原点， length 是箭头的长度，默认值为 1， hex 是箭头颜色，默认 0xffff00, headLength 箭头头部的长度，默认是 0.2 * length, headWidth, 默认值是 0.2 * length
const arrowHelper = new THREE.ArrowHelper( dir, origin, length, hex );
scene.add( arrowHelper );
```

# AxesHelper
用于模拟坐标轴，红色代表 X 轴，绿色代表 Y 轴，蓝色代表 Z 轴
```js
// length 是坐标轴长度
const length = 5;
const axesHelper = new THREE.AxesHelper( length );
scene.add( axesHelper );
```

# BoxHelper
用于展示物体的世界轴心对齐的包围盒的辅助对象
```js
const sphere = new THREE.SphereGeometry();
const object = new THREE.Mesh( sphere, new THREE.MeshBasicMaterial( 0xff0000 ) );
// 设置 box helper 所应用的 物体 和 box 的颜色，生成的盒子会包含物体以及物体所有的子节点
const box = new THREE.BoxHelper( object, 0xffff00 );
scene.add( box );
```

# CameraHelper
用于模拟相机视锥体的辅助对象, CameraHelper 必须是 scene 的子对象
```js
const camera = new THREE.PerspectiveCamera( 75, window.innerWidth / window.innerHeight, 0.1, 1000 );
const helper = new THREE.CameraHelper( camera );
scene.add( helper );
```

# GridHelper
坐标格辅助对象。坐标格实际上是二维线数组
```js
// size 是坐标格尺寸，默认值为 10
// divisions 是坐标格细分次数，默认为 10
const size = 10;
const divisions = 10;

const gridHelper = new THREE.GridHelper( size, divisions );
scene.add( gridHelper );
```

# PlaneHelper
用于模拟平面的辅助对象
```js
const plane = new THREE.Plane( new THREE.Vector3( 1, 1, 0.2 ), 3 );
// 设置辅助平面的 单边长度，默认是 1，和辅助对象的颜色
const helper = new THREE.PlaneHelper( plane, 1, 0xffff00 );
scene.add( helper );
```