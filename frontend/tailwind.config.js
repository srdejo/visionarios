/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{html,ts}'],
  theme: {
    extend: {
      colors: {
        orange: {
          DEFAULT: '#FAA61A',
          hover: '#B87200',
        },
        dark: {
          DEFAULT: '#111111',
          black: '#000000',
        },
        muted: {
          1: '#6B6B6B',
          2: '#5C5C5C',
          3: '#7A7A7A',
          4: '#9A9A9A',
        },
        line: {
          1: '#E1E1E1',
          2: '#E9E9E9',
          3: '#EFEFEF',
          4: '#F0F0F0',
        },
        input: '#FAFAFA',
        danger: '#B00020',
      },
      fontFamily: {
        oswald: ['Oswald', 'sans-serif'],
        barlow: ['Barlow', 'sans-serif'],
        script: ['Sacramento', 'cursive'],
      },
    },
  },
  plugins: [],
};
