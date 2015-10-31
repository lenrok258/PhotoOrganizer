#!/bin/bash
./gradlew clean installApp
cp -r ./build/install/PhotoOrganizer/* dist/
