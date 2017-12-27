#!/bin/bash
app=$APPLICATION
env=$TRACK
target=""

for i in "$@"
do
  case $i in
    --target=*)
      target="${i#*=}"
      shift # past argument=value
    ;;
    *)
      # ignore
      shift # past argument=value
    ;;
  esac
done

if [[ $env == "" ]]
then
  echo "No env mentioned."
  exit 2
fi

if [[ $target == "" ]]
then
  echo "No target mentioned. Using default."
  target="build"
fi

echo "Building ${app} for ${env}."

set -ex

# clear build folder
echo "Clearing target..."
rm -rf $target
mkdir -p "${target}"

# prepare dist
echo "Preparing dist..."
cp -r resources/* conf/
if [[ -d resources.${env} ]]
then
  cp -a resources.${env}/* conf/
fi
JAVA_OPTS="" sbt -v -mem 512  clean test dist

echo "Copying dist output to target"
unzip target/universal/annotation-avengers.zip -d ${target}

set +ex
