@echo off
chcp 65001 > nul
echo ==========================================================
echo               ECOSHOP — KHỞI CHẠY ỨNG DỤNG
echo ==========================================================
if not exist "bin" mkdir bin
echo [*] Đang biên dịch mã nguồn Java...
javac -encoding UTF-8 -d bin src\app\*.java src\model\*.java src\service\*.java src\exception\*.java
if %ERRORLEVEL% NEQ 0 (
    echo [x] Biên dịch thất bại! Vui lòng kiểm tra lại mã nguồn.
    pause
    exit /b %ERRORLEVEL%
)
echo [✓] Biên dịch thành công! Đang khởi động EcoShop...
java -cp bin app.EcoShopApp
pause

