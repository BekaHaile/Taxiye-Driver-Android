package product.clicklabs.jugnoo.driver.utils;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class CustomX509TrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] certs,
            String authType) throws CertificateException {

        // Here you can verify the servers certificate. (e.g. against one which is stored on mobile device)
    	
    }

    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}