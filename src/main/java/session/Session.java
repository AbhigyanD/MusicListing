package session;

import com.google.cloud.firestore.Firestore;

/**
 * Abstraction for the current user session and shared infrastructure (e.g. DB).
 * Inject this instead of using static globals so code is testable and has a
 * single source of truth for "current user" and "database".
 */
public interface Session {

    String getUsername();

    void setUsername(String username);

    /** May be null if not initialized (e.g. tests). */
    Firestore getFirestore();

    void setFirestore(Firestore firestore);
}
