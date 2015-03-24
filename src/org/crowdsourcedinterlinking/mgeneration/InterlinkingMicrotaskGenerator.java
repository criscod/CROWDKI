package org.crowdsourcedinterlinking.mgeneration;

import java.util.Set;


import org.crowdsourcedinterlinking.model.Interlinking;
import org.crowdsourcedinterlinking.model.Microtask;
import org.crowdsourcedinterlinking.model.TypeOfMappingGoal;

public interface InterlinkingMicrotaskGenerator {

	public Set<Microtask> createMicrotasks(Interlinking a);

}
