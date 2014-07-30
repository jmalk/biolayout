#! /bin/bash

TAG=`git describe`
BRANCH=`git rev-parse --abbrev-ref HEAD`

if [ "${BRANCH}" == "master" ];
then
    echo ${TAG}
else
    echo ${TAG}-${BRANCH}
fi
