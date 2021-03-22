# recipes-external
## python3-scikit-learn

#### Note:
This layer is compatible with `dunfell` release and tested on an `armv8` board.

### Install gfortran on the build host.
> `gfortran` library needs to be installed on the build host for successful compilation of `scikit-learn` python package.
>> The dependency `HOSTTOOLS += "gfortran"` is added in the `conf/layer.conf` file. 
>>> If you do not need to install the `scikit-learn` package, you can remove `HOSTTOOLS += "gfortran"` from `conf/layer.conf` file.
```shell script
sudo apt-get install gfortran
```
### Bitbake commands
```shell script
bitbake python3-scipy
bitbake python3-scipy-native
bitbake python3-scikit-learn
```

### Dependecy chain list for installing `scikit-learn` package on the target
#### bitbake commands
```shell script
bitbake python3-scipy
bitbake python3-scipy-native
bitbkae python3-scikit-learn
bitbake python3-joblib
bitbake python3-pytest
bitbake python3-atomicwrites
bitbake python3-attrs
bitbake python3-importlib-metadata
bitbake python3-pathlib2
bitbake python3-zipp
bitbake python3-more-itertools
bitbake python3-packaging
bitbake python3-pluggy
bitbake python3-py
bitbake python3-wcwidth
bitbake libgfortran
bitbake lapack
bitbake openblas
bitbake python3-threadpoolctl
```

#### opkg commands
```shell script
opkg install python3-joblib_1.0.0-r0_aarch64.ipk
opkg install python3-atomicwrites_1.3.0-r0_aarch64.ipk
opkg install python3-attrs_19.3.0-r0_aarch64.ipk
opkg install python3-pathlib2_2.3.5-r0_aarch64.ipk
opkg install python3-more-itertools_8.2.0-r0_aarch64.ipk
opkg install python3-zipp_0.6.0-r0_aarch64.ipk
opkg install python3-importlib-metadata_1.5.2-r0_aarch64.ipk
opkg install python3-packaging_20.3-r0_aarch64.ipk
opkg install python3-pluggy_0.13.1-r0_aarch64.ipk
opkg install python3-py_1.8.1-r0_aarch64.ipk
opkg install python3-wcwidth_0.1.8-r0_aarch64.ipk
opkg install python3-pytest_5.3.5-r0_aarch64.ipk
opkg install libgfortran5_9.3.0-r0_aarch64.ipk
opkg install libopenblas0_0.3.10-r0_aarch64.ipk
opkg install liblapack3_3.9.0-r0_aarch64.ipk
opkg install python3-scipy_1.5.3-r0_aarch64.ipk
opkg install python3-threadpoolctl_2.1.0-r0_aarch64.ipk
opkg install python3-scikit-learn_0.24.0-r0_aarch64.ipk
```