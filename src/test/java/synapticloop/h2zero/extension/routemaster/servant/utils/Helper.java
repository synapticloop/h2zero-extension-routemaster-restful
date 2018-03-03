package synapticloop.h2zero.extension.routemaster.servant.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class Helper {
	public static IHTTPSession getHttpSession() {
		return(new MockHttpSession());
	}
	
	public static String readResponse(Response response) throws IOException {
		InputStream inputStream = response.getData();

		try {
			inputStream = response.getData();
			String source = IOUtils.toString(inputStream, "UTF-8");
			return(source);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}
}
