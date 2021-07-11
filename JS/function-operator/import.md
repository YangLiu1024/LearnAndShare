the static `import` statement is used to import read only live bindings which are *exported* by another module

```js
import defaultExport from "module-name"; // import defaut export
import * as name from "module-name"; //import all exports as namespace
import { export1 } from "module-name";//import named export
import { export1 as alias1 } from "module-name";//import named export and referred by alias
import { export1 , export2 } from "module-name";//import multiple named export
import { foo , bar } from "module-name/path/to/specific/un-exported/file"; //import from specifed file
import defaultExport, * as name from "module-name";
import "module-name"; //import the module just for its side effect(the global code) without importing anything 
var promise = import("module-name");
```