package org.crowdsourcedinterlinking.mpublication;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.crowdsourcedinterlinking.model.*;
import org.crowdsourcedinterlinking.util.ConfigurationManager;
import org.crowdsourcedinterlinking.util.ParseLinksInOrder;
import org.crowdsourcedinterlinking.util.URIutils;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

public class CwdfInterlinkingMicrotaskPublisherImpl implements
		MicrotaskPublisher {

	private int unitsToOrder = 0;

	private String jsWorkerCode;



	public String uploadMicrotask(Microtask microtask, Service service) {
		String id = null;
		try {

			String typeOfJob = null;

			if (microtask instanceof InterlinkValidationWithFullContextJobMicrotaskImpl) {
				typeOfJob = "InterlinkValidationWithFullContextJobMicrotaskImpl";
			} else if (microtask instanceof InterlinkValidationJobMicrotaskImpl) {
				typeOfJob = "InterlinkValidationJobMicrotaskImpl";
			} else if (microtask instanceof InterlinkIdentificationWithFullContextJobMicrotaskImpl) {
				typeOfJob = "InterlinkIdentificationWithFullContextJobMicrotaskImpl";
			} else if (microtask instanceof InterlinkIdentificationJobMicrotaskImpl) {
				typeOfJob = "InterlinkIdentificationJobMicrotaskImpl";
			}

			JobMicrotaskImpl job = (JobMicrotaskImpl) microtask;
			//.goldUnits() has the units of the job - pos and neg are the sources to check
			if(ConfigurationManager.getInstance().getLinksInOrder().equals(ParseLinksInOrder.Lists))
			{
				this.unitsToOrder = job.getListOfUnits().size()
						+ job.getGoldenUnits().size();
			}
			else
			{
				if(ConfigurationManager.getInstance().getLinksInOrder().equals(ParseLinksInOrder.Lists))
				{
					this.unitsToOrder = job.getListOfUnits().size()
							+ job.getGoldenUnits().size();
				}
				else {
					this.unitsToOrder = job.getSetOfUnits().size()
							+ job.getGoldenUnits().size();
				}
			}


			System.out.println("job: " + job.getId() + " units to order: "
					+ this.unitsToOrder);
			CwdfService cwdf = (CwdfService) service;

			// --------------------- Create the job (this method is called for
			// each of the jobs to be created) ---------------

			// HttpClient client = CwdfHttpClient.getInstance().getClient();
			HttpClient client = new DefaultHttpClient();

			HttpPost postJob = new HttpPost(cwdf.getCreateJobURL());
			postJob.setHeader("Accept", cwdf.getCreateJobAccept());
			postJob.setHeader("Content-Type", cwdf.getCreateJobContentType());

			// get the info that should be used for creating the
			String title = job.getTitle();
			String instructions = job.getInstructions();
			String cml = job.getCml();
			String language = job.getLanguage();
			String judgmentsPerUnit = new Integer(job.getJudgmentsPerUnit())
					.toString();
			String maxJudgmentsPerWorker = new Integer(
					job.getMaxJudgmentsPerWorker()).toString();
			String pagesPerAssignment = new Integer(job.getPagesPerAssignment())
					.toString();
			String unitsPerAssignment = new Integer(job.getUnitsPerAssignment())
					.toString();
			String goldPerAssignment = new Integer(job.getGoldPerAssignment())
					.toString();
			String cents = new Integer(ConfigurationManager.getInstance()
					.getCentsPerPager()).toString();

			if (job.getMaxJudgmentsPerWorker() < 28) // a message says error
														// must be <=28
			{
				maxJudgmentsPerWorker = "28";
			}

			// TODO: change the way the HS is prepared before





		 

			String parameters = "job[title]=" + title + "&job[instructions]="
					+ instructions + "&job[cml]=" + cml + "&job[language]=" + language
					+ "&job[judgments_per_unit]=" + judgmentsPerUnit
					+ "&job[max_judgments_per_worker]=" + maxJudgmentsPerWorker
					+ "&job[pages_per_assignment]=" + pagesPerAssignment
					+ "&job[units_per_assignment]=" + unitsPerAssignment
					+ "&job[gold_per_assignment]=" + goldPerAssignment
					+ "&job[payment_cents]=" + cents;



			// checking
			System.out.println("create job url: " + cwdf.getCreateJobURL());
			System.out.println("Accept: " + cwdf.getCreateJobAccept());
			System.out.println("Content-Type :"
					+ cwdf.getCreateJobContentType());
			System.out.println("urlEncodedParameters: " + parameters);
			// end checking

			postJob.setEntity(new StringEntity(parameters));

			HttpResponse response = client.execute(postJob);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				System.out.println("Success in creating the job");

				HttpEntity responseEntity = response.getEntity();
				if (responseEntity != null) {
					ObjectMapper mapper = new ObjectMapper();
					InputStream in = responseEntity.getContent();
					Map<String, Object> jobData = mapper.readValue(in,
							new TypeReference<Map<String, Object>>() {
							});

					for (Map.Entry<String, Object> attribute : jobData
							.entrySet()) {
						if (attribute.getKey().equals("id")) {
							String generatedJobId = attribute.getValue()
									.toString();
							id = generatedJobId;
							ConfigurationManager.getInstance().addJobToControl(
									id);
							System.out.println("JOB WITH ID: " + generatedJobId
									+ " has been generated");
						}
						if (attribute.getKey().equals("title")) {
							String titleRead = attribute.getValue().toString();
							System.out.println("title: " + titleRead
									+ " has been generated");
						}
					}

				}

				File resultsFile = new File(ConfigurationManager.getInstance()
						.getCurrentTrackFile());

				Files.append(id + "," + typeOfJob, resultsFile,
						Charset.defaultCharset());
				String ls = System.getProperty("line.separator");
				Files.append(ls, resultsFile, Charset.defaultCharset());



				HttpClient client2 = new DefaultHttpClient();

				System.out.println("create job url: "
						+ cwdf.getUploadUnitsToJobURL(id));
				/*HttpPost postJob2 = new HttpPost(
						"https://api.crowdflower.com/v1/jobs/234985/upload?key=32c441799be374c58a0b4a0dc92644f78949cdf3");*/
				HttpPost postJob2 = new HttpPost(
						cwdf.getUploadUnitsToJobURL(id));
				postJob2.setHeader("Accept", cwdf.getUploadUnitsToJobAccept());
				postJob2.setHeader("Content-Type",
						cwdf.getUploadUnitsToJobContentType());
				postJob2.setHeader("Content-Encoding", "utf-8");

				HttpClient client2E = new DefaultHttpClient();

				HttpPost postJob2E = new HttpPost(
						cwdf.getUploadUnitsToJobURL(id));
				postJob2E.setHeader("Accept", cwdf.getUploadUnitsToJobAccept());
				postJob2E.setHeader("Content-Type",
						cwdf.getUploadUnitsToJobContentType());
				postJob2E.setHeader("Content-Encoding", "utf-8");



				Set<UnitDataEntryImpl> setOfUnits = job.getSetOfUnits();
				List<UnitDataEntryImpl> listOfUnits = job.getListOfUnits();

				JsonFactory jsonFactory = new JsonFactory().configure(
						JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
				ObjectMapper mapper2 = new ObjectMapper(jsonFactory);



				String validationRadioLabel = null;
				String identificationRadioLabel1 = null;
				String identificationRadioLabel2 = null;
				

				int featureNumber = 1;
				int featureNumber2 = 1;

				System.out.println("a");
				String data = new String();
				String dataExp = new String();
				int count=1;
				for (UnitDataEntryImpl u : listOfUnits) {


					Map<String, String> unitData = new HashMap<String, String>();

					if (u instanceof InterlinkValidationWithFullContextUnitDataEntryImpl) {

						unitData.put("a",
								((InterlinkValidationUnitDataEntryImpl) u)
										.getLabelA());
						unitData.put("b",
								((InterlinkValidationUnitDataEntryImpl) u)
										.getLabelB());
						
						unitData.put("firstWordA",
								((InterlinkValidationWithFullContextUnitDataEntryImpl) u).getFirstWordA());
						
						unitData.put("firstWordB",
								((InterlinkValidationWithFullContextUnitDataEntryImpl) u).getFirstWordB());
						

						List<FeatureTextValue> listOfFeaturesA = ((InterlinkValidationWithFullContextUnitDataEntryImpl) u)
								.getListOfFeaturesA();

						featureNumber = 1;
						for (FeatureTextValue ftvA : listOfFeaturesA) {
							// fill in the message to show wrt feature (i.e. is
							// located in -- this is equivalent to the
							// "Definition" text that was before fixed because
							// it was always the same no matter the specific
							// data to be aligned)
							unitData.put(
									"messagefeature" + featureNumber + "a",
									ftvA.getMessageText());
							// fill in the value
							String valueUTF8A = new String(ftvA.getValue()
									.getBytes(Charsets.UTF_8));

							System.out
									.println("**************writing the value a: "
											+ valueUTF8A);

							/*unitData.put("value" + featureNumber + "a",
									valueUTF8A);*/
							unitData.put("value" + featureNumber + "a",
									ftvA.getValue());
							
							
							featureNumber = featureNumber + 1;
						}

						if (featureNumber < 7) {
							for (int i = featureNumber; i <= 7; i++) {
								unitData.put("messagefeature" + i + "a",
										"not available");
								// fill in the value
								unitData.put("value" + i + "a", "not available");

							}
						}
						List<FeatureTextValue> listOfFeaturesB = ((InterlinkValidationWithFullContextUnitDataEntryImpl) u)
								.getListOfFeaturesB();

						featureNumber2 = 1;
						for (FeatureTextValue ftvB : listOfFeaturesB) {
							// fill in the message to show wrt feature (i.e. is
							// located in -- this is equivalent to the
							// "Definition" text that was before fixed because
							// it was always the same no matter the specific
							// data to be aligned)
							unitData.put("messagefeature" + featureNumber2
									+ "b", ftvB.getMessageText());
							// fill in the value
							String valueUTF8B = new String(ftvB.getValue()
									.getBytes(Charsets.UTF_8));
							System.out
									.println("**************writing the value b: "
											+ valueUTF8B);

							/*unitData.put("value" + featureNumber2 + "b",
									valueUTF8B);*/
							unitData.put("value" + featureNumber2 + "b",
									ftvB.getValue());
							

							featureNumber2 = featureNumber2 + 1;
						}

						if (featureNumber2 < 7) {
							for (int i = featureNumber2; i <= 7; i++) {

								unitData.put("messagefeature" + i + "b",
										"not available");
								// fill in the value
								unitData.put("value" + i + "b", "not available");
							}
						}

						String uriRelation = ((InterlinkValidationWithFullContextUnitDataEntryImpl) u)
								.getRelation();
						String localNameRelation = URIutils
								.getDefaultLabel(uriRelation);// gets the local
																// name for
																// labels, but
																// it is also
																// useful here
						if (localNameRelation.equals("sameAs")) {
							validationRadioLabel = ConfigurationManager
									.getInstance()
									.getCmlValidationInterlinkingRadioLabelSame();
						} else {
							validationRadioLabel = ConfigurationManager
									.getInstance()
									.getValidationRadioLabelByLocalName(
											localNameRelation.toLowerCase());
						}

						unitData.put("validationradiolabel",
								validationRadioLabel); // This is the message to
														// show depending on the
														// property of the
														// interlink

						unitData.put(
								"uria",
								((InterlinkValidationWithFullContextUnitDataEntryImpl) u)
										.getElementA());

						unitData.put(
								"urib",
								((InterlinkValidationWithFullContextUnitDataEntryImpl) u)
										.getElementB());

						unitData.put("relation", uriRelation);


					}

					else {

						if (u instanceof InterlinkValidationUnitDataEntryImpl) {

							// added the same as with full context, now this is
							// not used -- only the with full context

							unitData.put("a",
									((InterlinkValidationUnitDataEntryImpl) u)
											.getLabelA());
							unitData.put("b",
									((InterlinkValidationUnitDataEntryImpl) u)
											.getLabelB());
							List<FeatureTextValue> listOfFeaturesA = ((InterlinkValidationWithFullContextUnitDataEntryImpl) u)
									.getListOfFeaturesA();

							featureNumber = 1;
							for (FeatureTextValue ftvA : listOfFeaturesA) {
								// fill in the message to show wrt feature (i.e.
								// is located in -- this is equivalent to the
								// "Definition" text that was before fixed
								// because it was always the same no matter the
								// specific data to be aligned)
								unitData.put("messagefeature" + featureNumber
										+ "a", ftvA.getMessageText());
								// fill in the value
								String valueUTF8A = new String(ftvA.getValue()
										.getBytes(Charsets.UTF_8));

								/*unitData.put("value" + featureNumber + "a",
										valueUTF8A);*/
								
								unitData.put("value" + featureNumber + "a",
										ftvA.getValue());

								featureNumber = featureNumber + 1;
							}

							if (featureNumber < 7) {
								for (int i = featureNumber; i <= 7; i++) {
									unitData.put("messagefeature" + i + "a",
											"not available");
									// fill in the value
									unitData.put("value" + i + "a",
											"not available");

								}
							}
							List<FeatureTextValue> listOfFeaturesB = ((InterlinkValidationWithFullContextUnitDataEntryImpl) u)
									.getListOfFeaturesB();

							featureNumber2 = 1;
							for (FeatureTextValue ftvB : listOfFeaturesB) {
								// fill in the message to show wrt feature (i.e.
								// is located in -- this is equivalent to the
								// "Definition" text that was before fixed
								// because it was always the same no matter the
								// specific data to be aligned)
								unitData.put("messagefeature" + featureNumber2
										+ "b", ftvB.getMessageText());
								// fill in the value
								String valueUTF8B = new String(ftvB.getValue()
										.getBytes(Charsets.UTF_8));

								/*unitData.put("value" + featureNumber2 + "b",
										valueUTF8B);*/
								unitData.put("value" + featureNumber2 + "b",
										ftvB.getValue());

								featureNumber2 = featureNumber2 + 1;
							}

							if (featureNumber2 < 7) {
								for (int i = featureNumber2; i <= 7; i++) {

									unitData.put("messagefeature" + i + "b",
											"not available");
									// fill in the value
									unitData.put("value" + i + "b",
											"not available");
								}
							}

							String uriRelation = ((InterlinkValidationWithFullContextUnitDataEntryImpl) u)
									.getRelation();
							String localNameRelation = URIutils
									.getDefaultLabel(uriRelation );// gets the
																	// local
																	// name for
																	// labels,
																	// but it is
																	// also
																	// useful
																	// here
							if (localNameRelation.equals("sameAs")) {
								validationRadioLabel = ConfigurationManager
										.getInstance()
										.getCmlValidationInterlinkingRadioLabelSame();
							} else {
								validationRadioLabel = ConfigurationManager
										.getInstance()
										.getValidationRadioLabelByLocalName(
												localNameRelation.toLowerCase());
							}

							unitData.put("validationradiolabel",
									validationRadioLabel); // This is the
															// message to show
															// depending on the
															// property of the
															// interlink

							unitData.put("uria",
									((InterlinkValidationUnitDataEntryImpl) u)
											.getElementA());

							unitData.put("urib",
									((InterlinkValidationUnitDataEntryImpl) u)
											.getElementB());

							unitData.put("relation", uriRelation);
						}

						else {

							if (u instanceof InterlinkIdentificationWithFullContextUnitDataEntryImpl) {

								unitData.put(
										"a",
										((InterlinkIdentificationUnitDataEntryImpl) u)
												.getLabelA());
								unitData.put(
										"b",
										((InterlinkIdentificationUnitDataEntryImpl) u)
												.getLabelB());

								List<FeatureTextValue> listOfFeaturesA = ((InterlinkIdentificationWithFullContextUnitDataEntryImpl) u)
										.getListOfFeaturesA();

								featureNumber = 1;
								for (FeatureTextValue ftvA : listOfFeaturesA) {
									// fill in the message to show wrt feature
									// (i.e. is located in -- this is equivalent
									// to the "Definition" text that was before
									// fixed because it was always the same no
									// matter the specific data to be aligned)
									unitData.put("messagefeature"
											+ featureNumber + "a",
											ftvA.getMessageText());
									// fill in the value
									String valueUTF8A = new String(ftvA
											.getValue()
											.getBytes(Charsets.UTF_8));

									/*unitData.put("value" + featureNumber + "a",
											valueUTF8A);*/
									unitData.put("value" + featureNumber + "a",
											ftvA
											.getValue());

									featureNumber = featureNumber + 1;
								}

								if (featureNumber < 7) {
									for (int i = featureNumber; i <= 7; i++) {
										unitData.put(
												"messagefeature" + i + "a",
												"not available");
										// fill in the value
										unitData.put("value" + i + "a",
												"not available");

									}
								}
								List<FeatureTextValue> listOfFeaturesB = ((InterlinkIdentificationWithFullContextUnitDataEntryImpl) u)
										.getListOfFeaturesB();

								featureNumber2 = 1;
								for (FeatureTextValue ftvB : listOfFeaturesB) {
									// fill in the message to show wrt feature
									// (i.e. is located in -- this is equivalent
									// to the "Definition" text that was before
									// fixed because it was always the same no
									// matter the specific data to be aligned)
									unitData.put("messagefeature"
											+ featureNumber2 + "b",
											ftvB.getMessageText());
									// fill in the value
									String valueUTF8B = new String(ftvB
											.getValue()
											.getBytes(Charsets.UTF_8));

									/*unitData.put(
											"value" + featureNumber2 + "b",
											valueUTF8B);*/
									unitData.put("value" + featureNumber2 + "b",
											ftvB
											.getValue());
									
									featureNumber2 = featureNumber2 + 1;
								}

								if (featureNumber2 < 7) {
									for (int i = featureNumber2; i <= 7; i++) {

										unitData.put(
												"messagefeature" + i + "b",
												"not available");
										// fill in the value
										unitData.put("value" + i + "b",
												"not available");
									}
								}

								identificationRadioLabel1 = ConfigurationManager
										.getInstance()
										.getIdentificationRadioLabel1();
								unitData.put("identificationradiolabel1",
										identificationRadioLabel1); // This is
																	// the
																	// message
																	// to show
																	// depending
																	// on the
																	// property
																	// of the
																	// interlink
								identificationRadioLabel2 = ConfigurationManager
										.getInstance()
										.getIdentificationRadioLabel2();
								unitData.put("identificationradiolabel1",
										identificationRadioLabel2);
								unitData.put(
										"identificationrandomradiolabel",
										this.getRandomizedIdentificationRandomRadioLabel());
							}

							else {
								if (u instanceof InterlinkIdentificationUnitDataEntryImpl) {

									// It is written the same but only the one
									// with full context will be used
									unitData.put(
											"a",
											((InterlinkIdentificationUnitDataEntryImpl) u)
													.getLabelA());
									unitData.put(
											"b",
											((InterlinkIdentificationUnitDataEntryImpl) u)
													.getLabelB());

									List<FeatureTextValue> listOfFeaturesA = ((InterlinkIdentificationWithFullContextUnitDataEntryImpl) u)
											.getListOfFeaturesA();

									featureNumber = 1;
									for (FeatureTextValue ftvA : listOfFeaturesA) {
										// fill in the message to show wrt
										// feature (i.e. is located in -- this
										// is equivalent to the "Definition"
										// text that was before fixed because it
										// was always the same no matter the
										// specific data to be aligned)
										unitData.put("messagefeature"
												+ featureNumber + "a",
												ftvA.getMessageText());
										// fill in the value
										String valueUTF8A = new String(ftvA
												.getValue().getBytes(
														Charsets.UTF_8));

										/*unitData.put("value" + featureNumber
												+ "a", valueUTF8A);*/
										unitData.put("value" + featureNumber + "a",
												ftvA
												.getValue());
										featureNumber = featureNumber + 1;
									}

									if (featureNumber < 7) {
										for (int i = featureNumber; i <= 7; i++) {
											unitData.put("messagefeature" + i
													+ "a", "not available");
											// fill in the value
											unitData.put("value" + i + "a",
													"not available");

										}
									}
									List<FeatureTextValue> listOfFeaturesB = ((InterlinkIdentificationWithFullContextUnitDataEntryImpl) u)
											.getListOfFeaturesB();

									featureNumber2 = 1;
									for (FeatureTextValue ftvB : listOfFeaturesB) {
										// fill in the message to show wrt
										// feature (i.e. is located in -- this
										// is equivalent to the "Definition"
										// text that was before fixed because it
										// was always the same no matter the
										// specific data to be aligned)
										unitData.put("messagefeature"
												+ featureNumber2 + "b",
												ftvB.getMessageText());
										// fill in the value
										String valueUTF8B = new String(ftvB
												.getValue().getBytes(
														Charsets.UTF_8));

										/*unitData.put("value" + featureNumber2
												+ "b", valueUTF8B);*/
										unitData.put("value" + featureNumber2 + "b",
												ftvB
												.getValue());

										featureNumber2 = featureNumber2 + 1;
									}

									if (featureNumber2 < 7) {
										for (int i = featureNumber2; i <= 7; i++) {

											unitData.put("messagefeature" + i
													+ "b", "not available");
											// fill in the value
											unitData.put("value" + i + "b",
													"not available");
										}
									}

									identificationRadioLabel1 = ConfigurationManager
											.getInstance()
											.getIdentificationRadioLabel1();
									unitData.put("identificationradiolabel1",
											identificationRadioLabel1); // This
																		// is
																		// the
																		// message
																		// to
																		// show
																		// depending
																		// on
																		// the
																		// property
																		// of
																		// the
																		// interlink
									identificationRadioLabel2 = ConfigurationManager
											.getInstance()
											.getIdentificationRadioLabel2();
									unitData.put("identificationradiolabel1",
											identificationRadioLabel2);
									unitData.put(
											"identificationrandomradiolabel",
											this.getRandomizedIdentificationRandomRadioLabel());

									unitData.put(
											"uria",
											((InterlinkIdentificationWithFullContextUnitDataEntryImpl) u)
													.getElementA());

									unitData.put(
											"urib",
											((InterlinkIdentificationWithFullContextUnitDataEntryImpl) u)
													.getElementB());

								}
							}
						}
					}



					Map<String, String> map = new HashMap<String, String>();
				

					
					


					// System.out.println("size of unit data dump "+out.size());
					if(count<= ConfigurationManager.getInstance().getExpertiseCount())
					{
						dataExp = dataExp +mapper2.writeValueAsString(unitData);

					}
					else
					{
						data = data + mapper2.writeValueAsString(unitData);

					}



				} // for end


				StringEntity sEntE = new StringEntity(dataExp, ContentType.create(
						"application/json", "UTF-8"));
				// postJob2.setEntity(new StringEntity(data));
				postJob2E.setEntity(sEntE);
				System.out.println("data sent as entity: " + dataExp);
				System.out.println(postJob2E.toString());

				HttpResponse response2E = client2E.execute(postJob2E);

				int statusCode2E = response2E.getStatusLine().getStatusCode();

				if (statusCode2E == 200) {
					System.out.println("success in uploading the expertise units");
				} else {
					throw new Exception(
							"CrowdFlower did not succeed in uploading the units: "
									+ statusCode2E
									+ response2E.getStatusLine()
									.getReasonPhrase());
				}

				StringEntity sEnt = new StringEntity(data, ContentType.create(
						"application/json", "UTF-8"));
				// postJob2.setEntity(new StringEntity(data));
				postJob2.setEntity(sEnt);
				System.out.println("data sent as entity: " + data);
				System.out.println(postJob2.toString());

				HttpResponse response2 = client2.execute(postJob2);

				int statusCode2 = response2.getStatusLine().getStatusCode();

				if (statusCode2 == 200) {
					System.out.println("success in uploading the units");
				} else {
					throw new Exception(
							"CrowdFlower did not succeed in uploading the units: "
									+ statusCode2
									+ response2.getStatusLine()
									.getReasonPhrase());
				}
				

				String parameters3 = null;

				// -----------------------GOLDEN UNITS
				// ----------------------------

				HttpPost postJob3 = new HttpPost(cwdf.getCreateUnitInJobURL(id));
				postJob3.setHeader("Accept", cwdf.getCreateUnitInJobAccept());
				postJob3.setHeader("Content-Type",
						cwdf.getCreateUnitInJobContentType());
				postJob3.setHeader("Content-Encoding", "utf-8");

				System.out.println("a");
				Set<UnitDataEntryImpl> goldenUnits = job.getGoldenUnits();

				Set<Interlink> goldenUnitsSourcePos = job.getGoldenUnitsSourcePos();
				Set<Interlink> goldenUnitsSourceNeg = job.getGoldenUnitsSourceNeg();

				System.out.println("b");
				
				String elementA = null;
				String elementB = null;
				String labelA = null;
				String labelB = null;
				String relation = null;
				String goldenAnswer = null;
				String difficulty = null;
				List<FeatureTextValue> listOfFeaturesA = new ArrayList<FeatureTextValue>();
				List<FeatureTextValue> listOfFeaturesB = new ArrayList<FeatureTextValue>();
				String firstWordA = null; 
				String firstWordB = null; 
				
				for (UnitDataEntryImpl g : goldenUnits) {

					if (g instanceof InterlinkValidationWithFullContextUnitDataEntryImpl) {
						InterlinkValidationWithFullContextUnitDataEntryImpl validationG = (InterlinkValidationWithFullContextUnitDataEntryImpl) g;
						elementA = validationG.getElementA();
						elementB = validationG.getElementB();
						relation = validationG.getRelation();
						labelA = validationG.getLabelA();
						labelB = validationG.getLabelB();
						listOfFeaturesA = validationG.getListOfFeaturesA();
						listOfFeaturesB = validationG.getListOfFeaturesB();
						goldenAnswer = ConfigurationManager.getInstance()
								.getGoldMessageValidation();
						difficulty = new Integer(validationG.getDifficulty())
								.toString();
						
						firstWordA = validationG.getFirstWordA();
						firstWordB = validationG.getFirstWordB();
					}

					else {
						if (g instanceof InterlinkIdentificationWithFullContextUnitDataEntryImpl) {

							InterlinkIdentificationWithFullContextUnitDataEntryImpl identificationG = (InterlinkIdentificationWithFullContextUnitDataEntryImpl) g;
							elementA = identificationG.getElementA();
							elementB = identificationG.getElementB();
							labelA = identificationG.getLabelA();
							labelB = identificationG.getLabelB();
							listOfFeaturesA = identificationG
									.getListOfFeaturesA();
							listOfFeaturesB = identificationG
									.getListOfFeaturesB();
							goldenAnswer = ConfigurationManager.getInstance()
									.getGoldMessageIdentification();
							difficulty = new Integer(
									identificationG.getDifficulty()).toString();

						}
					}
					System.out.println("golden units");
					// golden units

					String[] arrayLabelA = labelA.split("\\s");
					String[] arrayLabelB = labelB.split("\\s");
					String reasonPos = "They refer to the same.";
					String reasonNeg="They do not refer to the same.";
					String mainPos="yes";
					String mainNeg="no";

					String reason=new String();
					String main=new String();

					Model modelTemp = ModelFactory.createDefaultModel();
					Resource rA = modelTemp.createResource(elementA);
					Resource rB = modelTemp.createResource(elementB);
					Property rel = modelTemp.createProperty(relation);
					Interlink iG=new Interlink(rA,rB,rel);
					if (goldenUnitsSourcePos.contains(iG))
					{
						main=mainPos;
						reason=reasonPos;
					}
					else if(goldenUnitsSourceNeg.contains(iG))
					{
						main=mainNeg;
						reason=reasonNeg;
					}
					if (g instanceof InterlinkValidationWithFullContextUnitDataEntryImpl) {

						String featureParameters = getStringGoldParameters(g);
						String localNameRelation = URIutils
								.getDefaultLabel(relation);

						
						
						
						
						
						
						
						if (cml.contains(ConfigurationManager.getInstance()
								.getCmlVerifwordNotTurnedStringInterlinking())) {
							// parameters3
							// ="unit[golden]="+true+"&unit[data][do_you_see_any_connection_between_concept_a_and_concept_b_gold][]="+goldenAnswer+"&unit[data][do_you_see_any_connection_between_concept_a_and_concept_b_gold_reason][]=They have almost the same name and they refer to the same element"+"&unit[data][a]="+elementA+"&unit[data][b]="+elementB+"&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[data][relation]==&unit[data][superclassa]=not available&unit[data][superclassb]=not available&unit[data][siblingsa]=not available&unit[data][siblingsb]=not available&unit[data][subclassesa]=not available&unit[data][subclassesb]=not available&unit[data][instancesa]=not available&unit[data][instancesb]=not available"+"&unit[difficulty]="+difficulty;
							// parameters3
							// ="unit[golden]="+true+"&unit[data][main_gold][]="+goldenAnswer+"&unit[data][main_gold_reason][]=They have almost the same name and they refer to the same element"+"&unit[data][a]="+elementA+"&unit[data][b]="+elementB+"&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[data][relation]==&unit[data][superclassa]=not available&unit[data][superclassb]=not available&unit[data][siblingsa]=not available&unit[data][siblingsb]=not available&unit[data][subclassesa]=not available&unit[data][subclassesb]=not available&unit[data][instancesa]=not available&unit[data][instancesb]=not available&unit[data][uria]=not available&unit[data][urib]=not available"+"&unit[difficulty]="+difficulty;

							if (localNameRelation.equals("sameAs")) {
								validationRadioLabel = ConfigurationManager
										.getInstance()
										.getCmlValidationInterlinkingRadioLabelSame();
							} else {
								validationRadioLabel = ConfigurationManager
										.getInstance()
										.getValidationRadioLabelByLocalName(
												localNameRelation.toLowerCase());
							}

							//replaced main_gold goldAnswer with main
							
							

							parameters3 = "unit[golden]="
									+ true
									+ "&unit[data][a]="
									+ labelA
									+ "&unit[data][b]="
									+ labelB
									+ "&unit[data][relation]="
									+ relation
									+ featureParameters
									+ "&unit[data][uria]="
									+ elementA
									+ "&unit[data][urib]="
									+ elementB
									+ "&unit[data][main_gold][]="
									+ main
									+ "&unit[data][main_gold_reason][]="+reason+"&unit[data][verifword_gold][]="
									// + elementA
									//+ firstWordA
									+ labelA
									+ "&unit[data][verifword_gold_reason][]=This is the word that is shown &unit[difficulty]="
									+ difficulty
									+ "&unit[data][validationradiolabel]="
									+ validationRadioLabel;
							
							
							System.out.println("parameters3: "+parameters3);
						/*
							REAL
							parameters3 = "unit[golden]="
									+ true
									+ "&unit[data][a]="
									+ labelA
									+ "&unit[data][b]="
									+ labelB
									+ "&unit[data][relation]="
									+ relation
									+ featureParameters
									+ "&unit[data][uria]="
									+ elementA
									+ "&unit[data][urib]="
									+ elementB
									+ "&unit[data][main_gold][]="
									+ goldenAnswer
									+ "&unit[data][main_gold_reason][]=They refer to the same element&unit[data][verifword_gold][]="
									// + elementA
									+ labelA
									+ "&unit[data][verifword_gold_reason][]=This is the word that is shown&unit[data][verifnumber_gold][]="
									+ arrayLabelA.length
									+ "&unit[data][verifnumber_gold_reason][]=The answer must be a number that shows the number of words shown &unit[difficulty]="
									+ difficulty
									+ "&unit[data][validationradiolabel]="
									+ validationRadioLabel;*/

						} else if (cml.contains(ConfigurationManager
								.getInstance()
								.getCmlVerifwordTurnedStringInterlinking())) {

							if (localNameRelation.equals("sameAs")) {
								validationRadioLabel = ConfigurationManager
										.getInstance()
										.getCmlValidationInterlinkingRadioLabelSame();
							} else {
								validationRadioLabel = ConfigurationManager
										.getInstance()
										.getValidationRadioLabelByLocalName(
												localNameRelation.toLowerCase());
							}

							
							
							parameters3 = "unit[golden]="
									+ true
									+ "&unit[data][a]="
									+ labelA
									+ "&unit[data][b]="
									+ labelB
									+ "&unit[data][relation]="
									+ relation
									+ featureParameters
									+ "&unit[data][uria]="
									+ elementA
									+ "&unit[data][urib]="
									+ elementB
									+ "&unit[data][main_gold][]="
									+ main
									+ "&unit[data][main_gold_reason][]="+reason+"&unit[data][verifword_gold][]="
									// + elementB
									+ labelB
									+ "&unit[data][verifword_gold_reason][]=This is the word that is shown &unit[difficulty]="
									+ difficulty
									+ "&unit[data][validationradiolabel]="
									+ validationRadioLabel;
							
							/*REAL
							parameters3 = "unit[golden]="
									+ true
									+ "&unit[data][a]="
									+ labelA
									+ "&unit[data][b]="
									+ labelB
									+ "&unit[data][relation]="
									+ relation
									+ featureParameters
									+ "&unit[data][uria]="
									+ elementA
									+ "&unit[data][urib]="
									+ elementB
									+ "&unit[data][main_gold][]="
									+ goldenAnswer
									+ "&unit[data][main_gold_reason][]=They refer to the same element&unit[data][verifword_gold][]="
									// + elementB
									+ labelB
									+ "&unit[data][verifword_gold_reason][]=This is the word that is shown&unit[data][verifnumber_gold][]="
									+ arrayLabelB.length
									+ "&unit[data][verifnumber_gold_reason][]=The answer must be a number that shows the number of words shown &unit[difficulty]="
									+ difficulty
									+ "&unit[data][validationradiolabel]="
									+ validationRadioLabel;*/

						}
					} else if (g instanceof InterlinkIdentificationWithFullContextUnitDataEntryImpl) {
						String featureParameters = getStringGoldParameters(g);
						String localNameRelation = URIutils
								.getDefaultLabel(relation);
						// parameters3
						// ="unit[golden]="+true+"&unit[data][do_you_see_any_connection_between_concept_a_and_concept_b_gold][]="+goldenAnswer+"&unit[data][do_you_see_any_connection_between_concept_a_and_concept_b_gold_reason][]=They have almost the same name and they refer to the same element"+"&unit[data][a]="+elementA+"&unit[data][b]="+elementB+"&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[data][superclassa]=not available&unit[data][superclassb]=not available&unit[data][siblingsa]=not available&unit[data][siblingsb]=not available&unit[data][subclassesa]=not available&unit[data][subclassesb]=not available&unit[data][instancesa]=not available&unit[data][instancesb]=not available"+"&unit[difficulty]="+difficulty;
						// parameters3
						// ="unit[golden]="+true+"&unit[data][main_gold][]="+goldenAnswer+"&unit[data][main_gold_reason][]=They have almost the same name and they refer to the same element"+"&unit[data][a]="+elementA+"&unit[data][b]="+elementB+"&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[data][superclassa]=not available&unit[data][superclassb]=not available&unit[data][siblingsa]=not available&unit[data][siblingsb]=not available&unit[data][subclassesa]=not available&unit[data][subclassesb]=not available&unit[data][instancesa]=not available&unit[data][instancesb]=not available&unit[data][uria]=not available&unit[data][urib]=not available"+"&unit[difficulty]="+difficulty;
						// if
						// (cml.contains("Select the name of Concept A"))
						if (cml.contains(ConfigurationManager.getInstance()
								.getCmlVerifwordNotTurnedStringInterlinking())) {
							// parameters3
							// ="unit[golden]="+true+"&unit[data][a]="+elementA+"&unit[data][b]="+elementB+"&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[data][superclassa]=not available&unit[data][superclassb]=not available&unit[data][siblingsa]=not available&unit[data][siblingsb]=not available&unit[data][subclassesa]=not available&unit[data][subclassesb]=not available&unit[data][instancesa]=not available&unit[data][instancesb]=not available&unit[data][uria]=not available&unit[data][urib]=not available"+"&unit[data][main_gold][]="+goldenAnswer+"&unit[data][main_gold_reason][]=They have almost the same name and they refer to the same element&unit[data][verifword_gold][]="+elementA+"&unit[data][verifword_gold_reason][]=This is the word that is shown&unit[data][verifnumber_gold][]=1&unit[data][verifnumber_gold_reason][]=The answer must be a number that shows the number of words shown &unit[difficulty]="+difficulty;
							parameters3 = "unit[golden]="
									+ true
									+ "&unit[data][a]="
									+ labelA
									+ "&unit[data][b]="
									+ labelB
									+ featureParameters
									+ "&unit[data][uria]="
									+ elementA
									+ "&unit[data][urib]="
									+ elementB
									+ "&unit[data][main_gold][]="
									+ main
									+ "&unit[data][main_gold_reason][]="+reason+"&unit[data][verifword_gold][]="
									// + elementA
									// + firstWordA
									+ labelA
									+ "&unit[data][verifword_gold_reason][]=This is the word that is shown &unit[difficulty]="
									+ difficulty;

							System.out
									.println("cml contains Concept A question");
						} else if (cml.contains(ConfigurationManager
								.getInstance()
								.getCmlVerifwordTurnedStringInterlinking())) {
							// parameters3
							// ="unit[golden]="+true+"&unit[data][a]="+elementA+"&unit[data][b]="+elementB+"&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[data][superclassa]=not available&unit[data][superclassb]=not available&unit[data][siblingsa]=not available&unit[data][siblingsb]=not available&unit[data][subclassesa]=not available&unit[data][subclassesb]=not available&unit[data][instancesa]=not available&unit[data][instancesb]=not available&unit[data][uria]=not available&unit[data][urib]=not available"+"&unit[data][main_gold][]="+goldenAnswer+"&unit[data][main_gold_reason][]=They have almost the same name and they refer to the same element&unit[data][verifword_gold][]="+elementB+"&unit[data][verifword_gold_reason][]=This is the word that is shown&unit[data][verifnumber_gold][]=1&unit[data][verifnumber_gold_reason][]=The answer must be a number that shows the number of words shown &unit[difficulty]="+difficulty;
							parameters3 = "unit[golden]="
									+ true
									+ "&unit[data][a]="
									+ labelA
									+ "&unit[data][b]="
									+ labelB
									+ featureParameters
									+ "&unit[data][uria]="
									+ elementA
									+ "&unit[data][urib]="
									+ elementB
									+ "&unit[data][main_gold][]="
									+ main
									+ "&unit[data][main_gold_reason][]="+reason+"&unit[data][verifword_gold][]="
									// + elementB
									// + firstWordB
									+ labelB
									+ "&unit[data][verifword_gold_reason][]=This is the word that is shown &unit[difficulty]="
									+ difficulty;

							System.out
									.println("cml contains Concept B question");
						}

					}
					
					


					// "unit[golden]="+true+"&unit[data][check_all_that_apply_gold]="+goldenAnswer+"&unit[check_all_that_apply_gold_reason]=They have almost the same name and they refer to the same element"+"&unit[data][a]="+elementA+"&unit[data][b]="+elementB+"&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[data][superclassa]=not available&unit[data][superclassb]=not available&unit[data][siblingsa]=not available&unit[data][siblingsb]=not available&unit[data][subclassesa]=not available&unit[data][subclassesb]=not available&unit[data][instancesa]=not available&unit[data][instancesb]=not available"+"&unit[difficulty]="+difficulty;

					// checking
					System.out.println("create unit url: "
							+ cwdf.getCreateUnitInJobURL(id));
					System.out.println("Accept: "
							+ cwdf.getCreateUnitInJobAccept());
					System.out.println("Content-Type :"
							+ cwdf.getCreateUnitInJobContentType());
					System.out.println("urlEncodedParameters: " + parameters3);
					// end checking

					postJob3.setEntity(new StringEntity(parameters3, ContentType.create(
							"application/x-www-form-urlencoded","UTF-8")));
					

					HttpClient client3 = new DefaultHttpClient();
					HttpResponse response3 = client3.execute(postJob3);
					int statusCode3 = response3.getStatusLine().getStatusCode();
					if (statusCode3 == 200) {
						System.out
								.println("Success in creating the golden unit for the job");
					} else {
						
						throw new Exception(
								"CrowdFlower did not succeed in creating a golden unit for the job: "
										+ statusCode3
										+ response3.getStatusLine()
												.getReasonPhrase());
					}

				}


				// ------

				

				/* Change the excluded/ included countries
				String parameters4 = null;
				HttpPut putJob = new HttpPut(cwdf.getChangeJobURL(id));
				putJob.setHeader("Accept", cwdf.getChangeJobAccept());
				putJob.setHeader("Content-Type", cwdf.getChangeJobContentType());

				// parameters4 =
				// "job[excluded_countries][]=IN&job[included_countries][]=ES,US,DE,FR";
				parameters4 = "job[excluded_countries][]=IN";
				putJob.setEntity(new StringEntity(parameters4));
				HttpClient client4 = new DefaultHttpClient();
				HttpResponse response4 = client4.execute(putJob);

				int statusCode4 = response4.getStatusLine().getStatusCode();
				if (statusCode4 == 200) {
					System.out
							.println("Success in updating the job with excluded countries");
				} else {
					throw new Exception(
							"CrowdFlower did not succeed in updating the job with excluded countries: "
									+ statusCode4
									+ response4.getStatusLine()
											.getReasonPhrase());
				}*/

			} else {
				throw new Exception(
						"CrowdFlower did not succeed in creating a job: "
								+ statusCode);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return id;
	}

	public void orderMicrotask(String idMicrotask, Service service) {
		try {

			CwdfService cwdf = (CwdfService) service;
			HttpClient client = new DefaultHttpClient();

			System.out.println("create job url: "
					+ cwdf.getOrderJobURL(idMicrotask));
			HttpPost postJob = new HttpPost(cwdf.getOrderJobURL(idMicrotask));
			postJob.setHeader("Accept", cwdf.getOrderJobAccept());
			postJob.setHeader("Content-Type", cwdf.getOrderJobContentType());

			// String parameters3="order[debit]=2&order[channels]=MobMerge";
			// String parameters3 = "debit[units_count]=2&channels[]=MobMerge";
			String parameters = "debit[units_count]=" + this.unitsToOrder
					+ "&channels[]=mob";

			postJob.setEntity(new StringEntity(parameters));

			HttpResponse response = client.execute(postJob);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				System.out.println("Success in ordering the job");
			} else {
				throw new Exception(
						"CrowdFlower did not succeed in ordering the job: "
								+ statusCode
								+ response.getStatusLine().getReasonPhrase());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void uploadCSVfile(String idJob, String pathOfFile, Service service) {
		try {
			// subirlo con idJob - en web server //The CSV files must be
			// publicly available otherwise this cannot be done
		} catch (Exception e) {

		}
	}

	private String getRandomizedIdentificationRandomRadioLabel() {

		Random r = new Random();

		List<String> labels = new ArrayList<String>();

		labels.add("{{a}} knows {{b}}");
		labels.add("{{b}} is author of {{a}}");
		labels.add("{{a}} was presented by {{b}}");

		int random1 = r.nextInt(labels.size());

		return labels.get(random1);
	}

	private String getStringGoldParameters(UnitDataEntryImpl ud) {

		String resultParameters = new String("");

		// repeated update superclass only one call

		if (ud instanceof InterlinkIdentificationWithFullContextUnitDataEntryImpl) {
			InterlinkIdentificationWithFullContextUnitDataEntryImpl intIdentData = (InterlinkIdentificationWithFullContextUnitDataEntryImpl) ud;
			List<FeatureTextValue> listOfFeaturesA = intIdentData
					.getListOfFeaturesA();
			int featureNumberA = 1;
			// String firstWordA = intIdentData.getFirstWordA();
			// String firstWordB = intIdentData.getFirstWordB();

			for (FeatureTextValue ftvA : listOfFeaturesA) {

				resultParameters = resultParameters + "&unit[data][value"
						+ featureNumberA + "a]=" + ftvA.getValue()
						+ "&unit[data][messagefeature" + featureNumberA + "a]="
						+ ftvA.getMessageText();
				/*
				 * if (featureNumberA==1) { String firstValueA =
				 * listOfFeaturesA.get(0).getValue();
				 * 
				 * String tempA = firstValueA; String a=null;
				 * 
				 * while (tempA.startsWith(" ")) { a= tempA.substring(1,
				 * tempA.length()-1); tempA=a; }
				 * 
				 * 
				 * String[] wordsA= tempA.split(" "); // It contains "'" in the
				 * beginning firstWordA=wordsA[0].substring(1);
				 * 
				 * }
				 */

				featureNumberA = featureNumberA + 1;
			}

			List<FeatureTextValue> listOfFeaturesB = intIdentData
					.getListOfFeaturesB();

			int featureNumberB = 1;
			for (FeatureTextValue ftvB : listOfFeaturesB) {
				resultParameters = resultParameters + "&unit[data][value"
						+ featureNumberB + "b]=" + ftvB.getValue()
						+ "&unit[data][messagefeature" + featureNumberB + "b]="
						+ ftvB.getMessageText();

				/*
				 * if (featureNumberB==1) {
				 * 
				 * String firstValueB = listOfFeaturesB.get(0).getValue();
				 * 
				 * 
				 * String tempB = firstValueB; String b=null;
				 * 
				 * while (tempB.startsWith(" ")) { b= tempB.substring(1,
				 * tempB.length()-1); tempB=b; }
				 * 
				 * 
				 * String[] wordsB= tempB.split(" "); // It contains "'" in the
				 * beginning firstWordB=wordsB[0].substring(1); }
				 */

				featureNumberB = featureNumberB + 1;
			}

			if (featureNumberA < 7) {
				for (int i = featureNumberA; i <= 7; i++) {

					resultParameters = resultParameters + "&unit[data][value"
							+ i + "a]=not available"
							+ "&unit[data][messagefeature" + i
							+ "a]=not available";

				}
			}

			if (featureNumberB < 7) {
				for (int i = featureNumberB; i <= 7; i++) {

					resultParameters = resultParameters + "&unit[data][value"
							+ i + "b]=not available"
							+ "&unit[data][messagefeature" + i
							+ "b]=not available";

				}
			}
			// resultParameters =
			// resultParameters+"&unit[data][firstWordA]="+firstWordA+"&unit[data][firstWordB]="+firstWordB;

		} else if (ud instanceof InterlinkValidationWithFullContextUnitDataEntryImpl) {
			InterlinkValidationWithFullContextUnitDataEntryImpl intValData = (InterlinkValidationWithFullContextUnitDataEntryImpl) ud;
			List<FeatureTextValue> listOfFeaturesA = intValData
					.getListOfFeaturesA();
			int featureNumberA = 1;

			String firstWordA = intValData.getFirstWordA();
			String firstWordB = intValData.getFirstWordB();

			for (FeatureTextValue ftvA : listOfFeaturesA) {

				resultParameters = resultParameters + "&unit[data][value"
						+ featureNumberA + "a]=" + ftvA.getValue()
						+ "&unit[data][messagefeature" + featureNumberA + "a]="
						+ ftvA.getMessageText();

				/*
				 * if (featureNumberA==1) { String firstValueA =
				 * listOfFeaturesA.get(0).getValue();
				 * 
				 * String tempA = firstValueA; String a=null;
				 * 
				 * while (tempA.startsWith(" ")) { a= tempA.substring(1,
				 * tempA.length()-1); tempA=a; }
				 * 
				 * 
				 * String[] wordsA= tempA.split(" "); // It contains "'" in the
				 * beginning firstWordA=wordsA[0].substring(1);
				 * 
				 * }
				 */

				featureNumberA = featureNumberA + 1;
			}

			List<FeatureTextValue> listOfFeaturesB = intValData
					.getListOfFeaturesB();

			int featureNumberB = 1;
			for (FeatureTextValue ftvB : listOfFeaturesB) {
				resultParameters = resultParameters + "&unit[data][value"
						+ featureNumberB + "b]=" + ftvB.getValue()
						+ "&unit[data][messagefeature" + featureNumberB + "b]="
						+ ftvB.getMessageText();

				/*
				 * if (featureNumberB==1) {
				 * 
				 * String firstValueB = listOfFeaturesB.get(0).getValue();
				 * 
				 * 
				 * String tempB = firstValueB; String b=null;
				 * 
				 * while (tempB.startsWith(" ")) { b= tempB.substring(1,
				 * tempB.length()-1); tempB=b; }
				 * 
				 * 
				 * String[] wordsB= tempB.split(" "); // It contains "'" in the
				 * beginning firstWordB=wordsB[0].substring(1); }
				 */

				featureNumberB = featureNumberB + 1;
			}

			if (featureNumberA < 7) {
				for (int i = featureNumberA; i <= 7; i++) {

					resultParameters = resultParameters + "&unit[data][value"
							+ i + "a]=not available"
							+ "&unit[data][messagefeature" + i
							+ "a]=not available";

				}
			}

			if (featureNumberB < 7) {
				for (int i = featureNumberB; i <= 7; i++) {

					resultParameters = resultParameters + "&unit[data][value"
							+ i + "b]=not available"
							+ "&unit[data][messagefeature" + i
							+ "b]=not available";

				}
			}
			resultParameters = resultParameters + "&unit[data][firstWordA]="
					+ firstWordA + "&unit[data][firstWordB]=" + firstWordB;

		}

		return resultParameters;

	}

}
