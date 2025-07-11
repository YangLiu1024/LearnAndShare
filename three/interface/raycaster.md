# 光线投射 raycaster
这个类用于进行光线投射。光线投射用于进行鼠标拾取
```js
const raycaster = new THREE.Raycaster();
const pointer = new THREE.Vector2();

function onPointerMove( event ) {

	// 将鼠标位置归一化为设备坐标。x 和 y 方向的取值范围是 (-1 to +1)

	pointer.x = ( event.clientX / window.innerWidth ) * 2 - 1;
	pointer.y = - ( event.clientY / window.innerHeight ) * 2 + 1;

}

function render() {

	// 通过摄像机和鼠标位置更新射线
	raycaster.setFromCamera( pointer, camera );

	// 计算物体和射线的焦点
	const intersects = raycaster.intersectObjects( scene.children );

	for ( let i = 0; i < intersects.length; i ++ ) {

		intersects[ i ].object.material.color.set( 0xff0000 );

	}

	renderer.render( scene, camera );

}

window.addEventListener( 'pointermove', onPointerMove );

window.requestAnimationFrame(render);
```

```js
// origin 是射线的原点
// direction 是归一化的射线方向
// near, 只检测 near 之外的点
// far, 只检测 far 之内的点
Raycaster(origin: Vector, direction: Vector3, near, far)
```
## 方法
* set(origin: Vector3, direction: Vector), 设置射线的原点和方向
* setFromCamera(coords: Vector2, camera: Camera), coords 是标准化设备坐标中鼠标的位置，应该在 [-1, 1] 之间。通过摄像机和屏幕点更新射线
* intersectObject(object: Object3D, recursive: boolean)： Array, 检测与 object 之间的相交情况，recursive 如果为 true, 则递归检查子节点. 返回所有的相交情况。返回结果会按照相交部分的距离进行排序，最近的位于第一个。该方法返回的数据对象格式为 {distance, point, face, faceIndex, object}. distance 是射线投射原点和相交部分之间的距离，point 是相交部分的点，世界坐标，face 是相交的面，faceIndex, 是相交的面的索引。 object 是相交的物体。当计算射线与物体是否相交的时候，raycaster 将传入的对象委托给 raycast  方法。这让不同对象可以有自己不同的 碰撞检测。 请注意对于网格来说，面必须朝向射线的原点，以遍其能够被检测到。用于交互的射线穿过面的背侧时，将不会被检测到。如果需要对面的两侧进行光线投射，需要将 material 中的 side 属性设置为 THREE.DoubleSide
* intersectObjects()