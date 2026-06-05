@echo off

REM 获取当前目录路径
SET "current_dir=%cd%"

REM 查找当前目录下的唯一 JAR 文件
for %%I in ("%current_dir%\*.jar") do (
    SET "jar_file=%%~nxI"
    SET /A jar_count+=1
)

REM 如果不存在或存在多个 JAR 文件，则给出提示并退出
if %jar_count% neq 1 (
    if %jar_count% equ 0 (
        echo 未找到 JAR 文件。
    ) else (
        echo 发现多个 JAR 文件，请保留唯一的 JAR 文件。
    )
    exit /b
)

REM 后台启动 Java 应用，并将输出重定向到 NUL
start "%jar_file%" java -jar "%current_dir%\%jar_file%" > NUL

REM 获取 Java 进程的 PID
for /f "tokens=2 delims=," %%a in ('tasklist /fi "imagename eq java.exe" /v /fo csv ^| findstr "%jar_file%"') do (
    echo %%a > service.pid
)

echo start Success.
