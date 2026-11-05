@echo off
setlocal
cd /d "%~dp0"
chcp 65001 > nul

echo ==========================================================
echo               ECOSHOP - KHOI CHAY UNG DUNG
echo ==========================================================

set "JAVAC="
set "JAVA="

:: 1. Kiem tra bien moi truong JAVA_HOME san co
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\javac.exe" (
        if exist "%JAVA_HOME%\bin\java.exe" (
            set "JAVAC=%JAVA_HOME%\bin\javac.exe"
            set "JAVA=%JAVA_HOME%\bin\java.exe"
            goto :JDK_FOUND
        )
    )
)

:: 2. Tu dong tim JDK trong Program Files
for /d %%D in ("%ProgramFiles%\Java\jdk*") do (
    if exist "%%~fD\bin\javac.exe" (
        if exist "%%~fD\bin\java.exe" (
            set "JAVAC=%%~fD\bin\javac.exe"
            set "JAVA=%%~fD\bin\java.exe"
            goto :JDK_FOUND
        )
    )
)

:: 3. Kiem tra JDK cac ban pho bien (Eclipse Adoptium / Microsoft / Corretto / Zulu)
for /d %%D in ("%ProgramFiles%\Eclipse Adoptium\jdk*" "%ProgramFiles%\Microsoft\jdk*" "%ProgramFiles%\Amazon Corretto\jdk*" "%ProgramFiles%\Zulu\jdk*") do (
    if exist "%%~fD\bin\javac.exe" (
        if exist "%%~fD\bin\java.exe" (
            set "JAVAC=%%~fD\bin\javac.exe"
            set "JAVA=%%~fD\bin\java.exe"
            goto :JDK_FOUND
        )
    )
)

:: 4. Kiem tra trong VS Code extensions (neu may dung Extension Pack for Java)
for /d %%D in ("%USERPROFILE%\.vscode\extensions\redhat.java-*\jre\*") do (
    if exist "%%~fD\bin\javac.exe" (
        if exist "%%~fD\bin\java.exe" (
            set "JAVAC=%%~fD\bin\javac.exe"
            set "JAVA=%%~fD\bin\java.exe"
            goto :JDK_FOUND
        )
    )
)

:: 5. Kiem tra javac va java trong bien moi truong PATH
where javac >nul 2>nul
if %ERRORLEVEL% equ 0 (
    where java >nul 2>nul
    if %ERRORLEVEL% equ 0 (
        set "JAVAC=javac"
        set "JAVA=java"
        goto :JDK_FOUND
    )
)

:JDK_NOT_FOUND
echo [LOI] Khong tim thay trinh bien dich Java (javac)!
echo.
echo Nguyen nhan: May tinh chua duoc cai dat JDK (Java Development Kit)
echo           hoac chua thiet lap bien moi truong JAVA_HOME / PATH.
echo.
echo Huong dan khac phuc:
echo   1. Tai va cai dat JDK 21+ tai: https://adoptium.net (hoac Oracle JDK)
echo   2. Neu da cai dat, hay them bien moi truong JAVA_HOME tro den thu muc JDK
echo      hoac them thu muc bin cua JDK vao bien PATH.
echo ==========================================================
pause
exit /b 1

:JDK_FOUND
echo [*] Trinh bien dich duoc su dung:
echo     javac: "%JAVAC%"
echo     java:  "%JAVA%"
echo.

:: Tao thu muc bin neu chua co
if not exist "bin" mkdir bin

:: Quet ma nguon va tien hanh bien dich
echo [*] Dang quet ma nguon va bien dich...
if exist sources.txt del sources.txt
dir /s /b src\*.java > sources.txt 2>nul

if not exist sources.txt (
    echo [LOI] Khong tim thay file ma nguon Java trong thu muc src!
    pause
    exit /b 1
)

"%JAVAC%" -encoding UTF-8 -d bin @sources.txt
set COMPILE_ERR=%ERRORLEVEL%
if exist sources.txt del sources.txt

if %COMPILE_ERR% neq 0 (
    echo.
    echo [x] Bien dich that bai! Vui long kiem tra lai ma nguon hoac phien ban JDK.
    pause
    exit /b %COMPILE_ERR%
)

:: Khoi chay ung dung
echo.
echo [OK] Bien dich thanh cong! Dang khoi dong EcoShop...
echo ==========================================================
echo.
"%JAVA%" -cp bin app.EcoShopApp

if %ERRORLEVEL% neq 0 (
    echo.
    echo [!] Ung dung ket thuc voi ma loi: %ERRORLEVEL%
)

echo.
pause