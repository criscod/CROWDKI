package org.crowdsourcedinterlinking.util;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

import com.hp.hpl.jena.rdf.model.ModelFactory;

public class URIutils {

	public static String getDefaultLabel(String URI) {
		String result = null;
		try {

			if (URI.startsWith("http://")) {
				Model model = ModelFactory.createDefaultModel();
				Resource r = model.createResource(URI);
				if (r.getURI().contains("-") || r.getURI().contains("_"))
				{
					if(r.getURI().contains("#"))
							{
						String[] parts = r.getURI().split("#");
						result = parts[parts.length-1];
						
							}
					else // should be either ... /name or ...#name
					{
						String[] parts = r.getURI().split("/");
						result = parts[parts.length-1];
					}
					
				}
				else
				{
				result = r.getLocalName();
				}
			} else {
				result = URI;
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return result;
	}

}
