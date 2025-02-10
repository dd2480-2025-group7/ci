package com.group7.ciapp;

import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class TokenGetterTest {
    
    @Test
    /**
     * Test if the function returns a RSAPrivateKey when the key is in the correct format
     */
    public void testRSAPrivateKey() throws Exception {
        String fakePrivateKey = "-----BEGIN PRIVATE KEY-----\r\n" + //
                        "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDXc7xVZ8N3rzK7\r\n" + //
                        "rA6TSjfUHB1Qkc2yzOvE+oS1wC4BLwWm5SLwf2+HmYE+KiM/zxKi8Hmw5SbYz7IJ\r\n" + //
                        "NEP6dw/pBlCkaTeKILacjzIizFVMTG2jeFbuTtngWcllRC8ObO/G4AS2CiXq2dFh\r\n" + //
                        "vHpJRgqu253SBW0GxmtfN+4UF3TRmPCEVP1ptkx0kSn4dOGMNcWAEE3Ut6+UgUr8\r\n" + //
                        "rPgFW2Xg+6kcmpU2qQsA/rboUEcOgfZfWjBkF/5ea+gVd1dvUBqu+2XNMxIH3kfu\r\n" + //
                        "3OWe1lndubJroBt6bEqJpoiH7SmKY/+VYApkTt8JC2no+h2hLzdYDNsF4NL0wDrK\r\n" + //
                        "kCWbmN8zAgMBAAECggEAGgbsMHEJhvLOMSe21wo3NlNzIyGJ9NiWfsQ4tfASXqg8\r\n" + //
                        "iY3Sq7TpVzsvBsB2Y6XzPWXJfZohGD325u++aoppjJ/rrADd6bzL8pvF1bhTcZUm\r\n" + //
                        "BYJrFfzGizitgKM9AAQe7ypux0Lwk69egO0Q5LsmXTtAlHd9VT0xCV6rTxgKT6tu\r\n" + //
                        "MIEdf19pfrng20X3otwR6+Z5TboKN+X4IKNmpKQTm8eXrxxs31Gn/d00CKUgFihg\r\n" + //
                        "D0YAKPHfBeOxpUQb9T5lpFOmQgehK1rufc9dPf2h1AMESRsU++LB6Ed5YRt+9JHd\r\n" + //
                        "Xd01QYzXaqJlJ4im1uzj73ZMwz7FO+tD21gUrSLSsQKBgQD96TahdOYLShRl0HAd\r\n" + //
                        "Cfkpqgn/7dEz63p0f/BoUVc2TIQ2m2fPpe/iY5xdBwvLdSUUUOHS1R/JGeQptGTR\r\n" + //
                        "Wr41xNMGQ4S9bpQXnf6WemPIRzBhRqAIdO31SZfHX2DuJdvgfEhOSpF37C6VMvj+\r\n" + //
                        "uci9je2dgKCfyFC1X02yEEhZ2QKBgQDZOYUvm8v9lJFo5pPKVbAePvkKSC/KE2eF\r\n" + //
                        "JNTmnp6MIPUvWn09quXeWw7MrQIbU8MOXhQDsy8t726y3novHmu1rnT9R9l97Izm\r\n" + //
                        "26gtiMnhXwV5wQHRSNMGvQ3ke3XBvpeD5X/BDkHyblJgkG/TSO7Of0H+Yk+sT/TI\r\n" + //
                        "bkTPqTVt6wKBgQDmygD3Fps2rboGpUQSkQsh/MWkE9Tw3Vvr4pJxL2YXG8udGvpr\r\n" + //
                        "+oclfqcUw5/L6gmvAqmQilme3+LMqYRt6o4zr1ikO+C+knEXWmua+VoTI+nssVYq\r\n" + //
                        "+aWFK8RC2wXu0QifbjgGua6pk0rwIGnElrfI2+WcINtfAKOjiCHF9RjeWQKBgDdw\r\n" + //
                        "XLN32aoQPsQ9BL8A+3/cpoafQcMbDLKIYeWx2dj9mFXLjGcutJf5OV+3T/BVglSq\r\n" + //
                        "YgVHt+TLVL9F1H0uEGM/8Q+rRLDErHlG2jc00AE61RTr02Dsax8vJNqJP9aAo/6/\r\n" + //
                        "lclZsC8FRPTsJ/4OgRQmmlsoEBl0Fo8IxXsSPladAoGAA+S8c7pk5+Uf6HYOOHJ5\r\n" + //
                        "OxNb2YB5IUaQoPtAJo36o2LTwv8cWXqpeivgXrKSeCm5ceqTsTmBb+zXXEl2i3QD\r\n" + //
                        "pJlg5mTKRZcYNbMWpUhxil0eLHzaGseAwfZMwmNvn5qReu9RQbvvJpKDJVYUA43B\r\n" + //
                        "R55HgqW4jddDtB8mOMHpLko=\r\n" + //
                        "-----END PRIVATE KEY-----\r\n" + //
                        "";
        fakePrivateKey = fakePrivateKey.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "")
        .replaceAll("\\s", "");
        RSAPrivateKey RSAKey = TokenGetter.getPrivateKey(fakePrivateKey);
        
        assertTrue(RSAKey instanceof RSAPrivateKey);
    }

    @Test
    /**
     * Test if the function throws an IllegalArgumentException when the key is in the wrong beginning and ending format
     */
    public void testRSAPrivateKey_Wrong_format() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            String fakePrivateKey = "-----BEGINA PRIVATE KEY-----\r\n" + //
                        "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDXc7xVZ8N3rzK7\r\n" + //
                        "rA6TSjfUHB1Qkc2yzOvE+oS1wC4BLwWm5SLwf2+HmYE+KiM/zxKi8Hmw5SbYz7IJ\r\n" + //
                        "NEP6dw/pBlCkaTeKILacjzIizFVMTG2jeFbuTtngWcllRC8ObO/G4AS2CiXq2dFh\r\n" + //
                        "vHpJRgqu253SBW0GxmtfN+4UF3TRmPCEVP1ptkx0kSn4dOGMNcWAEE3Ut6+UgUr8\r\n" + //
                        "rPgFW2Xg+6kcmpU2qQsA/rboUEcOgfZfWjBkF/5ea+gVd1dvUBqu+2XNMxIH3kfu\r\n" + //
                        "3OWe1lndubJroBt6bEqJpoiH7SmKY/+VYApkTt8JC2no+h2hLzdYDNsF4NL0wDrK\r\n" + //
                        "kCWbmN8zAgMBAAECggEAGgbsMHEJhvLOMSe21wo3NlNzIyGJ9NiWfsQ4tfASXqg8\r\n" + //
                        "iY3Sq7TpVzsvBsB2Y6XzPWXJfZohGD325u++aoppjJ/rrADd6bzL8pvF1bhTcZUm\r\n" + //
                        "BYJrFfzGizitgKM9AAQe7ypux0Lwk69egO0Q5LsmXTtAlHd9VT0xCV6rTxgKT6tu\r\n" + //
                        "MIEdf19pfrng20X3otwR6+Z5TboKN+X4IKNmpKQTm8eXrxxs31Gn/d00CKUgFihg\r\n" + //
                        "D0YAKPHfBeOxpUQb9T5lpFOmQgehK1rufc9dPf2h1AMESRsU++LB6Ed5YRt+9JHd\r\n" + //
                        "Xd01QYzXaqJlJ4im1uzj73ZMwz7FO+tD21gUrSLSsQKBgQD96TahdOYLShRl0HAd\r\n" + //
                        "Cfkpqgn/7dEz63p0f/BoUVc2TIQ2m2fPpe/iY5xdBwvLdSUUUOHS1R/JGeQptGTR\r\n" + //
                        "Wr41xNMGQ4S9bpQXnf6WemPIRzBhRqAIdO31SZfHX2DuJdvgfEhOSpF37C6VMvj+\r\n" + //
                        "uci9je2dgKCfyFC1X02yEEhZ2QKBgQDZOYUvm8v9lJFo5pPKVbAePvkKSC/KE2eF\r\n" + //
                        "JNTmnp6MIPUvWn09quXeWw7MrQIbU8MOXhQDsy8t726y3novHmu1rnT9R9l97Izm\r\n" + //
                        "26gtiMnhXwV5wQHRSNMGvQ3ke3XBvpeD5X/BDkHyblJgkG/TSO7Of0H+Yk+sT/TI\r\n" + //
                        "bkTPqTVt6wKBgQDmygD3Fps2rboGpUQSkQsh/MWkE9Tw3Vvr4pJxL2YXG8udGvpr\r\n" + //
                        "+oclfqcUw5/L6gmvAqmQilme3+LMqYRt6o4zr1ikO+C+knEXWmua+VoTI+nssVYq\r\n" + //
                        "+aWFK8RC2wXu0QifbjgGua6pk0rwIGnElrfI2+WcINtfAKOjiCHF9RjeWQKBgDdw\r\n" + //
                        "XLN32aoQPsQ9BL8A+3/cpoafQcMbDLKIYeWx2dj9mFXLjGcutJf5OV+3T/BVglSq\r\n" + //
                        "YgVHt+TLVL9F1H0uEGM/8Q+rRLDErHlG2jc00AE61RTr02Dsax8vJNqJP9aAo/6/\r\n" + //
                        "lclZsC8FRPTsJ/4OgRQmmlsoEBl0Fo8IxXsSPladAoGAA+S8c7pk5+Uf6HYOOHJ5\r\n" + //
                        "OxNb2YB5IUaQoPtAJo36o2LTwv8cWXqpeivgXrKSeCm5ceqTsTmBb+zXXEl2i3QD\r\n" + //
                        "pJlg5mTKRZcYNbMWpUhxil0eLHzaGseAwfZMwmNvn5qReu9RQbvvJpKDJVYUA43B\r\n" + //
                        "R55HgqW4jddDtB8mOMHpLko=\r\n" + //
                        "-----ENDA PRIVATE KEY-----\r\n" + //
                        "";
            fakePrivateKey = fakePrivateKey.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");
            TokenGetter.getPrivateKey(fakePrivateKey);
        });
    }


    @Test
    /**
     * Test if the function throws an InvalidKeySpecException when the key is in the wrong format, i.e in PKCS#1 format instead of PKCS#8
     */
    public void testRSAPrivateKey_InvalidKeySpec() throws Exception {
        assertThrows(InvalidKeySpecException.class, () -> {
            String fakePrivateKey = "-----BEGIN RSA PRIVATE KEY-----\r\n" + //
                                "MIIEowIBAAKCAQEAr2xKE7fuT/VV2Lk7gfCkA4xOTcFXWboTJ6ZGx1zWCP8d1pY5\r\n" + //
                                "mYPx/dTUgDtUjaYGIRJy6G8xYLZvj22aY3l/DdfgLfk4Br9katexMSmKR0C9hVBW\r\n" + //
                                "DbCk6ROK9dqEXuzGmpXhfcYs/9dL2N+CptjsS3PcBjxslcBJhUM60jLV+13No95D\r\n" + //
                                "Bw1f1PCEb3QNffxxVBEYLzv12xgafSjaCo+uY/BUgKbmU3OO6W1w+8z817t+n11m\r\n" + //
                                "ufobCHpyx5f9x7O66gEcT8YT6FtYEPSYVbxPqXveBZaVAUe0uKlvd7yZE5ZAfyKH\r\n" + //
                                "LNpT85ay/yfA6O4B9hwslM2El5ge3FKL53jVFQIDAQABAoIBACoD+QsXgPX4OB9A\r\n" + //
                                "IFtbOGFcK84OSn5kZZ2XwviRiPcfKXWhxfp5oo5t+Racf0As/WdS7KA0r6IvF/HF\r\n" + //
                                "qdZ8/VwisiA4wn1FPocjkZ5JiYPO4wWo6+97+UeU5XHErHMy6U+RqfAutMzzM0im\r\n" + //
                                "ofuV1aStw4tf0g8c/s7y/wXk5KD/XNQtdaIqIS3lipIFhvVLSdPsUFU8KhiNKU99\r\n" + //
                                "QbGNC7rFvrjdU0jbVgIWPuF7ffT3c0aR6x68Y8b8FOLiD140n7grvllSklnUia2S\r\n" + //
                                "zctC4mhZT/9Vfa8cUio+ODGeaXiPqmPsWKJ2kUPSOSRXyG69E5feOXb4uSK4nEWm\r\n" + //
                                "7kRXMDkCgYEA6f38oU9/QLjrmjBxC8onVvLpro0V/B7j3PC+IAJynxwwr4dWb5P6\r\n" + //
                                "gOVNx+hUxAmDyTg5afmhJXXj9U0ZH5h+cLYN5d9tXHVZQsnozMp2RDGsJxxHZqgN\r\n" + //
                                "/hG/EaWe/B1M3XRb+1FV/PjrRZXONdtDfJAw5uZtJ3eUx9tVUjtb9B0CgYEAv+wW\r\n" + //
                                "Xsttdw9aARcQqF9LUltpLMiHHYasYju/M2g/axyWQ6S20DVl4rZtURnbUSjn6ppV\r\n" + //
                                "9Zr+0puhJWwQGbqMmyDEXlV8myV5+KMM1TfmBrffwcuZLu8bJ2RKZDdxQ+HUKGWs\r\n" + //
                                "sxNqzdurmFxIiEjY/hg2kvZ2tJunB8lHLA7VI1kCgYAt9/rIigCa63lFqlybD1TZ\r\n" + //
                                "LRGhfBAknsDvJ9CCI1j3Tyd1ZGYjt3OQHPxB2K/Gb4QXZNdKrYLuBBILn+Depyu3\r\n" + //
                                "4twqG9G1R0yI5Xe5u9CuJwAGEvUoAr30+vGJevsX1n3CR9jGL71v3EsEOaDwTaod\r\n" + //
                                "b4pb4krxZPmypbFGXWj8NQKBgQCX3GaDEbKHQAV20Vpj8Ct4ek5Jmk6XhWXHwQD0\r\n" + //
                                "22s7BP69fYsOSwJYlwL+5lfM63I/B2o3EjLKUUz8gHpo8Vyqrw3SmxAi70+I9vOD\r\n" + //
                                "j1ybOkl6XfDS224ACHJ4xAoCraGjaXhypk2foE8yGutls0bIID6moRCirntHRPPl\r\n" + //
                                "H8N02QKBgAKc7vSuvEnmlhlMZt5bipCclTdtKbYELtbvslk2eNSqya3n3BO3t7FW\r\n" + //
                                "jyfvT56ZJ5UXsS0TYtq5XcVWr3ywdwsr5lwb/2lgtGv40NVfKG2OWqTY4s342HCo\r\n" + //
                                "GtWJ8KdHv7ZgDmkuSR/fJka8sqjTVzeWvzyKCz7kSx+K0QvIPHqj\r\n" + //
                                "-----END RSA PRIVATE KEY-----";
            fakePrivateKey = fakePrivateKey.replace("-----BEGIN RSA PRIVATE KEY-----", "").replace("-----END RSA PRIVATE KEY-----", "")
            .replaceAll("\\s", "");
            TokenGetter.getPrivateKey(fakePrivateKey);
        });
    }
}
