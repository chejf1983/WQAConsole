REM 声明采用UTF-8编码
chcp 65001

del .\*.jar

@echo off
echo copy excl开发包
copy ..\..\..\lib\jexcelapi\jxl.jar .\
echo copy jyloo皮肤开发包
copy ..\..\..\lib\jyloo\synthetica_2.17.1_eval\synthetica.jar .\
copy ..\..\..\lib\jyloo\syntheticaAluOxide.jar .\


echo copy 自定义平台开发包
copy ..\..\..\PlatForm\commonbean\dist\commonbean.jar .\
copy ..\..\..\PlatForm\WindowsIOdrv\windows_io_driver\store\windows_io_driver.jar .\

pause