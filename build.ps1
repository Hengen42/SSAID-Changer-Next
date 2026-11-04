Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   SSAID Changer Next - Build & Deploy  " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.21.21-hotspot"
$env:ANDROID_HOME = "C:\Users\Hengen\AppData\Local\Android\Sdk"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host "`n[1/3] Building Release APK..." -ForegroundColor Yellow
& "$scriptDir\gradlew.bat" -p "$scriptDir" assembleRelease --no-daemon

if ($LASTEXITCODE -ne 0) {
    Write-Host "`n[ERROR] Gradle build failed!" -ForegroundColor Red
    exit 1
}

$apk = Get-ChildItem -Path "$scriptDir\app\build\outputs\apk\release" -Filter "*.apk" -Recurse | Select-Object -First 1

if (-not $apk) {
    Write-Host "`n[ERROR] APK not found!" -ForegroundColor Red
    exit 1
}

Write-Host "`n[2/3] Built successfully: $($apk.Name) ($([math]::Round($apk.Length / 1MB, 2)) MB)" -ForegroundColor Green

# Check if ADB device is attached
$devices = adb devices | Where-Object { $_ -match "\tdevice$" }

if ($devices) {
    Write-Host "`n[3/3] Connected device found. Installing APK..." -ForegroundColor Yellow
    adb install -r $apk.FullName
    if ($LASTEXITCODE -eq 0) {
        Write-Host "`n[SUCCESS] SSAID Changer Next installed successfully on your device!" -ForegroundColor Green
    } else {
        Write-Host "`n[WARNING] ADB install failed." -ForegroundColor Yellow
    }
} else {
    Write-Host "`n[INFO] No ADB device connected. APK is ready in: $($apk.FullName)" -ForegroundColor Cyan
}

Write-Host "`nDone." -ForegroundColor Cyan
