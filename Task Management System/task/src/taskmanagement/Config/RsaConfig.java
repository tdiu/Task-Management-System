package taskmanagement.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@Configuration
public class RsaConfig {

    @Bean
    public KeyPair keyPair() {
        try {
           KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
           keyGen.initialize(2048);
           return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
