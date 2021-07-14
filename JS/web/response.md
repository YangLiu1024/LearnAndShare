The `response` interface of the *Fetch API* represents the response to a request

# Response Properties
* Response.body => read only, a readable stream of the body contents
* Response.bodyUsed => read only, a falg indicate if the body of this reponse has been used yet
* Response.headers => read only, the headers object associated with the response
* Response.ok => read only, a boolean falg indicate whether the response was successful(the status in range 200-299 or not)
* Response.redirected => read only, indicates whether or not the response is the result of a redirect
* Response.status => read only, the status code for a response
* Response.statusText => read only, the status message corresponding to the status code
* Response.type => read only, the type of response, eg basic.cors
* Response.url => read only, the URL of response

# Response Methods
* arrayBuffer() => return a promise that resolve with an *ArrayBuffer* representation of the response body
* blob() => return a pomise that resolve with a *Blob* representation of the response body
* text() => return a promise that resolve with a text representation of the response body
* formData() => return a promise that resolve with a *FormData* representation of the response body
* json() => return a promise that resolve with the result of parsing the response body text as JSON
* redirect() => create a new response with a different URL
* clone() => create a clone of a *Response* object
* error() => return a new *Response* object associated with a network error

# Example
Fetch local image
```js
//html
<img src="" class="my-image"/>

//js
let myImage = document.querySelector('.my-image')
//fetch local image resource
fetch('flower.png')
    .then(res => {
        if (!res.ok) {
            throw new Error('Fail to fetch image ' + res.status)
        }
        return res.blob()
    })
    .then(blob => myImage.src=URL.createObjectURL(blob))
    .catch(err => {

    })
```
Ajax call
```js
// Function to do an Ajax call
const doAjax = async () => {
  const response = await fetch('Ajax.php'); // Generate the Response object
  if (response.ok) {
    return await response.json(); // Get JSON value from the response body
  } else {
    return Promise.reject('*** PHP file not found');
  }
}

// Call the function and output value or error message to console
doAjax().then(console.log).catch(console.log);
```
