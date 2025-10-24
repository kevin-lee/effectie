import clsx from 'clsx';
import React, {ReactNode} from 'react';
import Heading from '@theme/Heading';
import styles from './styles.module.css';

type FeatureItem = {
  title: string;
  Svg: React.ComponentType<React.ComponentProps<'svg'>>;
  description: ReactNode;
};

const FeatureList: FeatureItem[] = [
  {
    title: <>Effect</>,
    Png: require('@site/static/img/io-2-200x200.png').default,
    description: (
        <>
          Effectie is a tool for FP Effect libraries like Cats Effect and
          Scalaz Effect.
        </>
    ),
  },
  {
    title: <>Tagless Final</>,
    Png: require('@site/static/img/f_-200x200.png').default,
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
    Png: require('@site/static/img/lambda-centred-200x200.png').default,
    description: (
        <>
          So that you can keep the abstraction and also enjoy coding
          with referential transparency!
        </>
    ),
  },
];


function FeatureSvg({title, Svg, description}: FeatureItem) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <Svg className={styles.featureSvg} role="img" />
      </div>
      <div className="text--center padding-horiz--md">
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
    </div>
  );
}

function FeaturePng({Png, title, description}) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <img src={Png} className={styles.featurePng} alt={title} />
      </div>
      <div className="text--center padding-horiz--md">
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures(): JSX.Element {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            props.hasOwnProperty('Svg') ?
              <FeatureSvg key={idx} {...props} /> :
              <FeaturePng key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
