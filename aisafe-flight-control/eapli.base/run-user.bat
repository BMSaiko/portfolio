REM set the class path,
REM assumes the build was executed with maven copy-dependencies
SET BASE_CP=exemplo.app.user.console\target\exemplo.app.user.console-1.4.0-SNAPSHOT.jar;exemplo.app.user.console\target\dependency\*;

REM call the java VM, e.g, 
java -cp %BASE_CP% eapli.exemplo.app.user.console.ExemploUserApp
