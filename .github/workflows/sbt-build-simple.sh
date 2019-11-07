#!/bin/bash -e

set -x

if [ -z "$1" ]
  then
    echo "Scala version is missing. Please enter the Scala version."
    echo "sbt-build-simple.sh 2.11.12"
    exit 1
else
  scala_version=$1
  echo "============================================"
  echo "Build projects (Simple)"
  echo "--------------------------------------------"
  echo ""
  CURRENT_BRANCH_NAME="${GITHUB_REF#refs/heads/}"
  if [[ "$CURRENT_BRANCH_NAME" == "master" || "$CURRENT_BRANCH_NAME" == "release" ]]
  then
    sbt -J-Xmx2048m "; ++ ${scala_version}!; clean; test; packagedArtifacts"
  else
    sbt -J-Xmx2048m "; ++ ${scala_version}!; clean; test; package"
  fi

  echo "============================================"
  echo "Building projects (Simple): Done"
  echo "============================================"
fi
