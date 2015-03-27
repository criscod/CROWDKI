package org.crowdsourcedinterlinking.mgeneration;

import org.crowdsourcedinterlinking.model.Interlinking;
import org.crowdsourcedinterlinking.model.Microtask;

import java.util.Set;
/**
 * @author csarasua
 */
public interface InterlinkingMicrotaskGenerator {

    public Set<Microtask> createMicrotasks(Interlinking a);

}
