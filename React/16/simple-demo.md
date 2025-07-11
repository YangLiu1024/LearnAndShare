# demo
简单写一个 scheduler, 通过 message channel 来做时间切片. 把长任务分解为多个小任务，保证浏览器有足够的时间来更新页面，响应用户
```html
<div>
<button id="btn1">Button1</button>
<button id="btn2">Button2</button>
<button id="btn3">Button3</button>
</div>
```
```js
const taskQueue = [];

const getTime = () => performance.now();

let startTime = -1;

const frameInterval = 10; // 10ms

const shouldYieldToHost = () => {
	const timeElapsed = getTime() - startTime;
  return timeElapsed > frameInterval;
}

const peek = () => taskQueue[0];

const performWorkUtilDeadline = () => {
   const time = getTime();
   startTime = time;
   
   let hasMoreWork = false;
   
   try {
     hasMoreWork = workLoop(time);
   } finally {
   	if (hasMoreWork) {
      schedulePerformWorkUtilDeadline()
    } 
   }
}

const channel = new MessageChannel()
channel.port1.onmessage = performWorkUtilDeadline
  
const schedulePerformWorkUtilDeadline = () => {
	channel.port2.postMessage(null)
}

const handleTask = () => {
	for (let j = 0; j < 5000; j++) {
  	document.getElementById("btn1").attributes
      	document.getElementById("btn2").attributes
          	document.getElementById("btn3").attributes
  }
}

const scheduleCallback = (callback, id) => {
  const task = {
  	callback,
    id,
  }
  taskQueue.push(task);
}

let taskIndex = 0;
while (taskIndex < 2000) {
	//scheduleCallback(handleTask,taskIndex)
  handleTask();
  taskIndex++;
}

schedulePerformWorkUtilDeadline()

document.getElementById("btn1").onclick = () => {
	console.log('button clicked')
}
const workLoop = (currentTime) => {
	let task = peek()
  while (task) {
  	if (shouldYieldToHost()) {
      break;
    }
    task.callback();
    console.log(`task ${task.id} finished`)
    task = taskQueue.shift();
  }
  return task!= null
}

```
```css
#btn1:hover {
  background-color: red;
}
```