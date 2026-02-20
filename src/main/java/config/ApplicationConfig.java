package config;

/**
 * Centralized application configuration.
 * Reads from environment variables with fallbacks so behaviour can change
 * per environment without code changes (externalized configuration).
 */
public final class ApplicationConfig {

    private static final String DEFAULT_CREDENTIALS_PATH =
            "src/main/java/database/csc207musicapp-firebase-adminsdk-gzeyt-8c0d614d66.json";
    private static final String DEFAULT_PROJECT_ID = "csc207musicapp";

    private ApplicationConfig() {}

    /** Path to Firebase service account JSON. Override with env var FIREBASE_CREDENTIALS_PATH. */
    public static String getFirebaseCredentialsPath() {
        String env = System.getenv("FIREBASE_CREDENTIALS_PATH");
        return env != null && !env.isBlank() ? env.trim() : DEFAULT_CREDENTIALS_PATH;
    }

    /** Firestore project ID. Override with env var FIRESTORE_PROJECT_ID. */
    public static String getFirestoreProjectId() {
        String env = System.getenv("FIRESTORE_PROJECT_ID");
        return env != null && !env.isBlank() ? env.trim() : DEFAULT_PROJECT_ID;
    }
}
