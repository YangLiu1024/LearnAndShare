# Lane
React 和 Scheduler 是两套优先级机制，Scheduler 是将不同 priorityLevel 的 callback 映射为不同的 expirationTime, React 对于优先级的需求为
* 有不同优先级
* 可能同时存在几个同优先级的更新，所以还需要有 *批* 的概念
* 方便进行优先级相关计算
为了满足如上需求，React 设计了 Lane 模型
## Lane 模型
Lane 模型使用 31 位的二进制表示 31 条赛道，位数越小的赛道 优先级越高，某些相邻的赛道拥有相同的优先级。
```js
export const NoLanes: Lanes = /*                        */ 0b0000000000000000000000000000000;
export const NoLane: Lane = /*                          */ 0b0000000000000000000000000000000;

export const SyncLane: Lane = /*                        */ 0b0000000000000000000000000000001;
export const SyncBatchedLane: Lane = /*                 */ 0b0000000000000000000000000000010;

export const InputDiscreteHydrationLane: Lane = /*      */ 0b0000000000000000000000000000100;
const InputDiscreteLanes: Lanes = /*                    */ 0b0000000000000000000000000011000;

const InputContinuousHydrationLane: Lane = /*           */ 0b0000000000000000000000000100000;
const InputContinuousLanes: Lanes = /*                  */ 0b0000000000000000000000011000000;

export const DefaultHydrationLane: Lane = /*            */ 0b0000000000000000000000100000000;
export const DefaultLanes: Lanes = /*                   */ 0b0000000000000000000111000000000;

const TransitionHydrationLane: Lane = /*                */ 0b0000000000000000001000000000000;
const TransitionLanes: Lanes = /*                       */ 0b0000000001111111110000000000000;

const RetryLanes: Lanes = /*                            */ 0b0000011110000000000000000000000;

export const SomeRetryLane: Lanes = /*                  */ 0b0000010000000000000000000000000;

export const SelectiveHydrationLane: Lane = /*          */ 0b0000100000000000000000000000000;

const NonIdleLanes = /*                                 */ 0b0000111111111111111111111111111;

export const IdleHydrationLane: Lane = /*               */ 0b0001000000000000000000000000000;
const IdleLanes: Lanes = /*                             */ 0b0110000000000000000000000000000;

export const OffscreenLane: Lane = /*                   */ 0b1000000000000000000000000000000;
```
其中，同步通道 *SyncLane* 优先级为第一位，往下到 *SelectiveHydrationLane*，赛道的优先级逐步降低。  
可以看到有一些变量占用了几条赛道，比如
```js
const InputDiscreteLanes: Lanes = /*                    */ 0b0000000000000000000000000011000;
export const DefaultLanes: Lanes = /*                   */ 0b0000000000000000000111000000000;
const TransitionLanes: Lanes = /*                       */ 0b0000000001111111110000000000000;
```
这就是 *批* 的概念，被称作 *Lanes*, 其中 *InputDiscretLanes* 是用户交互触发更新会拥有的优先级范围，*DefaultLanes* 是请求数据返回后触发更新拥有的优先级范围，*TransitionLanes* 是 *Suspense*, *useTransition*, *useDefferedValue* 拥有的优先级范围。这其中有一个细节，越低优先级的 lanes 占用的赛道越多，这是因为越低优先级的更新越容易被打断，导致积压下来，所以需要更多的位。相反，最高优先级的同步更新不需要多余的 lanes.

## 优先级相关计算
因为 lane 对应了二进制的位，那么优先级的相关操作就是就是各种位运算