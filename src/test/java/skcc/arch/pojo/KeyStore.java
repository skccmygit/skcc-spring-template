package skcc.arch.pojo;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Enumeration;

public class KeyStore {
    public static void main(String[] args) {
        try {

            String importCertPath = "인증서_파일_경로";
            java.security.KeyStore keyStore = addCertificate(importCertPath, "test-moon");
            getCertList(keyStore);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getCertList(java.security.KeyStore keyStore) throws KeyStoreException {
        // KeyStore의 alias 목록 가져오기
        Enumeration<String> aliases = keyStore.aliases();
        System.out.println("=== Keystore Alias 목록 ===");
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            System.out.println("Alias: " + alias);

            // 인증서 가져오기
            Certificate certificate = keyStore.getCertificate(alias);
            if (certificate != null) {
//                    System.out.println("Certificate Type: " + certificate.getType());
//                    System.out.println("Certificate: " + certificate.toString());
            } else {
                System.out.println("Certificate: 인증서를 찾을 수 없습니다.");
            }

            System.out.println("------------------------------");
        }
    }


    public static java.security.KeyStore addCertificate(String certPath, String certAlias) throws Exception {
        // 인증서를 로드 (PEM 또는 DER 포맷 사용 가능)
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        try (InputStream certInputStream = new FileInputStream(certPath)) {
            Certificate certificate = cf.generateCertificate(certInputStream);

            // Java의 기본 키스토어(java.home에 기본 위치로 배포되는 cacerts) 로드
            char[] password = "changeit".toCharArray(); // 기본 비밀번호 (changeit)
            java.security.KeyStore keyStore = java.security.KeyStore.getInstance(java.security.KeyStore.getDefaultType());
            try (InputStream keyStoreStream = new FileInputStream(System.getProperty("java.home") + "/lib/security/cacerts")) {
                System.out.println("## JAVA HOME: " + System.getProperty("java.home") );
                keyStore.load(keyStoreStream, password);
            }

            // 키스토어에 인증서를 추가
            keyStore.setCertificateEntry(certAlias, certificate);

            // 새로운 키스토어를 로드한 TrustManagerFactory 생성
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            // 새 TrustManagerFactory 사용으로 SSLContext 업데이트
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            SSLContext.setDefault(sslContext);

            System.out.println("Certificate successfully added to the TrustStore.");

            return keyStore;
        }
    }
}
