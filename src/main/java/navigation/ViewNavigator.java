package navigation;

import data_transfer_object.Artist;
import data_transfer_object.Recording;

import java.util.Map;

/**
 * Abstraction for navigating between screens.
 * Views and presenters should depend on this interface, not on AppCoordinator,
 * so navigation can be tested or replaced (e.g. different flows for different builds).
 */
public interface ViewNavigator {

    void createLoginView();

    void createSignUpView();

    void createMainMenuView();

    void createArtistListingView();

    void createArtistDetailView(Recording[] topSongs, Map<String, String> comments,
                               Artist artist, Double averageRating);

    void createEventListingView();

    void createSearchSelectionView();

    void createUserAccountView();
}
