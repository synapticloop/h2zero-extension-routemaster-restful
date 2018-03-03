package synapticloop.h2zero.extension.routemaster.servant;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.junit.Test;

import fi.iki.elonen.NanoHTTPD.Response;
import synapticloop.h2zero.extension.routemaster.servant.utils.MockHttpSession;
import synapticloop.sample.h2zero.sqlite3.routemaster.servant.PetServant;

public class PetServantTest {

	@Test
	public void testGetFindAll() throws IOException {
		List<String> list = new ArrayList<String>();
		PetServant petServant = new PetServant("/", list);
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
	public void testDoPost() {
		List<String> list = new ArrayList<String>();
		PetServant petServant = new PetServant("/", list);
		Response doPostResponse = petServant.doPost(null, new MockHttpSession(), null, null);
		assertEquals(200, doPostResponse.getStatus().getRequestStatus());
	}

}
