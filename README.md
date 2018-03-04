 <a name="#documentr_top"></a>

> **This project requires JVM version of at least 1.7**






<a name="documentr_heading_0"></a>

# h2zero-extension-routemaster-restful <sup><sup>[top](documentr_top)</sup></sup>



> Routemaster extension for h2zero to generate restful services






<a name="documentr_heading_1"></a>

# Table of Contents <sup><sup>[top](documentr_top)</sup></sup>



 - [h2zero-extension-routemaster-restful](#documentr_heading_0)
 - [Table of Contents](#documentr_heading_1)
 - [Overview](#documentr_heading_2)
   - [What you need to do](#documentr_heading_3)
   - [The h2zero configuration file](#documentr_heading_4)
   - [A simple (ish) example](#documentr_heading_5)
   - [Things to note from the above](#documentr_heading_6)
 - [A simple (ish) example](#documentr_heading_7)
 - [Things to note](#documentr_heading_8)
   - [Always get the default context](#documentr_heading_9)
   - [Get the parser](#documentr_heading_10)
   - [Render](#documentr_heading_11)
 - [Building the Package](#documentr_heading_12)
   - [*NIX/Mac OS X](#documentr_heading_13)
   - [Windows](#documentr_heading_14)
 - [How to test](#documentr_heading_15)
 - [Running the Tests](#documentr_heading_16)
   - [*NIX/Mac OS X](#documentr_heading_17)
   - [Windows](#documentr_heading_18)
   - [Dependencies - Gradle](#documentr_heading_19)
   - [Dependencies - Maven](#documentr_heading_20)
   - [Dependencies - Downloads](#documentr_heading_21)






<a name="documentr_heading_2"></a>

# Overview <sup><sup>[top](documentr_top)</sup></sup>

This project is an extension to `h2zero` that generates a restful interface to the database (GET, POST, PUT, DELETE).  



<a name="documentr_heading_3"></a>

## What you need to do <sup><sup>[top](documentr_top)</sup></sup>

  1. Create an extension class that `extends` `synapticloop.h2zero.extension.Extension`
  1. Implement the `public void generate(JSONObject extensionOptions, Database database, Options options, File outFile, boolean verbose) throws RenderException, ParseException;` method
  1. Set-up the h2zero file:
  1. Away you go



<a name="documentr_heading_4"></a>

## The h2zero configuration file <sup><sup>[top](documentr_top)</sup></sup>

see the `src/test/resources/sample-include-sqlite3.h2zero` file for a configuration example below




<a name="documentr_heading_5"></a>

## A simple (ish) example <sup><sup>[top](documentr_top)</sup></sup>

The main class entry point is the `RoutemasterRestfulServletExtension.java`



```
{
"options": {
	"metrics": false,
	"database": "sqlite3",

	"generators": [ "java", "sql", "reports" ],

	"extensions" : [
		"synapticloop.h2zero.extension.routemaster.RoutemasterRestfulServletExtension"
	],
	
	"synapticloop.h2zero.extension.routemaster.RoutemasterRestfulServletExtension": {
		"pathPrefix": "extension"
	},

	"validators": {
		"UpdaterNameValidator": {
			"allowablePrefixes": "reset,"
		},

		"FinderNameValidator": {
			"allowablePrefixes": "find,calculate"
		}
	},

	"output": {
		"java": "src/test/java/",
		"sql": "src/test/resources/",
		"webapp": "src/test/resources/"
	}
},

"database": {
	"schema": "sample",
	"package": "synapticloop.sample.h2zero.sqlite3",
	"defaultStatementCacheSize": 1024,

	"tables": [
		{ "include": "./user_type.json" },
		{ "include": "./user_title.json" },
		{ "include": "./user.json" },
		{ "include": "./pet.sqlite3.json" },
		{ "include": "./user_pet.json" }
	],
}
}

```





<a name="documentr_heading_6"></a>

## Things to note from the above <sup><sup>[top](documentr_top)</sup></sup>



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



<a name="documentr_heading_7"></a>

# A simple (ish) example <sup><sup>[top](documentr_top)</sup></sup>

The main class entry point is the `RoutemasterRestfulServletExtension.java`



```
package synapticloop.h2zero.extension.routemaster;

/*
 * Copyright (c) 2018 synapticloop.
 * 
 * All rights reserved.
 *
 * This source code and any derived binaries are covered by the terms and
 * conditions of the Licence agreement ("the Licence").  You may not use this
 * source code or any derived binaries except in compliance with the Licence.
 * A copy of the Licence is available in the file named LICENCE shipped with
 * this source code or binaries.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * Licence for the specific language governing permissions and limitations
 * under the Licence.
 */

import java.io.File;
import java.util.List;

import org.json.JSONObject;

import synapticloop.h2zero.extension.Extension;
import synapticloop.h2zero.model.Database;
import synapticloop.h2zero.model.Options;
import synapticloop.h2zero.model.Table;
import synapticloop.templar.Parser;
import synapticloop.templar.exception.ParseException;
import synapticloop.templar.exception.RenderException;
import synapticloop.templar.utils.TemplarContext;

/**
 * This extension generates files that create a RESTful interface to a database 
 * through RouteMaster, based on NanoHTTPD
 */
public class RoutemasterRestfulServletExtension extends Extension {

	@Override
	public void generate(JSONObject extensionOptions, Database database, Options options, File outFile, boolean verbose) throws RenderException, ParseException {
		// you __ALWAYS__ want to get the defaultTemplarContext.
		TemplarContext templarContext = getDefaultTemplarContext(extensionOptions, database, options);

		// write out the BaseRestServant class
		writeBaseRestServant(database, options, outFile, verbose, templarContext);

		// write out the rest servants, one for each table
		writeRestServants(database, options, outFile, verbose, templarContext);

		// write out the routemaster routes file
		writeRestServantRoutes(database, options, outFile, verbose, templarContext);
	}

	/**
	 * Write out the base rest servant - which is a single class, upon which all 
	 * other files are dependent.
	 * 
	 * @param database The h2zero database model
	 * @param options The h2zero options
	 * @param outFile The base directory
	 * @param verbose whether the user wants verbose output
	 * @param templarContext the templar context
	 * 
	 * @throws ParseException If there was an error parsing the templar template
	 * @throws RenderException If there was an error rendering the file
	 */
	private void writeBaseRestServant(Database database, Options options, File outFile, boolean verbose, TemplarContext templarContext) throws ParseException, RenderException {
		// get the templar parser
		Parser baseRestServantTemplarParser = getParser("/java-create-routemaster-base-rest-servant.templar", verbose);

		// determine the file path
		String pathname = outFile + options.getOutputJava() + database.getPackagePath() + "/routemaster/servant/BaseServant.java";

		// render to the file
		renderToFile(templarContext, baseRestServantTemplarParser, pathname, verbose);
	}

	/**
	 * Write out the RESTful servant, one for each table
	 * 
	 * @param database The h2zero database model
	 * @param options The h2zero options
	 * @param outFile The base directory
	 * @param verbose whether the user wants verbose output
	 * @param templarContext the templar context
	 * 
	 * @throws ParseException If there was an error parsing the templar template
	 * @throws RenderException If there was an error rendering the file
	 */
	private void writeRestServants(Database database, Options options, File outFile, boolean verbose, TemplarContext templarContext) throws RenderException, ParseException {
		// get the templar parser
		Parser restServantTemplarParser = getParser("/java-create-routemaster-rest-servant.templar", verbose);

		// get all of the tables
		List<Table> tables = database.getTables();
		for (Table table : tables) {
			// for each table, add it to the context
			templarContext.add("table", table);

			// determine the file path
			String pathname = outFile + options.getOutputJava() + database.getPackagePath() + "/routemaster/servant/" + table.getJavaClassName() + "Servant.java";

			// render to the file
			renderToFile(templarContext, restServantTemplarParser, pathname, verbose);
		}
	}

	/**
	 * Write out the example routes for the RESTful interface
	 * 
	 * @param database The h2zero database model
	 * @param options The h2zero options
	 * @param outFile The base directory
	 * @param verbose whether the user wants verbose output
	 * @param templarContext the templar context
	 * 
	 * @throws ParseException If there was an error parsing the templar template
	 * @throws RenderException If there was an error rendering the file
	 */
	private void writeRestServantRoutes(Database database, Options options, File outFile, boolean verbose, TemplarContext templarContext) throws ParseException, RenderException {
		// get the templar parser
		Parser baseRestServantTemplarParser = getParser("/java-create-routemaster-rest-servant-routes.templar", verbose);

		// render to the file
		String pathname = outFile + options.getOutputSql() + "routemaster.rest.properties";

		// render to the file
		renderToFile(templarContext, baseRestServantTemplarParser, pathname, verbose);
	}

}


```






<a name="documentr_heading_8"></a>

# Things to note <sup><sup>[top](documentr_top)</sup></sup>

Looking at the above class, it is relatively straight-forward to generate 
multiple files.  Below is a list of things to note:



<a name="documentr_heading_9"></a>

## Always get the default context <sup><sup>[top](documentr_top)</sup></sup>



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





<a name="documentr_heading_10"></a>

## Get the parser <sup><sup>[top](documentr_top)</sup></sup>

Use the `getParser("/classpath/to/template.templar", verbose)` method which will set up the parser for you.



<a name="documentr_heading_11"></a>

## Render <sup><sup>[top](documentr_top)</sup></sup>

Use the `renderToFile(templarContext, templarParserFile, "output/path/file.name", verbose);` which will also 
add the output to the summary statistics.



<a name="documentr_heading_12"></a>

# Building the Package <sup><sup>[top](documentr_top)</sup></sup>



<a name="documentr_heading_13"></a>

## *NIX/Mac OS X <sup><sup>[top](documentr_top)</sup></sup>

From the root of the project, simply run

`./gradlew build`




<a name="documentr_heading_14"></a>

## Windows <sup><sup>[top](documentr_top)</sup></sup>

`./gradlew.bat build`


This will compile and assemble the artefacts into the `build/libs/` directory.

Note that this may also run tests (if applicable see the Testing notes)




<a name="documentr_heading_15"></a>

# How to test <sup><sup>[top](documentr_top)</sup></sup>

As the extension will generate additional code for the project, there is a 
slight roundabout way of testing the code.

  1. build the code and publish it to maven local
  1. have a separate build.gradle file that will reference the class path (including maven local repository)

The simplest way is to:

`gradle build -x test`

then

`gradle -b build.h2zero.sqlite3.gradle h2zero`

or more simply (for *NIX machines)

`gradle build -x test; gradle -b build.h2zero.sqlite3.gradle h2zero`



<a name="documentr_heading_16"></a>

# Running the Tests <sup><sup>[top](documentr_top)</sup></sup>



<a name="documentr_heading_17"></a>

## *NIX/Mac OS X <sup><sup>[top](documentr_top)</sup></sup>

From the root of the project, simply run

`gradle --info test`

if you do not have gradle installed, try:

`gradlew --info test`



<a name="documentr_heading_18"></a>

## Windows <sup><sup>[top](documentr_top)</sup></sup>

From the root of the project, simply run

`gradle --info test`

if you do not have gradle installed, try:

`./gradlew.bat --info test`


The `--info` switch will also output logging for the tests



<a name="documentr_heading_19"></a>

## Dependencies - Gradle <sup><sup>[top](documentr_top)</sup></sup>



```
dependencies {
	runtime(group: 'synapticloop', name: 'h2zero-extension-routemaster-restful', version: '1.0.0', ext: 'jar')

	compile(group: 'synapticloop', name: 'h2zero-extension-routemaster-restful', version: '1.0.0', ext: 'jar')
}
```



or, more simply for versions of gradle greater than 2.1



```
dependencies {
	runtime 'synapticloop:h2zero-extension-routemaster-restful:1.0.0'

	compile 'synapticloop:h2zero-extension-routemaster-restful:1.0.0'
}
```





<a name="documentr_heading_20"></a>

## Dependencies - Maven <sup><sup>[top](documentr_top)</sup></sup>



```
<dependency>
	<groupId>synapticloop</groupId>
	<artifactId>h2zero-extension-routemaster-restful</artifactId>
	<version>1.0.0</version>
	<type>jar</type>
</dependency>
```





<a name="documentr_heading_21"></a>

## Dependencies - Downloads <sup><sup>[top](documentr_top)</sup></sup>


You will also need to download the following dependencies:



### cobertura dependencies

  - `net.sourceforge.cobertura:cobertura:2.1.1`: (It may be available on one of: [bintray](https://bintray.com/net.sourceforge.cobertura/maven/cobertura/2.1.1/view#files/net.sourceforge.cobertura/cobertura/2.1.1) [mvn central](http://search.maven.org/#artifactdetails|net.sourceforge.cobertura|cobertura|2.1.1|jar))


### compile dependencies

  - `synapticloop:h2zero:2.2.1`: (It may be available on one of: [bintray](https://bintray.com/synapticloop/maven/h2zero/2.2.1/view#files/synapticloop/h2zero/2.2.1) [mvn central](http://search.maven.org/#artifactdetails|synapticloop|h2zero|2.2.1|jar))
  - `synapticloop:routemaster:2.3.0`: (It may be available on one of: [bintray](https://bintray.com/synapticloop/maven/routemaster/2.3.0/view#files/synapticloop/routemaster/2.3.0) [mvn central](http://search.maven.org/#artifactdetails|synapticloop|routemaster|2.3.0|jar))


### runtime dependencies

  - `synapticloop:h2zero:2.2.1`: (It may be available on one of: [bintray](https://bintray.com/synapticloop/maven/h2zero/2.2.1/view#files/synapticloop/h2zero/2.2.1) [mvn central](http://search.maven.org/#artifactdetails|synapticloop|h2zero|2.2.1|jar))
  - `synapticloop:routemaster:2.3.0`: (It may be available on one of: [bintray](https://bintray.com/synapticloop/maven/routemaster/2.3.0/view#files/synapticloop/routemaster/2.3.0) [mvn central](http://search.maven.org/#artifactdetails|synapticloop|routemaster|2.3.0|jar))


### shadow dependencies

  - `mysql:mysql-connector-java:6.0.6`: (It may be available on one of: [bintray](https://bintray.com/mysql/maven/mysql-connector-java/6.0.6/view#files/mysql/mysql-connector-java/6.0.6) [mvn central](http://search.maven.org/#artifactdetails|mysql|mysql-connector-java|6.0.6|jar))
  - `org.xerial:sqlite-jdbc:3.21.0.1`: (It may be available on one of: [bintray](https://bintray.com/org.xerial/maven/sqlite-jdbc/3.21.0.1/view#files/org.xerial/sqlite-jdbc/3.21.0.1) [mvn central](http://search.maven.org/#artifactdetails|org.xerial|sqlite-jdbc|3.21.0.1|jar))
  - `commons-io:commons-io:2.5`: (It may be available on one of: [bintray](https://bintray.com/commons-io/maven/commons-io/2.5/view#files/commons-io/commons-io/2.5) [mvn central](http://search.maven.org/#artifactdetails|commons-io|commons-io|2.5|jar))
  - `synapticloop:h2zero:2.2.1`: (It may be available on one of: [bintray](https://bintray.com/synapticloop/maven/h2zero/2.2.1/view#files/synapticloop/h2zero/2.2.1) [mvn central](http://search.maven.org/#artifactdetails|synapticloop|h2zero|2.2.1|jar))
  - `synapticloop:routemaster:2.3.0`: (It may be available on one of: [bintray](https://bintray.com/synapticloop/maven/routemaster/2.3.0/view#files/synapticloop/routemaster/2.3.0) [mvn central](http://search.maven.org/#artifactdetails|synapticloop|routemaster|2.3.0|jar))


### testCompile dependencies

  - `junit:junit:4.12`: (It may be available on one of: [bintray](https://bintray.com/junit/maven/junit/4.12/view#files/junit/junit/4.12) [mvn central](http://search.maven.org/#artifactdetails|junit|junit|4.12|jar))
  - `org.mockito:mockito-all:1.10.19`: (It may be available on one of: [bintray](https://bintray.com/org.mockito/maven/mockito-all/1.10.19/view#files/org.mockito/mockito-all/1.10.19) [mvn central](http://search.maven.org/#artifactdetails|org.mockito|mockito-all|1.10.19|jar))
  - `com.github.stefanbirkner:system-rules:1.16.1`: (It may be available on one of: [bintray](https://bintray.com/com.github.stefanbirkner/maven/system-rules/1.16.1/view#files/com.github.stefanbirkner/system-rules/1.16.1) [mvn central](http://search.maven.org/#artifactdetails|com.github.stefanbirkner|system-rules|1.16.1|jar))
  - `commons-io:commons-io:2.5`: (It may be available on one of: [bintray](https://bintray.com/commons-io/maven/commons-io/2.5/view#files/commons-io/commons-io/2.5) [mvn central](http://search.maven.org/#artifactdetails|commons-io|commons-io|2.5|jar))
  - `com.mashape.unirest:unirest-java:1.4.9`: (It may be available on one of: [bintray](https://bintray.com/com.mashape.unirest/maven/unirest-java/1.4.9/view#files/com.mashape.unirest/unirest-java/1.4.9) [mvn central](http://search.maven.org/#artifactdetails|com.mashape.unirest|unirest-java|1.4.9|jar))


### testRuntime dependencies

  - `junit:junit:4.12`: (It may be available on one of: [bintray](https://bintray.com/junit/maven/junit/4.12/view#files/junit/junit/4.12) [mvn central](http://search.maven.org/#artifactdetails|junit|junit|4.12|jar))
  - `org.mockito:mockito-all:1.10.19`: (It may be available on one of: [bintray](https://bintray.com/org.mockito/maven/mockito-all/1.10.19/view#files/org.mockito/mockito-all/1.10.19) [mvn central](http://search.maven.org/#artifactdetails|org.mockito|mockito-all|1.10.19|jar))
  - `com.github.stefanbirkner:system-rules:1.16.1`: (It may be available on one of: [bintray](https://bintray.com/com.github.stefanbirkner/maven/system-rules/1.16.1/view#files/com.github.stefanbirkner/system-rules/1.16.1) [mvn central](http://search.maven.org/#artifactdetails|com.github.stefanbirkner|system-rules|1.16.1|jar))
  - `mysql:mysql-connector-java:6.0.6`: (It may be available on one of: [bintray](https://bintray.com/mysql/maven/mysql-connector-java/6.0.6/view#files/mysql/mysql-connector-java/6.0.6) [mvn central](http://search.maven.org/#artifactdetails|mysql|mysql-connector-java|6.0.6|jar))
  - `org.xerial:sqlite-jdbc:3.21.0.1`: (It may be available on one of: [bintray](https://bintray.com/org.xerial/maven/sqlite-jdbc/3.21.0.1/view#files/org.xerial/sqlite-jdbc/3.21.0.1) [mvn central](http://search.maven.org/#artifactdetails|org.xerial|sqlite-jdbc|3.21.0.1|jar))
  - `commons-io:commons-io:2.5`: (It may be available on one of: [bintray](https://bintray.com/commons-io/maven/commons-io/2.5/view#files/commons-io/commons-io/2.5) [mvn central](http://search.maven.org/#artifactdetails|commons-io|commons-io|2.5|jar))
  - `com.mashape.unirest:unirest-java:1.4.9`: (It may be available on one of: [bintray](https://bintray.com/com.mashape.unirest/maven/unirest-java/1.4.9/view#files/com.mashape.unirest/unirest-java/1.4.9) [mvn central](http://search.maven.org/#artifactdetails|com.mashape.unirest|unirest-java|1.4.9|jar))

**NOTE:** You may need to download any dependencies of the above dependencies in turn (i.e. the transitive dependencies)

--

> `This README.md file was hand-crafted with care utilising synapticloop`[`templar`](https://github.com/synapticloop/templar/)`->`[`documentr`](https://github.com/synapticloop/documentr/)

--
