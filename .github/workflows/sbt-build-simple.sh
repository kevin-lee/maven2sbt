#!/bin/bash -e

set -x

if [ -z "$2" ]
  then
    echo "Missing parameters. Please enter the [project] and [Scala version]."
    echo "sbt-build-simple.sh core 2.12.10"
    exit 1
else
  project_name=$1
  scala_version=$2
  echo "============================================"
  echo "Build projects (Simple)"
  echo "--------------------------------------------"
  echo ""
  CURRENT_BRANCH_NAME="${GITHUB_REF#refs/heads/}"
  if [[ "$CURRENT_BRANCH_NAME" == "main" || "$CURRENT_BRANCH_NAME" == "release" ]]
  then
    sbt -J-Xmx2048m "; project ${project_name}; ++ ${scala_version}! -v; clean; test; packagedArtifacts"
  else
    sbt -J-Xmx2048m "; project ${project_name}; ++ ${scala_version}! -v; clean; test; package"
  fi

  echo "============================================"
  echo "Building projects (Simple): Done"
  echo "============================================"
fi
