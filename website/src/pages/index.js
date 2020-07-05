import React from 'react';
import clsx from 'clsx';
import Layout from '@theme/Layout';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import useBaseUrl from '@docusaurus/useBaseUrl';
import styles from './styles.module.css';

const features = [
  {
    title: <>Effect</>,
    imageUrl: 'img/io_-200x200.png',
    description: (
      <>
        Effectie is a tool for FP Effect libraries like Cats Effect and
        Scalaz Effect.
      </>
    ),
  },
  {
    title: <>Tagless Final</>,
    imageUrl: 'img/f_-200x200.png',
    description: (
      <>
        It helps you have abstraction in your code using Tagless Final.
        So instead of <code>IO(a): IO[A]</code>,
        you can do <code>effectOf(a): F[A]</code>
      </>
    ),
  },
  {
    title: <>Referential Transparency</>,
    imageUrl: 'img/lambda-centred-200x200.png',
    description: (
      <>
        So that you can keep the abstraction and also enjoy coding
        with referential transparency!
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
      description="Effectie, a tool for FP Effect libraries">
      <header className={clsx('hero hero--primary', styles.heroBanner)}>
        <div className="container">
          <img src={`${useBaseUrl('img/')}/poster.png`} alt="Project Logo" />
          <h1 className="hero__title">{siteConfig.title}</h1>
          <p className="hero__subtitle">{siteConfig.tagline}</p>
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
