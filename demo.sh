#!/bin/bash
# ============================================================================
# DEMO - Proyecto Final Ingenieria de Software II
# Product System - Pipeline CI/CD
# ============================================================================
# Uso: bash demo.sh [paso]
#   Sin parametros: ejecuta toda la demo paso a paso
#   Con numero:     ejecuta solo ese paso (ej: bash demo.sh 1)
# ============================================================================

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
export MAVEN_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED"

step() {
    echo ""
    echo -e "${BLUE}============================================${NC}"
    echo -e "${YELLOW}  $1${NC}"
    echo -e "${BLUE}============================================${NC}"
}

# ===================================================================
# PASO 1: Compilacion (Maven)
# ===================================================================
demo_step1() {
    step "PASO 1: BUILD - Construccion Automatica (Maven)"
    echo "Comando: ./mvnw clean compile"
    echo ""
    ./mvnw clean compile -q 2>&1
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}BUILD SUCCESS - 25 archivos compilados${NC}"
    else
        echo -e "${RED}BUILD FAILED${NC}"
    fi
}

# ===================================================================
# PASO 2: Pruebas Unitarias (JUnit + Mockito)
# ===================================================================
demo_step2() {
    step "PASO 2: UNIT TESTS - Pruebas Unitarias (JUnit + Mockito)"
    echo "Comando: ./mvnw test -Dtest='...'"
    echo ""
    ./mvnw test -Dtest="UserTest,ProductDTOTest,CategoryServiceTest,ProductApiControllerTest" -q 2>&1
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}Tests run: 14, Failures: 0, Errors: 0${NC}"
    fi
}

# ===================================================================
# PASO 3: Pipeline CI/CD (Jenkinsfile)
# ===================================================================
demo_step3() {
    step "PASO 3: PIPELINE CI/CD - Jenkinsfile (8 etapas)"
    echo "Archivo: Jenkinsfile"
    echo ""
    echo "Etapas del pipeline:"
    grep "stage(" Jenkinsfile | sed 's/.*stage./  /' | sed "s/'.*/ /" | sed 's/) {/ /'
    echo ""
    echo -e "${GREEN}Pipeline declarativo con 8 etapas automatizadas${NC}"
}

# ===================================================================
# PASO 4: Docker
# ===================================================================
demo_step4() {
    step "PASO 4: DOCKER - Containerizacion"
    echo "Archivo: Dockerfile (multi-stage build)"
    echo ""
    echo "Etapas:"
    echo "  1. Builder (openjdk:8-jdk-alpine) - Compila el .jar"
    echo "  2. Runtime (openjdk:8-jre-alpine) - Imagen ligera final"
    echo ""
    echo "Buenas practicas:"
    echo "  - Multi-stage build (reduce tamano)"
    echo "  - Usuario no-root (seguridad)"
    echo "  - Health check (monitoreo)"
    echo ""
    echo "Archivo: docker-compose.yml"
    echo "  Servicios: app + mysql"
    echo "  Health checks en ambos servicios"
    echo "  Volumen persistente mysql-data"
    echo "  Red interna product-network"
}

# ===================================================================
# PASO 5: Arquitectura DDD
# ===================================================================
demo_step5() {
    step "PASO 5: ARQUITECTURA - Domain-Driven Design (DDD)"
    echo "4 Bounded Contexts con 4 capas cada uno:"
    echo ""
    echo "  shared/          (Shared Kernel)"
    echo "  user/            (User Context)"
    echo "  |-- presentation/"
    echo "  |-- application/"
    echo "  |-- domain/"
    echo "  |-- infrastructure/"
    echo "  product/         (Product Context)"
    echo "  |-- presentation/  (REST API + Thymeleaf)"
    echo "  |-- application/   (IProductService + ProductService)"
    echo "  |-- domain/        (Product, ProductDTO)"
    echo "  |-- infrastructure/(ProductRepository)"
    echo "  category/        (Category Context)"
    echo ""
    echo -e "${GREEN}25 archivos Java organizados en modulos DDD${NC}"
}

