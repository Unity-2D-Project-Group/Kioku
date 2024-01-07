package pt.iade.memoriescompanionapp.utilities;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class WebRequest {
    public final static String LOCALHOST = "http://10.0.2.2:5000";

    protected URL url;

    public WebRequest(URL url) {
        this.url = url;
    }

    public String performGetRequest() throws IOException, URISyntaxException {
        return performGetRequest(null);
    }

    public String performGetRequest(HashMap<String, String> params) throws IOException, URISyntaxException {
        URI uri = buildUri(params);
        HttpURLConnection urlConnection = (HttpURLConnection) uri.toURL().openConnection();
        String result = null;
        try {
            if(urlConnection.getResponseCode() == 200){
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                result = readStreamToString(in);
            }else{
                InputStream in = new BufferedInputStream(urlConnection.getErrorStream());
                result = readStreamToString(in);
            }
        } finally {
            urlConnection.disconnect();
        }

        return result;
    }

    public void performPostRequest(HashMap<String, String> params) throws IOException, URISyntaxException {
        String body = null;
        for (String key : params.keySet()) {
            if (body == null) {
                body = key + "=" + URLEncoder.encode(params.get(key), StandardCharsets.UTF_8.toString());
            } else {
                body += "&" + key + "=" + URLEncoder.encode(params.get(key), StandardCharsets.UTF_8.toString());
            }
        }
        byte[] postData = body.getBytes(StandardCharsets.UTF_8);

        URI uri = buildUri(null);
        HttpURLConnection urlConnection = (HttpURLConnection) uri.toURL().openConnection();
        urlConnection.setRequestMethod("POST");
        //urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        //urlConnection.setRequestProperty("Content-Length", Integer.toString(postData.length));
        urlConnection.setUseCaches(false);
        urlConnection.setDoOutput(true);
        urlConnection.connect();
        try(OutputStream os = urlConnection.getOutputStream()) {
            os.write(postData, 0, postData.length);
        } finally {
            urlConnection.disconnect();
        }
    }

    protected String readStreamToString(InputStream in) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(in);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        for (int result = bis.read(); result != -1; result = bis.read()) {
            buf.write((byte) result);
        }

        return buf.toString("UTF-8");
    }

    protected URI buildUri(HashMap<String, String> params) throws IOException, URISyntaxException {
        URI uri = new URI(url.toString());

        if (params != null) {
            String query = uri.getQuery();
            for (String key : params.keySet()) {
                if (query == null) {
                    query = key + "=" + URLEncoder.encode(params.get(key), StandardCharsets.UTF_8.toString());
                } else {
                    query += "&" + key + "=" + URLEncoder.encode(params.get(key), StandardCharsets.UTF_8.toString());
                }
            }

            uri = new URI(uri.getScheme(), uri.getAuthority(),
                    uri.getPath(), query, uri.getFragment());
        }

        return uri;
    }
}
