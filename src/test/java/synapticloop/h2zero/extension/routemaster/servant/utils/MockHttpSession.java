package synapticloop.h2zero.extension.routemaster.servant.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD.CookieHandler;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.ResponseException;

public class MockHttpSession implements IHTTPSession {
	Map<String, List<String>> parameters = new HashMap<String, List<String>>();

	@Override
	public void execute() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public CookieHandler getCookies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getInputStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Method getMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getParms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, List<String>> getParameters() {
		return parameters;
	}

	@Override
	public String getQueryParameterString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUri() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void parseBody(Map<String, String> files) throws IOException, ResponseException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getRemoteIpAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteHostName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addParameter(String name, Object value) {
		List<String> values = null;
		if(parameters.containsKey(name)) {
			values = parameters.get(name);
		} else {
			values = new ArrayList<String>();
		}

		values.add(value.toString());
		parameters.put(name, values);
	}
}