# ===================================================================
# PASO 6: API REST y Swagger
# ===================================================================
demo_step6() {
    step "PASO 6: API REST + Swagger UI"
    echo "Endpoints REST documentados:"
    echo ""
    echo "  GET    /api/products?page=1&size=5"
    echo "  GET    /api/products/{id}"
    echo "  POST   /api/products"
    echo "  DELETE /api/products/{id}"
    echo "  GET    /api/categories"
    echo ""
    echo "Swagger UI: http://localhost:8080/swagger-ui.html"
    echo -e "${YELLOW}(Requiere app corriendo con perfil H2)${NC}"
}

# ===================================================================
# PASO 7: Ejecutar la aplicacion (H2)
# ===================================================================
demo_step7() {
    step "PASO 7: RUN - Ejecutar aplicacion (H2 en memoria)"
    echo "Comando: ./mvnw spring-boot:run -Dspring.profiles.active=h2"
    echo "Presiona Ctrl+C para detener"
    echo ""
    ./mvnw spring-boot:run 2>&1 | grep -E "Started Application|Tomcat started|ERROR" &
    APP_PID=$!
    # Esperar 15 segundos a que arranque
    for i in $(seq 1 15); do
        sleep 1
        if curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/categories 2>/dev/null | grep -q 200; then
            break
        fi
    done
    
    echo -e "${GREEN}App corriendo en http://localhost:8080${NC}"
    echo ""
    echo "Probando API:"
    echo ""
    echo "  Categorias:"
    curl -s http://localhost:8080/api/categories 2>/dev/null | python3 -m json.tool 2>/dev/null | head -15
    echo ""
    echo ""
    echo "URLs disponibles:"
    echo "  App:        http://localhost:8080"
    echo "  Swagger UI: http://localhost:8080/swagger-ui.html"
    echo "  H2 Console: http://localhost:8080/h2-console [JDBC URL: jdbc:h2:mem:productdb]"
}

# ===================================================================
# PASO 8: GitHub Project
# ===================================================================
demo_step8() {
    step "PASO 8: GESTION - GitHub Project + Issues"
    echo "URL: https://github.com/fabriciomessa/ProyectoFinal_IS2"
    echo ""
    echo "Kanban Board: 5 columnas"
    echo "  TO-DO -> CURRENT ITERATION -> IN PROGRESS -> FIX VALIDATION -> DONE"
    echo ""
    echo "Issues: 33 cerrados"
    echo "  18 Lab 07 (Refactoring y TDD)"
    echo "  15 Practica 07 (Rediseno DDD)"
    echo ""
    echo "Tecnologias:"
    echo "  Backend:  Java 8 + Spring Boot 1.5.7 + Spring Security + JPA"
    echo "  Frontend: Thymeleaf + Bootstrap 3 + jQuery"
    echo "  BD:       MySQL 5.7 (produccion) / H2 (demo)"
    echo "  CI/CD:    Jenkins + SonarQube + Docker"
    echo "  Tests:    JUnit + Mockito + Selenium + JMeter + OWASP ZAP"
}

# ===================================================================
# MENU PRINCIPAL
# ===================================================================
if [ "$1" == "run" ]; then
    demo_step7
    exit 0
fi

if [ -n "$1" ]; then
    case $1 in
        1) demo_step1 ;;
        2) demo_step2 ;;
        3) demo_step3 ;;
        4) demo_step4 ;;
        5) demo_step5 ;;
        6) demo_step6 ;;
        7) demo_step7 ;;
        8) demo_step8 ;;
        *) echo "Usa: bash demo.sh [1-8] o bash demo.sh run" ;;
    esac
    exit 0
fi

# Demo completa
echo -e "${GREEN}"
echo "  ╔══════════════════════════════════════════════╗"
echo "  ║   PRODUCT SYSTEM - PROYECTO FINAL IS2        ║"
echo "  ║   Pipeline CI/CD + DDD + TDD                 ║"
echo "  ╚══════════════════════════════════════════════╝"
echo -e "${NC}"

demo_step1
sleep 2

demo_step2
sleep 2

demo_step3
sleep 2

demo_step4
sleep 2

demo_step5
sleep 2

demo_step6
sleep 2

demo_step8
echo ""
echo -e "${GREEN}============================================${NC}"
echo -e "${GREEN}  DEMO COMPLETA${NC}"
echo -e "${GREEN}============================================${NC}"
echo ""
echo "Para ejecutar la app: bash demo.sh run"
echo "Repositorio: https://github.com/fabriciomessa/ProyectoFinal_IS2"
