import path from 'path'
import { fileURLToPath } from 'url'
import dotenv from 'dotenv'
import express from 'express'
import cors from 'cors'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
dotenv.config({ path: path.join(__dirname, '.env') })

const app = express()
const PORT = process.env.PORT || 3001

app.use(cors())
app.use(express.json())

// ─── Spotify for metadata + art, iTunes fallback for 30s audio previews. ───

let spotifyToken = null
let spotifyTokenExpiry = 0

async function getSpotifyToken() {
  const id = process.env.SPOTIFY_CLIENT_ID
  const secret = process.env.SPOTIFY_CLIENT_SECRET
  if (!id || !secret) {
    console.warn('Spotify: missing SPOTIFY_CLIENT_ID or SPOTIFY_CLIENT_SECRET in server/.env')
    return null
  }
  if (spotifyToken && Date.now() < spotifyTokenExpiry) return spotifyToken
  try {
    const res = await fetch('https://accounts.spotify.com/api/token', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        Authorization: 'Basic ' + Buffer.from(id + ':' + secret).toString('base64'),
      },
      body: 'grant_type=client_credentials',
    })
    const data = await res.json().catch(() => ({}))
    if (!res.ok) {
      console.warn('Spotify token failed:', res.status, data.error || data)
      return null
    }
    spotifyToken = data.access_token
    spotifyTokenExpiry = Date.now() + (data.expires_in - 60) * 1000
    return spotifyToken
  } catch (e) {
    console.warn('Spotify token error:', e.message)
    return null
  }
}

/** Map Spotify track object to our Moment shape. */
function trackToMoment(t) {
  return {
    id: t.id,
    title: t.name,
    artist: t.artists?.[0]?.name || 'Unknown',
    artistId: t.artists?.[0]?.id,
    artUrl: t.album?.images?.[0]?.url || 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Music_icon.svg/240px-Music_icon.svg.png',
    audioUrl: t.preview_url || undefined,
    durationSec: 30,
    spotifyUrl: t.external_urls?.spotify || `https://open.spotify.com/track/${t.id}`,
  }
}

/** Look up a 30s preview URL from iTunes (free, no auth). */
async function getItunesPreview(title, artist) {
  try {
    const q = encodeURIComponent(`${title} ${artist}`.trim())
    const res = await fetch(`https://itunes.apple.com/search?term=${q}&media=music&limit=1`)
    if (!res.ok) return null
    const data = await res.json()
    return data.results?.[0]?.previewUrl || null
  } catch {
    return null
  }
}

/** Fill in missing audioUrl from iTunes for a list of moments. */
async function fillPreviews(moments) {
  const missing = moments.filter(m => !m.audioUrl)
  if (missing.length === 0) return moments
  await Promise.all(
    missing.map(async (m) => {
      const url = await getItunesPreview(m.title, m.artist)
      if (url) m.audioUrl = url
    })
  )
  return moments
}

const SPOTIFY_PAGE_LIMIT = 10

/** Search Spotify for tracks; returns moments (songs) directly from Spotify. */
async function spotifySearchTracks(q, total = 20) {
  const token = await getSpotifyToken()
  if (!token) {
    console.warn('Search skipped: Spotify not configured. Add SPOTIFY_CLIENT_ID and SPOTIFY_CLIENT_SECRET to server/.env')
    return { moments: [], error: 'spotify_unconfigured' }
  }
  const query = encodeURIComponent(String(q).trim())
  if (!query) return { moments: [] }
  try {
    const all = []
    for (let offset = 0; all.length < total; offset += SPOTIFY_PAGE_LIMIT) {
      const res = await fetch(
        `https://api.spotify.com/v1/search?q=${query}&type=track&limit=${SPOTIFY_PAGE_LIMIT}&offset=${offset}`,
        { headers: { Authorization: 'Bearer ' + token } }
      )
      const data = await res.json().catch(() => ({}))
      if (!res.ok) {
        console.warn('Spotify search failed:', res.status, data.error?.message || data)
        break
      }
      const tracks = data.tracks?.items || []
      if (tracks.length === 0) break
      all.push(...tracks)
    }
    if (all.length === 0) return { moments: [], error: 'spotify_error' }
    const moments = all.slice(0, total).map(trackToMoment)
    await fillPreviews(moments)
    return { moments }
  } catch (e) {
    console.warn('Spotify search error:', e.message)
    return { moments: [], error: 'spotify_error' }
  }
}

/** Default feed: get tracks directly from Spotify (search for popular/recent). */
async function spotifyFeed(limit = 20) {
  const token = await getSpotifyToken()
  if (!token) return []
  try {
    // Use a few searches and merge so we get a varied feed (Spotify has no single "trending" for tracks without a playlist)
    const queries = ['popular', 'hits 2024', 'top']
    const seen = new Set()
    const moments = []
    for (const q of queries) {
      const res = await fetch(
        `https://api.spotify.com/v1/search?q=${encodeURIComponent(q)}&type=track&limit=10`,
        { headers: { Authorization: 'Bearer ' + token } }
      )
      if (!res.ok) continue
      const data = await res.json()
      const tracks = data.tracks?.items || []
      for (const t of tracks) {
        if (seen.has(t.id)) continue
        seen.add(t.id)
        moments.push(trackToMoment(t))
        if (moments.length >= limit) break
      }
      if (moments.length >= limit) break
    }
    return moments
  } catch (_) {
    return []
  }
}

app.get('/api/feed', async (req, res) => {
  try {
    const moments = await spotifyFeed(20)
    if (moments.length === 0) {
      console.warn('Feed empty. Check server/.env has SPOTIFY_CLIENT_ID and SPOTIFY_CLIENT_SECRET (from https://developer.spotify.com/dashboard).')
    }
    await fillPreviews(moments)
    res.json(moments)
  } catch (err) {
    console.warn('Feed failed:', err.message)
    res.json([])
  }
})

app.get('/api/search', async (req, res) => {
  const q = req.query.q
  try {
    const result = await spotifySearchTracks(q || '', 20)
    res.json(result)
  } catch (err) {
    console.warn('Search failed:', err.message)
    res.json({ moments: [], error: 'spotify_error' })
  }
})

app.get('/api/health', (req, res) => res.json({ ok: true }))

function startServer(port) {
  const server = app.listen(port, () => {
    const hasSpotify = Boolean(process.env.SPOTIFY_CLIENT_ID && process.env.SPOTIFY_CLIENT_SECRET)
    console.log(`API http://localhost:${port}`)
    if (hasSpotify) {
      console.log('Spotify: configured. Audio previews: iTunes fallback for tracks without Spotify previews.')
    } else {
      console.log('Spotify: NOT configured. Add SPOTIFY_CLIENT_ID + SPOTIFY_CLIENT_SECRET in server/.env — required for feed and search.')
    }
  })
  server.on('error', (err) => {
    if (err.code === 'EADDRINUSE') {
      console.error(`\nPort ${port} is already in use. To free it, run:`)
      console.error(`  lsof -i :${port}   # find PID`)
      console.error(`  kill <PID>\n`)
      process.exit(1)
    }
    throw err
  })
}

startServer(PORT)
