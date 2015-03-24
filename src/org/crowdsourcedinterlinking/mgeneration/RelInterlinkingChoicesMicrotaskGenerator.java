package org.crowdsourcedinterlinking.mgeneration;

import org.crowdsourcedinterlinking.model.Microtask;
import org.crowdsourcedinterlinking.model.InterlinkingChoices;

import java.util.Set;

/**
 * @author csarasua
 */
public interface RelInterlinkingChoicesMicrotaskGenerator {

    public Set<Microtask> createMicrotasks(InterlinkingChoices a);
}
