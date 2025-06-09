@echo off
echo Starting Hospital Information Management System (HIMS)...
echo ======================================

:: Check if port 8080 is in use
netstat -ano | findstr :8080 > nul
if %errorlevel% equ 0 (
    echo Port 8080 is in use, switching to port 8081
    set PORT=8081
) else (
    set PORT=8080
)

:: Start backend service
echo Starting backend service on port %PORT%...
start cmd /k "cd backend && .\mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=%PORT%"

:: Wait for backend to start
echo Waiting for backend service to start...
timeout /t 5 /nobreak > nul

:: Start frontend service
echo Starting frontend service...
start cmd /k "cd frontend && npm run dev"

echo ======================================
echo Services are starting...
echo Backend API URL: http://localhost:%PORT%
echo Frontend URL: http://localhost:5173 (or check the frontend console output)
echo.
echo System accounts:
echo   Admin: admin/admin
echo   Doctor: mei/1
echo.
echo Press any key to close this window. Services will continue running.
pause > nul 2