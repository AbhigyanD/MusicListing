import { useCallback, useRef, useState } from 'react'

let globalAudio: HTMLAudioElement | null = null
let globalUrl: string | null = null

export function useAudio() {
  const [playing, setPlaying] = useState(false)
  const [currentUrl, setCurrentUrl] = useState<string | null>(null)
  const playingRef = useRef(false)

  const play = useCallback((url: string) => {
    if (globalUrl === url && globalAudio && !globalAudio.paused) return
    if (globalUrl === url && globalAudio) {
      globalAudio.play().then(() => {
        playingRef.current = true
        setPlaying(true)
      }).catch(() => {})
      return
    }

    if (globalAudio) {
      globalAudio.pause()
      globalAudio.src = ''
      globalAudio = null
    }

    const audio = new Audio(url)
    audio.loop = true
    globalAudio = audio
    globalUrl = url
    setCurrentUrl(url)

    audio.onpause = () => { playingRef.current = false; setPlaying(false) }
    audio.onplay = () => { playingRef.current = true; setPlaying(true) }

    audio.play().then(() => {
      playingRef.current = true
      setPlaying(true)
    }).catch(() => {})
  }, [])

  const pause = useCallback(() => {
    if (globalAudio) {
      globalAudio.pause()
      playingRef.current = false
      setPlaying(false)
    }
  }, [])

  const toggle = useCallback((url: string) => {
    if (globalUrl === url && globalAudio && !globalAudio.paused) {
      pause()
    } else {
      play(url)
    }
  }, [pause, play])

  const stop = useCallback(() => {
    if (globalAudio) {
      globalAudio.pause()
      globalAudio.src = ''
      globalAudio = null
    }
    globalUrl = null
    setCurrentUrl(null)
    playingRef.current = false
    setPlaying(false)
  }, [])

  const isPlaying = useCallback((url: string) => globalUrl === url && playingRef.current, [])

  return { play, pause, toggle, stop, playing, currentUrl, isPlaying }
}
