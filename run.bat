@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"
chcp 65001 > nul

SET "JAVA_HOME=C:\Users\Lenovo\.vscode\extensions\redhat.java-1.58.0-win32-x64\jre\25.0.4.1-win32-x86_64"
SET "JAVAC=%JAVA_HOME%\bin\javac.exe"
SET "JAVA=%JAVA_HOME%\bin\java.exe"

echo ==========================================================
echo               ECOSHOP - KHOI CHAY UNG DUNG
echo ==========================================================

rem 1. Kiem tra JDK
where javac >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [LOI] Khong tim thay trinh bien dich javac!
    echo Vui long kiem tra lai bien moi truong PATH hoac cai dat JDK 21+.
    pause
    exit /b 1
)

rem 2. Tao thu muc bin neu chua co
if not exist "bin" mkdir bin

rem 3. Quet tat ca file nguon Java de bien dich
echo [*] Dang quet ma nguon va bien dich...
dir /s /b src\*.java > sources.txt
"%JAVAC%" -encoding UTF-8 -d bin @sources.txt
set COMPILE_ERR=%ERRORLEVEL%
if exist sources.txt del sources.txt

if %COMPILE_ERR% NEQ 0 (
    echo [x] Bien dich that bai! Vui long kiem tra lai ma nguon.
    pause
    exit /b %COMPILE_ERR%
)

echo [OK] Bien dich thanh cong! Dang khoi dong EcoShop...
echo ==========================================================
"%JAVA%" -cp bin app.EcoShopApp
pause
