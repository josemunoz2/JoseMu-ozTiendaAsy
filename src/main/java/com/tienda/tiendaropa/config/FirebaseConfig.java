package com.tienda.tiendaropa.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initFirebase() {
        try {
            String jsonCredentials = System.getenv("FIREBASE_CREDENTIALS_JSON");
            if (jsonCredentials == null || jsonCredentials.isEmpty()) {
                throw new RuntimeException("❌ Variable de entorno FIREBASE_CREDENTIALS_JSON no definida");
            }

            InputStream serviceAccount = new ByteArrayInputStream(jsonCredentials.getBytes(StandardCharsets.UTF_8));

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            System.out.println("✅ Firebase inicializado correctamente");

        } catch (Exception e) {
            throw new RuntimeException("❌ Error inicializando Firebase", e);
        }
    }
}