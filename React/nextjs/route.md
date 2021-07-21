# Next Routing

*Next.js* has a file-system based router built on the concept of *pages* directory. 

## Index routes
Nextjs router will automatically route files named `index` to the root of the directory
```js
pages/index.js => /
pages/post/index.js => /post
```
## Nested routes
If you create a nested folder structure files will be automatically routed in the same way still
```js
pages/post/first-post.js => /post/first-post
```

## Dynamic route segments
Defining routes by using predefined paths is not always enough for complex applications, for example, we have huge size of static pages, if we pre-render static html for all those pages, it will take forever.

Then we can set *fallback* to true, and let *getStaticPaths* just return part of our static pages paths. For all other static pages, render at request time.

In this situation, we need to use dynamic routes. In Nextjs, you can add brackets to a page to create a dynamic route, such as `pages/posts/[id].tsx`

### Static Generation
In some situation, we have huge size of static pages, but pre-render all of them take too much time when build production, Nextjs allow us use dymanic route to just pre-render part of them. In this way, we need to implement ***getStaticPaths***
```js
//pages/posts/[id].tsx
export const getStaticPaths = async () => {
    const paths = await ...    //to fetch the static pages paths
    return {
        paths,
        fallback: true
    }
}
```
the return value for ***getStaticPaths*** is an object,add it must has following properties:
* paths => an array of object, each object stand for one path, and this object must have a property *params* which in format {params : {id : "ssg-render"}}
* fallback => the fallback mode, could be `false`/`true`/`blocking`, *false* means unmatched URL will result in 404 error directly, *true* means when URL unmatched, return fallback version of page firstly, then render the page at backend, finally, send the results to client to update the page. *block* means do not send fallback version to client, block to wait backend finish rendering, then send the result to client

After get the paths, we need to implement ***getStaticProps***. Note that this method can also work for static route. Just need to remember this method is used to fetch data at build time and then render static page
```js
//pages/posts/[id].tsx
export const getStaticProps = async ({params}) => {//for dynamic route, nextjs will pass a context object to this method which contain propery 'params' and 'preview'
    const data = await fetchPostData(params.id)
    return {
        props: {
            data
        },
        revalidate: 10,//after this amount of seconds, Nextjs will mark this page as invalidate, if a request come in, nextjs will re-generate this page
        redirect: {destination: "/posts", permanent: false},
        notFound: false
    }
}
```

### Server Side Render
for dynamic route in *Server Side Render*, we need to implement ***getServerSideProps*** 
```js
export const getServerSideProps = async (context) => {
    return {
        props: {

        }
    }
}
```
the *context* contain following properties:
* params => the parames that match the dynamic route
* query => the query data in the URL
* req => HTTP request
* res => HTTP response

the return value for ***getServerSideProps***  is almost same as ***getStaticProps*** except that there is no *revalidate*, since ***getServerSideProps*** render this page at request time already.

### Multiple Dymanic Route Segment
When a route contain multiple dymanic segment, such as `pages/[id]/[comment].tsx`, the *params* object need to specify all parameters
```js
export const getServerSideProps = async ({params}) => {
    //now the params should be in format {id: "sg-ssr", comment: "a-comment"} for pages/sg-ssr/a-comment
}
```
### Catch all route
to catch all routes, just need to add three dots(`...`) inside the brackets, such as `pages/posts/[...id].tsx`, this route will catch `/posts/a`,`/posts/a/b` and `/posts/a/b/c` and so on.

And the *params* object need to be updated too, for example, for `/posts/a/b`, the params will be {id: ["a", "b"]}, its value is an array.

Note that `pages/posts/[...id].tsx` does not match the URL `/posts`, when user input `/posts`, it will render `/pages/posts/index.js` if it exist, if not, result in 404 error.

To make `pages/posts/[...id].tsx` match `/posts`, need to include the parameter in double brackets `pages/posts/[[...id]].tsx`

### Caveats
when a URL match static/dynamic route/catch all route, the priority is `prefined routes > dynamic routes > catch all routes`.

Take a look at following example
* `pages/post/create.js` will match `/post/create`
* `pages/post/[id].js` will match `/post/1`, `/post/2`, etc, but will not match `/post/create`
* `pages/post/[...slug].js` will match `/post/1/2`, `/post/a/b/c`, but not `/post/abc`, `/post/create`