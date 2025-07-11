# WebGLProgram
THREE.js 为 着色器提供了一些默认的属性和 uniforms

# Vertex Shader
```js
// = object.matrixWorld
// matrixWorld 表示了当前物体所经历的所有 model 转换，即从局部坐标系到世界坐标系的转换，包含了平移，缩放，旋转, 表示把物体移进世界坐标系的变换
// object.matrix 表示了当前物体相对于父对象的 model 转换， matrixWorld = parent.worldMatrix * matrix
// 所有物体都有自己的 matrix 和 matrixWorld, 包括 camera
uniform mat4 modelMatrix;

// = camera.matrixWorldInverse * object.matrixWorld
// 摄像机也有自己的 matrix, matrixWorld。 在把物体移到世界坐标系后，还需要把物体移动到摄像机的视点坐标系里，那么就需要
// camera.matrixWorldInverse * object.matrixWorld, 把物体的坐标系转换到以摄像机为原点的坐标系里
// 称之为 modelViewMatrix 
uniform mat4 modelViewMatrix;

// = camera.projectionMatrix
// 视点坐标系最后还需要映射到裁剪空间，就需要投影矩阵
uniform mat4 projectionMatrix;

// = camera.matrixWorldInverse
// 将世界坐标转换到 视点坐标系的变换矩阵
uniform mat4 viewMatrix;

// = inverse transpose of modelViewMatrix
uniform mat3 normalMatrix;

// = camera position in world space
uniform vec3 cameraPosition;
```