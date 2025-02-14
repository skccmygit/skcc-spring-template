package skcc.arch.pojo;

import java.io.FileInputStream;
import java.security.cert.Certificate;
import java.util.Enumeration;

public class KeyStore {
    public static void main(String[] args) {
        try {
            // 인증서 스토어 경로 (JAVA_HOME/lib/security/cacerts)
            String keystorePath = System.getProperty("java.home") + "/lib/security/cacerts";

            // 인증서 스토어 비밀번호
            String keystorePassword = "changeit"; // 기본 비밀번호 (변경된 경우 수정 필요)

            System.out.println("Key Store Path: " + keystorePath);

            // KeyStore 로드
            java.security.KeyStore keyStore = java.security.KeyStore.getInstance("JKS"); // Java Key Store 형식 사용
            try (FileInputStream keyStoreStream = new FileInputStream(keystorePath)) {
                keyStore.load(keyStoreStream, keystorePassword.toCharArray());
            }

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
