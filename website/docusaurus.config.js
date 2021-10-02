const algoliaConfig = require('./algolia.config.json');
const googleAnalyticsConfig = require('./google-analytics.config.json');

const isEmptyObject = obj => {
  for (field in obj) return false;
  return true;
};

const isSearchable = !isEmptyObject(algoliaConfig);
const hasGoogleAnalytics = !isEmptyObject(googleAnalyticsConfig);

const websiteConfig = {
  title: 'maven2sbt',
  tagline: 'Convert Maven&apos;s <code class="front-code">pom.xml</code> into <code class="front-code">build.sbt</code>',
  url: 'https://maven2sbt.kevinly.dev',
  baseUrl: '/',
  favicon: 'img/favicon.png',
  organizationName: 'Kevin-Lee', // Usually your GitHub org/user name.
  projectName: 'maven2sbt', // Usually your repo name.
  themeConfig: {
    prism: {
      theme: require('prism-react-renderer/themes/nightOwlLight'),
      darkTheme: require('prism-react-renderer/themes/palenight'),
      additionalLanguages: [ 'java', 'scala'],
    },
    navbar: {
      title: 'maven2sbt',
      logo: {
        alt: 'maven2sbt Logo',
        src: 'img/maven2sbt-logo-32x32.png',
      },
      items: [
        {
          to: 'docs/',
          activeBasePath: 'docs',
          label: 'Docs',
          position: 'left',
        },
        {
          href: 'https://github.com/Kevin-Lee/maven2sbt',
          label: 'GitHub',
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
              label: 'Use CLI',
              to: 'docs/cli/get-cli',
            },
          ],
        },
        {
          title: 'More',
          items: [
            {
              label: 'GitHub',
              href: 'https://github.com/Kevin-Lee/maven2sbt',
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
      copyright: `Copyright © ${new Date().getFullYear()} maven2sbt written by <a href="https://github.com/Kevin-Lee" target="_blank"><b>Kevin Lee</b></a>,.The website built with Docusaurus.<br>Some icons made by <a href="https://www.flaticon.com/free-icon/file_1126880" title="prettycons">prettycons</a> and <a href="https://www.flaticon.com/authors/smartline" title="Smartline">Smartline</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>`,
    },
  },
  presets: [
    [
      '@docusaurus/preset-classic',
      {
        docs: {
          sidebarPath: require.resolve('./sidebars.js'),
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
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
if (hasGoogleAnalytics) {
  websiteConfig['themeConfig']['gtag'] = googleAnalyticsConfig;
}

module.exports = websiteConfig;
