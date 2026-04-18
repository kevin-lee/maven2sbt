import type {SidebarsConfig} from '@docusaurus/plugin-content-docs';

// This runs in Node.js - Don't use client-side code here (browser APIs, JSX...)

/**
 * Creating a sidebar enables you to:
 - create an ordered group of docs
 - render a sidebar for each doc of that group
 - provide next/previous navigation

 The sidebars can be generated from the filesystem, or explicitly defined here.

 Create as many sidebars as you want.
 */
const sidebars: SidebarsConfig = {
  docs: [
    {
      type: 'category',
      label: 'maven2sbt',
      collapsed: false,
      items: ['getting-started'],
    },
    {
      type: 'category',
      label: 'CLI',
      collapsed: false,
      items: ['cli/get-cli', 'cli/how-to-use'],
    },
    {
      type: 'category',
      label: 'Library',
      collapsed: true,
      items: ['library/get-library', 'library/how-to-use'],
    },
  ],
};

export default sidebars;
