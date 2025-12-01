@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@echo off
setlocal enabledelayedexpansion

set MAVEN_PROJECTBASEDIR=%~dp0
if "%MAVEN_PROJECTBASEDIR%"=="" set MAVEN_PROJECTBASEDIR=.
if "%MAVEN_PROJECTBASEDIR:~-1%"=="\" set MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

set WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar
set WRAPPER_PROPERTIES=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties
set WRAPPER_FALLBACK_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar

if not exist "%WRAPPER_JAR%" (
    echo Downloading Maven wrapper jar...
    if not exist "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper" mkdir "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper"
    set WRAPPER_URL=
    if exist "%WRAPPER_PROPERTIES%" (
        for /f "tokens=2 delims==" %%A in ('findstr /b /c:"wrapperUrl" "%WRAPPER_PROPERTIES%"') do set WRAPPER_URL=%%A
    )
    if "!WRAPPER_URL!"=="" set WRAPPER_URL=%WRAPPER_FALLBACK_URL%
    powershell -Command "Invoke-WebRequest -Uri '!WRAPPER_URL!' -OutFile '%WRAPPER_JAR%'"
)

set JAVA_EXE=%JAVA_HOME%\bin\java.exe
if exist "%JAVA_EXE%" (
    "%JAVA_EXE%" ^
        -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" ^
        -classpath "%WRAPPER_JAR%" ^
        org.apache.maven.wrapper.MavenWrapperMain %*
) else (
    java ^
        -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" ^
        -classpath "%WRAPPER_JAR%" ^
        org.apache.maven.wrapper.MavenWrapperMain %*
)

endlocal


