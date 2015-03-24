package org.crowdsourcedinterlinking.rcollection;

import org.crowdsourcedinterlinking.model.Interlinking;

/**
 * @author csarasua
 */
public interface InterlinkingResultProcessor {
    public void serialiseSelectedAlignmentToAlignmentAPIFormat(
            Interlinking crowdInterlinking);
    public void serialiseInterlinkingToNTriples(Interlinking crowdInterlinking);
}
