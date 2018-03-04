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
import synapticloop.sample.h2zero.sqlite3.deleter.PetDeleter;
import synapticloop.sample.h2zero.sqlite3.finder.PetFinder;
import synapticloop.sample.h2zero.sqlite3.model.Pet;
import synapticloop.sample.h2zero.sqlite3.model.util.Constants;

public class PetServant extends BaseServant {

	private static final String APPLICATION_JSON = "application/json";

	public PetServant(String routeContext, List<String> params) {
		super(routeContext, params);
	}

	@Override
	public Response doGet(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {

		if(null != restParams) {
			String primaryKeyString = restParams.get(Constants.PET_ID_PET);
			if(null != primaryKeyString && primaryKeyString.trim().length() != 0) {
				try {
					Long primaryKey = Long.parseLong(primaryKeyString);
					Pet pet = PetFinder.findByPrimaryKey(primaryKey);
					return(HttpUtils.okResponse(APPLICATION_JSON, pet.toJsonString()));
				} catch (NumberFormatException ex) {
					return(HttpUtils.badRequestResponse(APPLICATION_JSON, getErrorObjectAsString("Could not parse '" + primaryKeyString + "' to a valid number.")));
				} catch(H2ZeroFinderException ex) {
					return(HttpUtils.notFoundResponse(APPLICATION_JSON, getErrorObjectAsString("Could not find 'Pet' with primary key of '" + primaryKeyString + "'.")));
				}
			}
		}

		// at this point - there were no parameters, or the parameter was incorrect, so find all

		List<Pet> findAll;
		try {
			findAll = PetFinder.findAll();
		} catch (SQLException ex) {
			return(HttpUtils.internalServerErrorResponse(APPLICATION_JSON, getErrorObjectAsString("SQL Exception, message was: " + ex.getMessage())));
		}
		StringBuilder stringBuilder = new StringBuilder("[");
		boolean first = true;
		for (Pet pet : findAll) {
			if(!first) {
				stringBuilder.append(",");
			}
			stringBuilder.append(pet.toJsonString());
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
			String nmPet = castString(getFirstParameter("nmPet", parameters));
			Integer numAge = castInteger(getFirstParameter("numAge", parameters));
			Float fltWeight = castFloat(getFirstParameter("fltWeight", parameters));
			Date dtBirthday = castDate(getFirstParameter("dtBirthday", parameters));
			String imgPhoto = castString(getFirstParameter("imgPhoto", parameters));
			Pet pet = new Pet(null, nmPet, numAge, fltWeight, dtBirthday, imgPhoto);

			pet.insert();
			primaryKey = pet.getPrimaryKey();
		} catch (ServantException | H2ZeroPrimaryKeyException ex) {
			return(HttpUtils.badRequestResponse(APPLICATION_JSON, getErrorObjectAsString(ex.getMessage())));
		} catch (SQLException ex) {
			return(HttpUtils.internalServerErrorResponse(APPLICATION_JSON, getErrorObjectAsString("SQL Exception, message was: " + ex.getMessage())));
		}
		return(HttpUtils.okResponse(APPLICATION_JSON, "{\"primaryKey\": " + primaryKey + "}"));
	}

	@Override
	public Response doPut(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {

		Pet pet = null;

		if(null != restParams) {
			String primaryKeyString = restParams.get(Constants.PET_ID_PET);
			if(null != primaryKeyString) {
				try {
					Long primaryKey = Long.parseLong(primaryKeyString);
					pet = PetFinder.findByPrimaryKey(primaryKey);
				} catch (NumberFormatException ex) {
					return(HttpUtils.badRequestResponse(APPLICATION_JSON, getErrorObjectAsString("Could not parse '" + primaryKeyString + "' to a valid number.")));
				} catch(H2ZeroFinderException ex) {
					return(HttpUtils.notFoundResponse(APPLICATION_JSON, getErrorObjectAsString("Could not find 'Pet' with primary key of '" + primaryKeyString + "'.")));
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
			pet.setNmPet(castString(getFirstParameter("nmPet", parameters)));
			pet.setNumAge(castInteger(getFirstParameter("numAge", parameters)));
			pet.setFltWeight(castFloat(getFirstParameter("fltWeight", parameters)));
			pet.setDtBirthday(castDate(getFirstParameter("dtBirthday", parameters)));
			pet.setImgPhoto(castString(getFirstParameter("imgPhoto", parameters)));

			pet.update();
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
			String primaryKeyString = restParams.get(Constants.PET_ID_PET);
			if(null != primaryKeyString) {
				try {
					Long primaryKey = Long.parseLong(primaryKeyString);
					int numDeleted = PetDeleter.deleteByPrimaryKey(primaryKey);
					if(1 != numDeleted) {
						return(HttpUtils.notFoundResponse(APPLICATION_JSON, getErrorObjectAsString("Could not find 'Pet' with primary key of '" + primaryKeyString + "'.")));
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
