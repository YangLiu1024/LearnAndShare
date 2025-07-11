# BufferGeometry
用于表述 面片，线或点，包括顶点位置，面片索引，法向量，颜色值，和自定义缓存属性值。使用 buffer geometry 可以有效减少向 GPU 传输上述数据所需的开销。
```js
const geometry = new THREE.BufferGeometry();
// 创建一个简单的矩形. 在这里我们左上和右下顶点被复制了两次。
// 因为在两个三角面片里，这两个顶点都需要被用到。
const vertices = new Float32Array( [
	-1.0, -1.0,  1.0,
	 1.0, -1.0,  1.0,
	 1.0,  1.0,  1.0,

	 1.0,  1.0,  1.0,
	-1.0,  1.0,  1.0,
	-1.0, -1.0,  1.0
] );

// itemSize = 3 因为每个顶点都是一个三元组。
geometry.setAttribute( 'position', new THREE.BufferAttribute( vertices, 3 ) );
const material = new THREE.MeshBasicMaterial( { color: 0xff0000 } );
const mesh = new THREE.Mesh( geometry, material );
```

## 属性
* attributes, 通过 hashmap 存储当前 buffer geometry 关联的属性。 key 是属性名称，value 是 buffer
* boundingBox: Box3, 当前 buffer geometry 的外边界矩形，可以通过 computeBoundingBox() 计算
* boundingSphere: Sphere, 当前 buffer geometry 的外边界球形，可以通过 computeBoudingSphere() 计算
* drawRange(start: number, count: number), 默认值为 0, Infinity

## 方法
* setAttribute(name: string， BufferAttribute attribute): this, 为当前 geometry 设置一个属性
* applyMatrix4(mat), 用给定矩阵，转换当前 geometry
* clone(): BufferGeometry, clone 当前 geometry
* copy(buf: BufferGeometry)
* computeBoundingBox(): void, 更新 boundingBox 属性
* computeBoundingSphere()： void, 更新 boundingsphere 属性
* dispose(), 消耗对象
* rotateX(radians)
* rotateY()
* rotateZ()
* scale(x, y, z)
* translate(x, y, z)
* setDrawRange(start, count)
* setFromPoints(points: Array), 通过点队列设置该 geometry 的 attributes

# BufferAttribute
这个类用于存储和 BufferGeometry 相关联的属性。在 buffer attribute 中，数据被存储为任意长度的矢量
```js
// array 必须是 typed array, item Size 是每个顶点所占用的数组元素个数
// normalized 指明缓存中的数据如何与 GLSL 代码中的数据对应，比如 array 是 Uint16Array 类似，且 normalized 为 true, 则数据会被归一化到 [0, 1.0]
// 如果不归一化，则数据会直接映射为 float 值
BufferAttribute(array: TypedArray, itemSize: number, normalized: boolean)
```

## 属性 
* array, 保存着 属性关联的数据 数组
* count, array size / item size 之后的结果
* itemSize

## 方法
* 