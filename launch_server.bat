@echo off
chcp 65001 > nul
start /b java -Dfile.encoding=UTF-8 -jar .\build\libs\SecureServer-1.0-SNAPSHOT.jar %1 %2

:: параметр 1 - тип (например, --dhi)
:: параметр 2 - подтип (--alg, --el)