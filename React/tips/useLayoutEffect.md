# useLayoutEffect
`useLayoutEffect(setup, deps?)` 是为了能够 measuring layout before browser repaints the screen. 也就是说，该 hook 会 block browser repaints.  
它的使用场景在于，有的时候，控件在渲染的时候，需要知道它的位置和大小。  
比如 tooltip, tooltip 在渲染的时候，需要知道它所 hover 元素的位置，以及它自己的大小，这样才能知道，tooltip 到底应该渲染在 hover 元素的什么位置
```js
import { useRef, useLayoutEffect, useEffect, useState } from 'react';
import { createPortal } from 'react-dom';
import TooltipContainer from './TooltipContainer.js';

export default function Tooltip({ children, targetRect }) {
  const ref = useRef(null);
  const [tooltipHeight, setTooltipHeight] = useState(0);

  // 如果使用 useLayoutEffect, React 会阻止 browser paint 直到 useLayoutEffect 执行完毕
  // 所以当 user 看到 tooltip 的时候，它的位置已经是正确的了
  // 所以即使 tooltipHeight 经历了从 0 到 height 的变化，user 也只能看到一次渲染结果
  useLayoutEffect(() => {
    const { height } = ref.current.getBoundingClientRect();
    setTooltipHeight(height);
  }, []);

  // This artificially slows down rendering
  let now = performance.now();
  while (performance.now() - now < 100) {
    // Do nothing for a bit...
  }
  // 如果是使用 useEffect, React 并不会阻止 browser paint, 所以 browser 会很快的以 0 渲染一次，再以 height 渲染一次
  // 为了让 user 能够看到两次渲染的变化，添加了 performance 的循环，来延迟渲染
  useEffect(() => {
    const { height } = ref.current.getBoundingClientRect();
    setTooltipHeight(height);
  }, []);

  let tooltipX = 0;
  let tooltipY = 0;
  if (targetRect !== null) {
    tooltipX = targetRect.left;
    tooltipY = targetRect.top - tooltipHeight;
    if (tooltipY < 0) {
      // It doesn't fit above, so place below.
      tooltipY = targetRect.bottom;
    }
  }

  return createPortal(
    <TooltipContainer x={tooltipX} y={tooltipY} contentRef={ref}>
      {children}
    </TooltipContainer>,
    document.body
  );
}
```
```js
// TooltipContainer.js
export default function TooltipContainer({ children, x, y, contentRef }) {
  return (
    <div
      style={{
        position: 'absolute',
        pointerEvents: 'none',
        left: 0,
        top: 0,
        transform: `translate3d(${x}px, ${y}px, 0)`
      }}
    >
      <div ref={contentRef} className="tooltip">
        {children}
      </div>
    </div>
  );
}

```