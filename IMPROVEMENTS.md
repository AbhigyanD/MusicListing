# Software Development Principles — Improvement Guide

This document maps **good software development principles** to concrete changes you can make in this codebase. Each section names the principle, the current issue, and a recommended direction.

---

## 1. Dependency Inversion (DIP)

**Principle:** High-level modules should not depend on low-level modules; both should depend on abstractions.

**Current issues:**
- `AppCoordinator` directly instantiates concrete classes (`DBUserAccessObject`, `MusicBrainzArtistRepository`, etc.). Swapping implementations (e.g. for tests or a different DB) requires editing the coordinator.
- Views call `AppCoordinator.getInstance()` and thus depend on the concrete coordinator.
- Data access objects read `CurrentUser.db` (static) instead of receiving a `Firestore` instance.

**Recommendations:**
- **Inject dependencies via constructors.** Pass interfaces (e.g. `LoginDataAccessInterface`, `Firestore`) into interactors and DAOs instead of creating them inside the coordinator.
- **Introduce a `ViewNavigator` (or `ScreenFactory`) interface** that defines methods like `createLoginView()`, `createMainMenuView()`, etc. `AppCoordinator` implements it. Views and presenters depend on `ViewNavigator`, not on `AppCoordinator` — so you can test or swap UI flow without the real coordinator.
- **Composition root:** Do all wiring (building object graph) in one place (e.g. `App.main()` or a dedicated `CompositionRoot` class). The rest of the app only receives dependencies and does not `new` up its collaborators.

---

## 2. Single Responsibility (SRP)

**Principle:** A class should have only one reason to change.

**Current issues:**
- `AppCoordinator` both holds navigation flow and constructs every screen’s full dependency graph (views, presenters, interactors, controllers, DAOs). It’s a “god” object.

**Recommendations:**
- **Split by feature:** e.g. `LoginScreenFactory`, `ArtistListingScreenFactory` — each knows how to build one screen and its dependencies. `AppCoordinator` (or the composition root) only calls these factories and/or holds the navigator interface.
- **Keep presenters focused:** They should only translate use-case outcomes into view/navigation calls, not contain business rules or data access.

---

## 3. Avoid Global Mutable State

**Principle:** Global mutable state makes behavior hard to reason about and tests hard to run in isolation.

**Current issues:**
- `CurrentUser` exposes `public static String username` and `public static Firestore db`. Any code can read/write them; tests and multiple “sessions” can interfere.

**Recommendations:**
- **Introduce a `Session` (or `CurrentUser`) abstraction:** An interface with `getUsername()`, `setUsername()`, `getFirestore()`. One implementation holds the current user and DB reference. Create a single instance at startup and **inject** it into the coordinator, interactors that need to set the user (e.g. login), and DAOs that need the DB. No static fields.
- **Pass “current user” where needed:** E.g. pass `Session` (or just username) into the writer use case and into views that display the username, instead of reading from a global.

---

## 4. Externalized Configuration

**Principle:** Configuration (paths, URLs, feature flags) should not be hardcoded; it should come from config files or environment variables.

**Current issues:**
- Firebase credentials path and project ID are hardcoded in `FireStoreInitializer`. This makes environments (dev/test/prod) and local setups brittle and can leak project details.

**Recommendations:**
- **Use environment variables** (e.g. `FIREBASE_CREDENTIALS_PATH`, `FIRESTORE_PROJECT_ID`) with sensible defaults or a small `ApplicationConfig` class that reads from env.
- **Never commit credentials.** Document that the credentials file must be present at the path (or path from env) and add that path to `.gitignore` if it isn’t already.

---

## 5. Explicit Error Handling and Fail-Fast

**Principle:** Errors should be visible and handled explicitly; avoid silent failures and returning `null` for “error”.

**Current issues:**
- `FireStoreInitializer.initializeFirestore()` returns `null` on failure and only logs; callers may get NPEs.
- `DBPublicAccessObject.readContents()` returns `null` when the document doesn’t exist or on exception; callers must remember to null-check.
- Some `catch` blocks only print to stderr and continue, leaving the system in an unclear state.

**Recommendations:**
- **Prefer throwing or returning a result type:** e.g. throw a custom `DatabaseException` from the initializer if Firestore cannot be created, and handle it in `App.main()` (show message and exit or retry). For “optional” data, consider `Optional<Map<String, Object>>` or a small `Result<T, E>` instead of `null`.
- **Validate at boundaries:** Validate input in the controller or use case and fail fast with clear messages (you already do some of this in `WriterInteractor`).

---

## 6. Testability

**Principle:** Design so that units can be tested in isolation with mocks and fakes.

**Current issues:**
- `LoginInteractor` sets `CurrentUser.username` (static). Testing login success requires either resetting globals between tests or accepting shared state.
- DAOs read `CurrentUser.db`; to test them you need a real or emulated Firestore unless you inject the client.
- Views that call `AppCoordinator.getInstance()` are hard to test without bringing up the real navigation.

