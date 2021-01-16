DEPENDS += "openblas"
DEPENDS_remove_class-native = "libgfortran"

EXTRA_OECMAKE += " -DUSE_OPTIMIZED_BLAS=1"

BBCLASSEXTEND = "native"