#! /bin/sh

THIS_DIR=`dirname $0`

rm DetectJVM.class DetectJVM.jar DetectJVM.exe
javac -source 1.5 -target 1.5 DetectJVM.java && \
    jar cfe DetectJVM.jar DetectJVM DetectJVM.class && \
    launch4j `readlink -m DetectJVM.xml`
