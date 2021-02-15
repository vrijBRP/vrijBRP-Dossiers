#!/usr/bin/env bash

dir=$(dirname "$0")

tag="vrijbrp/dossiers"
jar="dossiers.jar"
jar_path="${dir}/../jar/target/${jar}"

[[ ! -f "${jar_path}" ]] && {
  echo "${jar_path} not found"
  echo "Probably project needs to be built first"
  exit 1
}

cp "${jar_path}" "${dir}/${jar}"
docker build -t "${tag}" --build-arg "jar=${jar}" "${dir}"