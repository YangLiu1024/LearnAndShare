# Introduction to API Routes
API routes provide a solution to build your API with Next.js.

Any file inside the folder *pages/api/* is mapped to `/api/*` and will be treated as an API endpoint instead of a *page*. And they are server-side only bundles and wont increase client-side bundle size.
```js
//pages/api/user.js
export default function handler(req, res) {
    res.status(200).json({name: "Jone Doe"})
}
```
To distinguish HTTP methods, you can use `req.method === 'POST'` to check.

Note that *API Routes* do not specify CORS headers, meaning they are same-origin only by default. You can customize such behavior by wrapping the request handler with the *cors* middleware.

## Dynamic API Routes
API Routes support dynamic routes, and follow the same file naming rule used for *pages*
```js
//pages/api/post/[id].js
export default function handler(req, res) {
    const { id } = req.query
    res.end(`Post: ${id}`)
}
```
## Catch All Routes
Same as *pages*, we can add three dots(`...`) to brackets to catch all paths, such as `/pages/api/posts/[...id].js`, it catch `/api/posts/a` and `/api/posts/a/b/c` and so on.

At the same time, the query object for this route will contain an array value
```js
//pages/api/posts/[...id].js
export default function handler(req, res) {
    const { id } = req.query // will be {id: ["a", "b"]} for path /api/posts/a/b
    res.end(`Post: ${id.join(',')}`)
}
```
Note that `/pages/api/posts/[...id].js` does not match `/api/posts`, to change this, wrap the catch-all route in double brackets, `/pages/api/posts/[[...id]].js`

## API Middlewares
API routes provide built in middlewares which parse the incoming request (req). Those middlewares are:
* `req.cookies` => an object containing the cookies sent by the request. defaults is empty `{}`
* `req.query` => an object containing the query string, defaults is empty `{}`
* `req.body` => an object containing the body parsed by `content-type`, or `null` is no body was sent

## Custom Config
Each API route can export a `config` object to change the default configs, which are the following
```js
export const config = {
  api: {
    bodyParser: {
      sizeLimit: '1mb',
    },
  },
}
```
