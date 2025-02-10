package com.group7.ciapp;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

// openssl pkcs8 \
//   -topk8 \
//   -inform PEM \
//   -outform PEM \
//   -in original_key.pem \
//   -out pkcs8_key.pem \
//   -nocrypt

/**
 * This class is responsible for getting the Installation Access Token for the
 * GitHub App.
 */
public class TokenGetter {

    /**
     * Default constructor for TokenGetter.
     */
    public TokenGetter() {
        // Default constructor
    }

    /**
     * Get Installation Access Token for the GitHub App.
     * 
     * @param app_id (String) GitHub App ID.
     * @return (String) Installation Access Token.
     * @throws Exception when an error occurs
     *                   while getting the
     *                   installation access
     *                   token.
     */
    public static String token(String app_id)
            throws Exception {
        File keyFile = new File(System.getenv("PRIVATE_KEY_PATH"));
        String key = new String(Files.readAllBytes(keyFile.toPath()));
        key = key.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        RSAPrivateKey privateKey = getPrivateKey(key);

        // Generate JWT
        String jwt = JWT.create()
                .withIssuer(app_id)
                .withIssuedAt(new Date(System.currentTimeMillis() - 60 * 1000)) // 60s clock drift
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000)) // 10 min expiry
                .sign(Algorithm.RSA256(null, privateKey));

        // Get installation id
        Integer installationId = getInstallationId(jwt);

        // using installation id, get final access token
        String accessToken = getInstallationAccessToken(installationId, jwt);

        return accessToken;
    }

    /**
     * Get installation id for the GitHub app.
     * 
     * @param jwt (String) JSON Web Token.
     * @return (Integer) installation id, null if not found.
     * @throws Exception if an error occurs while getting the installation id.
     */
    private static Integer getInstallationId(String jwt)
            throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(String.format("https://api.github.com/app/installations"));
        request.setHeader(HttpHeaders.ACCEPT, "application/vnd.github.v3+json");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
        request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        request.addHeader(HttpHeaders.USER_AGENT, "Apache HttpClient");

        CloseableHttpResponse response = client.execute(request);

        // Get JSON response as org.JSON.JSONObject
        String responseString = EntityUtils.toString(response.getEntity());
        JSONArray jsonResponse = new org.json.JSONArray(responseString);

        for (int i = 0; i < jsonResponse.length(); i++) {
            JSONObject installation = jsonResponse.getJSONObject(i);
            JSONObject account = installation.getJSONObject("account");
            String htmlUrl = account.getString("html_url");

            if (htmlUrl.equals("https://github.com/dd2480-2025-group7")) {
                return installation.getInt("id");
            }
        }
        return null;
    }

    /**
     * Get installation access token for the GitHub app.
     * 
     * @param installation_id (Integer) Installation ID.
     * @param jwt             (String) JSON Web Token.
     * @return (String) Installation Access Token.
     * @throws Exception if an error occurs while getting the installation access
     *                   token.
     */
    private static String getInstallationAccessToken(Integer installation_id, String jwt) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost request = new HttpPost(String
                .format("https://api.github.com/app/installations/%d/access_tokens", installation_id));
        request.setHeader(HttpHeaders.ACCEPT, "application/vnd.github.v3+json");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
        request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        request.addHeader(HttpHeaders.USER_AGENT, "Apache HttpClient");
        CloseableHttpResponse response = client.execute(request);

        // Get JSON response as org.JSON.JSONObject
        String responseString = EntityUtils.toString(response.getEntity());
        JSONObject jsonResponse = new org.json.JSONObject(responseString);

        // return .token
        return jsonResponse.getString("token");
    }

    /**
     * Get Private Key from String.
     * 
     * @param Key (String) Private Key in String format.
     * @return (RSAPrivateKeyv) Private Key
     * @throws Exception if an error occurs while getting the private key.
     */
    private static RSAPrivateKey getPrivateKey(String key)
            throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }
}