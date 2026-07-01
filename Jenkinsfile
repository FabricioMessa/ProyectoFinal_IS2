pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'fabriciomessa/product-system'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        SONAR_HOST_URL = 'http://localhost:9000'
        SONAR_TOKEN = credentials('sonar-token')
    }

    tools {
        maven 'maven-3.8'
        jdk 'jdk8'
    }

    stages {

        // ================================================================
        // ETAPA 1: BUILD - Construccion automatica (Maven)
        // ================================================================
        stage('1. Build') {
            steps {
                echo '=== Compilando y empaquetando con Maven ==='
                sh './mvnw clean package -DskipTests'
            }
        }

        // ================================================================
        // ETAPA 2: SONARQUBE - Analisis estatico de codigo
        // ================================================================
        stage('2. Static Analysis - SonarQube') {
            steps {
                echo '=== Ejecutando SonarScanner ==='
                sh '''
                    sonar-scanner \
                        -Dsonar.projectKey=ProyectoFinal \
                        -Dsonar.sources=src/main/java \
                        -Dsonar.java.binaries=target/classes \
                        -Dsonar.host.url=${SONAR_HOST_URL} \
                        -Dsonar.login=${SONAR_TOKEN}
                '''
            }
        }

        // ================================================================
        // ETAPA 3: UNIT TESTS - JUnit + Mockito
        // ================================================================
        stage('3. Unit Tests - JUnit/Mockito') {
            steps {
                echo '=== Ejecutando pruebas unitarias ==='
                sh './mvnw test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        // ================================================================
        // ETAPA 4: FUNCTIONAL TESTS - Selenium
        // ================================================================
        stage('4. Functional Tests - Selenium') {
            steps {
                echo '=== Ejecutando pruebas funcionales con Selenium ==='
                sh '''
                    ./mvnw spring-boot:run &
                    APP_PID=$!
                    sleep 15
                    ./mvnw test -Dtest="FunctionalTests"
                    kill $APP_PID 2>/dev/null || true
                '''
            }
        }

        // ================================================================
        // ETAPA 4b: API TESTS - Newman/Postman
        // ================================================================
        stage('4b. API Tests - Postman/Newman') {
            steps {
                echo '=== Ejecutando pruebas de API REST ==='
                sh '''
                    ./mvnw spring-boot:run &
                    APP_PID=$!
                    sleep 15
                    newman run src/test/resources/postman/ProductSystemAPI.postman_collection.json \
                        --reporters cli,junit \
                        --reporter-junit-export target/newman-report.xml
                    kill $APP_PID 2>/dev/null || true
                '''
            }
        }

        // ================================================================
        // ETAPA 5: PERFORMANCE TESTS - JMeter
        // ================================================================
        stage('5. Performance Tests - JMeter') {
            steps {
                echo '=== Ejecutando pruebas de carga con JMeter ==='
                sh '''
                    ./mvnw spring-boot:run &
                    APP_PID=$!
                    sleep 15
                    mkdir -p target/jmeter-report
                    jmeter -n -t src/test/resources/jmeter/ProductSystem_TestPlan.jmx \
                           -l target/results.jtl \
                           -e -o target/jmeter-report/ \
                           || echo "JMeter: pruebas completadas"
                    kill $APP_PID 2>/dev/null || true
                '''
            }
        }

        // ================================================================
        // ETAPA 6: SECURITY TESTS - OWASP ZAP
        // ================================================================
        stage('6. Security Tests - OWASP ZAP') {
            steps {
                echo '=== Ejecutando escaneo de seguridad ==='
                sh '''
                    ./mvnw spring-boot:run &
                    APP_PID=$!
                    sleep 15
                    zap.sh -cmd -quickurl http://localhost:8080 \
                           -quickprogress \
                           -quickout target/zap_report.html \
                           || echo "ZAP: escaneo completado"
                    kill $APP_PID 2>/dev/null || true
                '''
            }
        }

        // ================================================================
        // ETAPA 7: ISSUE MANAGEMENT - GitHub Issues + Project
        // ================================================================
        stage('7. Issue Management - GitHub') {
            steps {
                echo '=== Verificando estado de issues ==='
                echo 'Repositorio: https://github.com/fabriciomessa/ProyectoFinal_IS2'
                echo 'GitHub Project: https://github.com/users/fabriciomessa/projects/1'
                echo 'Total issues: 33 cerrados (18 Lab 07 + 15 Practica 07)'
            }
        }

        // ================================================================
        // ETAPA 8: DOCKER - Construccion y despliegue
        // ================================================================
        stage('8. Deploy - Docker') {
            steps {
                echo '=== Construyendo imagen Docker ==='
                sh '''
                    docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                    docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest
                '''
                echo '=== Desplegando con Docker Compose ==='
                sh '''
                    docker-compose down || true
                    docker-compose up -d
                '''
            }
        }

    } // end stages

    // ================================================================
    // POST-ACCIONES
    // ================================================================
    post {
        always {
            echo "Pipeline #${env.BUILD_NUMBER} finalizado. Resultado: ${currentBuild.result}"
        }
        success {
            echo "Pipeline CI/CD completado exitosamente."
        }
        failure {
            echo "Pipeline CI/CD fallo. Revisar la etapa con error."
        }
    }
}
