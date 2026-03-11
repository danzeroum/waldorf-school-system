/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts,scss}"
  ],
  theme: {
    extend: {
      // === WALDORF DESIGN SYSTEM ===
      colors: {
        // Primária — Verde Floresta
        'waldorf-green': {
          50:  '#f0f7f0',
          100: '#d9eedd',
          200: '#b3ddb8',
          300: '#80c489',
          400: '#4ea85a',
          500: '#2d8c3a',  // principal
          600: '#237030',
          700: '#1b5626',
          800: '#153f1d',
          900: '#0e2b14',
        },
        // Secundária — Âmbar Quente
        'waldorf-amber': {
          50:  '#fff8e7',
          100: '#ffefc2',
          200: '#ffd97a',
          300: '#ffbf2a',
          400: '#f0a500',  // principal
          500: '#c98800',
          600: '#a06b00',
          700: '#7a5000',
          800: '#573800',
          900: '#3a2500',
        },
        // Terciária — Terracota
        'waldorf-terra': {
          50:  '#fdf3ee',
          100: '#fae1d1',
          200: '#f4bfa0',
          300: '#ec9566',
          400: '#e06b35',
          500: '#c9511e',  // principal
          600: '#a33f17',
          700: '#7d2f11',
          800: '#59210c',
          900: '#3b1508',
        },
        // Neutros Quentes
        'waldorf-cream': {
          50:  '#fdfaf5',
          100: '#f9f3e6',
          200: '#f0e4c8',
          300: '#e5d0a4',
          400: '#d4b87a',
          500: '#c09952',
        },
        // Cinzas com temperatura quente
        'waldorf-gray': {
          50:  '#f8f6f3',
          100: '#eeebe5',
          200: '#ddd8cf',
          300: '#c4bdb1',
          400: '#a89e91',
          500: '#8c8278',
          600: '#6e6560',
          700: '#534e4a',
          800: '#3a3835',
          900: '#252321',
        },
      },
      fontFamily: {
        'serif': ['Playfair Display', 'Georgia', 'serif'],
        'sans': ['Inter', 'system-ui', 'sans-serif'],
        'mono': ['JetBrains Mono', 'Consolas', 'monospace'],
      },
      fontSize: {
        'xs':   ['0.75rem',  { lineHeight: '1.5' }],
        'sm':   ['0.875rem', { lineHeight: '1.6' }],
        'base': ['1rem',     { lineHeight: '1.7' }],
        'lg':   ['1.125rem', { lineHeight: '1.6' }],
        'xl':   ['1.25rem',  { lineHeight: '1.5' }],
        '2xl':  ['1.5rem',   { lineHeight: '1.4' }],
        '3xl':  ['1.875rem', { lineHeight: '1.3' }],
        '4xl':  ['2.25rem',  { lineHeight: '1.2' }],
      },
      borderRadius: {
        'sm':   '0.375rem',
        DEFAULT: '0.5rem',
        'md':   '0.75rem',
        'lg':   '1rem',
        'xl':   '1.5rem',
        '2xl':  '2rem',
      },
      boxShadow: {
        'waldorf-sm': '0 1px 3px rgba(45,140,58,0.08), 0 1px 2px rgba(0,0,0,0.06)',
        'waldorf':    '0 4px 6px rgba(45,140,58,0.07), 0 2px 4px rgba(0,0,0,0.06)',
        'waldorf-md': '0 10px 15px rgba(45,140,58,0.07), 0 4px 6px rgba(0,0,0,0.05)',
        'waldorf-lg': '0 20px 25px rgba(45,140,58,0.06), 0 10px 10px rgba(0,0,0,0.04)',
      },
      spacing: {
        '18': '4.5rem',
        '22': '5.5rem',
        '72': '18rem',
        '80': '20rem',
        '88': '22rem',
        '96': '24rem',
      },
    },
  },
  plugins: [],
};
