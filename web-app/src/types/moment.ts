export interface Moment {
  id: string
  title: string
  artist: string
  artistId?: string
  /** URL to cover/art image */
  artUrl: string
  /** Optional: audio preview URL (e.g. MusicBrainz or future stream) */
  audioUrl?: string
  /** Duration in seconds (e.g. 30 for a 30s hook) */
  durationSec?: number
  /** Optional video URL for short clip */
  videoUrl?: string
  /** Spotify track URL for full playback embed */
  spotifyUrl?: string
}
