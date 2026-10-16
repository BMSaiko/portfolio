REM set the class path,
REM assumes the build was executed with maven copy-dependencies
SET BASE_CP=exemplo.app.backoffice.console\target\exemplo.app.backoffice.console-1.4.0-SNAPSHOT.jar;exemplo.app.backoffice.console\target\dependency\*;

REM call the java VM, e.g, 
java -cp %BASE_CP% eapli.exemplo.app.backoffice.console.ExemploBackoffice
