#!/bin/bash

COMP="/software/sun-jdk-1.6.0-latest-el6-x86_64/bin/javac"
JUNIT="./libs/junit-4.11.jar"
CLASSPATH=".:$JUNIT"
OUTDIR="./bin"

EXTRA=""

FILES="src/*.java"

mkdir "$OUTDIR" 2> /dev/null

cmd=`echo "$COMP" "$EXTRA" -classpath "$CLASSPATH" -d "$OUTDIR" "$FILES"`
echo "Compilation command: \"$cmd\" ";

echo "-------------------------------"

$cmd

if [ $? -eq 0 ]; then
	echo "Compilation succeeded!";
else
	echo "Compilation failed!";
fi
