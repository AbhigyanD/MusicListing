import { useState } from 'react'
import { AuthProvider, useAuth } from './contexts/AuthContext'
import { Feed } from './components/Feed'
import { LoginScreen } from './components/LoginScreen'
import { SignupScreen } from './components/SignupScreen'

function AppContent() {
  const { user, loading, signOut, hasAuth } = useAuth()
  const [showSignup, setShowSignup] = useState(false)
  const [showAuthModal, setShowAuthModal] = useState(false)
  const [searchQuery, setSearchQuery] = useState('')
  const [searchInput, setSearchInput] = useState('')

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    setSearchQuery(searchInput.trim())
  }

  if (loading) {
    return (
      <div className="h-screen bg-feed-bg flex items-center justify-center">
        <div className="w-10 h-10 border-2 border-feed-accent border-t-transparent rounded-full animate-spin" />
      </div>
    )
  }

  return (
    <div className="h-full w-full bg-feed-bg">
      <header className="fixed top-0 left-0 right-0 z-30 flex items-center gap-3 h-12 px-4 bg-feed-bg/80 backdrop-blur-sm border-b border-white/5">
        <span className="text-white font-semibold text-sm tracking-wide shrink-0">MOMENTS</span>
        <form onSubmit={handleSearchSubmit} className="flex-1 flex max-w-md">
          <input
            type="search"
            placeholder="Search songs…"
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
            className="w-full px-3 py-1.5 rounded-lg bg-white/10 border border-white/10 text-white placeholder-feed-mute text-sm focus:outline-none focus:ring-1 focus:ring-feed-accent focus:border-transparent"
            aria-label="Search songs"
          />
          <button
            type="submit"
            className="ml-2 px-3 py-1.5 rounded-lg bg-feed-accent text-white text-sm font-medium shrink-0"
          >
            Search
          </button>
          {searchQuery && (
            <button
              type="button"
              onClick={() => { setSearchInput(''); setSearchQuery(''); }}
              className="ml-2 px-2 py-1.5 rounded-lg text-feed-mute hover:text-white text-sm shrink-0"
              title="Clear search"
            >
              Clear
            </button>
          )}
        </form>
        {user ? (
          <div className="flex items-center gap-3 shrink-0">
            <span className="text-feed-mute text-xs truncate max-w-[120px]">{user.email}</span>
            <button
              type="button"
              onClick={() => signOut()}
              className="text-feed-mute hover:text-white text-sm"
            >
              Sign out
            </button>
          </div>
        ) : hasAuth ? (
          <div className="flex items-center gap-2 shrink-0">
            <button
              type="button"
              onClick={() => { setShowSignup(false); setShowAuthModal(true) }}
              className="px-3 py-1.5 rounded-lg text-feed-mute hover:text-white text-sm font-medium"
            >
              Sign in
            </button>
            <button
              type="button"
              onClick={() => { setShowSignup(true); setShowAuthModal(true) }}
              className="px-3 py-1.5 rounded-lg bg-white/10 hover:bg-white/20 text-white text-sm font-medium"
            >
              Create account
            </button>
          </div>
        ) : null}
      </header>
      <main className="pt-12 h-full">
        <Feed searchQuery={searchQuery} />
      </main>

      {/* Login / Signup overlay only when user clicks Sign in or Create account */}
      {hasAuth && !user && showAuthModal && (showSignup ? (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm p-4">
          <div className="relative w-full max-w-sm">
            <button type="button" onClick={() => setShowAuthModal(false)} className="absolute -top-10 right-0 text-feed-mute hover:text-white text-sm" aria-label="Close">✕</button>
            <SignupScreen />
            <button
              type="button"
              onClick={() => setShowSignup(false)}
              className="absolute -top-10 left-0 text-feed-mute text-sm hover:text-white"
            >
              ← Back to sign in
            </button>
          </div>
        </div>
      ) : (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm p-4">
          <div className="relative w-full max-w-sm">
            <button type="button" onClick={() => setShowAuthModal(false)} className="absolute -top-10 right-0 text-feed-mute hover:text-white text-sm" aria-label="Close">✕</button>
            <LoginScreen />
            <button
              type="button"
              onClick={() => setShowSignup(true)}
              className="absolute -bottom-12 left-1/2 -translate-x-1/2 text-feed-accent text-sm hover:underline"
            >
              Create account
            </button>
          </div>
        </div>
      ))}
    </div>
  )
}

export default function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  )
}
