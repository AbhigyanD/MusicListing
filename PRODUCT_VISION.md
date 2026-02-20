# Product Vision: Short-Form Music Discovery (Vine → YouTube → TikTok for Music)

## The Disruption

**Spotify = search and play.** You already know what you want.  
**TikTok/Reels = scroll and react.** You don’t search; the feed is the product.

We’re not building “Spotify with a different skin.” We’re building **the feed that replaces “what should I play?”** with **“one swipe, one moment, one song.”**

---

## Positioning

| | Spotify / Apple Music | TikTok / Reels | **This product** |
|---|----------------------|----------------|-------------------|
| **Input** | Search, playlists, albums | Infinite vertical feed (video-first) | **Infinite vertical feed (music-first)** |
| **Unit of consumption** | Track / album | 15–60s video clip | **15–60s “moment”: hook + art + optional clip** |
| **Discovery** | Browse, recommendations, social | Algorithm + scroll | **Scroll-only discovery + reactions + share** |
| **Creation** | Artist/label only | UGC short video | **UGC “moments” (audio + visual or audio-only)** |

**One-line pitch:**  
*“TikTok for music: swipe through moments, not playlists. One hook, one vibe, one tap to play. No search bar.”*

---

## Core UX Principles

1. **Feed is the home.** No “library” or “search” as the default. Open app → first moment starts (or continues). Like opening TikTok and landing in the feed.
2. **Vertical, full-screen cards.** One moment per screen. Swipe up = next, swipe down = previous. Optional: tap to pause/play, double-tap to like.
3. **Music plays with the card.** Audio is the hero; visual (art, optional short video) supports the moment. No “now playing” bar that competes with the feed—the current card *is* the now playing.
4. **Moments, not full tracks (initially).** A “moment” = 15–60 seconds (e.g. the hook). Reduces commitment and increases variety. Later: “play full track” or “add to queue.”
5. **Reactions over playlists.** Like, comment, share, “save moment.” Algorithm (later) uses this. No “create playlist” as the primary action.
6. **Create = record a moment.** Users can attach a short clip (or static art) to a song segment. UGC turns the feed into community, not just catalog.

---

## Why This Can Win

- **Attention:** Short-form has already won (TikTok, Reels, Shorts). Music is the next layer: same habit, music-first.
- **Discovery gap:** Playlists and search favor the same hits. A feed + algorithm can surface niches and new artists in a way that feels like “stumbling onto” something, not searching.
- **Low friction:** No “build a playlist” or “choose an album.” Open → scroll → react. Fits mobile and passive listening.
- **Differentiation:** No one owns “TikTok for music” at scale. Spotify is adding clips; we’re *starting* from the clip.

---

## Tech Direction (Efficiency, Speed, Flexibility)

The existing **Java + Swing** app is a great backend/library of use cases (auth, artists, events, comments). For a **new product** that feels like TikTok and plays music:

- **Frontend:** **Web-first (React + Vite + TypeScript)** or **Flutter**.  
  - Web: one codebase, deploy anywhere, easy to add PWA, later wrap (Capacitor/Electron) for desktop/mobile. Best for speed of iteration and hiring.  
  - Flutter: if the goal is native mobile-only and maximum performance on device.

- **Backend / API:**  
  - **Option A:** Keep **Java** as the API (Spring Boot or existing stack). Frontend calls REST/GraphQL. Reuse Firebase/Firestore, MusicBrainz, and all current logic.  
  - **Option B:** **Node.js (Fastify/Express)** or **Go (Echo/Gin)** as a BFF that talks to Firebase and optionally to the Java service. Good if you want one language (JS/TS) across front + API or need very high throughput (Go).  
  - **Option C:** **Serverless (e.g. Firebase Functions + Firestore)**. Frontend talks to Firebase directly for auth and data; heavier logic in Cloud Functions. Minimal backend to maintain.

- **Media:**  
  - **Audio:** HTML5 `Audio` or Howler.js in the web app. Sources: MusicBrainz preview URLs, or later licensed streams (e.g. Spotify Web API, Apple Music API).  
  - **Video (optional):** Short clips in Firebase Storage or S3, served via CDN. Play with HTML5 video or a lightweight player (e.g. video.js).

- **Recommendation:**  
  - **Phase 1:** New **React (Vite) + TypeScript** frontend with a TikTok-style vertical feed and audio playback. API: **Firebase (Auth + Firestore)** from the client, plus a **thin Node or Java API** that returns “moments” (e.g. from MusicBrainz + your DB).  
  - **Phase 2:** Add creation (upload moment), reactions (like/comment in Firestore), and a simple recommendation (e.g. “next = random from same genre or same artist”).

---

## What We’re Building in This Repo

- **`PRODUCT_VISION.md`** (this file): product and tech direction.
- **`web-app/`**: New React + Vite + TypeScript app with:
  - Full-screen vertical feed (TikTok-style), scroll-snap per card.
  - “Moment” cards: full-bleed artwork, title, artist, play/pause (audio when `audioUrl` is set).
  - Next/previous via **scroll**, **arrow keys** (↑/↓), or **space**. Touch-friendly.
  - Sample data in the client; replace with your API (Java, Node, or Firebase) when ready.
- **Existing `src/main/java`**: Stays as the backend or data source. Options:
  - **A)** Expose a REST endpoint (e.g. Spring Boot) that returns a list of “moments” (artist + recording + optional preview URL from MusicBrainz). Web app `fetch()`es that.
  - **B)** Add a thin **Node.js or Go** BFF that calls your Java service and/or Firebase and returns the feed JSON. Keeps the web app backend-agnostic and lets you optimize for speed (Go) or one language (Node).

Result: **one repo, two surfaces**—legacy Java UI (or headless Java API) + new **disruptive feed-first web product** that actually plays music when you plug in audio URLs.
