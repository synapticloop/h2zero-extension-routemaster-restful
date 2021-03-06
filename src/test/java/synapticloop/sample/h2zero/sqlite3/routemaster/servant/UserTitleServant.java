package synapticloop.sample.h2zero.sqlite3.routemaster.servant;

//- - - - thoughtfully generated by synapticloop h2zero - - - - 
//with the use of synapticloop templar templating language
//        (java-create-routemaster-rest-servant.templar)

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.ResponseException;
import synapticloop.h2zero.base.exception.H2ZeroFinderException;
import synapticloop.h2zero.base.exception.H2ZeroPrimaryKeyException;
import synapticloop.nanohttpd.utils.HttpUtils;
import synapticloop.sample.h2zero.sqlite3.finder.UserTitleFinder;
import synapticloop.sample.h2zero.sqlite3.model.UserTitle;
import synapticloop.sample.h2zero.sqlite3.model.util.Constants;

public class UserTitleServant extends BaseServant {

	private static final String APPLICATION_JSON = "application/json";

	public UserTitleServant(String routeContext, List<String> params) {
		super(routeContext, params);
	}

	@Override
	public Response doGet(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {

		if(null != restParams) {
			String primaryKeyString = restParams.get(Constants.USER_TITLE_ID_USER_TITLE);
			if(null != primaryKeyString && primaryKeyString.trim().length() != 0) {
				try {
					Long primaryKey = Long.parseLong(primaryKeyString);
					UserTitle userTitle = UserTitleFinder.findByPrimaryKey(primaryKey);
					return(HttpUtils.okResponse(APPLICATION_JSON, userTitle.toJsonString()));
				} catch (NumberFormatException ex) {
					return(HttpUtils.badRequestResponse(APPLICATION_JSON, getErrorObjectAsString("Could not parse '" + primaryKeyString + "' to a valid number.")));
				} catch(H2ZeroFinderException ex) {
					return(HttpUtils.notFoundResponse(APPLICATION_JSON, getErrorObjectAsString("Could not find 'UserTitle' with primary key of '" + primaryKeyString + "'.")));
				}
			}
		}

		// at this point - there were no parameters, or the parameter was incorrect, so find all

		List<UserTitle> findAll;
		try {
			findAll = UserTitleFinder.findAll();
		} catch (SQLException ex) {
			return(HttpUtils.internalServerErrorResponse(APPLICATION_JSON, getErrorObjectAsString("SQL Exception, message was: " + ex.getMessage())));
		}
		StringBuilder stringBuilder = new StringBuilder("[");
		boolean first = true;
		for (UserTitle userTitle : findAll) {
			if(!first) {
				stringBuilder.append(",");
			}
			stringBuilder.append(userTitle.toJsonString());
			first = false;
		}
		stringBuilder.append("]");
		return(HttpUtils.okResponse(APPLICATION_JSON, stringBuilder.toString()));
	}

	@Override
	public Response doPost(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {
		// This is a constant table and cannot be altered
		return(HttpUtils.forbiddenResponse());
	}

	@Override
	public Response doPut(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {
		// This is a constant table and cannot be altered
		return(HttpUtils.forbiddenResponse());
	}

	@Override
	public Response doDelete(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {
		// This is a constant table and cannot be altered
		return(HttpUtils.forbiddenResponse());
	}

}
