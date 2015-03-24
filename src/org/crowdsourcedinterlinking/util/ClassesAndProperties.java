package org.crowdsourcedinterlinking.util;

import com.hp.hpl.jena.rdf.model.Resource;

public class ClassesAndProperties {

	/*
	 * The way in which Classes and properties are identified probably will need to be changed if axioms are allowed
	 * 
	 * Is an expression also a unique Resource? -- blank nodes? 
	 */
	
	public static boolean isClass(Resource r) {
		boolean result = true;

		try {

			String localName = r.getLocalName();
			String firstCharacterLocalName = localName.substring(0, 1);
			String firstCharacterLocalNameLower = firstCharacterLocalName
					.toLowerCase();

			if (firstCharacterLocalName.equals(firstCharacterLocalNameLower)) {
				result = false;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static boolean isProperty(Resource r)
	{
		boolean result = false;

		try {

			String localName = r.getLocalName();
			String firstCharacterLocalName = localName.substring(0, 1);
			String firstCharacterLocalNameLower = firstCharacterLocalName
					.toLowerCase();

			if (firstCharacterLocalName.equals(firstCharacterLocalNameLower)) {
				result = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
