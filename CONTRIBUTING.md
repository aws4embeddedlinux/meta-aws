# Contributing Guidelines

Thank you for your interest in contributing to our project. Whether
it's a bug report, new feature, correction, or additional
documentation, we greatly value feedback and contributions from our
community.

Please read through this document before submitting any issues or pull
requests to ensure we have all the necessary information to
effectively respond to your bug report or contribution.

## Reporting Bugs/Feature Requests

We welcome you to use the GitHub issue tracker to report bugs or
suggest features.

When filing an issue, please check existing open, or recently closed,
issues to make sure somebody else hasn't already reported the
issue. Please try to include as much information as you can. Details
like these are incredibly useful:

* A reproducible test case or series of steps
* The version of our code being used
* Any modifications you've made relevant to the bug
* Anything unusual about your environment or deployment

## Contributing via Pull Requests

Contributions via pull requests are much appreciated. Before sending
us a pull request, please ensure that:

1. You open an issue to discuss any significant work - we would hate
   for your time to be wasted.
2. You are working against the intended Yocto Project *release-next*
   branch. Changes are no longer accepted direct to *release*
   branches.
3. You check existing open, and recently merged, pull requests to make
   sure someone else hasn't addressed the problem already.
4. Typically bug fixes must already be accepted into the *master* branch before they can be backported to a *release* branch, unless the bug in question does not affect the *master* branch or the fix on the *master* branch is unsuitable for backporting.

To send us a pull request, please:

1. Fork the repository.
2. Modify the source; please focus on the specific change you are
   contributing. If you also reformat all the code, it will be hard
   for us to focus on your change.
3. Ensure local tests pass.
4. Commit to your fork using clear commit messages.
5. Send us a pull request, answering any default questions in the pull
   request interface.
6. Wait for a repository maintainer to look at your PR, run it in CI, test, and review. If additional changes or discussion is needed, a maintainer will get back to you, so please stay invovled in the conversation. Note: PRs from forks will not run in CI automatically for security reasons. If you make a PR and see that CI is left pending, this is normal and expected.
7. After your pull request is merged into *release-next* there will be done some additional testing and after that is successfully finished it will be merged into the  *release* branch. Usually this will take 24h for *master* and 7 days for *release* branches.

GitHub provides additional document on [forking a repository](https://help.github.com/articles/fork-a-repo/) and
[creating a pull request](https://help.github.com/articles/creating-a-pull-request/).

## Quality
With every pull request following tests are performed:
### build
All recipes are tested to build with qemuarm, qemuarm64, qemux86-64.
### ptest
We like to have a [ptest](https://wiki.yoctoproject.org/wiki/Ptest) for every recipe. With every pull request we ptest with the different architectures we support. 
If there is a ptest it must pass. To get an idea how to write an ptest just grep for it in the layer.
### oelint-adv
We think having the same "coding" style for recipes is a good idea. Therefore we test with [oelint-adv](https://github.com/priv-kweihmann/oelint-adv) for errors. 
### checklayer
Meta-aws is Yocto project [compatible](https://www.yoctoproject.org/software-overview/layers/). To keep this status we test this.

## Finding contributions to work on

Looking at the existing issues is a great way to find something to
contribute on. As our projects, by default, use the default GitHub
issue labels (enhancement/bug/duplicate/help
wanted/invalid/question/wontfix), looking at any 'help wanted' issues
is a great place to start.


## Code of Conduct
This project has adopted the [Amazon Open Source Code of
Conduct](https://aws.github.io/code-of-conduct).  For more information
see the [Code of Conduct
FAQ](https://aws.github.io/code-of-conduct-faq) or contact
opensource-codeofconduct@amazon.com with any additional questions or
comments.

## Security issue notifications
If you discover a potential security issue in this project we ask that
you notify AWS/Amazon Security via our [vulnerability reporting
page](http://aws.amazon.com/security/vulnerability-reporting/). Please
do **not** create a public github issue.

## Licensing

See the [LICENSE](LICENSE) file for our project's licensing. We will
ask you to confirm the licensing of your contribution.

We may ask you to sign a [Contributor License Agreement
(CLA)](http://en.wikipedia.org/wiki/Contributor_License_Agreement) for
larger changes.
