#!/bin/bash -e

set -x

if [ -z "$1" ]
  then
    echo "Missing parameters. Please enter the [Scala version]."
    echo "sbt-build.sh 2.13.4"
    exit 1
else
  : ${CURRENT_BRANCH_NAME:?"CURRENT_BRANCH_NAME is missing."}

  scala_version=$1
  echo "============================================"
  echo "Build projects"
  echo "--------------------------------------------"
  echo ""
  echo "mkdir -p dotty-docs"
  mkdir -p dotty-docs
  export SOURCE_DATE_EPOCH=$(date +%s)
  echo "SOURCE_DATE_EPOCH=$SOURCE_DATE_EPOCH"

  if [[ "$CURRENT_BRANCH_NAME" == "main" || "$CURRENT_BRANCH_NAME" == "release" ]]
  then
#    sbt -J-Xmx2048m ++${scala_version}! -v clean; coverage; test; coverageReport; coverageAggregate
#    sbt -J-Xmx2048m ++${scala_version}! -v coveralls
#    sbt -J-Xmx2048m ++${scala_version}! -v clean; packagedArtifacts
    sbt \
      -J-Xmx2048m \
      ++${scala_version}! \
      -v \
      clean \
      test \
      packagedArtifacts \
      universal:packageBin \
      universal:packageZipTarball \
      debian:packageBin

    ls -ld cli/target/maven2sbt-cli*
    ls -ld cli/target/universal/maven2sbt-cli*

    if [ "$2" == "report" ]
    then
      echo "sbt -J-Xmx2048m ++${scala_version}! -v clean coverage test coverageReport coverageAggregate coveralls"
      sbt \
        -J-Xmx2048m \
        ++${scala_version}! \
        -v \
        clean \
        coverage \
        test \
        coverageReport \
        coverageAggregate \
        coveralls
    fi

  else
    sbt \
      -J-Xmx2048m \
      ++${scala_version}! \
      -v \
      clean \
      test \
      package
  fi


  echo "============================================"
  echo "Building projects: Done"
  echo "============================================"
fi
