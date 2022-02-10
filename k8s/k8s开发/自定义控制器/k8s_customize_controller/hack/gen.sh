#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

SCRIPT_ROOT=$(dirname "${BASH_SOURCE[0]}")/..

# generate the code with:
# - --output-base because this script should also be able to run inside the vendor dir of
#   k8s.io/kubernetes. The output-base is needed for the generators to output into the vendor dir
#   instead of the $GOPATH directly. For normal projects this can be dropped.
$SCRIPT_ROOT/vendor/k8s.io/code-generator/generate-groups.sh all \
  k8s_customize_controller/pkg/client k8s_customize_controller/pkg/apis bolingcavalry:v1 \
  --output-base ${SCRIPT_ROOT}/.. \
  --go-header-file ${SCRIPT_ROOT}/hack/boilerplate.go.txt