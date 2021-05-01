module.exports = {
  someSidebar: [
    {
      type: 'category',
      label: 'Effectie',
      collapsed: false,
      items: ['getting-started'],
    },
    {
      type: 'category',
      label: 'Effectie - Cats Effect',
      collapsed: false,
      items: [
        'cats-effect/cats-effect',
        'cats-effect/effect-constructor',
        'cats-effect/can-catch',
        'cats-effect/can-handle-error',
        'cats-effect/from-future',
        'cats-effect/optiont-support',
        'cats-effect/eithert-support',
        'cats-effect/console-effect',
      ]
    },
    {
      type: 'category',
      label: 'Effectie - Monix',
      collapsed: true,
      items: [
        'monix/monix',
        'monix/effect-constructor',
        'monix/can-catch',
        'monix/can-handle-error',
        'monix/from-future',
        'monix/optiont-support',
        'monix/eithert-support',
        'monix/console-effect',
      ]
    },
    {
      type: 'category',
      label: 'Effectie - Scalaz Effect',
      collapsed: true,
      items: [
        'scalaz-effect/scalaz-effect',
        'scalaz-effect/effect-constructor',
        'scalaz-effect/can-catch',
        'scalaz-effect/optiont-support',
        'scalaz-effect/eithert-support',
        'scalaz-effect/console-effect',
      ]
    },
  ],
};
