# Object
Geometry 定义了物体的顶点，material 定义了物体的材质。BufferGeometry 是底层的 geometry, 关联了顶点的各种 attributes. Material 定义了物体每个 item 该怎么渲染，颜色，宽度，等等。  
Object 则定义了该以什么形状来组织顶点

# Line
一条连续的线，即把 geometry 里面所有的顶点绘制为一条连续的线，对应的 gl mode 是 gl.LINE_STRIP, 即下一个点连接上一个点
```js
const material = new THREE.LineBasicMaterial({
	color: 0x0000ff
});

const points = [];
points.push( new THREE.Vector3( - 10, 0, 0 ) );
points.push( new THREE.Vector3( 0, 10, 0 ) );
points.push( new THREE.Vector3( 10, 0, 0 ) );

const geometry = new THREE.BufferGeometry().setFromPoints( points );

const line = new THREE.Line( geometry, material );
scene.add( line );
```

# LineLoop
类似于 Line, 但是最后一点会和第一点连接，对应于 gl.LINE_LOOP

# LineSegments
类似于 Line, 但是线段之间并不连接，对应的是 gl.LINES

# Points
一个用于显示点的类，对应于 gl.POINTS

# Mesh
表示基于以三角形为多边形网络的物体。
```js
const geometry = new THREE.BoxGeometry( 1, 1, 1 );
const material = new THREE.MeshBasicMaterial( { color: 0xffff00 } );
const mesh = new THREE.Mesh( geometry, material );
scene.add( mesh );
```

# Group
它几乎和 Object3D 是一样的，其目的是使得组中对象在语法的结构上更加清晰
```js
const geometry = new THREE.BoxGeometry( 1, 1, 1 );
const material = new THREE.MeshBasicMaterial( {color: 0x00ff00} );

const cubeA = new THREE.Mesh( geometry, material );
cubeA.position.set( 100, 100, 0 );

const cubeB = new THREE.Mesh( geometry, material );
cubeB.position.set( -100, -100, 0 );

//create a group and add the two cubes
//These cubes can now be rotated / scaled etc as a group
const group = new THREE.Group();
group.add( cubeA );
group.add( cubeB );

scene.add( group );
```