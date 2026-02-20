import { useState } from 'react'
import type { Moment } from '../types/moment'
import { useLikes } from '../hooks/useLikes'

interface MomentCardProps {
  moment: Moment
  isActive: boolean
  onNext: () => void
  onPrev: () => void
  play: (url: string) => void
  pause: () => void
  toggle: (url: string) => void
  isPlaying: (url: string) => boolean
}

function spotifyEmbedUrl(spotifyUrl: string) {
  const match = spotifyUrl.match(/track\/([a-zA-Z0-9]+)/)
  const id = match ? match[1] : ''
  return `https://open.spotify.com/embed/track/${id}?utm_source=generator&theme=0`
}

export function MomentCard({ moment, isActive, onNext, onPrev, play, pause, toggle, isPlaying }: MomentCardProps) {
  const hasAudio = moment.audioUrl != null && moment.audioUrl !== ''
  const playing = hasAudio && isPlaying(moment.audioUrl!)
  const { count: likeCount, liked, toggle: toggleLike } = useLikes(moment.id)
  const [showEmbed, setShowEmbed] = useState(false)

  const handleTap = () => {
    if (!hasAudio) return
    toggle(moment.audioUrl!)
  }

  const hasSpotify = Boolean(moment.spotifyUrl)

  return (
    <div
      className="moment-slide relative flex flex-col items-center justify-end bg-feed-bg text-white pb-20 px-6"
      style={{ minHeight: '100vh' }}
    >
      {/* Background art (full bleed, dimmed) */}
      <div className="absolute inset-0 z-0">
        <div
          className="absolute inset-0 bg-cover bg-center scale-105"
          style={{ backgroundImage: `url(${moment.artUrl})` }}
        />
        <div className="absolute inset-0 bg-gradient-to-t from-feed-bg via-feed-bg/80 to-transparent" />
      </div>

      {/* Tap anywhere to pause/resume (below buttons/actions) */}
      <div className="absolute inset-0 z-[5] cursor-pointer" onClick={handleTap} role="button" aria-label={playing ? 'Pause' : 'Play'} tabIndex={-1} />

      {/* Center: cover + play */}
      <div className="relative z-10 flex flex-col items-center flex-1 justify-center w-full max-w-sm pointer-events-none">
        <div
          className="relative rounded-2xl overflow-hidden shadow-2xl ring-2 ring-white/20 transition transform"
          style={{ width: 280, height: 280 }}
        >
          <img
            src={moment.artUrl}
            alt=""
            className="w-full h-full object-cover"
            draggable={false}
          />
          {hasAudio && (
            <div className="absolute inset-0 flex items-center justify-center bg-black/30">
              <span
                className={`w-16 h-16 rounded-full bg-white/90 flex items-center justify-center text-2xl ${
                  playing ? 'animate-pulse-soft' : ''
                }`}
              >
                {playing ? '⏸' : '▶'}
              </span>
            </div>
          )}
        </div>
        <p className="mt-4 text-xl font-semibold text-center line-clamp-1">{moment.title}</p>
        <p className="text-feed-mute text-sm">{moment.artist}</p>

        {hasAudio && (
          <p className="text-feed-mute/50 text-xs mt-1">30s preview — tap art to play</p>
        )}

        {/* Spotify full-song embed toggle */}
        {hasSpotify && (
          <div className="mt-3 w-full pointer-events-auto">
            <button
              type="button"
              onClick={() => setShowEmbed(!showEmbed)}
              className="flex items-center justify-center gap-2 w-full py-2 rounded-xl bg-[#1DB954]/90 hover:bg-[#1DB954] text-white text-sm font-semibold transition"
            >
              <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor"><path d="M12 0C5.4 0 0 5.4 0 12s5.4 12 12 12 12-5.4 12-12S18.66 0 12 0zm5.521 17.34c-.24.359-.66.48-1.021.24-2.82-1.74-6.36-2.101-10.561-1.141-.418.122-.779-.179-.899-.539-.12-.421.18-.78.54-.9 4.56-1.021 8.52-.6 11.64 1.32.42.18.479.659.301 1.02zm1.44-3.3c-.301.42-.841.6-1.262.3-3.239-1.98-8.159-2.58-11.939-1.38-.479.12-1.02-.12-1.14-.6-.12-.48.12-1.021.6-1.141C9.6 9.9 15 10.561 18.72 12.84c.361.181.54.78.241 1.2zm.12-3.36C15.24 8.4 8.82 8.16 5.16 9.301c-.6.179-1.2-.181-1.38-.721-.18-.601.18-1.2.72-1.381 4.26-1.26 11.28-1.02 15.721 1.621.539.3.719 1.02.419 1.56-.299.421-1.02.599-1.559.3z"/></svg>
              {showEmbed ? 'Hide Spotify Player' : 'Play Full Song'}
            </button>
            {showEmbed && isActive && (
              <div className="mt-2 rounded-xl overflow-hidden">
                <iframe
                  src={spotifyEmbedUrl(moment.spotifyUrl!)}
                  width="100%"
                  height="152"
                  frameBorder="0"
                  allow="autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture"
                  loading="lazy"
                  className="rounded-xl"
                  title={`${moment.title} on Spotify`}
                />
              </div>
            )}
          </div>
        )}
      </div>

      {/* Bottom actions */}
      <div className="relative z-10 flex gap-6 text-feed-mute text-sm mt-4 pointer-events-auto">
        <button
          type="button"
          onClick={toggleLike}
          className="flex flex-col items-center gap-1 hover:text-white transition"
        >
          <span className={`text-2xl ${liked ? 'text-red-400' : ''}`}>{liked ? '❤️' : '🤍'}</span>
          <span>{likeCount > 0 ? likeCount : 'Like'}</span>
        </button>
        <button type="button" className="flex flex-col items-center gap-1 hover:text-white transition">
          <span className="text-2xl">💬</span>
          <span>Comment</span>
        </button>
        <button
          type="button"
          className="flex flex-col items-center gap-1 hover:text-white transition"
          onClick={() => window.open(`https://twitter.com/intent/tweet?text=${encodeURIComponent(`${moment.title} – ${moment.artist}`)}`, '_blank')}
        >
          <span className="text-2xl">↗</span>
          <span>Share</span>
        </button>
        {hasSpotify && (
          <a
            href={moment.spotifyUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="flex flex-col items-center gap-1 hover:text-white transition text-[#1DB954]"
          >
            <span className="text-2xl">🎧</span>
            <span>Spotify</span>
          </a>
        )}
      </div>

      {/* Invisible nav areas */}
      <button
        type="button"
        className="absolute top-1/2 left-0 w-1/4 h-1/2 -translate-y-1/2 z-20 opacity-0 hover:opacity-100 focus:opacity-100"
        aria-label="Previous"
        onClick={onPrev}
      />
      <button
        type="button"
        className="absolute top-1/2 right-0 w-1/4 h-1/2 -translate-y-1/2 z-20 opacity-0 hover:opacity-100 focus:opacity-100"
        aria-label="Next"
        onClick={onNext}
      />
    </div>
  )
}
