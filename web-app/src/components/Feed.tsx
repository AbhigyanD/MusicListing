import { useRef, useState, useCallback, useEffect } from 'react'
import { useAudio } from '../hooks/useAudio'
import { MomentCard } from './MomentCard'
import type { Moment } from '../types/moment'

const API_FEED = '/api/feed'
const API_SEARCH = '/api/search'

interface FeedProps {
  /** When set, fetches search results instead of the default feed */
  searchQuery?: string
}

export function Feed({ searchQuery = '' }: FeedProps) {
  const containerRef = useRef<HTMLDivElement>(null)
  const [index, setIndex] = useState(0)
  const [moments, setMoments] = useState<Moment[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const { play, pause, toggle, isPlaying } = useAudio()

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError(null)
    const url = searchQuery.trim()
      ? `${API_SEARCH}?q=${encodeURIComponent(searchQuery.trim())}`
      : API_FEED
    fetch(url)
      .then((res) => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        return res.json()
      })
      .then((data) => {
        if (!cancelled) {
          const list = Array.isArray(data) ? data : (data?.moments ?? [])
          setMoments(Array.isArray(list) ? list : [])
          setIndex(0)
          if (searchQuery.trim() && list.length === 0 && data?.error === 'spotify_unconfigured') {
            setError('Spotify not configured. Add SPOTIFY_CLIENT_ID and SPOTIFY_CLIENT_SECRET to web-app/server/.env and restart the server.')
          }
        }
      })
      .catch((err) => {
        if (!cancelled) setError(err.message || 'Failed to load')
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => { cancelled = true }
  }, [searchQuery])

  const goNext = useCallback(() => {
    if (moments.length === 0) return
    setIndex((i) => {
      const next = (i + 1) % moments.length
      const el = containerRef.current
      if (el) el.scrollTo({ top: next * el.clientHeight, behavior: 'smooth' })
      return next
    })
  }, [moments.length])

  const goPrev = useCallback(() => {
    if (moments.length === 0) return
    setIndex((i) => {
      const prev = (i - 1 + moments.length) % moments.length
      const el = containerRef.current
      if (el) el.scrollTo({ top: prev * el.clientHeight, behavior: 'smooth' })
      return prev
    })
  }, [moments.length])

  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      const tag = (e.target as HTMLElement)?.tagName
      if (tag === 'INPUT' || tag === 'TEXTAREA' || tag === 'SELECT') return
      if (e.key === 'ArrowDown' || e.key === ' ') {
        e.preventDefault()
        goNext()
      } else if (e.key === 'ArrowUp') {
        e.preventDefault()
        goPrev()
      }
    }
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [goNext, goPrev])

  useEffect(() => {
    const el = containerRef.current
    if (!el || moments.length === 0) return
    const onScroll = () => {
      const h = el.clientHeight
      const i = Math.round(el.scrollTop / h)
      setIndex(Math.min(Math.max(0, i), moments.length - 1))
    }
    el.addEventListener('scroll', onScroll, { passive: true })
    return () => el.removeEventListener('scroll', onScroll)
  }, [moments.length])

  // Auto-play the active card's audio when scrolling
  useEffect(() => {
    const m = moments[index]
    if (m?.audioUrl) {
      play(m.audioUrl)
    }
  }, [index, moments])

  if (loading) {
    return (
      <div className="h-full flex flex-col items-center justify-center gap-4 text-white">
        <div className="w-10 h-10 border-2 border-feed-accent border-t-transparent rounded-full animate-spin" />
        <p className="text-feed-mute text-sm">Loading moments…</p>
      </div>
    )
  }

  if (error) {
    return (
      <div className="h-full flex flex-col items-center justify-center gap-4 text-white px-6">
        <p className="text-red-400 text-center">{error}</p>
        <p className="text-feed-mute text-sm text-center">Start the API with: npm run server (in web-app/server) or npm run dev:all</p>
        <button
          type="button"
          onClick={() => window.location.reload()}
          className="px-4 py-2 rounded-lg bg-feed-accent text-white text-sm font-medium"
        >
          Retry
        </button>
      </div>
    )
  }

  if (moments.length === 0) {
    return (
      <div className="h-full flex flex-col items-center justify-center text-white px-6 gap-3">
        <p className="text-feed-mute text-center">
          {searchQuery.trim() ? `No results for “${searchQuery.trim()}”. Try another search.` : 'No tracks loaded. Search for any song above to get started.'}
        </p>
        <p className="text-white/60 text-sm text-center">Use the search bar in the header → type a song or artist and press Search.</p>
      </div>
    )
  }

  return (
    <div ref={containerRef} className="feed-container">
      {moments.map((moment, i) => (
        <MomentCard
          key={moment.id}
          moment={moment}
          isActive={i === index}
          onNext={goNext}
          onPrev={goPrev}
          play={play}
          pause={pause}
          toggle={toggle}
          isPlaying={isPlaying}
        />
      ))}
    </div>
  )
}
