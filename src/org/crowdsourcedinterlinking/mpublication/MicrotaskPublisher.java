package org.crowdsourcedinterlinking.mpublication;

import org.crowdsourcedinterlinking.model.Microtask;

public interface MicrotaskPublisher {

	public String uploadMicrotask(Microtask microtask, Service service);

	public void orderMicrotask(String idMicrotask, Service service);

}
