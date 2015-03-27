package org.crowdsourcedinterlinking.mpublication;
/**
 * @author csarasua
 */
public abstract class Service {

    protected String baseURL;

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

}
