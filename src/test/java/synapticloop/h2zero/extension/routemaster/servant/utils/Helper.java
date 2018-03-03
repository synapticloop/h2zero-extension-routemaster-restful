package synapticloop.h2zero.extension.routemaster.servant.utils;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;

public class Helper {
	public static IHTTPSession getHttpSession() {
		return(new MockHttpSession());
	}
}
