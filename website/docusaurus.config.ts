import {themes as prismThemes} from 'prism-react-renderer';
import type {Config} from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';

import LatestVersion from '@types/commonTypes';

const algoliaConfig = require('./algolia.config.json');
const googleAnalyticsConfig = require('./google-analytics.config.json');

// const lightCodeTheme = require('prism-react-renderer/themes/github');
// const darkCodeTheme = require('prism-react-renderer/themes/dracula');
const lightCodeTheme = prismThemes.nightOwlLight;
const darkCodeTheme = prismThemes.nightOwl;


const isEmptyObject = (obj: object) => Object.keys(obj).length === 0;

const isSearchable = !isEmptyObject(algoliaConfig);
const hasGoogleAnalytics = !isEmptyObject(googleAnalyticsConfig);
const gtag = hasGoogleAnalytics ? { 'gtag': googleAnalyticsConfig } : null;

import LatestVersionImported from './latestVersion.json';
const latestVersionFound = LatestVersionImported as LatestVersion;

const websiteConfig = {
  title: 'Effectie',
  tagline: 'A Tool for FP Effect Libraries',
  url: 'https://effectie.kevinly.dev',
  baseUrl: '/',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'throw',
  favicon: 'img/favicon.png',
  organizationName: 'kevin-lee', // Usually your GitHub org/user name.
  projectName: 'effectie', // Usually your repo name.
  themeConfig: {
    image: 'img/refined4s-social-card.jpg',
    docs: {
      sidebar: {
        hideable: true,
      },
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
          type: 'docsVersionDropdown',
          position: 'right',
          dropdownActiveClassDisabled: true,
          dropdownItemsAfter: [
            {
              to: '/versions',
              label: 'All versions',
            },
          ],
        },
        {
          href: 'https://github.com/kevin-lee/effectie',
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
              label: 'For Cats Effect 2',
              to: 'docs/cats-effect2',
            },
            {
              label: 'For Monix',
              to: 'docs/monix3',
            },
          ],
        },
        {
          title: 'More',
          items: [
            {
              label: 'GitHub',
              href: 'https://github.com/kevin-lee/effectie',
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
      copyright: `Copyright © 2019 Effectie is designed and developed by <a href="https://github.com/kevin-lee" target="_blank">Kevin Lee</a>.<br>The website built with Docusaurus.`,
    },
    prism: {
      theme: lightCodeTheme,
      darkTheme: darkCodeTheme,
      additionalLanguages: [
        'bash',
        'diff',
        'json',
        'java',
        'scala',
      ],
    },
  },
  presets: [
    [
      '@docusaurus/preset-classic',
      {
        docs: {
          path: '../generated-docs/docs/',
          sidebarPath: require.resolve('./sidebars.js'),
          "lastVersion": "current",
          "versions": {
            "v1": {
              "label": "v1",
              "path": "v1",
            },
            "current": {
              "label": `v${latestVersionFound.version}`,
            },
          }
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
        ...gtag,
      },
    ],
  ],
  plugins: [],
};

if (isSearchable) {
  websiteConfig['themeConfig']['algolia'] = algoliaConfig;
}

export default websiteConfig;
