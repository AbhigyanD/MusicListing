# Music Feed — full working app

TikTok-style **vertical feed of music moments**: **songs come directly from Spotify** (feed + search), with 30s preview playback. Optional **sign-in** and **likes** (Firebase).

## Quick run (feed + playback, no auth)

You need **both** the API and the frontend.

```bash
cd web-app
npm install
cd server && npm install && cd ..
npm run dev:all
```

- **API** runs at [http://localhost:3001](http://localhost:3001) (feed and search from Spotify; requires Spotify credentials in `server/.env`).
- **App** runs at [http://localhost:5173](http://localhost:5173) and proxies `/api` to the API.

Or in two terminals:

```bash
# Terminal 1
cd web-app/server && npm install && node --watch server.js

# Terminal 2
cd web-app && npm install && npm run dev
```

Then open [http://localhost:5173](http://localhost:5173). Use **↓ / space** for next, **↑** for previous. Tap the cover to **play/pause**.

## Required: Spotify credentials (songs come from Spotify only)

Feed and search both use the **Spotify API** — no MusicBrainz, no placeholder audio. You need credentials or the feed/search will be empty.

1. In [Spotify for Developers](https://developer.spotify.com/dashboard) create an app and copy **Client ID** and **Client Secret**.
2. In `web-app/server`, create a `.env` file (see `.env.example`) with:
   ```
   SPOTIFY_CLIENT_ID=your_id
   SPOTIFY_CLIENT_SECRET=your_secret
   ```
3. Restart the API. You’ll get tracks and 30s previews directly from Spotify.

## Optional: sign-in and likes (Firebase)

1. Copy `.env.example` to `.env` and add your Firebase Web app config (from Firebase Console → Project settings → Your apps).
2. In Firebase Console:
   - **Authentication** → enable Email/Password (and optionally Google).
   - **Firestore** → create a collection `moment_likes` (or let the app create it on first like). Add a rule so only signed-in users can read/write:

```txt
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /moment_likes/{doc} {
      allow read: if true;
      allow create: if request.auth != null && request.resource.data.userId == request.auth.uid;
      allow delete: if request.auth != null && resource.data.userId == request.auth.uid;
    }
  }
}
```

3. Restart the app (`npm run dev:all`). You’ll see **Sign in** and **Create account**; after signing in, **Like** is persisted in Firestore.

Without Firebase, the app still works: feed + playback; Like counts stay local (no persistence).

## What’s included

| Feature | How |
|--------|-----|
| **Feed** | Node API calls MusicBrainz (recordings + cover art), returns JSON. Fallback sample data if MB fails. |
| **Playback** | 30s previews from Spotify (when available). Feed and search both use Spotify as the only source. |
| **Auth** | Firebase Auth (email/password). Optional; if not configured, you go straight to the feed. |
| **Likes** | Firestore collection `moment_likes`; signed-in users can like; count shown on card. |
| **Share** | Share button opens Twitter intent with track title and artist. |

## Build for production

```bash
npm run build
```

Serves from `dist/`. For production you must run the API separately (e.g. same host with a reverse proxy, or deploy API to Railway/Render/Fly) and set the app’s API base URL (or keep using relative `/api` if you proxy).

## Repo layout

- `web-app/` — React app (feed UI, auth, likes).
- `web-app/server/` — Node API (MusicBrainz feed + preview URLs).
- Root `src/main/java/` — existing Java backend; can be used later for more data or auth.

See **PRODUCT_VISION.md** in the repo root for product and tech direction.
