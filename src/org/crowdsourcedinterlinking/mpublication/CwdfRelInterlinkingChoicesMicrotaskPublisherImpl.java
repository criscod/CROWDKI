package org.crowdsourcedinterlinking.mpublication;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.crowdsourcedinterlinking.model.Microtask;
import org.crowdsourcedinterlinking.model.RelInterlinkingChoicesJobMicrotaskImpl;
import org.crowdsourcedinterlinking.util.ConfigurationManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author csarasua
 */
public class CwdfRelInterlinkingChoicesMicrotaskPublisherImpl implements MicrotaskPublisher {

    private int unitsToOrder = 1;

    public String uploadMicrotask(Microtask microtask, Service service) {

        String id = null;


        RelInterlinkingChoicesJobMicrotaskImpl job = (RelInterlinkingChoicesJobMicrotaskImpl) microtask;
        CwdfService cwdf = (CwdfService) service;

        System.out.println("inside upload microtask");
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

        String parameters = "job[title]=" + title + "&job[instructions]="
                + instructions + "&job[cml]=" + cml
                // + "&job[judgments_per_unit]=" + judgmentsPerUnit
                //+ "&job[max_judgments_per_worker]=" + maxJudgmentsPerWorker
                + "&job[payment_cents]=" + cents;
        System.out.println(parameters);

        try {
            //was postJob.setEntity(new StringEntity(parameters));
            StringEntity se = new StringEntity(parameters, ContentType.create("application/application/x-www-form-urlencoded", "UTF-8"));
            postJob.setEntity(se);
            System.out.println("postJob " + postJob);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpResponse response = null;
        try {
            response = client.execute(postJob);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            System.out.println("Success in creating the job");

            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                ObjectMapper mapper = new ObjectMapper();
                InputStream in = null;
                try {
                    in = responseEntity.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Map<String, Object> jobData = null;
                try {
                    jobData = mapper.readValue(in,
                            new TypeReference<Map<String, Object>>() {
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
        } else {
            System.out.println("problem: " + response.getStatusLine().getReasonPhrase());
        }

        return id;
    }

    public void orderMicrotask(String idMicrotask, Service service) {
        //better to order from the Requester UI once checked that CrowdFlower created the task correctly
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
}
