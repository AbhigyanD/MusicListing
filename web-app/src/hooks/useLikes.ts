import { useState, useEffect, useCallback } from 'react'
import { hasFirebase, db, auth } from '../lib/firebase'
import {
  collection,
  query,
  where,
  setDoc,
  deleteDoc,
  doc,
  getDoc,
  getCountFromServer,
} from 'firebase/firestore'

const LIKES_COLLECTION = 'moment_likes'

export function useLikes(momentId: string) {
  const [count, setCount] = useState(0)
  const [liked, setLiked] = useState(false)
  const [loading, setLoading] = useState(hasFirebase)

  const userId = auth?.currentUser?.uid ?? null

  const refresh = useCallback(async () => {
    if (!hasFirebase || !db) {
      setLoading(false)
      return
    }
    try {
      const q = query(
        collection(db, LIKES_COLLECTION),
        where('momentId', '==', momentId)
      )
      const snapshot = await getCountFromServer(q)
      setCount(snapshot.data().count)

      if (userId) {
        const likeRef = doc(db, LIKES_COLLECTION, `${momentId}_${userId}`)
        const likeSnap = await getDoc(likeRef)
        setLiked(likeSnap.exists())
      } else {
        setLiked(false)
      }
    } catch (_) {
      setCount(0)
      setLiked(false)
    } finally {
      setLoading(false)
    }
  }, [momentId, userId])

  useEffect(() => {
    refresh()
  }, [refresh])

  const toggle = useCallback(async () => {
    if (!hasFirebase || !db || !userId) return
    const docId = `${momentId}_${userId}`
    const ref = doc(db, LIKES_COLLECTION, docId)
    try {
      if (liked) {
        await deleteDoc(ref)
        setLiked(false)
        setCount((c) => Math.max(0, c - 1))
      } else {
        await setDoc(ref, {
          momentId,
          userId,
          createdAt: new Date().toISOString(),
        })
        setLiked(true)
        setCount((c) => c + 1)
      }
    } catch (_) {
      refresh()
    }
  }, [momentId, userId, liked, refresh])

  return { count, liked, toggle, loading }
}
