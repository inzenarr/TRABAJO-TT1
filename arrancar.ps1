# Script de arranque — Simulación TT1
# Uso: .\arrancar.ps1
# Requisitos: Docker Desktop en ejecución, Maven instalado (en PATH o en MAVEN_HOME)

$ErrorActionPreference = "Stop"

# Detectar mvn: PATH > MAVEN_HOME > ruta por defecto de VS Code Maven extension
$MVN = $null
if (Get-Command mvn -ErrorAction SilentlyContinue) {
    $MVN = "mvn"
} elseif ($env:MAVEN_HOME -and (Test-Path "$env:MAVEN_HOME\bin\mvn.cmd")) {
    $MVN = "$env:MAVEN_HOME\bin\mvn.cmd"
} else {
    $localMvn = Get-ChildItem "$env:USERPROFILE\.maven" -Recurse -Filter "mvn.cmd" -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($localMvn) { $MVN = $localMvn.FullName }
}
if (-not $MVN) {
    Write-Host "      ERROR: No se encontro Maven. Instala Maven y asegurate de que 'mvn' esta en el PATH." -ForegroundColor Red
    exit 1
}

$PROYECTO  = Split-Path -Parent $MyInvocation.MyCommand.Definition

# Leer FRONTEND_PATH desde .env si existe
$FRONTEND_PATH = $null
$envFile = Join-Path $PROYECTO ".env"
if (Test-Path $envFile) {
    Get-Content $envFile | Where-Object { $_ -match "^\s*FRONTEND_PATH\s*=\s*(.+)" } | ForEach-Object {
        $val = $Matches[1].Trim()
        if ($val -ne "") { $FRONTEND_PATH = $val }
    }
}

$conFrontend = $FRONTEND_PATH -and (Test-Path $FRONTEND_PATH)
$pasos = if ($conFrontend) { 4 } else { 3 }

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Simulacion TT1 — Script de arranque  " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# ── 1. Verificar que Docker está disponible ──────────────────────────────────
Write-Host "[1/$pasos] Verificando Docker..." -ForegroundColor Yellow
try {
    $null = docker info 2>&1
    Write-Host "      Docker OK" -ForegroundColor Green
} catch {
    Write-Host "      ERROR: Docker Desktop no esta en ejecucion. Arrancalo y vuelve a intentarlo." -ForegroundColor Red
    exit 1
}

# ── 2. Compilar el backend ───────────────────────────────────────────────────
Write-Host "[2/$pasos] Compilando el backend (Maven)..." -ForegroundColor Yellow
Set-Location $PROYECTO
& $MVN package -DskipTests -q
if ($LASTEXITCODE -ne 0) {
    Write-Host "      ERROR: La compilacion del backend ha fallado." -ForegroundColor Red
    exit 1
}
Write-Host "      Backend OK" -ForegroundColor Green

# ── 3. Compilar el frontend (opcional) ──────────────────────────────────────
if ($conFrontend) {
    Write-Host "[3/$pasos] Compilando el frontend (Maven)..." -ForegroundColor Yellow
    & $MVN package -DskipTests -q -f (Join-Path $FRONTEND_PATH "pom.xml")
    if ($LASTEXITCODE -ne 0) {
        Write-Host "      ERROR: La compilacion del frontend ha fallado." -ForegroundColor Red
        exit 1
    }
    Write-Host "      Frontend OK" -ForegroundColor Green
} elseif ($FRONTEND_PATH) {
    Write-Host "      AVISO: FRONTEND_PATH=$FRONTEND_PATH no existe. Se omite el frontend." -ForegroundColor DarkYellow
}

# ── 3/4. Construir imágenes y arrancar contenedores ─────────────────────────
$paso = if ($conFrontend) { 4 } else { 3 }
Write-Host "[$paso/$pasos] Construyendo imagenes Docker y arrancando contenedores..." -ForegroundColor Yellow
Set-Location $PROYECTO
if ($conFrontend) {
    docker compose --profile frontend up --build -d
} else {
    docker compose up --build -d
}
if ($LASTEXITCODE -ne 0) {
    Write-Host "      ERROR: docker compose up ha fallado." -ForegroundColor Red
    exit 1
}
Write-Host "      Contenedores arrancados" -ForegroundColor Green

# ── Resumen ──────────────────────────────────────────────────────────────────
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Aplicacion lista!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
if ($conFrontend) {
    Write-Host "  Frontend  ->  http://localhost:8081/solicitud"
}
Write-Host "  API REST  ->  http://localhost:5000/swagger-ui/index.html"
Write-Host ""
Write-Host "  Para ver los logs:      docker compose logs -f"
Write-Host "  Para parar la app:      docker compose down"
Write-Host ""
