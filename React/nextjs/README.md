# Introduction to Next.js

To build a complete web application with React from scratch, there are many important details you need to consider:

* Code has to be bundled using a bundler like webpack and transformed using a compiler like Babel.
* You need to do production optimizations such as code splitting.
* You might want to statically pre-render some pages for performance and SEO. You might also want to use server-side rendering or client-side rendering.
* You might have to write some server-side code to connect your React app to your data store.

A framework can solve these problems. But such a framework must have the right level of abstraction — otherwise it won’t be very useful. It also needs to have great "Developer Experience", ensuring you and your team have an amazing experience while writing code.

Next.js is a React framework, which provide 
* an intuitive page-based routing system
* pre-rendering on each *page*, both Static Generation and Server Side Rendering
* Automatic code splitting
* Client side routing
* built-in css and sass support
* hot reload
* API routes with serverless functions


## Create a Next.js app
### System Requirements
* Node.js 10.13 or above
### Command
```bash
npx create-next-app nextjs-blog --use-npm --example "https://github.com/vercel/next-learn-starter/tree/master/learn-starter"
```
This command will create a next.js app from the specified template repository
### Start the app
```bash
cd nextjs-blog
npm run dev
```
### TypsScripts
To use typescripts, add a *tsconfig.json* file, and start the server again, the next.js will warn you to install typescripts
```bash
npm install typescripts @types/react --save
```

## Pages
In Next.js, the file in *pages* directory is a React Component, and each file is associated with a route based on their file name. For example, the *pages/index.js* is associated with */* route, *pages/posts/first-post.js* is associated with */posts/first-post*

If we create a new page unders *pages* directory, such as *pages/posts/first-post.js*, then we can access this page through http://localhost:3000/posts/first-post.

Note that for each page, its component must be exported as default.

## Nextjs Link
normally, we use tag `<a>` to link other page, in nextjs, it use `<Link>` to wrap the `<a>`, and allow you to do client-side navigation to a different page in the application.

```js
<Link href="/posts/first-post">
    <a>First Post</a>
</Link>
```

Note that if you want to add css to this link, add it to `<a>` instead of `<Link>`

## Code Splitting and Prefetching
Nextjs does code splitting automaticlly, which means each page only loads whats necessary for that page. For other pages, will not be served initially.

In a production build of Next.js, whenever `<Link>` component appear in the browser's viewpoint, Next.js will prefetch the code for the linked page in the background. By the time you click the link, the code for the desitination page will already be loaded in the background, and the page transition will be near-instant.

## Assets
Next.js can serve static assets, like images, under the top-level *public* directory. Files inside *public* can be referenced from the root of the application.

For example, you can upload a image, such as apple.jpg to *public/images*, then you can refer it directly
```js
<img src="/images/apple.jpg">
```

## Nextjs Image
Nextjs provide a extension, *'next/image'*, for the `<img>` tag, which support
* responsive on different screen size
* only load image when it enter the viewport

Instead of optimizing images at build time, Next.js optimizes images on-demand, as users request them. your build times aren't increased, whether shipping 10 images or 10 million images.

Images are lazy loaded by default. That means your page speed isn't penalized for images outside the viewport. Images load as they are scrolled into viewport.

```js
import Image from 'next/image'

<Image src="/images/apple.jpg" height={400} width={400}/>
```

## Nextjs Head
To support customize the content for HTML `<head>`, such as page title, etc, Nextjs provide the `<Head>`.
```js
import Head from 'next/head'

return (
    <div>
        <Head>
            <title>First Post</title>
        </Head>
        <!-- rest code-->
    </div>
)
```
You can see that when you switch to *First Post* page, the page title change to *First Post* instead of *http://localhost:3000/posts/first-post*

## Nextjs Component
To create a reusable component, create a top-level directory named *components*, and put each component file inside it.

For example, to make all pages share same layout, such as same header and footer, we can add a file called *`layout.tsx`*, and use it as container in each page.

## Nextjs styled component
To apply css to component, we can add a css file next to component, and import it as module directly. To do this, the css file suffix should be *module.css*. CSS Module will locally scope CSS by automatically creating a unique class name, this allow you to use same CSS class names in different files without worrying about collisions.
```css
/*layout.module.css*/
.container {
    max-width: 36rem;
    padding: 0 1rem;
    margin: 3rem auto 6rem;
}
```
in *layout.tsx*, import it
```js
import styles from './layout.module.css'

export default function Layout({children}) {
    return (
        <div className={styles.container}>{children}</div>
    )
}
```
for CSS in *node_modules*, we can import it directly
## Nextjs Global CSS
CSS Module is usefull for single component, but if we want to share some CSS among all pages, we need global css file. To support this, Nextjs allow us to create a file under *pages* directory named *_app.tsx* with following content
```js
export default function App({Component, pageProps}) {
    return <Component {...pageProps} />
}
```
this *APP* component is the top-level component which will be common across all the different pages.

