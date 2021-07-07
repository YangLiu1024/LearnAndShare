# Introduction to next/router

Nextjs allow you access `router` object inside each page. 

## Router Properties 
The following is the definition of the `router` object returned by `useRouter`:
* `pathname`: `String` => current route. the path of the page in `/pages`, not include configured `basePath` or `locale`, such as `/posts/[id]`
* `query`: `Object` => the query string parsed to an object. it will be an empty object during prerendering if the page does not have data fetching requirements. Defaults is `{}`. for `/posts/abc`, the query will be `{id: "abc"}`
* `asPath`: `String` => the path(include the query) shown in browser without configured `basePath` or `locale`. normally, apply `query` to `pathname` can deduce `asPath`
* `isFallBack`: `boolean` => the flag whether the current page is in *fallback* mode
* `basePath`: `String` => the active *basePath*(if enabled)
* `isPreview`: `boolean` => the flag whether the application is in preview mode

## Router Methods
### router.push
Used to handle client-side transitions
```js
router.push(url, as, options)
```
* `url` is the URL to navigate to
* `as` the optional decorator for the URL that will be shown in the browser.
* `options` optional configuration object, containing:
  * `scroll` optional boolean, control scrolling to the top of the page after navigation, defaults is `true`
  * `shallow` update the page without rerunning date fetching methods, defaults is `false`

***router.push*** is useful to do redirecting
```js
import { useEffect } from 'react'
import { useRouter } from 'next/router'

// Here you would fetch and return the user
const useUser = () => ({ user: null, loading: false })

export default function Page() {
  const { user, loading } = useUser()
  const router = useRouter()

  useEffect(() => {
    if (!(user || loading)) {
      router.push('/login')
    }
  }, [user, loading])

  return <p>Redirecting...</p>
}
```
for the `url` and `as` parameters, besides use page path directly, we can use a URL object to define
```js
import { useRouter } from 'next/router'

export default function ReadMore({ post }) {
  const router = useRouter()

  return (
    <button
      type="button"
      onClick={() => {
        router.push({ // equals to router.push('/post/abc') if post.id = 'abc'
          pathname: '/post/[pid]',
          query: { pid: post.id },
        })
      }}
    >
      Click here to read more
    </button>
  )
}
```
### router.replace
Compared with ***router.push***, ***router.replace*** will not add a new URL entry into `history` stack
```js
router.replace(url, as, options)
```
for the parameters, they are totally same as ***router.push***
### router.prefetch
Prefetch pages for faster client-side transitions. This method is only useful for navigations without `next/link`, since `next/link` takes care of prefetching pages automatically.
```js
router.prefetch(url, as)
```
assume that you have a login page, and after login, redirect to *dashboard* page
```js
import { useCallback, useEffect } from 'react'
import { useRouter } from 'next/router'

export default function Login() {
  const router = useRouter()
  const handleSubmit = useCallback((e) => {
    e.preventDefault()

    fetch('/api/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        /* Form data */
      }),
    }).then((res) => {
      // Do a fast client-side transition to the already prefetched dashboard page
      if (res.ok) router.push('/dashboard')
    })
  }, [])

  useEffect(() => {
    // Prefetch the dashboard page
    router.prefetch('/dashboard')
  }, [])

  return (
    <form onSubmit={handleSubmit}>
      {/* Form fields */}
      <button type="submit">Login</button>
    </form>
  )
}
```
### router.back
navigate back in history, equals to clicking the browser's back button. it execute `window.history.back()`

### router.reload
reload the current URL. equals to clicking the browser's refresh button. it execute `window.location.reload()`

## Router Events
You can listen to different events happening inside the Nextjs router. List as below:
* `routeChangeStart(url, {shallow})` => Fire when a route start to change
* `routeChangeCompolete(url, { shallow})` => Fire when a route changed completely
* `routeChangeError(err, url, { shallow})` => Fire when there's an error when changeing routes, or a route load is cancelled. you can use `err.cancelled` to check

For example, you can modify `pages/_app.js` to add listener to router event
```js
//pages/_app.js
import { useEffect } from 'react'
import { useRouter } from 'next/router'

export default function MyApp({ Component, pageProps }) {
  const router = useRouter()

  useEffect(() => {
    const handleRouteChange = (url, { shallow }) => {
      console.log(
        `App is changing to ${url} ${
          shallow ? 'with' : 'without'
        } shallow routing`
      )
    }

    router.events.on('routeChangeStart', handleRouteChange)

    // If the component is unmounted, unsubscribe
    // from the event with the `off` method:
    return () => {
      router.events.off('routeChangeStart', handleRouteChange)
    }
  }, [])

  return <Component {...pageProps} />
}
```