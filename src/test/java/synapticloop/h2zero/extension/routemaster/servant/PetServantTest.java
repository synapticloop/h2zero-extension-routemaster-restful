package synapticloop.h2zero.extension.routemaster.servant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;
import synapticloop.h2zero.base.exception.H2ZeroFinderException;
import synapticloop.h2zero.extension.routemaster.servant.utils.Helper;
import synapticloop.h2zero.extension.routemaster.servant.utils.MockHttpSession;
import synapticloop.h2zero.model.Constant;
import synapticloop.sample.h2zero.sqlite3.finder.PetFinder;
import synapticloop.sample.h2zero.sqlite3.model.Pet;
import synapticloop.sample.h2zero.sqlite3.model.util.Constants;
import synapticloop.sample.h2zero.sqlite3.routemaster.servant.PetServant;

public class PetServantTest {
	private PetServant petServant;

	@Before
	public void before() {
		List<String> list = new ArrayList<String>();
		petServant = new PetServant("/", list);
	}

	@Test
	public void testGetFindAll() throws IOException {
		Response doGetResponse = petServant.doGet(null, null, null, null);
		assertEquals(200, doGetResponse.getStatus().getRequestStatus());

		InputStream inputStream = null;
		try {
			inputStream = doGetResponse.getData();
			String source = IOUtils.toString(inputStream, "UTF-8");

			// test that this is parse-able
			new JSONArray(source);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	@Test
	public void doUnparseableGet() {
		Map<String, String> restParams = new HashMap<String, String>();
		restParams.put(Constants.PET_ID_PET, "this is not a parseable number");
		Response doGetResponse = petServant.doGet(null, null, restParams, null);
		assertEquals(400, doGetResponse.getStatus().getRequestStatus());

	}

	@Test
	public void testDoPost() throws IOException, H2ZeroFinderException {
		MockHttpSession mockHttpSession = new MockHttpSession();
		mockHttpSession.addParameter(Constants.PET_NM_PET, "Fido");
		mockHttpSession.addParameter(Constants.PET_IMG_PHOTO, "img-photo");
		mockHttpSession.addParameter(Constants.PET_DT_BIRTHDAY, "2000-01-01");
		mockHttpSession.addParameter(Constants.PET_NUM_AGE, 7);
		mockHttpSession.addParameter(Constants.PET_FLT_WEIGHT, 2.3f);
		Response doPostResponse = petServant.doPost(null, mockHttpSession, null, null);
		assertEquals(200, doPostResponse.getStatus().getRequestStatus());

		InputStream inputStream = null;
		JSONObject jsonObject = null;
		try {
			inputStream = doPostResponse.getData();
			String source = IOUtils.toString(inputStream, "UTF-8");

			// test that this is parse-able
			jsonObject = new JSONObject(source);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}

		long primaryKey = jsonObject.getLong("primaryKey");
		// now try and get the results

		Pet pet = PetFinder.findByPrimaryKey(primaryKey);
		assertEquals("Fido", pet.getNmPet());
		assertEquals("img-photo", pet.getImgPhoto());
		assertEquals("2000-01-01", pet.getDtBirthday().toString());
		assertEquals(new Integer(7), pet.getNumAge());
		assertEquals(new Float(2.3), pet.getFltWeight());

		// now also do the get request on the values
		Map<String, String> restParams = new HashMap<String, String>();
		restParams.put(Constants.PET_ID_PET, Long.toString(primaryKey));

		Response doGetResponse = petServant.doGet(null, null, restParams, null);
		assertEquals(200, doGetResponse.getStatus().getRequestStatus());
		try {
			inputStream = doGetResponse.getData();
			String source = IOUtils.toString(inputStream, "UTF-8");
			// test that this is parse-able
			jsonObject = new JSONObject(source);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}

		Response doDeleteResponse = petServant.doDelete(null, null, restParams, null);
		assertEquals(200, doDeleteResponse.getStatus().getRequestStatus());
		pet = PetFinder.findByPrimaryKeySilent(primaryKey);
		assertNull(pet);
	}

	@Test
	public void doForbiddenDelete() {
		Response doDeleteResponse = petServant.doDelete(null, null, null, null);
		assertEquals(NanoHTTPD.Response.Status.FORBIDDEN.getRequestStatus(), doDeleteResponse.getStatus().getRequestStatus());
	}

	@Test
	public void doNotFoundDelete() {
		Map<String, String> restParams = new HashMap<String, String>();
		restParams.put(Constants.PET_ID_PET, "-1");
		Response doDeleteResponse = petServant.doDelete(null, null, restParams, null);
		assertEquals(NanoHTTPD.Response.Status.NOT_FOUND.getRequestStatus(), doDeleteResponse.getStatus().getRequestStatus());
	}

	@Test
	public void doNotParseableDelete() throws IOException {
		Map<String, String> restParams = new HashMap<String, String>();
		restParams.put(Constants.PET_ID_PET, "this cannot be a number");
		Response doDeleteResponse = petServant.doDelete(null, null, restParams, null);
		System.out.println(Helper.readResponse(doDeleteResponse));
		assertEquals(NanoHTTPD.Response.Status.BAD_REQUEST.getRequestStatus(), doDeleteResponse.getStatus().getRequestStatus());
	}

	
	@Test
	public void doNullPost() {
		Response doPostResponse = petServant.doPost(null, null, null, null);
		assertEquals(NanoHTTPD.Response.Status.BAD_REQUEST.getRequestStatus(), doPostResponse.getStatus().getRequestStatus());
	}
}