So to support Global CSS, just import the CSS file in *_app.tsx*. Note that to make it take effect, need to restart the server. And note that global css can only be import from *_app.tsx* file

In production, all CSS Module files will be automatically concatenated into *many minified and code-split* `.css` files.

## Nextjs Pre-rendering
By default, Nextjs pre-rendering every page. That means Nextjs generate HTML for each page in advance, instead of having it all done by client-side JS code.

Each generated HTML is associated with minimal JS code necessary for that page. When a page is loaded by browser, its JS code runs and make the page fully interactive.

To prove this, you can simply disbale JS on your browser, and refresh your page, you can find the page is still rendered, just without CSS. That's because Nextjs has pre-rendered the page into static HTML, allowing you to see the page without running JS.

And Nextjs has two forms of pre-rendering: *Static Generation* and *Server Side Render*, the difference is in ***when*** it generate the HTML for a page.
* Static Generation. Generate the HTML at *build* time, the pre-rendered HTML is then reused on each request.
* Server Side Render. Generate the HTML on each request

### Development VS Production
Note that in development mode, every page is pre-rendered on each request--even for pages that use *Static Generation*. 

To prove this, we can display current time in page, and test the difference under different mode
```js
<p>{new Date().toTimeString()}</p>
```
For production mode, run below command
```js
npm run build && npm run start
```
the page will show current time, such as *11:10:00 GMT*, if you refresh the page, the time will get updated, that's since webpack extra a JS code for it, when the page refresh, the JS code will be executed and then update the page. If you disable the JS, and click refresh, the page will always show *11:10:00 GMT*, the initial build time. This approve the page is *Static Generation* and pre-rendered and the generated HTML is reused on reach request.

For development mode, run below command
```js
npm run dev
```
you will find no matter you disable JS or not, the time will always get updated when you refresh the page. Thats because in development mode, the page is pre-rendered on each request, and the pre-rendered is finished on server side. This is also why you disable the JS on browser does not take effect.

### Static Generation VS Server Side Render
We recommend using *Static Generation* (with and without data) whenever possible which makes it much faster than having a server render the page on every request.

You should ask yourself: "Can I pre-render this page ahead of a user's request?" If the answer is yes, then you should choose Static Generation.

On the other hand, Static Generation is not a good idea if you cannot pre-render a page ahead of a user's request. Maybe your page shows frequently updated data, and the page content changes on every request.

In that case, you can use Server-side Rendering. It will be slower, but the pre-rendered page will always be up-to-date. Or you can skip pre-rendering and use client-side JavaScript to populate frequently updated data.

### Static Generation
*Static Generation* can be done with and without data.

If the page does not require fetching external data, it will automatically be statically generated when the app is built for production.

However, for some pages, it might depend on some external data, you need to access file system, fetch external API, query database at build time. In this case, we should use API *`getStaticProps`*.

In Nextjs, when you export a page component, you can also export an `<async>` function called *`getStaticProps`*. This function runs at build time in production at server side, and inside this function, you can fetch external data and send it as props to the page.
```js
export async function getStaticProps(context) {
  return {
    props: {}, // will be passed to the page component as props
  }
}
```
the *context* parameter is an object containing the following keys:
- `params` contains the route parameters for pages using dynamic routes. for example, if the page name is `[id].js`, then the `params` will look like `{id: ...}`
- `preview` is `true` if the page is in preview mode and `undefined` otherwise

And *`getStaticProps`* should return an object with
- `props` the props that will be received by the page component
- `revalidate` an optional amount in seconds after which a page re-generation can occur. This means even if a page is generated at build time, it can also be invalidated after specifed time count, server side will regenerate this page when a request come in after the time count. This allow you to update a single page without re-build whole product.
- `notFound` an optional boolean value to allow the page to return a 404 status and page
- `redirect` allow to redirect to internal and external resources, should match the shape `{destination: string, permanent: boolean}`

note that if `fallback` is `false` in `getStaticPaths`, the `nouFound` is not necessary.

Note that in development, this function runs on each request instead.

