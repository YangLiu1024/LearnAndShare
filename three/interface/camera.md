# Camera
Camera 基类定义了一些属性和方法
## 属性
* matrixWorldInverse, inverse matrix of .matrixWorld
* projectionMatrix, 投影矩阵
* projectMatrixInverse


常用的 相机有 
## PerspectiveCamera(透视相机)
这一摄像机被用来模拟人眼所看到的景象，是 3D 场景渲染中最常用的投影模式  

PerspectiveCamera(fov?: number, aspect?: number, near?: number, far?: number)
```js
const camera = new THREE.PerspectiveCamera( 45, width / height, 1, 1000 );
scene.add( camera );

// 更新摄像机投影矩阵
// 因为投影矩阵并不会经常更新，所以设置为需手动更新
camera.updateProjectionMatrix()
```

## OrthographicCamera
这个摄像机使用 正交投影 来进行投影，在这种模式下，无论物体距离摄像机远或者近，在最终渲染的图片里，物体的大小都保持不变  
这对于渲染 2D 场景是非常有用的
```js
OrthographicCamera(left, right, top, bottom, near, far)

const camera = new THREE.OrthographicCamera( width / - 2, width / 2, height / 2, height / - 2, 1, 1000 );
scene.add( camera );
```
