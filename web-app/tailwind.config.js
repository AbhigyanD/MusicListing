/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        feed: {
          bg: '#0a0a0a',
          card: '#141414',
          accent: '#22c55e',
          mute: '#71717a',
        },
      },
      animation: {
        'pulse-soft': 'pulse-soft 1.5s ease-in-out infinite',
      },
      keyframes: {
        'pulse-soft': {
          '0%, 100%': { opacity: '1' },
          '50%': { opacity: '0.7' },
        },
      },
    },
  },
  plugins: [],
}
