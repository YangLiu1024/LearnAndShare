the *Headers* interface of the *Fetch* API allow you to perform various actions on HTTP request and response headers.

These actions include retrieving, setting, adding to, and removing headers from the list of the request's headers. 

A Headers object has an associated header list, which is initially empty and consists of zero or more name and value pairs

You can retrieve a *Headers* object via `Request.headers` and `Response.headers` properties.

# Headers methods
* append
* delete
* entries
* get
* has
* keys
* set
* values

```js
var myHeaders = new Headers({
    'Content-Type': 'text/xml'
});

// or, using an array of arrays:
myHeaders = new Headers([
    ['Content-Type', 'text/xml']
]);

myHeaders.get('Content-Type') // should return 'text/xml'
```