```js
import { getSortedPostsData } from '../utils/posts'

export default function Home({allPosts}) {
  return (
    <Layout home>
      <!-- other code-->
      <section className={`${utilStyles.headingMd} ${utilStyles.padding1px}`}>
        <h2 className={utilStyles.headingLg}>Blog</h2>
        <ul className={utilStyles.list}>
          {
            allPosts.map(({id, title, date}) => (
              <li className={utilStyles.listItem} key={id}>
                {title}
                <br/>
                {id}
                <br/>
                {date}
              </li>
            ))
          }
        </ul>
      </section>
    </Layout>
  )
}
export async function getStaticProps() {
  const allPosts = getSortedPostsData()
  return {
    props: {
      allPosts
    }
  }
}
```
*`getSortedPostsData`* return a array which contain all posts information, including the id, title, date for each md file.

Note that the return value of  *`getStaticProps`* must be an object, and it should has a *`props`* key, and the `allPosts` need to be wrapped in *`props`*. The *`props`* will be passed to Home component as a prop

Besides fetching data from file system, *`getStaticProps`* can also fetch data from external API or database. 

And keep in mind that this function only ***run on the server side***, it will never run on client side. it won't even be included in the JS bundle for the browser. That means you can write code such as querying database without sending the data to browser.

## Server Side Render
When the page content is based on the user request, it should use *Server Side Render*.

Similar with *Static Generation*, we need to export a *`getServerSideProps`* function
```js
export async function getServerSideProps(context) {
  return {
    props: {}, // will be passed to the page component as props
  }
}
```
the *'context'* here is an object, list some important keys of it:
* params: if this page use dynamic route, this param contains the route parameters
* req: the HTTP request object
* res: the HTTP response object
* query: an object representing the query string

*`getServerSideProps`* should return an object with
* props: an optional object with the props that will be received by the page component
* notFound: an optional boolean value to allow the page to return a 404 status
* redirect: an optional redirect value to allow redirecting to internal and external resources. it should match the shape of <code>{destination:string, permanent:boolean}</code>

```js
export async function getServerSideProps(context) {
  const res = await fetch(`https://.../data`)
  const data = await res.json()

  if (!data) {
    return {
      redirect: {
        destination: '/',
        permanent: false,
      },
    }
  }

  return {
    props: {}, // will be passed to the page component as props
  }
}
```
To give a quickly example, lets define a page which support to display user query info in request url.
```js
import Layout from '../components/layout'
import utilStyles from '../styles/common.module.css'

export default function Visitor({query}) {
    return (
        <Layout home={false}>
            <section className={utilStyles.headingMd}>
                <p>Hi visitor</p>
                <ul className={utilStyles.list}>
                {
                    Object.entries(query).map(([key, value]) => (
                        <li className={utilStyles.listItem} key={key}>
                            {key} = {value}
                        </li>
                    ))
                }
                </ul>
            </section>
        </Layout>
    )
}

export async function getServerSideProps(context) {
    //the query here is an object, store the query information in URL
    //for example, when user type http://localhost:3000/visitor?name=yangliu&sex=male
    //the query will be
    // {
    //     name: 'yangliu',
    //     sex: 'male'
    // }
    const query = context.query
    return {
        props: {
            query
        }
    }
}
```
You can notice that *`getServerSideProps`* pass the *query* information in *context* to *Visitor* component as a prop. And *Visitor* page will be rendered on each request.

## Client Side Render
If do not need pre-render, we can also use *Client Side Render*
* Statically generate parts of the page that do not require external data
* when the page loads, fetch external data from the client useing JS and populate the remaining parts.

## Nextjs Dynamic Route
In some case, the page path depend on the external data. For example, we want to scan all the markdown files under *posts* folder, then convert each file to a post page component. the page path will be dynamic.

To support this, Nextjs provide the dynamic route. we need to create a file, the file name should obey the format: `[$name].tsx`, the file name begin with `[` and end with `]`, and the name is arbitrary. In our case, we named it as `[id].tsx`.

in this file, we will write code to render a post page, just like other pages. What's new is that we need to export an async function called `getStaticPaths` to return a list of all possible values for `id`(note that each value should be an object that contains key `params`, and its value should be an object which contains key `id`), and use `getStaticProps` with params to get necessary data for given specified `id`
```js
//this method will be called by getStaticPaths
//the return value should be an array of objects,
//and the object should match the shape, otherwise, getStaticPaths will fail
// {
//     params: {
//         id: 'the id'
//     }
// }
export function getAllPostId() {
    const fileNames = fs.readdirSync(postsDirectory)

    return fileNames.map(fileName => {
        return {
          params: {
            id: fileName.replace(/\.md$/, '')
          }
        }
      })
}
```
in `[id].tsx`, we need to implement *`getStaticPaths`*
```js
export async function getStaticPaths() {
    const paths = getAllPostId()
    //the 'paths' and 'fallback' are required in the return object
    return {
      //the key name must be 'paths', the value is an array of objects
      paths,
      //if false, then any path not return by 'getStaticPaths' will result in 404
      //this is useful when there are only small number of paths to pre-render
      //if true, the path not in 'getStaticPaths' list will not result in 404, instead, Nextjs will server a 'fallback' version of that page on the first request, in background, Nextjs will invoke 'getStaticProps' through request path, when this is done, send it to browser, the page get updated. At same time, Nextjs add this path to the list of pre-render pages, subsequent request to the same path will serve the generated page, just like other pages pre-rendered at build time
      fallback: false
    }
}
```
for *fallback* version page,it means the page is rendered with empty props. To check if a page is in *fallback* mode, you can use `next/router`
```js
// pages/posts/[id].js
import { useRouter } from 'next/router'

