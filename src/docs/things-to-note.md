

# Things to note

Looking at the above class, it is relatively straight-forward to generate 
multiple files.  Below is a list of things to note:

## Always get the default context

```
        // you __ALWAYS__ want to get the defaultTemplarContext.
        TemplarContext templarContext = getDefaultTemplarContext(extensionOptions, database, options);
```

This will set everything up, and place the following things into the context.

```
		TemplarContext templarContext = new TemplarContext(templarConfiguration);
		templarContext.add(JSONKeyConstants.DATABASE, database);                    // key is "database"
		templarContext.add(JSONKeyConstants.OPTIONS, options);                      // key is "options"
		templarContext.add(JSONKeyConstants.EXTENSION_OPTIONS, extensionOptions);   // key is "extensionOptions"
```

## Get the parser

Use the `getParser("/classpath/to/template.templar", verbose)` method which will set up the parser for you.

## Render

Use the `renderToFile(templarContext, templarParserFile, "output/path/file.name", verbose);` which will also 
add the output to the summary statistics.