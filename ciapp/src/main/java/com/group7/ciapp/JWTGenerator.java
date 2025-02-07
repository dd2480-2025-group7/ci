package com.group7.ciapp;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class JWTGenerator {
    
    public static String token(String app_id) {
        try {
            File keyFile = new File("private-key.pem");
            String key = new String(Files.readAllBytes(keyFile.toPath()));
            RSAPrivateKey privateKey = getPrivateKey(key);

            // Generate JWT
            String jwt = JWT.create()
                .withIssuer(app_id)
                .withIssuedAt(new Date(System.currentTimeMillis() - 60 * 1000)) // 60s clock drift
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000)) // 10 min expiry
                .sign(Algorithm.RSA256(null, privateKey));

            
            return jwt;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static RSAPrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }
}