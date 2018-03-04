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
 - [Building the Package](#documentr_heading_3)
   - [*NIX/Mac OS X](#documentr_heading_4)
   - [Windows](#documentr_heading_5)
 - [Running the Tests](#documentr_heading_6)
   - [*NIX/Mac OS X](#documentr_heading_7)
   - [Windows](#documentr_heading_8)
   - [Dependencies - Gradle](#documentr_heading_9)
   - [Dependencies - Maven](#documentr_heading_10)
   - [Dependencies - Downloads](#documentr_heading_11)






<a name="documentr_heading_2"></a>

# Overview <sup><sup>[top](documentr_top)</sup></sup>


This project is an extension to `h2zero` that generates a restful interface to the database (GET, POST, PUT, DELETE).  

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
		writeRestServantRoutes(extensionOptions, database, options, outFile, verbose, templarContext);
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
	 * @param extensionOptions The extension options that are passed through from the h2zero file
	 * @param database The h2zero database model
	 * @param options The h2zero options
	 * @param outFile The base directory
	 * @param verbose whether the user wants verbose output
	 * @param templarContext the templar context
	 * 
	 * @throws ParseException If there was an error parsing the templar template
	 * @throws RenderException If there was an error rendering the file
	 */
	private void writeRestServantRoutes(JSONObject extensionOptions, Database database, Options options, File outFile, boolean verbose, TemplarContext templarContext) throws ParseException, RenderException {
		// get the templar parser
		Parser baseRestServantTemplarParser = getParser("/java-create-routemaster-rest-servant-routes.templar", verbose);

		// add the extensionOptions to the templar context so that they can be picked up
		templarContext.add("extensionOptions", extensionOptions);

		// render to the file
		String pathname = outFile + options.getOutputSql() + "routemaster.rest.properties";

		// render to the file
		renderToFile(templarContext, baseRestServantTemplarParser, pathname, verbose);
	}

}


```


'



<a name="documentr_heading_3"></a>

# Building the Package <sup><sup>[top](documentr_top)</sup></sup>



<a name="documentr_heading_4"></a>

## *NIX/Mac OS X <sup><sup>[top](documentr_top)</sup></sup>

From the root of the project, simply run

`./gradlew build`




<a name="documentr_heading_5"></a>

## Windows <sup><sup>[top](documentr_top)</sup></sup>

`./gradlew.bat build`


This will compile and assemble the artefacts into the `build/libs/` directory.

Note that this may also run tests (if applicable see the Testing notes)



<a name="documentr_heading_6"></a>

# Running the Tests <sup><sup>[top](documentr_top)</sup></sup>



<a name="documentr_heading_7"></a>

## *NIX/Mac OS X <sup><sup>[top](documentr_top)</sup></sup>

From the root of the project, simply run

`gradle --info test`

if you do not have gradle installed, try:

`gradlew --info test`



<a name="documentr_heading_8"></a>

## Windows <sup><sup>[top](documentr_top)</sup></sup>

From the root of the project, simply run

`gradle --info test`

if you do not have gradle installed, try:

`./gradlew.bat --info test`


The `--info` switch will also output logging for the tests



<a name="documentr_heading_9"></a>

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





<a name="documentr_heading_10"></a>

## Dependencies - Maven <sup><sup>[top](documentr_top)</sup></sup>



```
<dependency>
	<groupId>synapticloop</groupId>
	<artifactId>h2zero-extension-routemaster-restful</artifactId>
	<version>1.0.0</version>
	<type>jar</type>
</dependency>
```





<a name="documentr_heading_11"></a>

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
