package session;

import com.google.cloud.firestore.Firestore;

/**
 * Default in-memory implementation of Session.
 * Use one instance per application and inject it into coordinators, interactors, and DAOs.
 */
public final class SessionImpl implements Session {

    private String username = "";
    private Firestore firestore;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username != null ? username : "";
    }

    @Override
    public Firestore getFirestore() {
        return firestore;
    }

    @Override
    public void setFirestore(Firestore firestore) {
        this.firestore = firestore;
    }
}
