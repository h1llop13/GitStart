$ErrorActionPreference = "Stop"

$Repo = "h1llop13/GitStart"
$InstallDir = Join-Path $env:USERPROFILE ".gitstart"
$BinDir = Join-Path $env:USERPROFILE ".gitstart\bin"
$JarPath = Join-Path $InstallDir "gitstart.jar"
$CmdPath = Join-Path $BinDir "gitstart.cmd"

Write-Host ""
Write-Host "GitStart Installer"
Write-Host "=================="
Write-Host ""

function Test-Command {
    param([string]$Name)

    return [bool](Get-Command $Name -ErrorAction SilentlyContinue)
}

function Refresh-Path {
    $machinePath = [Environment]::GetEnvironmentVariable(
        "Path",
        "Machine"
    )

    $userPath = [Environment]::GetEnvironmentVariable(
        "Path",
        "User"
    )

    $env:Path = "$machinePath;$userPath"
}

function Install-Git {
    if (Test-Command "git") {
        Write-Host "✓ Git detected"
        return
    }

    Write-Host "Git not found."
    Write-Host "Installing Git..."

    winget install `
        --id Git.Git `
        -e `
        --accept-package-agreements `
        --accept-source-agreements

    Refresh-Path

    if (-not (Test-Command "git")) {
        throw "Git installation completed, but git is not available in PATH."
    }

    Write-Host "✓ Git installed"
}

function Install-GitHubCli {
    if (Test-Command "gh") {
        Write-Host "✓ GitHub CLI detected"
        return
    }

    Write-Host "GitHub CLI not found."
    Write-Host "Installing GitHub CLI..."

    winget install `
        --id GitHub.cli `
        -e `
        --accept-package-agreements `
        --accept-source-agreements

    Refresh-Path

    if (-not (Test-Command "gh")) {
        throw "GitHub CLI installation completed, but gh is not available in PATH."
    }

    Write-Host "✓ GitHub CLI installed"
}

function Get-JavaMajorVersion {
    if (-not (Test-Command "java")) {
        return $null
    }

    $output = & java -version 2>&1 | Select-Object -First 1

    if ($output -match '"(\d+)') {
        return [int]$Matches[1]
    }

    return $null
}

function Install-Java {
    $version = Get-JavaMajorVersion

    if ($version -ge 26) {
        Write-Host "✓ Java $version detected"
        return
    }

    Write-Host "Java 26+ not found."
    Write-Host "Searching available Java packages..."

    $candidates = @(
        "EclipseAdoptium.Temurin.26.JDK",
        "Microsoft.OpenJDK.26"
    )

    $installed = $false

    foreach ($packageId in $candidates) {

        winget show --id $packageId -e *> $null

        if ($LASTEXITCODE -eq 0) {

            Write-Host "Installing $packageId..."

            winget install `
                --id $packageId `
                -e `
                --accept-package-agreements `
                --accept-source-agreements

            $installed = $true
            break
        }
    }

    if (-not $installed) {
        throw "Could not find a Java 26 package in winget."
    }

    Refresh-Path

    $version = Get-JavaMajorVersion

    if ($null -eq $version -or $version -lt 26) {
        throw "Java was installed, but Java 26+ is not available in PATH."
    }

    Write-Host "✓ Java $version installed"
}

function Install-GitStart {
    Write-Host ""
    Write-Host "Installing GitStart..."

    New-Item `
        -ItemType Directory `
        -Force `
        -Path $InstallDir | Out-Null

    New-Item `
        -ItemType Directory `
        -Force `
        -Path $BinDir | Out-Null

    $downloadUrl = "https://github.com/$Repo/releases/latest/download/gitstart.jar"

    Invoke-WebRequest `
        -Uri $downloadUrl `
        -OutFile $JarPath `
        -UseBasicParsing

    @"
@echo off
java -jar "$JarPath" %*
"@ | Set-Content `
        -Path $CmdPath `
        -Encoding ASCII

    Write-Host "✓ GitStart installed"
}

function Add-GitStartToPath {
    $currentUserPath = [Environment]::GetEnvironmentVariable(
        "Path",
        "User"
    )

    $paths = $currentUserPath -split ";"

    if ($paths -notcontains $BinDir) {

        $newPath = if ([string]::IsNullOrWhiteSpace($currentUserPath)) {
            $BinDir
        }
        else {
            "$currentUserPath;$BinDir"
        }

        [Environment]::SetEnvironmentVariable(
            "Path",
            $newPath,
            "User"
        )

        Write-Host "✓ GitStart added to PATH"
    }

    $env:Path = "$env:Path;$BinDir"
}

if (-not (Test-Command "winget")) {
    Write-Host ""
    Write-Host "ERROR: winget not found."
    Write-Host "GitStart automatic installer requires Windows Package Manager."
    exit 1
}

Write-Host "Checking dependencies..."
Write-Host ""

Install-Git
Install-GitHubCli
Install-Java

Install-GitStart
Add-GitStartToPath

Write-Host ""
Write-Host "Installation complete!"
Write-Host ""
Write-Host "Run:"
Write-Host ""
Write-Host "  gitstart"
Write-Host ""

if (-not (& gh auth status *> $null)) {
    Write-Host "GitHub CLI is not authenticated yet."
    Write-Host ""
    Write-Host "Run:"
    Write-Host ""
    Write-Host "  gh auth login"
    Write-Host ""
}