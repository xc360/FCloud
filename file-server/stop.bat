@echo off
set pid_file=%cd%\service.pid
REM 判断 PID 文件是否存在
IF EXIST %pid_file% (
	for /f "delims=" %%i in (%pid_file%) do (
		taskkill /F /PID %%i
	)
	del %pid_file%
	echo Service Stop.
) ELSE (
    echo Service Not Start
)



