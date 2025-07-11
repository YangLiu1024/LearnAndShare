# Object3D
Object3D 是 three.js 里大部分对象的基类，提供了一些基础的属性和方法
## 属性
* parent, Object3D | null, 指向父节点
* children, Object3D[], 该节点的子节点们
* up, Vector3, 指定当前对象的 up 方向
* position, readonly Vector3, 对象的 local position
* rotation, Euler, 欧拉角，弧度， local rotation
* quaternion, 四元数, local
* scale, Vector3
* matrix, Matrix4, local transform matrix
* matrixWorld, the global transform matrix, 如果没有父节点，则等同于 matrix
## 方法
* applyMatrix4(mat: Matrix4), 将指定 matrix apply 到该节点，并更新对应的 position, scale 等属性
* applyQuaternion(qua: Quaternion)
* setRotationFromAxisAngle(axis: Vector3, angle: number), 内部更新 .quaternion 属性， axis 是 normalized vector, angle 是弧度
* setRotationFromEuler(euler: Euler)
* setRotationFromQuaternion(q: Quaternion)
* rotateOnAxis
* rotateOnWorldAxis
* rotateX(angle: number), rotate x axis in local space
* rotateY
* rotateZ
* translateOnAxis(axis: Vector3, distance: number)
* translateX
* translateY
* translateZ
* localToWorld(vec: Vector3)：Vector3, 转换 vec 到世界坐标系中，vec 是 local 坐标系
* worldToLocal(vec: Vector3): Vector3, 转换世界坐标系到local 坐标系，vec 是世界坐标系下的坐标
* lookAt(vec: Vector3), rotate object to face the point in world space
* lookAt(x, y, z)
* add(...objects: Object3D[]): this, 把其它 object 作为 child 添加
* remove(...object: Object3D[]): this, 删除子节点
* removeFromParent(), 把自己从父节点删掉
* clear(), remove all children
* getObjectById(id: number): Object3D | undefined, 从自己开始，遍历子节点
* getObjectByName(name: string)
* raycast(raycaster: Raycaster, intersects: Intersection[]): void, 抽象方法，返回 raycaster 和该对象的交集. 不同 Object3D 可能有不同的实现，用于射线和该物体的拾取检测
* traverse(callback: (object: Object3D) => any): void, execute callbacks on this object and all descents
* tranverseVisible, only traverse visible object
* updateMatrix(), 根据当前属性，更新 .matrix 矩阵
* updateMatrixWorld(force?: boolean), 更新 .matrixWorld
* updateWorldMatrix(updateParents: boolean, updateChildren: boolean)
* clone(recursive?: boolean): this
* copy(object: Object3D, recursive?: boolean), copy give object into this object