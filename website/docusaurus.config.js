const algoliaConfig = require('./algolia.config.json');
const googleAnalyticsConfig = require('./google-analytics.config.json');

const lightCodeTheme = require('prism-react-renderer/themes/nightOwlLight');
const darkCodeTheme = require('prism-react-renderer/themes/nightOwl');

const isEmptyObject = obj => {
  for (field in obj) return false;
  return true;
};

const isSearchable = !isEmptyObject(algoliaConfig);
const hasGoogleAnalytics = !isEmptyObject(googleAnalyticsConfig);
const gtag = hasGoogleAnalytics ? { 'gtag': googleAnalyticsConfig } : null;

const websiteConfig = {
  title: 'Effectie',
  tagline: 'A Tool for FP Effect Libraries',
  url: 'https://effectie.kevinly.dev',
  baseUrl: '/',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'throw',
  favicon: 'img/favicon.png',
  organizationName: 'Kevin-Lee', // Usually your GitHub org/user name.
  projectName: 'effectie', // Usually your repo name.
  themeConfig: {
    prism: {
      theme: lightCodeTheme,
      darkTheme: darkCodeTheme,
      additionalLanguages: [
        'java',
        'scala',
      ],
    },
    navbar: {
      title: 'Effectie',
      logo: {
        alt: 'Effectie Logo',
        src: 'img/effectie-logo-32x32.png',
      },
      items: [
        {
          to: 'docs/',
          activeBasePath: 'docs',
          label: 'Docs',
          position: 'left',
        },
        {
          href: 'https://github.com/Kevin-Lee/effectie',
          className: 'header-github-link',
          'aria-label': 'GitHub',
          position: 'right',
        },
      ],
    },
    footer: {
      style: 'dark',
      links: [
        {
          title: 'Docs',
          items: [
            {
              label: 'Getting Started',
              to: 'docs/',
            },
            {
              label: 'For Cats Effect',
              to: 'docs/cats-effect',
            },
            {
              label: 'For Scalaz Effect',
              to: 'docs/scalaz-effect',
            },
          ],
        },
        {
          title: 'More',
          items: [
            {
              label: 'GitHub',
              href: 'https://github.com/Kevin-Lee/effectie',
            },
            {
              label: 'Blog',
              href: 'https://blog.kevinlee.io',
            },
            {
              label: 'Homepage',
              href: 'https://kevinlee.io',
            },
          ],
        },
      ],
      copyright: `Copyright © 2019 Effectie is designed and developed by <a href="https://github.com/Kevin-Lee" target="_blank">Kevin Lee</a>.<br>The website built with Docusaurus.`,
    },
  },
  presets: [
    [
      '@docusaurus/preset-classic',
      {
        docs: {
          path: '../generated-docs/target/mdoc/',
          sidebarPath: require.resolve('./sidebars.js'),
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
        ...gtag,
      },
    ],
  ],
  plugins: [
    require.resolve('docusaurus-lunr-search'),
  ],
};

if (isSearchable) {
  websiteConfig['themeConfig']['algolia'] = algoliaConfig;
}

module.exports = websiteConfig;
