package com.thebubblenetwork.api.framework.util.http;

import javax.net.ssl.*;

/**
 * Created by Jacob on 13/12/2015.
 */
public class SSLUtil {
    public static TrustManager[] getNoTrustManager() {
        return new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }
        }};
    }

    public static KeyManager[] getNoKeyManager() {
        return null;
    }

    public static void setManagers(KeyManager[] keyManager, TrustManager[] trustManager) throws Exception {
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(keyManager, trustManager, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            throw new Exception("Could not set SSL managers", e);
        }
    }

    public static void allowAnySSL() throws Exception {
        setManagers(getNoKeyManager(), getNoTrustManager());
    }
}
