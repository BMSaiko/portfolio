@echo off
chcp 65001
cls

REM Run Maven clean and compile
mvn clean compile exec:java
