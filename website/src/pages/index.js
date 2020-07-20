import React from 'react';
import clsx from 'clsx';
import Layout from '@theme/Layout';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import useBaseUrl from '@docusaurus/useBaseUrl';
import styles from './styles.module.css';

const features = [
  {
    title: <>Maven to sbt</>,
    imageUrl: 'img/m2s.svg',
    description: (
      <>
        Convert Maven&apos;s <code>pom.xml</code> into sbt&apos;s <code>build.sbt</code>.
      </>
    ),
  },
  {
    title: <>Library</>,
    imageUrl: 'img/files-and-folders.svg',
    description: (
      <>
        It can be used as a normal Scala library.
      </>
    ),
  },
  {
    title: <>CLI</>,
    imageUrl: 'img/terminal.svg',
    description: (
      <>
        It can be used as a CLI tool with an easy installation if you already have Java.
      </>
    ),
  },
];

function Feature({imageUrl, title, description}) {
  const imgUrl = useBaseUrl(imageUrl);
  return (
    <div className={clsx('col col--4', styles.feature)}>
      {imgUrl && (
        <div className="text--center">
          <img className={styles.featureImage} src={imgUrl} alt={title} />
        </div>
      )}
      <h3>{title}</h3>
      <p>{description}</p>
    </div>
  );
}

function Home() {
  const context = useDocusaurusContext();
  const {siteConfig = {}} = context;
  return (
    <Layout
      title={`${siteConfig.title}`}
      description="Convert Maven's pom.xml into build.sbt">
      <header className={clsx('hero hero--primary', styles.heroBanner)}>
        <div className="container">
          <img src={`${useBaseUrl('img/')}/poster.png`} alt="Project Logo" />
          <h1 className="hero__title">{siteConfig.title}</h1>
          <p className="hero__subtitle"><div dangerouslySetInnerHTML={{__html: siteConfig.tagline}} /></p>
          <div className={styles.buttons}>
            <Link
              className={clsx(
                'button button--outline button--secondary button--lg',
                styles.getStarted,
              )}
              to={useBaseUrl('docs/')}>
              Get Started
            </Link>
          </div>
        </div>
      </header>
      <main>
        {features && features.length > 0 && (
          <section className={styles.features}>
            <div className="container">
              <div className="row">
                {features.map((props, idx) => (
                  <Feature key={idx} {...props} />
                ))}
              </div>
            </div>
          </section>
        )}
      </main>
    </Layout>
  );
}

export default Home;
