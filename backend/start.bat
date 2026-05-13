@echo off

set PATH=C:\Program Files\nodejs;%PATH%

echo Starting Frontend (port 5173)...
start "Frontend" cmd /k "cd /D D:\code\VUE && set PATH=C:\Program Files\nodejs;%%PATH%% && npm run dev"

echo Done. Open http://localhost:5173 when ready.
echo Make sure backend is running on port 8080.
pause
