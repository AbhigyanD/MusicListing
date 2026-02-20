import { useState } from 'react'
import { useAuth } from '../contexts/AuthContext'

export function SignupScreen() {
  const { signUp, hasAuth } = useAuth()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [confirm, setConfirm] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    if (password !== confirm) {
      setError('Passwords do not match')
      return
    }
    if (password.length < 6) {
      setError('Password must be at least 6 characters')
      return
    }
    setLoading(true)
    try {
      await signUp(email, password)
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Sign up failed')
    } finally {
      setLoading(false)
    }
  }

  if (!hasAuth) return null

  return (
    <div className="min-h-screen bg-feed-bg flex flex-col items-center justify-center p-6">
      <div className="w-full max-w-sm">
        <h1 className="text-xl font-semibold text-white text-center mb-6">Create account</h1>
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
            placeholder="Password (min 6)"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full px-4 py-3 rounded-xl bg-feed-card border border-white/10 text-white placeholder-feed-mute focus:outline-none focus:ring-2 focus:ring-feed-accent"
            required
          />
          <input
            type="password"
            placeholder="Confirm password"
            value={confirm}
            onChange={(e) => setConfirm(e.target.value)}
            className="w-full px-4 py-3 rounded-xl bg-feed-card border border-white/10 text-white placeholder-feed-mute focus:outline-none focus:ring-2 focus:ring-feed-accent"
            required
          />
          {error && <p className="text-red-400 text-sm">{error}</p>}
          <button
            type="submit"
            disabled={loading}
            className="w-full py-3 rounded-xl bg-feed-accent text-white font-medium hover:opacity-90 disabled:opacity-50"
          >
            {loading ? 'Creating…' : 'Sign up'}
          </button>
        </form>
      </div>
    </div>
  )
}
