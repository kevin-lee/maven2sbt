#!/bin/bash -e

set -x

if [ -z "$1" ]
  then
    echo "Missing parameter. Please enter the [Scala version]."
    echo "sbt-build.sh 2.12.10"
    exit 1
else
  scala_version=$1
  echo "============================================"
  echo "Build projects"
  echo "--------------------------------------------"
  echo ""
  export CI_BRANCH="${GITHUB_REF#refs/heads/}"
  if [[ "$CI_BRANCH" == "main" || "$CI_BRANCH" == "release" ]]
  then
    sbt -J-Xmx2048m "; ++ ${scala_version}! -v; clean; coverage; test; coverageReport; coverageAggregate"
    sbt -J-Xmx2048m "; ++ ${scala_version}! -v; coveralls"
    sbt -J-Xmx2048m "; ++ ${scala_version}! -v; clean; packagedArtifacts"
  else
    sbt -J-Xmx2048m "; ++ ${scala_version}! -v; clean; coverage; test; coverageReport; coverageAggregate; package"
    sbt -J-Xmx2048m "; ++ ${scala_version}! -v; coveralls"
  fi


  echo "============================================"
  echo "Building projects: Done"
  echo "============================================"
fi
