package org.crowdsourcedinterlinking.mgeneration;

import org.crowdsourcedinterlinking.model.InterlinkingChoices;

/**
 * @author csarasua
 * Abstract class generated for modularity reasons (in case new methods for generating choices are created, like the Interlinking generation)
 */
public abstract class InterlinkingChoicesGeneratorImpl implements InterlinkingChoicesGenerator {

    public abstract InterlinkingChoices generateChoices();
}
