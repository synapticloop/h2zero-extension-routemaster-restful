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
import synapticloop.sample.h2zero.sqlite3.deleter.UserDeleter;
import synapticloop.sample.h2zero.sqlite3.finder.UserFinder;
import synapticloop.sample.h2zero.sqlite3.model.User;
import synapticloop.sample.h2zero.sqlite3.model.util.Constants;

public class UserServant extends BaseServant {

	private static final String APPLICATION_JSON = "application/json";

	public UserServant(String routeContext, List<String> params) {
		super(routeContext, params);
	}

	@Override
	public Response doGet(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {

		if(null != restParams) {
			String primaryKeyString = restParams.get(Constants.USER_ID_USER);
			if(null != primaryKeyString && primaryKeyString.trim().length() != 0) {
				try {
					Long primaryKey = Long.parseLong(primaryKeyString);
					User user = UserFinder.findByPrimaryKey(primaryKey);
					return(HttpUtils.okResponse(APPLICATION_JSON, user.toJsonString()));
				} catch (NumberFormatException ex) {
					return(HttpUtils.badRequestResponse(APPLICATION_JSON, getErrorObjectAsString("Could not parse '" + primaryKeyString + "' to a valid number.")));
				} catch(H2ZeroFinderException ex) {
					return(HttpUtils.notFoundResponse(APPLICATION_JSON, getErrorObjectAsString("Could not find 'User' with primary key of '" + primaryKeyString + "'.")));
				}
			}
		}

		// at this point - there were no parameters, or the parameter was incorrect, so find all

		List<User> findAll;
		try {
			findAll = UserFinder.findAll();
		} catch (SQLException ex) {
			return(HttpUtils.internalServerErrorResponse(APPLICATION_JSON, getErrorObjectAsString("SQL Exception, message was: " + ex.getMessage())));
		}
		StringBuilder stringBuilder = new StringBuilder("[");
		boolean first = true;
		for (User user : findAll) {
			if(!first) {
				stringBuilder.append(",");
			}
			stringBuilder.append(user.toJsonString());
			first = false;
		}
		stringBuilder.append("]");
		return(HttpUtils.okResponse(APPLICATION_JSON, stringBuilder.toString()));
	}

	@Override
	public Response doPost(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {
		if(null == httpSession) {
			return(HttpUtils.badRequestResponse());
		}

		Map<String, String> files = new HashMap<String, String>();
		try {
			httpSession.parseBody(files);
		} catch (IOException | ResponseException ex) {
			return(HttpUtils.internalServerErrorResponse(APPLICATION_JSON, getErrorObjectAsString("Exception, message was: " + ex.getMessage())));
		}

		Map<String, List<String>> parameters = httpSession.getParameters();

		if(null == parameters) {
			return(HttpUtils.badRequestResponse());
		}

		Long primaryKey = null;
		try {
			Long idUserType = castLong(getFirstParameter("idUserType", parameters));
			Boolean flIsAlive = castBoolean(getFirstParameter("flIsAlive", parameters));
			Integer numAge = castInteger(getFirstParameter("numAge", parameters));
			String nmUsername = castString(getFirstParameter("nmUsername", parameters));
			String txtAddressEmail = castString(getFirstParameter("txtAddressEmail", parameters));
			String txtPassword = castString(getFirstParameter("txtPassword", parameters));
			Timestamp dtmSignup = castTimestamp(getFirstParameter("dtmSignup", parameters));
			User user = new User(null, idUserType, flIsAlive, numAge, nmUsername, txtAddressEmail, txtPassword, dtmSignup);

			user.insert();
			primaryKey = user.getPrimaryKey();
		} catch (ServantException | H2ZeroPrimaryKeyException ex) {
			return(HttpUtils.badRequestResponse(APPLICATION_JSON, getErrorObjectAsString(ex.getMessage())));
		} catch (SQLException ex) {
			return(HttpUtils.internalServerErrorResponse(APPLICATION_JSON, getErrorObjectAsString("SQL Exception, message was: " + ex.getMessage())));
		}
		return(HttpUtils.okResponse(APPLICATION_JSON, "{\"primaryKey\": " + primaryKey + "}"));
	}

	@Override
	public Response doPut(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {

		User user = null;

		if(null != restParams) {
			String primaryKeyString = restParams.get(Constants.USER_ID_USER);
			if(null != primaryKeyString) {
				try {
					Long primaryKey = Long.parseLong(primaryKeyString);
					user = UserFinder.findByPrimaryKey(primaryKey);
				} catch (NumberFormatException ex) {
					return(HttpUtils.badRequestResponse(APPLICATION_JSON, getErrorObjectAsString("Could not parse '" + primaryKeyString + "' to a valid number.")));
				} catch(H2ZeroFinderException ex) {
					return(HttpUtils.notFoundResponse(APPLICATION_JSON, getErrorObjectAsString("Could not find 'User' with primary key of '" + primaryKeyString + "'.")));
				}
			}
		} else {
			return(HttpUtils.badRequestResponse(APPLICATION_JSON, getErrorObjectAsString("Invalid route")));
		}


		Map<String, String> files = new HashMap<String, String>();
		try {
			httpSession.parseBody(files);
		} catch (IOException | ResponseException ex) {
			return(HttpUtils.internalServerErrorResponse(APPLICATION_JSON, getErrorObjectAsString("Exception, message was: " + ex.getMessage())));
		}

		Map<String, List<String>> parameters = httpSession.getParameters();
		try {
			user.setIdUserType(castLong(getFirstParameter("idUserType", parameters)));
			user.setFlIsAlive(castBoolean(getFirstParameter("flIsAlive", parameters)));
			user.setNumAge(castInteger(getFirstParameter("numAge", parameters)));
			user.setNmUsername(castString(getFirstParameter("nmUsername", parameters)));
			user.setTxtAddressEmail(castString(getFirstParameter("txtAddressEmail", parameters)));
			user.setTxtPassword(castString(getFirstParameter("txtPassword", parameters)));
			user.setDtmSignup(castTimestamp(getFirstParameter("dtmSignup", parameters)));

			user.update();
		} catch (ServantException | H2ZeroPrimaryKeyException ex) {
			return(HttpUtils.badRequestResponse(APPLICATION_JSON, getErrorObjectAsString(ex.getMessage())));
		} catch (SQLException ex) {
			return(HttpUtils.internalServerErrorResponse(APPLICATION_JSON, getErrorObjectAsString("SQL Exception, message was: " + ex.getMessage())));
		}

		return(HttpUtils.okResponse());
	}

	@Override
	public Response doDelete(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {
		if(null != restParams) {
			String primaryKeyString = restParams.get(Constants.USER_ID_USER);
			if(null != primaryKeyString) {
				try {
					Long primaryKey = Long.parseLong(primaryKeyString);
					int numDeleted = UserDeleter.deleteByPrimaryKey(primaryKey);
					if(1 != numDeleted) {
						return(HttpUtils.notFoundResponse(APPLICATION_JSON, getErrorObjectAsString("Could not find 'User' with primary key of '" + primaryKeyString + "'.")));
					}
					return(HttpUtils.okResponse());
				} catch (NumberFormatException ex) {
					return(HttpUtils.badRequestResponse(APPLICATION_JSON, getErrorObjectAsString("Could not parse '" + primaryKeyString + "' to a valid number.")));
				} catch(SQLException ex) {
					return(HttpUtils.internalServerErrorResponse(APPLICATION_JSON, getErrorObjectAsString("SQL Exception, message was: " + ex.getMessage())));
				}
			}
		}
		return(HttpUtils.forbiddenResponse());
	}

}
