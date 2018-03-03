package synapticloop.h2zero.extension.routemaster;

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

public class RoutemasterRestfulServletExtension extends Extension {

	@Override
	public void generate(JSONObject extensionOptions, Database database, Options options, File outFile, boolean verbose) throws RenderException, ParseException {
		TemplarContext templarContext = getDefaultTemplarContext(extensionOptions, database, options);

		Parser restServantTemplarParser = getParser("/java-create-routemaster-rest-servant.templar", verbose);

		Parser baseRestServantTemplarParser = getParser("/java-create-routemaster-base-rest-servant.templar", verbose);
		String pathname = outFile + options.getOutputJava() + database.getPackagePath() + "/routemaster/servant/BaseServant.java";
		renderToFile(templarContext, baseRestServantTemplarParser, pathname, verbose);

		List<Table> tables = database.getTables();
		for (Table table : tables) {
			templarContext.add("table", table);
			pathname = outFile + options.getOutputJava() + database.getPackagePath() + "/routemaster/servant/" + table.getJavaClassName() + "Servant.java";
			renderToFile(templarContext, restServantTemplarParser, pathname, verbose);
		}
	}

}
