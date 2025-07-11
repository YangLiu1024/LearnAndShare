# 数学库
Three.js 定义了一些基础的类来描述形状
# Box2
二维轴对齐盒, 盒子的边总是和 axis 平行
```js
// min 是盒子下边界，max 是盒子上边界
Box2(min: Vector2, max: Vector2)
```
## 方法
* containsBox(box: Box2): boolean, 检查是否包含 box
* containsPoint(p: Vector2): boolean
* distanceToPoint(p: Vector2): Float
* expandByPoint(p: Vector2), 扩充盒子来包含 point
* expandByScalar(scalar: float), 扩展盒子
* expandByVector(vec: Vector2)
* getCenter(target: Vector2): Vector2, 结果将被复制到 target 中
* getSize(target: Vector2): Vector2, 返回盒子的大小
* intersect(box: Box2), 返回两个盒子相交的部分
* intersectBox(box: Box2)： boolean, 检查两个盒子是否相交
* isEmpty(): boolean, 判断盒子是否包含点，如果上边界小于下边界，则为 empty
* makeEmpty()
* set(min, max)
* setFromCenterAndSize(center: Vector2, size: Vector2)
* setFromPoints(points: Array), 由这些点确定的空间将被盒子包围
* translate(offset: Vector2)
* union(box: Box2), union 两个盒子

# Box3
表示三维空间里的轴对齐包围盒，与 Box2 类似

# Euler
欧拉角描述一个旋转变换，通过指定轴顺序和其各个轴向上的指定旋转角度来旋转一个物体
```js
// x,y,z 是在每个轴上的旋转角度，弧度表示
// order 是旋转顺序，默认为 'XYZ', 必须大写
Euler(x, y, z, order)
```
# Quaternion
四元数也用来表示旋转，四元数需要被归一化
```js
Quaternion(x,y,z,w)
```
# Vector2
表示二维向量
```js
// x,y 默认值为 0
Vector2(x, y)
```
## 方法
* add(v: Vector2): this, 把传入的向量和当前向量相加
* addScalar(s: float): this, 把传入的标量和 x,y 分别相加
* angle(): float, 返回该向量和 x 轴正方向的弧度
* angleTo(v: Vector2), 以弧度返回该向量和指定向量之间的弧度
* applyMatrix3(mat: Matrix3), 将该向量乘以三阶矩阵，z 隐式为 1
* distanceTo(vec: Vector2), 计算该向量到指定点之间的距离
* manhattanDistanceTo(v: Vector2): float, 计算该向量到指定点的曼哈顿距离，曼哈顿距离是每个维度的距离之和
* distanceToSquared(v: Vector2): float, 计算平方距离
* divide(v: Vector2), 该向量除以 向量 v
* divideScalar(s: float)
* dot(v: Vector2): float, 计算点积， a * b = a1 * b1 + a2 * b2
* cross(v: Vector2): flaot, 计算叉积, 叉积的定义是 axb = |a| * |b| * |sin(θ)|，其方向是 a,b 的法向量
* getComponent(index)
* manhattanLength(), 返回向量的曼哈顿长度
* lengthSq(), 返回欧几里得长度，即直线长度的平方
* negate(), 向量取反
* normalize(), 向量归一化，长度为 1， 方向不变
* multiply(vec: Vector), 当前向量与传入向量相乘
* multiplyScalar(s: float)
* rotateAround(center: Vector2, angle: float), 围绕指定点，渲染指定弧度
* set(x,y)
* setComponent(index, value)
* setLength(l: float)
* setX
* setY
* sub(v: Vector2)
* subScalar(s)
* random(), 将 x,y 置为 0，1 之间的随机数，不包含 1

# Vector3
和 Vector2 类似，表示一个三维向量
# Vector4
和 Vector2 类似，表示一个四维向量

# Line3
用起点和终点表示的几何线段
```js
Line3(start: Vector3, end: Vector3)
```
## 方法
* closestPointToPoint(point: Vector3, clampToLine: boolean, target: Vector3): Vector3, 查找该线段上到指定 point 距离最短的点，clampLine 表示是否将结果限制在线段的起始点和终点之间， 最终结果 copy 到 target 里
* delta(target: Vector3), 返回线段的向量表示，结果 copy 到 target 里
* distance() 返回欧几里得距离
* distanceSq(), 返回直线距离的平方
* getCenter(target: Vector3)

# Triangle
一个三角形由三个角的顶点定义
```js
Triangle( a : Vector3, b : Vector3, c : Vector3 )
```

## 方法
* closestPointToPoint(), 返回三角形上最靠近指定点的点
* containsPoint(p: Vector3): boolean
* getArea() 返回面积
* getMidPoint(), 返回三角形中点
* getNormal(), 返回三角形法向量
* getPlane()： Plane, 根据三角形返回平面
* intersectBox(box: Box3), 检测是否与 box 相交

