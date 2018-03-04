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
