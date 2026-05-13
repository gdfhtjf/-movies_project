@echo off

set PATH=C:\Program Files\nodejs;%PATH%

echo ========================================
echo   在线电影购票系统 - 一键启动
echo ========================================

echo Starting Backend (port 8080)...
start "Backend" cmd /k "cd /D %~dp0backend && mvn spring-boot:run"

echo Waiting for backend to start...
timeout /t 15 /nobreak >nul

echo Starting Frontend (port 5173)...
start "Frontend" cmd /k "cd /D %~dp0frontend && set PATH=C:\Program Files\nodejs;%%PATH%% && npm run dev"

echo ========================================
echo Done. Open http://localhost:5173 when ready.
echo Admin login: Admin / 123456
echo ========================================
pause
