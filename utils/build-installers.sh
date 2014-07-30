#! /bin/bash

SCRIPT_NAME=`readlink -f $0`
SCRIPT_DIR=`dirname ${SCRIPT_NAME}`
SRC_DIR=`readlink -f ${SCRIPT_DIR}/..`
HTML_TEMPLATE="${SCRIPT_DIR}/index.html.template"
WEBSTART_TEMPLATE="${SCRIPT_DIR}/WebStart.jnlp.template"
BASE_URL="http://biolayout.org/internal"
BASE_NAME="BLE3D"

echo SCRIPT_NAME=${SCRIPT_NAME}
echo SCRIPT_DIR=${SCRIPT_DIR}
echo SRC_DIR=${SRC_DIR}

cd ${SRC_DIR}
VERSION=`${SCRIPT_DIR}/version.sh`

GIT_REV=`git rev-parse HEAD`
BUILDS_DIR="${SCRIPT_DIR}/builds"
BUILD_DIR="${BUILDS_DIR}/${VERSION}"
BUILD_URL="${BASE_URL}/${BUILD_NAME}"

echo VERSION=${VERSION}
echo GIT_REV=${GIT_REV}
echo BUILD_DIR=${BUILD_DIR}
echo BUILD_URL=${BUILD_URL}

rm -r ${BUILD_DIR}
mkdir -p ${BUILD_DIR}

# Windows
cd ${SRC_DIR}/nsis-installer
cat installer.nsi | sed -e "s/_VERSION_/${VERSION}/g" | makensis -
if [ "$?" != "0" ];
then
    exit $?
fi
cp ${SRC_DIR}/nsis-installer/${BASE_NAME}-${VERSION}-installer.exe ${BUILD_DIR}

# OS X
cd ${SRC_DIR}/target
genisoimage -D -V BioLayoutExpress3D -no-pad -uid 0 -gid 0 -dir-mode 755 -file-mode 644 \
    -apple -o ${BUILD_DIR}/${BASE_NAME}-${VERSION}.dmg dmg/

# Everything else
cp ${SRC_DIR}/target/${BASE_NAME}-${VERSION}.jar ${BUILD_DIR}

# Source code
cd ${SRC_DIR}
git archive --format zip -9 --output ${BUILD_DIR}/${BASE_NAME}-${VERSION}-source.zip ${GIT_REV}

cat ${HTML_TEMPLATE} | sed \
    -e "s/_BUILD_NAME_/${BUILD_NAME}/g" \
    -e "s/_VERSION_/${VERSION}/g" \
    -e "s/_GIT_REV_/${GIT_REV}/g" \
    > ${BUILD_DIR}/index.html
cat ${WEBSTART_TEMPLATE} | sed -e "s%_BUILD_URL_%${BUILD_URL}%g" \
    -e "s/_VERSION_/${VERSION}/g" -e "s/_HEAP_SIZE_/920m/g" > ${BUILD_DIR}/WebStart32.jnlp
cat ${WEBSTART_TEMPLATE} | sed -e "s%_BUILD_URL_%${BUILD_URL}%g" \
    -e "s/_VERSION_/${VERSION}/g" -e "s/_HEAP_SIZE_/32000m/g" > ${BUILD_DIR}/WebStart64.jnlp
cp ${SRC_DIR}/src/main/resources/Resources/Images/BioLayoutExpress3DLogo.png ${BUILD_DIR}
cp ${SRC_DIR}/src/main/resources/Resources/Images/BioLayoutExpress3DIcon.png ${BUILD_DIR}

rm ${BUILDS_DIR}/current
ln -s ${BUILD_DIR} ${BUILDS_DIR}/current
