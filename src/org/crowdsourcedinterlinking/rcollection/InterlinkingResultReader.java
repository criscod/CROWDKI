package org.crowdsourcedinterlinking.rcollection;

import org.crowdsourcedinterlinking.model.Interlink;
import org.crowdsourcedinterlinking.model.Response;
import org.crowdsourcedinterlinking.mpublication.Service;

import java.util.Set;

public interface InterlinkingResultReader {

	public Set<Response> readResponsesZip(String microtaskId, Service s);

	// only ID needed
	public Set<Interlink> readResponsesOfMicrotask(String microtaskId,
												 String type, Service service);

}
