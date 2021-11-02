# Markdown for the component
MDX 将 markdown 和 JSX 相结合，允许在 md file 中直接 import 其它 component 然后使用。
```mdx
import Chart from '../chart'

# Introduction to MDX

<Chart color="red"/>
```