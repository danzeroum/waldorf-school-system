/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts,scss}"
  ],
  theme: {
    extend: {
      // === QUINTAL AROEIRA — DESIGN SYSTEM ===
      colors: {
        // Primária — Verde Musgo (suave, orgânico)
        'waldorf-green': {
          50:  '#f2f5f2',
          100: '#dce5dc',
          200: '#b8ccb9',
          300: '#8fae91',
          400: '#6e9471',
          500: '#5B7F5E',  // principal — verde musgo Aroeira
          600: '#49664B',
          700: '#384E3A',
          800: '#2A3A2B',
          900: '#1C271D',
        },
        // Secundária — Dourado Quente
        'waldorf-amber': {
          50:  '#faf6ed',
          100: '#f2e8ce',
          200: '#e5d1a0',
          300: '#D4B876',
          400: '#C4A265',  // principal — dourado quente
          500: '#A88845',
          600: '#8A6D34',
          700: '#6B5428',
          800: '#4D3C1C',
          900: '#332812',
        },
        // Terciária — Terracota Suave
        'waldorf-terra': {
          50:  '#fdf4ef',
          100: '#fae3d5',
          200: '#f4c5aa',
          300: '#E8A47D',
          400: '#D4956A',  // principal — terracota suave
          500: '#B87A50',
          600: '#96603C',
          700: '#74492D',
          800: '#533320',
          900: '#382215',
        },
        // Neutros Quentes — Areia
        'waldorf-cream': {
          50:  '#F5F0E8',  // fundo principal — areia quente
          100: '#EDE6D8',
          200: '#E0D5C2',
          300: '#CFBFA5',
          400: '#BBAA88',
          500: '#A8946D',
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
        'serif': ['Lora', 'Georgia', 'serif'],
        'sans': ['Nunito', 'system-ui', 'sans-serif'],
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
        'waldorf-sm': '0 1px 3px rgba(91,127,94,0.08), 0 1px 2px rgba(0,0,0,0.06)',
        'waldorf':    '0 4px 6px rgba(91,127,94,0.07), 0 2px 4px rgba(0,0,0,0.06)',
        'waldorf-md': '0 10px 15px rgba(91,127,94,0.07), 0 4px 6px rgba(0,0,0,0.05)',
        'waldorf-lg': '0 20px 25px rgba(91,127,94,0.06), 0 10px 10px rgba(0,0,0,0.04)',
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
