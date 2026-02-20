import { useState } from 'react'
import { useAuth } from '../contexts/AuthContext'

export function LoginScreen() {
  const { signIn, hasAuth } = useAuth()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await signIn(email, password)
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Sign in failed')
    } finally {
      setLoading(false)
    }
  }

  if (!hasAuth) {
    return (
      <div className="min-h-screen bg-feed-bg flex flex-col items-center justify-center p-6 text-white">
        <p className="text-feed-mute text-sm text-center max-w-sm">
          Firebase is not configured. Add VITE_FIREBASE_* env vars to enable sign in. You can still use the feed without an account.
        </p>
        <a href="/" className="mt-4 text-feed-accent text-sm">Continue to feed →</a>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-feed-bg flex flex-col items-center justify-center p-6">
      <div className="w-full max-w-sm">
        <h1 className="text-xl font-semibold text-white text-center mb-6">Sign in to Moments</h1>
        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full px-4 py-3 rounded-xl bg-feed-card border border-white/10 text-white placeholder-feed-mute focus:outline-none focus:ring-2 focus:ring-feed-accent"
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full px-4 py-3 rounded-xl bg-feed-card border border-white/10 text-white placeholder-feed-mute focus:outline-none focus:ring-2 focus:ring-feed-accent"
            required
          />
          {error && <p className="text-red-400 text-sm">{error}</p>}
          <button
            type="submit"
            disabled={loading}
            className="w-full py-3 rounded-xl bg-feed-accent text-white font-medium hover:opacity-90 disabled:opacity-50"
          >
            {loading ? 'Signing in…' : 'Sign in'}
          </button>
        </form>
        <p className="mt-4 text-center text-feed-mute text-sm">
          No account? Sign up below.
        </p>
      </div>
    </div>
  )
}
