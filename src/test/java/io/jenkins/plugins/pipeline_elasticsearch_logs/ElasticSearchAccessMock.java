package io.jenkins.plugins.pipeline_elasticsearch_logs;

import static io.jenkins.plugins.pipeline_elasticsearch_logs.ElasticSearchConfiguration.CONNECTION_TIMEOUT_DEFAULT;
import static io.jenkins.plugins.pipeline_elasticsearch_logs.testutils.JSONUtils.prettyPrint;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.util.ArrayList;

import org.apache.http.conn.ConnectTimeoutException;

/**
 * This class mocks an ElasticSearchAccess and overrides the {@link ElasticSearchAccess#push(String)} method.
 * The data the plugin tries to send to Elasticsearch are collected and can be retrieved via {@link #getEntries()}.
 */
public class ElasticSearchAccessMock extends ElasticSearchAccess {

    private ArrayList<String> entries = new ArrayList<String>();
    private boolean printToLog = false;
    private boolean failConnection = false;

    public ElasticSearchAccessMock(boolean printToLog) throws URISyntaxException {
        super(new URI("http://localhost:9200/jenkins/_doc"), "test", "test", CONNECTION_TIMEOUT_DEFAULT);
        this.printToLog = printToLog;
    }

    @Override
    public void setTrustKeyStore(KeyStore trustKeyStore) {
    }

    @Override
    String testConnection() throws URISyntaxException, IOException {
        return "";
    }

    @Override
    public void push(String data) throws IOException {
        if(failConnection) {
            throw new ConnectTimeoutException("Connect to Elasticsearch failed: connect timed out");
        }
        data = prettyPrint(data);
        if (printToLog) {
            System.out.println(data);
        }
        entries.add(data);
    }

    /**
     * @return all entries the plugin tried to sent to Elasticsearch
     */
    public ArrayList<String> getEntries() {
        return entries;
    }

    public void failConnection(boolean failConnection) {
        this.failConnection = failConnection;
    }

}