function Post({ post }) {
  const router = useRouter()

  // If the page is not yet generated, this will be displayed
  // initially until getStaticProps() finishes running
  if (router.isFallback) {
    return <div>Loading...</div>
  }

  // Render post...
}
```
note that for `fallback: true` page, once the page is generated, subsequent request will reuse the generated page, just same as other pre-rendered static page. This option is usefull when you have huge size of static pages, you want to pre-render all those pages, but it will take forever. So we just pick a subset of pages, and use `fallback:true`, then when user request a page which is not in the initially pre-render list, show the *fallback* page first, then run *`getStaticProps`* at backend, when it finished, update the page, add this path to pre-render list.

`fallback:'blocking'` will not show *fallback* page, just block here and wait for the HTML to be generated, identical to *Server Side Render*. When it finished, cache the result for future request, add the path to pre-render list

Similar, we need to implement *`getStaticProps`* at same time
```js
import { getAllPostId, getPostData } from '../../utils/posts'

export async function getStaticProps({ params }) {
    const postData = await getPostData(params.id)
    return {
      props: {
        postData
      }
    }
}
```
Finally, render the post page with return *postData*

### Catch-all Routes
Dynamic routes can be extended to catch all paths by adding three dots(`...`) inside the brackets, for example:
* `pages/posts/[...id].js` match `/posts/a`, but also `/posts/a/b`, `/posts/a/b/c` and so on

in this way, the *`getStaticPaths`* must return an array as the value of the `id` key, such as
```js
return [
  {
    params: {
      // Statically Generates /posts/a/b/c
      id: ['a', 'b', 'c']
    }
  }
  //...
]
```
## 404 Page
To create a customized 404 page, create `/pages/404.tsx`. This file is statically generated at build time.

## Nextjs Environment Variables
Next.js has built-in support for loading environment variables from `.env*` files into `process.env`.
```js
//.env.development
DB_PATH=//localhost:3636
DB_USER=root
```
And the `process.env` can only be accessed in Nextjs data fetching methods and API routes.
```js
//pages/index.tsx
export function getStaticProps = async () => {
  //note that the process.env here is not a JS object actually,
  //Nextjs will replace the whole environment variable string to real value at build time
  const path = process.env.DB_PATH
  const user = process.env.DB_USER
}
```

note that `.env` is used for both *development* and *production* mode, and `.env.development` is used for *development*, `.env.production` is used for *production* mode.

Whats more, we can define `.env*.local` to override the environment variable setting in `.env*`.

And Nextjs will automatically expand variables(`$var`) in environment file, for example
```js
HOST_NAME=localhost
PORT=3000
HOST_PATH=http://$HOST_NAME:$PORT
```
to escape the dollar sign, need to use `\$`

To expose environment to browser, need to add prefix `NEXT_PUBLIC` to the environment variable
```js
NEXT_PUBLIC_ENVIRONMENT=Public environment variable
```
then it can be used anywhere
```js
//pages/index.tsx
export default function Page() {
  return (
    <div>
      <p>{process.env.NEXT_PUBLIC_ENVIRONMENT}</p>
    </div>
  )
}
```

## Depoy to Vercel
The easiest way to deploy Next.js to production is to use ***Vercel*** platform developed by the creator of Next.js. 

It only need several simple steps.

1. Sign up for [Vercel](https://vercel.com/signup)
2. Push your Next.js project to Github or Bitbucket, then import you project on Vercel
3. Click *Deploy* button, then you will get a deployment URL.

Whats more, if you create a Pull Request for this project, Vercel will detect this and create a development deployment for this branch, you can preview your change.