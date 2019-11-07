#!/bin/bash -e

set -x

if [ -z "$1" ]
  then
    echo "Scala version is missing. Please enter the Scala version."
    echo "sbt-build.sh 2.11.12"
    exit 1
else
  scala_version=$1
  echo "============================================"
  echo "Build projects"
  echo "--------------------------------------------"
  echo ""
  export CI_BRANCH="${GITHUB_REF#refs/heads/}"
  if [[ "$CI_BRANCH" == "master" || "$CI_BRANCH" == "release" ]]
  then
    sbt -J-Xmx2048m "; ++ ${scala_version}!; clean; coverage; test; coverageReport; coverageAggregate"
    sbt -J-Xmx2048m "; ++ ${scala_version}!; coveralls"
    sbt -J-Xmx2048m "; ++ ${scala_version}!; clean; packagedArtifacts"
  else
    sbt -J-Xmx2048m "; ++ ${scala_version}!; clean; coverage; test; coverageReport; coverageAggregate; package"
    sbt -J-Xmx2048m "; ++ ${scala_version}!; coveralls"
  fi


  echo "============================================"
  echo "Building projects: Done"
  echo "============================================"
fi
