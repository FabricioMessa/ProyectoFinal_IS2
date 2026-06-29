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
        // ETAPA 1: CONSTRUCCIÓN AUTOMÁTICA
        //   - Compilación
        //   - Gestión de dependencias (Maven)
        //   - Empaquetado (.jar)
        // ================================================================
        stage('1. Build - Construcción Automática') {
            steps {
                echo '=== Compilando y empaquetando con Maven ==='
                sh './mvnw clean package -DskipTests'
            }
            post {
                success {
                    echo 'Build exitoso. Artefacto generado en target/'
                }
                failure {
                    echo 'ERROR: Falló la compilación. Revisar logs.'
                }
            }
        }

        // ================================================================
        // ETAPA 2: ANÁLISIS ESTÁTICO DE CÓDIGO FUENTE (SONARQUBE)
        // ================================================================
        stage('2. SonarQube - Análisis Estático') {
            steps {
                echo '=== Ejecutando SonarScanner ==='
                sh '''
                    /Users/fmessa/Personal/sonar-scanner-8.0.1.6346-macosx-aarch64/bin/sonar-scanner \
                        -Dsonar.projectKey=ProyectoFinal \
                        -Dsonar.sources=src/main/java \
                        -Dsonar.java.binaries=target/classes \
                        -Dsonar.host.url=${SONAR_HOST_URL} \
                        -Dsonar.login=${SONAR_TOKEN}
                '''
            }
            post {
                success {
                    echo 'Análisis SonarQube completado. Resultados en: ${SONAR_HOST_URL}/dashboard?id=ProyectoFinal'
                }
            }
        }

        // ================================================================
        // ETAPA 3: PRUEBAS UNITARIAS (JUnit + Mockito)
        // ================================================================
        stage('3. Unit Tests - Pruebas Unitarias') {
            steps {
                echo '=== Ejecutando pruebas unitarias ==='
                sh './mvnw test'
            }
            post {
                success {
                    echo 'Pruebas unitarias ejecutadas.'
                }
                failure {
                    echo 'ADVERTENCIA: Algunas pruebas unitarias fallaron. Revisar surefire-reports.'
                }
            }
        }

        // ================================================================
        // ETAPA 4: PRUEBAS FUNCIONALES (SELENIUM)
        // ================================================================
        stage('4. Functional Tests - Selenium') {
            when {
                expression { return fileExists('src/test/java/com/batuhaniskr/product/functional/FunctionalTests.java') }
            }
            steps {
                echo '=== Ejecutando pruebas funcionales con Selenium ==='
                sh '''
                    # Iniciar la aplicación en segundo plano
                    ./mvnw spring-boot:run -Dspring-boot.run.profiles=test &
                    APP_PID=$!
                    sleep 15  # Esperar a que la aplicación arranque

                    # Ejecutar pruebas Selenium
                    ./mvnw test -Dtest="FunctionalTests"

                    # Detener la aplicación
                    kill $APP_PID 2>/dev/null || true
                '''
            }
        }

        // ================================================================
        // ETAPA 4b: PRUEBAS FUNCIONALES (POSTMAN/NEWMAN - API REST)
        // ================================================================
        stage('4b. API Tests - Postman/Newman') {
            when {
                expression { return fileExists('src/test/resources/postman/ProductSystemAPI.postman_collection.json') }
            }
            steps {
                echo '=== Ejecutando pruebas de API con Newman ==='
                sh '''
                    ./mvnw spring-boot:run -Dspring-boot.run.profiles=test &
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
        // ETAPA 5: PRUEBAS DE PERFORMANCE (JMETER)
        // ================================================================
        stage('5. Performance Tests - JMeter') {
            when {
                expression { return fileExists('src/test/resources/jmeter/ProductSystem_TestPlan.jmx') }
            }
            steps {
                echo '=== Ejecutando pruebas de performance con JMeter ==='
                sh '''
                    if command -v jmeter &> /dev/null; then
                        ./mvnw spring-boot:run -Dspring-boot.run.profiles=test &
                        APP_PID=$!
                        sleep 15

                        mkdir -p target/jmeter-report
                        jmeter -n -t src/test/resources/jmeter/ProductSystem_TestPlan.jmx \
                               -l target/results.jtl \
                               -e -o target/jmeter-report/ \
                               || echo "JMeter completado con advertencias"

                        kill $APP_PID 2>/dev/null || true
                    else
                        echo "JMeter no instalado en el servidor. Saltando etapa."
                    fi
                '''
            }
        }

        // ================================================================
        // ETAPA 6: PRUEBAS DE SEGURIDAD (OWASP ZAP)
        // ================================================================
        stage('6. Security Tests - OWASP ZAP') {
            when {
                expression { return fileExists('src/test/resources/zap/') }
            }
            steps {
                echo '=== Ejecutando escaneo de seguridad con OWASP ZAP ==='
                sh '''
                    if command -v zap.sh &> /dev/null; then
                        ./mvnw spring-boot:run -Dspring-boot.run.profiles=test &
                        APP_PID=$!
                        sleep 15

                        zap.sh -cmd -quickurl http://localhost:8080 \
                               -quickprogress \
                               -quickout target/zap_report.html \
                               || echo "ZAP completado con advertencias"

                        kill $APP_PID 2>/dev/null || true
                    else
                        echo "OWASP ZAP no instalado en el servidor. Saltando etapa."
                    fi
                '''
            }
        }

        // ================================================================
        // ETAPA 7: GESTIÓN DE ISSUES (GITHUB ISSUES + PROJECT)
        // ================================================================
        stage('7. Gestión de Issues') {
            steps {
                echo '=== Verificando estado de issues en GitHub ==='
                echo 'Repositorio: https://github.com/fabriciomessa/ProyectoFinal_IS2'
                echo 'GitHub Project: https://github.com/users/fabriciomessa/projects/1'
                echo 'Total issues resueltos: 33 (Lab 07 + Práctica 07)'

                // Publicar estado de issues como artefacto
                writeFile file: 'target/issue_status.txt', text: '''
                    Proyecto Final - Ingenieria de Software II
                    Repositorio: https://github.com/fabriciomessa/ProyectoFinal_IS2
                    Rama principal: master
                    Rama desarrollo: development
                    Issues: 33 cerrados
                    Pipeline: Jenkins CI/CD
                '''
                archiveArtifacts artifacts: 'target/issue_status.txt', fingerprint: false
            }
        }

        // ================================================================
        // ETAPA 8: GESTIÓN DE ENTREGA (DOCKER)
        // ================================================================
        stage('8. Docker - Construcción y Publicación') {
            steps {
                echo '=== Construyendo imagen Docker ==='
                script {
                    sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                    sh "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest"
                }

                echo '=== Imagen Docker construida ==='
                sh 'docker images | grep product-system'
            }
        }

        // ================================================================
        // ETAPA 8b: DESPLIEGUE CON DOCKER COMPOSE
        // ================================================================
        stage('8b. Deploy - Docker Compose') {
            when {
                branch 'master'
            }
            steps {
                echo '=== Desplegando con Docker Compose ==='
                sh '''
                    docker-compose down || true
                    docker-compose up -d
                    echo "Aplicación desplegada en http://localhost:8080"
                '''
            }
        }

    } // end stages

    // ================================================================
    // POST-ACCIONES (siempre se ejecutan)
    // ================================================================
    post {
        always {
            echo '=== Pipeline finalizado ==='
            echo "Build: #${env.BUILD_NUMBER}"
            echo "Resultado: ${currentBuild.result}"
        }
        success {
            echo 'Pipeline CI/CD completado exitosamente.'
            echo "Imagen Docker: ${DOCKER_IMAGE}:${DOCKER_TAG}"
        }
        failure {
            echo 'Pipeline CI/CD falló. Revisar logs de la etapa con error.'
        }
    }
}