# Plane
在三维空间里无限延伸的二维平面，平面方程由单位长度的法向量
```js
// normal 是法向量，constant 是原点到平面的距离
Plane(normal: Vector3, constant: float)
```
## 方法
* coplanarPoint() 返回一个共面点，通过原点到平面上投影算得
* distanceToPoint(p: Vector3), 点到平面得有符号距离
* distanceToSphere(sphere: Sphere), 返回球面得边缘到平面得最短距离
* intersectLine(line: Line3, target: Vector3), 返回平面与线段得交点，如果不相交，返回 null
* intersectsBox(box: Box3): boolean
* intersectsLine(line: Line3): boolean
* intersectsSphere(sphere: Sphere): boolean
* negate(), 将法向量取反
* normalize(), 归一化法向量，并相应得调整 constant 得值
* projectPoint(point: Vector3, target: Vector3), 返回 point 到该平面得投影点
* translate(offset: Vector3)

# Sphere
一个球由球心和半径定义
```js
Sphere(center: Vector3, radius: float)
```
## 方法
* containsPoint()
* distanceToPoint(), 如果点位于球内，则距离为负值
* expandByPoint()
* getBoundingBox()
* intersecsBox()
* intersectsPlane()
* intersectsSphere()
* setFromPoints(), 从指定 points 创建一个包含所有点得 sphere
* union()

# Matrix3
一个三阶矩阵
```js
// 以行优先的形式
Matrix3(n11,n12,n13,n21,n22,n23,n31,n32,n33)
```
## 属性
* elements, 矩阵的元素，以 column 优先的形式存储
## 方法
* set(n11,n12,n13,n21,n22,n23,n31,n32,n33), 以行优先的形式 set
* determinant(): float 返回矩阵的行列式
* fromArray(array: Array, offset: int), array 是列优先的数组
* invert(), 将当前矩阵翻转为它的逆矩阵
* getNormalMatrix(mat: Matrix4), 将该矩阵设置为给定矩阵的正规矩阵。正规矩阵是矩阵 m 的逆矩阵的转置矩阵
* identify() 将矩阵重置为单位矩阵
* multiply(mat: Matrix3), 当前矩阵乘以 mat
* multiplyScalar(s: float)
* premultiply(m: Matrix3), 将矩阵 m 乘以当前矩阵
* setFromMatrix4(mat: Matrix4), 取 ma4 左上角 3x3 的值
* toArray(array, offset), 把当前矩阵写入到 array 中
* transpose() 将当前矩阵转置

# Matrix4
Three.js 使用四阶矩阵进行坐标变换。 任何物体都有三个关联的矩阵
* Object3D.matrix, 存储物体的 local 变换矩阵，这是对象相对于其父对象的变换矩阵
* Object3D.matrixWorld, 对象的全局或世界变换矩阵。世界变换矩阵就是 this.parent.matrixWorld * this.matrix 的结果
* Object3D.modelViewMatrix, 表示对象相对于摄像机坐标系的变换矩阵。一个对象的 modelViewMatrix 是物体世界变换矩阵乘以摄像机相对于世界空间变换矩阵的逆矩阵

Camera 有三个额外的四维矩阵
* Camera.matrixWorldInverse
* Camera.projectionMatrix
* Camera.projectMatrixInverse

# Ray
射线由一个原点，向一个确定的方向发射，它被 Raycaster(光线投射) 所使用， 光线投射用于在各个物体之间进行拾取判定
```js
// direction 必须 归一化
Ray(origin: Vector3, direction: Vector3)
```
## 方法
* applyMatrix4(mat), 使用 mat 来变换 ray
* at(t: float, target), 根据指定距离，返回射线上的点位置
* closestPointToPoint(point), 沿着 ray 的方向，获得与传入 point 最近的点
* distanceSqToPoint(point), 
* distanceSqToSegment(v0, v1), 获得射线到线段的平方距离
* distanceToPlane(), 获取射线原点到平面的距离，如果不相交，则返回 null
* distanceToPoint()
* intersectBox(box, target), 返回 射线与 box 的交点，如果没有，则返回 null
* intersectPlane()
* intersectSphere()
* intersectTriangle()
* intersectsBox(box): boolean
* intersectsPlane()
* intersectsSphere()
* 
# MathUtils
Three.js 内置了这个类，具有多个数学实用函数
## 方法
* clamp(value, min, max), 限制 value 在 min, max 之间
* degToRad(degress), 度数到弧度的转换
* radToDeg(rad), 弧度到度数的转换
* generateUUID(), 创建全局 unique id
* randFloat(low, high), 返回 low, high 之间的随机数
* randFloatSpread(range), 在区间 [-range/2, rang/2] 之间的数
* randInt(low, high)