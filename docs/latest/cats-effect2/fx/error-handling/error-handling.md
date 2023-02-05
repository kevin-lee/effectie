---
id: error-handling
title: "Error Handling"
---
import DocCardList from '@theme/DocCardList';

`effectOf` and `pureOrError` can catch the error, so you don't get your program crashed with unwanted exception
if you use a right instances of `Fx`. The instances of `Fx` for Cats Effect 2 and 3, Monix 3 and Future can handle it.

If you want to do something with the error captured by the effect used in the instance of `Fx`, you can easily do with
error handling API provided by effectie.

<DocCardList />
