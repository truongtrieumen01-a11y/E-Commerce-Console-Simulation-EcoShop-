@echo off
chcp 65001 > nul
setlocal

cd /d "%~dp0"

set "SRC=src"
set "OUT=out"
set "JAVA_BIN="
set "JAVAC_BIN="

rem 1. Check JAVA_HOME
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\javac.exe" (
        set "JAVAC_BIN=%JAVA_HOME%\bin\javac.exe"
        set "JAVA_BIN=%JAVA_HOME%\bin\java.exe"
    )
)

rem 2. Auto detect VSCode RedHat Java Extension JRE
if not defined JAVAC_BIN (
    for /d %%i in ("%USERPROFILE%\.vscode\extensions\redhat.java*") do (
        for /d %%j in ("%%i\jre\*") do (
            if exist "%%j\bin\javac.exe" (
                set "JAVAC_BIN=%%j\bin\javac.exe"
                set "JAVA_BIN=%%j\bin\java.exe"
            )
        )
    )
)

rem 3. Auto detect Program Files Java JDK
if not defined JAVAC_BIN (
    for /d %%k in ("%ProgramFiles%\Java\jdk*") do (
        if exist "%%k\bin\javac.exe" (
            set "JAVAC_BIN=%%k\bin\javac.exe"
            set "JAVA_BIN=%%k\bin\java.exe"
        )
    )
)

rem 4. Check PATH
if not defined JAVAC_BIN (
    where javac >nul 2>&1
    if %ERRORLEVEL% equ 0 (
        set "JAVAC_BIN=javac"
        set "JAVA_BIN=java"
    )
)

if not defined JAVAC_BIN goto :no_jdk

echo === Dang bien dich EcoShop ===
if not exist "%OUT%" mkdir "%OUT%"

dir /s /B "%SRC%\*.java" > sources.txt
"%JAVAC_BIN%" -encoding UTF-8 -d "%OUT%" @sources.txt
set "COMPILE_STATUS=%ERRORLEVEL%"
if exist sources.txt del sources.txt

if %COMPILE_STATUS% neq 0 (
    echo [LOI] Bien dich that bai. Ma loi: %COMPILE_STATUS%
    pause
    exit /b %COMPILE_STATUS%
)

echo === Bien dich thanh cong ===
echo === Dang khoi dong EcoShop ===
echo.

"%JAVA_BIN%" -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp "%OUT%" app.EcoShopApp

pause
exit /b 0

:no_jdk
echo [LOI] Khong tim thay trinh bien dich Java: javac.exe
echo Vui long cai dat JDK hoac thiet lap bien moi truong JAVA_HOME.
echo.
pause
exit /b 1
