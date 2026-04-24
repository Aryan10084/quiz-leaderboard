@ECHO OFF
SETLOCAL

SET BASEDIR=%~dp0
IF "%BASEDIR:~-1%"=="\" SET BASEDIR=%BASEDIR:~0,-1%
SET WRAPPER_JAR=%BASEDIR%\.mvn\wrapper\maven-wrapper.jar

IF DEFINED JAVA_HOME (
  SET JAVA_EXEC=%JAVA_HOME%\bin\java.exe
) ELSE (
  SET JAVA_EXEC=java.exe
)

"%JAVA_EXEC%" "-Dmaven.multiModuleProjectDirectory=%BASEDIR%" -cp "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
ENDLOCAL