
## Things to note from the above

```
  "options:{
  
  ...
  
  
    "extensions" : [
        "synapticloop.h2zero.extension.routemaster.RoutemasterRestfulServletExtension"
    ],
    
    "synapticloop.h2zero.extension.routemaster.RoutemasterRestfulServletExtension": {
        "pathPrefix": "extension"
    },
    
  ...
  
  }
```

Extension are placed in the String JSON Array with the `extensions` key.  For any 
other options or configuration that is required by the extension, place another 
JSON object within the `options` object key on the class name that the extension 
uses.

# A simple (ish) example

The main class entry point is the `RoutemasterRestfulServletExtension.java`