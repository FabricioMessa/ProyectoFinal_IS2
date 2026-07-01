#!/bin/bash
# ============================================================================
# OWASP ZAP - Security Scan Script
# Proyecto Final - Ingenieria de Software II - UCSP
# ============================================================================
# Prerrequisitos:
#   1. OWASP ZAP instalado: brew install --cask owasp-zap
#   2. Aplicacion corriendo: ./mvnw spring-boot:run
#   3. ZAP en modo daemon o GUI abierto en puerto 8081
# ============================================================================

TARGET_URL="http://localhost:8080"
REPORT_DIR="src/test/resources/zap"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
REPORT_HTML="${REPORT_DIR}/zap_report_${TIMESTAMP}.html"
REPORT_XML="${REPORT_DIR}/zap_report_${TIMESTAMP}.xml"

echo "============================================"
echo "  OWASP ZAP - Security Scan"
echo "  Target: ${TARGET_URL}"
echo "  Timestamp: ${TIMESTAMP}"
echo "============================================"

# Verificar que ZAP este instalado
if ! command -v zap.sh &> /dev/null; then
    echo "ERROR: OWASP ZAP no esta instalado."
    echo "Instalar con: brew install --cask owasp-zap"
    echo "O descargar de: https://www.zaproxy.org/download/"
    exit 1
fi

mkdir -p "${REPORT_DIR}"

echo "Iniciando escaneo de seguridad..."

# Automated Scan (requiere ZAP corriendo en background o GUI abierta)
zap.sh -cmd \
    -quickurl "${TARGET_URL}" \
    -quickprogress \
    -quickout "${REPORT_HTML}" \
    2>&1 | tee "${REPORT_DIR}/zap_scan_${TIMESTAMP}.log"

echo ""
echo "============================================"
echo "  Escaneo completado"
echo "  Reporte HTML: ${REPORT_HTML}"
echo "  Log: ${REPORT_DIR}/zap_scan_${TIMESTAMP}.log"
echo "============================================"
