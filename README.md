# Moments — TikTok-Style Music Discovery

A full-screen, vertical-feed music app where every swipe is a new song. Built with **React + TypeScript + Vite** on the frontend and a **Node/Express** API that pulls tracks from **Spotify** with 30-second audio previews via **iTunes**.

**One-line pitch:** *Swipe through moments, not playlists. One hook, one vibe, one tap to play.*

---

## Demo

Open the app and you land in the feed. Each card is a "moment" — album art, track name, artist, and a looping 30-second preview that auto-plays as you scroll. Search any song or artist from the header. Tap anywhere to pause. Hit the green button to play the full track on Spotify.

---

## Features

- **Infinite vertical feed** — full-screen cards, scroll-snap, auto-advances audio
- **Search** — find any song or artist via Spotify's catalog
- **30-second audio previews** — auto-play on scroll, loop continuously, tap to pause/resume
- **Spotify embed** — "Play Full Song" opens the Spotify player for full-length playback
- **Like, Comment, Share** — reaction bar on every card (likes persisted via Firestore when configured)
- **Firebase Auth** (optional) — sign in / create account, shown as a non-blocking overlay
- **Keyboard navigation** — Arrow Up/Down and Space to scroll the feed
- **Dark, immersive UI** — blurred album art backgrounds, Tailwind CSS

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | React 18, TypeScript, Vite, Tailwind CSS |
| API | Node.js, Express |
| Music Data | Spotify Web API (track metadata + art) |
| Audio Previews | iTunes Search API (free 30s previews, no auth) |
| Auth (optional) | Firebase Authentication |
| Likes (optional) | Cloud Firestore |

---

## Project Structure

```
MusicListing/
├── web-app/                  # The new feed-first app
│   ├── src/
│   │   ├── components/       # Feed, MomentCard, LoginScreen, SignupScreen
│   │   ├── hooks/            # useAudio (global singleton player), useLikes
│   │   ├── contexts/         # AuthContext (Firebase auth)
│   │   ├── lib/              # Firebase config
│   │   ├── types/            # Moment type definition
│   │   ├── App.tsx           # Main app shell (header, search, feed, auth modal)
│   │   └── main.tsx          # Entry point
│   ├── server/
│   │   ├── server.js         # Express API — Spotify feed + search + iTunes previews
│   │   ├── .env              # Spotify credentials (not committed)
│   │   └── .env.example      # Template for Spotify credentials
│   ├── package.json
│   └── vite.config.ts        # Proxies /api → localhost:3001
├── src/main/java/            # Original Java/Swing app (Clean Architecture)
├── PRODUCT_VISION.md         # Product direction and UX principles
└── README.md                 # This file
```

---

## Quick Start

### Prerequisites

- **Node.js** 18+
- **Spotify Developer App** — get Client ID and Secret from [developer.spotify.com/dashboard](https://developer.spotify.com/dashboard)

### 1. Install dependencies

```bash
cd web-app
npm install
cd server
npm install
cd ..
```

### 2. Configure Spotify credentials

Create `web-app/server/.env`:

```env
SPOTIFY_CLIENT_ID=your_client_id_here
SPOTIFY_CLIENT_SECRET=your_client_secret_here
```

### 3. Run

```bash
# Start both API and frontend:
npm run dev:all

# Or separately:
npm run server    # API on http://localhost:3001
npm run dev       # Frontend on http://localhost:5173
```

Open **http://localhost:5173** — the feed loads with tracks from Spotify. Search for any song. Tap a card to pause/resume. Hit "Play Full Song" for the full track via Spotify.

---

## How It Works

1. **Feed** — The API searches Spotify for a mix of popular/trending tracks, returns metadata (title, artist, album art, Spotify URL). For each track missing a Spotify preview, it looks up a 30-second preview from the iTunes Search API.

2. **Search** — Type any query in the header. The API searches Spotify's catalog and returns up to 20 results with iTunes preview fallback.

3. **Playback** — A single global `Audio` instance ensures only one song plays at a time. Songs auto-play when you scroll to them, loop continuously, and pause on tap. The Spotify embed iframe gives full-song playback for logged-in Spotify users.

4. **Auth (optional)** — If Firebase is configured (`web-app/.env` with `VITE_FIREBASE_*` vars), users can sign in. Auth is non-blocking — the feed and search work without it. Likes are persisted to Firestore when auth is active.

---

## Optional: Firebase Setup

For user accounts and persistent likes, add Firebase config to `web-app/.env`:

```env
VITE_FIREBASE_API_KEY=...
VITE_FIREBASE_AUTH_DOMAIN=...
VITE_FIREBASE_PROJECT_ID=...
VITE_FIREBASE_STORAGE_BUCKET=...
VITE_FIREBASE_MESSAGING_SENDER_ID=...
VITE_FIREBASE_APP_ID=...
```

Without these, the app works fully — auth and likes are simply disabled.

---

## Original Java App

The `src/main/java/` directory contains the original **Music Artist and Event Management System** — a Java/Swing desktop app with Clean Architecture (entities, use cases, interface adapters, views). It supports:

- User signup/login (Firebase)
- Artist search and detail (MusicBrainz API)
- Comments and ratings (Firestore)
- Event search

To run: open `src/main/java/app/App.java` in your IDE with JDK 17+ and Maven.

---

## Team

- **Abhigyan** — GUI Development, API usage, documentation
- **Richard** — Database, API Search, Clean Architecture design
- **Nick** — Use Case implementation, architecture design
- **Chris** — Unit Testing, documentation

---

## License

MIT License. See [LICENSE](LICENSE) or the badge below.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
