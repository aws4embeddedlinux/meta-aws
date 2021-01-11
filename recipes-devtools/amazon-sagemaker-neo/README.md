## Amazon Sagemaker Neo DLR

### Dependencies
The following package is required for Amazon Sagemaker Neo DLR:
 * python3-distro

This package is not included in meta-python in Zeus or Dunfell. It is distributed with the meta-aws layer for those branches.
To add it to your project, add the following to `local.conf`:

``IMAGE_INSTALL_append = " python3-distro"``


### Changes to version 1.7.0

The recipe `neo-ai-dlr` composes multiple changes to files with the
v1.7.0 tag. For the most part, it is overcoming shortcomings in the
neo-ai-dlr build process to accomodate Embedded Linux.

1. **CMakeLists.txt**: patch: remove download of `googletests`, which
   is handled by dependency.
2. **CMakeLists.txt**: patch: remove download of custom files.  This
   is for testing, will be downloaded to ${WORKDIR} and then moved to
   the appropriate target.

   - `tflite-models/cat224-3.txt` => `${S}/build`
   - `test-data/street_small.npy` => `${S}/build`
   - `compiled-models/resnet_v1.5_50-ml_c4.tar.gz`, unpacks to
     `${WORKDIR}` => `${S}/build/resnet_v1_5_50`
   - `compiled-models/xgboost_test.tar.gz`
   - `compiled-models/release-1.5.0/ssd_mobilenet_v1_ppn_shared_box_predictor_300x300_coco14_sync_2018_07_03-LINUX_X86_64.tar.gz`
   - `compiled-models/release-1.5.0/automl-ml_m4.tar.gz`
   - `compiled-models/pipeline_model1-LINUX_X86_64.tar.gz`
   - `compiled-models/pipeline_model1-LINUX_X86_64.tar.gz`
   - `compiled-models/release-1.5.0/pipeline_model2-LINUX_X86_64.tar.gz`
   - `compiled-models/release-1.5.0/inverselabel-ml_m4.tar.gz`
