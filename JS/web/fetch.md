`Fetch` API provide an interface for fetching resources(including across the network) using `Request` and `Response`

it behavior simiar with *XMLHttpRequest*, but the new API provide a more powerful and flexiable feature set.

The `fetch` method take one mandatory argument, the path to the resource you want to fetch. it return a *Promise* that resolves to the response to that request.

You can also optionally pass in an *init* options object as the second argument.

The main difference between `fetch` and `ajax`
* the *Promise* returned from `fetch` ***won't*** reject on HTTP error status even if the response is an HTTP 404 or 500. Instead, it resolve normally(with *OK* status set to *false*), and it will only reject on network failure or if anything prevented the request from completing 
* `fetch` will not send cross-origin cookies unless you set the *credentials init options* 

```js
// Example POST method implementation:
async function postData(url = '', data = {}) {
  // Default options are marked with *
  const response = await fetch(url, {
    method: 'POST', // *GET, POST, PUT, DELETE, etc.
    mode: 'cors', // no-cors, *cors, same-origin
    cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
    credentials: 'same-origin', // include, *same-origin, omit
    headers: {
      'Content-Type': 'application/json'
      // 'Content-Type': 'application/x-www-form-urlencoded',
    },
    redirect: 'follow', // manual, *follow, error
    referrerPolicy: 'no-referrer', // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
    body: JSON.stringify(data) // body data type must match "Content-Type" header
  });
  return response.json(); // parses JSON response into native JavaScript objects
}

postData('https://example.com/answer', { answer: 42 })
  .then(data => {
    console.log(data); // JSON data parsed by `data.json()` call
  });
```

uploading multiple files
```js
//html
<input type="file" multiple/>

//js
const formData = new FormData();
const photos = document.querySelector('input[type="file"][multiple]');

formData.append('title', 'My Vegas Vacation');
for (let i = 0; i < photos.files.length; i++) {
  formData.append('photos', photos.files[i]);
}

fetch('https://example.com/posts', {
  method: 'POST',
  body: formData,
})
.then(response => response.json())
.then(result => {
  console.log('Success:', result);
})
.catch(error => {
  console.error('Error:', error);
})
```

# Feature detection
```js
if (window.fetch) {//check if fetch is defined 

}
```