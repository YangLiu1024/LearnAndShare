The Request interface of the Fetch API represents a resource request.

# Request properties
* body => read only, the readable body contents
* bodyUsed => read only, a boolean flag indicate whether the body has been used in a request yet
* cache => read only, the cache mode of the request, eg default/reload/no-cache
* credentials => read only, contain the credentials of the request, eg omit/same-orign/include, the default is same-origin
* destination => read only, return a string describing the request's destination. this is a string indicating the type of the content being requested
* headers => read only, the assocaited headers object of the request
* integrity => read only, contain the subresource integrity value of the request, eg sha256-BpfBasdlkjlk
* method => read only, the request's method, eg GET/POST
* mode => read only, the mode of the request, eg cors/no-cors/same-origin/navigate
* redirect => read only, the mode for how redirects are handled, eg follow/error/manual
* url => read only, the URL of the request

# Request methods
* arrayBuffer
* blob
* formData
* json
* text
* clone

note that the request body functions can be run only once, subsequent calls will resolve with empty body content

```js
const request = new Request('https://example.com', {method: 'POST', body: '{"foo": "bar"}'});

const url = request.url;
const method = request.method;
const credentials = request.credentials;
const bodyUsed = request.bodyUsed;
```