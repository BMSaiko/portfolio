#!/usr/bin/env bash

#REM set the class path,
#REM assumes the build was executed with maven copy-dependencies
export BASE_CP=exemplo.app.bootstrap/target/exemplo.app.bootstrap-1.4.0-SNAPSHOT.jar:exemplo.app.bootstrap/target/dependency/*;

#REM call the java VM, e.g,
java -cp $BASE_CP eapli.exemplo.app.bootstrap.ExemploBootstrap