**Recommendations:**
- **Constructor injection everywhere:** Interactors and DAOs receive their dependencies (data access, output boundary, session, Firestore) in the constructor. In tests, pass mocks/fakes.
- **Replace static `CurrentUser` with injectable `Session`:** Then in tests you inject a mock or in-memory `Session` and never touch Firestore.
- **Use the `ViewNavigator` interface in views:** In tests, inject a fake navigator that records calls or does nothing, so view logic can be tested without the real `AppCoordinator`.

---

## 7. Naming and Package Conventions

**Principle:** Consistent naming and package structure improve readability and tooling.

**Current issue:**
- Package name `Use_case` (capital U) is inconsistent with common Java convention (e.g. `use_case` or `application`).

**Recommendation:**
- Consider renaming to `use_case` (or `application.usecase`) for consistency. Do it in a single refactor and update imports to avoid confusion.

---

## 8. Security

**Principle:** Sensitive data (passwords, credentials) must not be stored or transmitted in plain form where avoidable.

**Current issues:**
- Passwords appear to be stored and compared in plain text in Firestore and in `User`. This is a significant risk if the DB or backups are exposed.

**Recommendations:**
- **Hash and salt passwords** before storing (e.g. BCrypt or Argon2). Store only the hash; on login, hash the provided password and compare with the stored hash. Never store or log plain passwords.
- **Keep credentials file out of version control** and load path from configuration (see §4).

---

## 9. Single Source of Truth for Wires

**Principle:** The way the application is assembled (which implementation is used for which interface) should live in one place.

**Current issue:**
- Every `create*View()` method in `AppCoordinator` repeats the same pattern: create view, create presenter, create DAO, create interactor, create controller, wire them. This is duplicated wiring logic.

**Recommendations:**
- **Extract factories or a composition root** that build each screen’s object graph once. The coordinator (or navigator) only calls “show login”, “show main menu”, etc., and receives already-wired screens. This reduces duplication and keeps “what depends on what” in one place.

---

## 10. Small, Focused Refactors

**Principle:** Apply improvements in small steps so the app keeps running and tests stay green.

**Recommendations:**
- Introduce interfaces (e.g. `Session`, `ViewNavigator`) and keep existing behavior: e.g. `AppCoordinator` implements `ViewNavigator` and still creates views the same way. Then gradually switch callers to depend on the interface.
- Add new classes (e.g. `ApplicationConfig`, `SessionImpl`) without removing the old ones at first; switch one caller at a time, then remove the old path once unused.
- Run tests and the app after each refactor.

---

## Summary Checklist

- [ ] Replace static `CurrentUser` with an injectable `Session` (interface + impl).
- [ ] Inject `Firestore` (and other external services) into DAOs via constructor.
- [ ] Externalize config (credentials path, project ID) via env or `ApplicationConfig`.
- [ ] Introduce `ViewNavigator` and have views/presenters depend on it instead of `AppCoordinator`.
- [ ] Split `AppCoordinator` into smaller factories or a single composition root.
- [ ] Use `Optional` or result types instead of `null` for “missing” data; throw or exit on fatal config/DB errors.
- [ ] Hash and salt passwords before storing.
- [ ] Align package naming (e.g. `use_case`) and document the layout.

Implementing these in the order above will improve testability, clarity, and maintainability while keeping the existing architecture intact.

---

## How to Integrate the New Abstractions

The following classes were added to support the principles above. Wire them in as follows.

### 1. `config.ApplicationConfig`

- **Use in `FireStoreInitializer`:** Replace the hardcoded path and project ID with `ApplicationConfig.getFirebaseCredentialsPath()` and `ApplicationConfig.getFirestoreProjectId()`.
- **At runtime:** Set env vars `FIREBASE_CREDENTIALS_PATH` and `FIRESTORE_PROJECT_ID` when needed (e.g. CI or production); otherwise defaults are used.

### 2. `session.Session` and `session.SessionImpl`

- **In `App.main()`:** Create a single `SessionImpl`, call `session.setFirestore(...)` with the result of `FireStoreInitializer.initializeFirestore()`, and pass this `Session` into your coordinator (or composition root).
- **In `AppCoordinator`:** Store the `Session` (e.g. constructor or `init(Session)`). When building login use case, pass `Session` into `LoginInteractor` so it can call `session.setUsername(username)` instead of `CurrentUser.username = username`.
- **In DAOs:** Accept `Firestore` in the constructor (from `session.getFirestore()`) instead of reading `CurrentUser.db`. The coordinator gets Firestore from the session and passes it when creating `DBUserAccessObject`, `DBPublicAccessObject`, etc.
- **In views that need the current username:** Receive `Session` (or the navigator that can provide it) when the view is created, and use `session.getUsername()` instead of `CurrentUser.username`.
- **In tests:** Inject a mock or in-memory `Session` (and optionally a mock `Firestore`) so no static state is used.

### 3. `navigation.ViewNavigator`

- **Make `AppCoordinator` implement `ViewNavigator`:** Move the existing `create*View()` methods into the interface implementation (they already match the interface).
- **Views and presenters:** Instead of calling `AppCoordinator.getInstance()` and then a method, receive a `ViewNavigator` (e.g. via setter or constructor) and call `navigator.createLoginView()`, etc. The coordinator can pass itself as the `ViewNavigator` when creating views.
- **In tests:** Inject a fake `ViewNavigator` that records calls or does nothing, so you can test view logic without the real coordinator.
