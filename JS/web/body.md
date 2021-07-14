Both `Request` and `Response` may contain body data. A body is an instance of any the following types:
* ArrayBuffer
* ArrayBufferView
* Blob/File
* string
* URLSearchParams
* FormData

The *Request* and *Response* interface share the following methods to extract a body, these all return a promise that is eventually resolved with the actual content
* arrayBuffer()
* blob()
* formData()
* json()
* text()

```js
const form = new FormData(document.getElementById('login-form'));
fetch('/login', {
  method: 'POST',
  body: form
});
```
both *Request* and *Response* will try to intelligently determine the content type through body content.

And a request will also automatically set a *Content-Type* header if none is set in the headers.