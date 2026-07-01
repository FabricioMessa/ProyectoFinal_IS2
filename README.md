# Product System

**Proyecto Final - Ingeniería de Software II**
**Universidad Católica San Pablo**

---

## Índice

1. [Equipo de Trabajo](#1-equipo-de-trabajo)
2. [Propósito del Proyecto](#2-propósito-del-proyecto)
3. [Funcionalidades](#3-funcionalidades)
4. [Modelo de Dominio](#4-modelo-de-dominio)
5. [Arquitectura](#5-arquitectura)
6. [Módulos y API REST](#6-módulos-y-api-rest)
7. [Pipeline CI/CD](#7-pipeline-cicd)
8. [Construcción y Ejecución](#8-construcción-y-ejecución)

---

## 1. Equipo de Trabajo

| Integrante | Rol |
|------------|-----|
| Fabricio Messa (Miembro A) | CI/CD Pipeline, Docker, SonarQube, Documentación |
| Paolo Mostajo | Pruebas Funcionales, Performance, Seguridad |
| Samir Carrera | API REST, Swagger, Frontend, Pruebas Unitarias |

---

## 2. Propósito del Proyecto

Product System es una aplicación web de gestión de productos e inventario que permite a usuarios registrados administrar su catálogo de productos con control de acceso basado en roles. El sistema implementa un pipeline de integración y despliegue continuo (CI/CD) aplicando principios de Clean Code, SOLID, Domain-Driven Design (DDD) y Test-Driven Development (TDD).

---

## 3. Funcionalidades

### Diagrama de Casos de Uso

```
┌──────────────────────────────────────────────┐
│              PRODUCT SYSTEM                   │
│                                               │
│  ┌─────────┐      ┌──────────────────┐       │
│  │  Actor   │      │   Funcionalidades │       │
│  ├─────────┤      ├──────────────────┤       │
│  │ Usuario  │─────>│ Registrarse       │       │
│  │          │─────>│ Iniciar Sesión    │       │
│  │          │─────>│ Cerrar Sesión     │       │
│  │          │─────>│ Listar Productos   │       │
│  │          │─────>│ Agregar Producto   │       │
│  │          │─────>│ Editar Producto    │       │
│  ├─────────┤      ├──────────────────┤       │
│  │ Admin    │─────>│ Eliminar Producto  │       │
│  │          │─────>│ Ver Todos Productos│       │
│  └─────────┘      └──────────────────┘       │
└──────────────────────────────────────────────┘
```

### Funcionalidades por Rol

**ROLE_USER:**
- Registro de cuenta
- Inicio y cierre de sesión
- CRUD de productos propios (crear, leer, actualizar, eliminar)
- Listado de productos con paginación

**ROLE_ADMIN:**
- Todas las funcionalidades de ROLE_USER
- Ver y eliminar productos de cualquier usuario

---

## 4. Modelo de Dominio

### Diagrama de Clases + Módulos

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│      User       │ 1───* │     Product     │ *───1 │    Category     │
│─────────────────│       │─────────────────│       │─────────────────│
│ + id: Long      │       │ + id: Integer   │       │ + id: Integer   │
│ + username: Str │       │ + name: String  │       │ + name: String  │
│ + email: String │       │ + price: BigDec │       └─────────────────┘
│ + password: Str │       │ + quantity: Int │
│ + roles: Set    │       │ + createdAt: Dt │
└────────┬────────┘       │ + updatedAt: Dt │
         │                └─────────────────┘
         │ *
    ┌────┴────┐
    │   Role   │
    │──────────│
    │ + id     │
    │ + name   │
    └─────────┘
```

### Bounded Contexts (Domain-Driven Design)

| Contexto | Entidades | Responsabilidad |
|----------|-----------|-----------------|
| **User** | User, Role | Autenticación, autorización, registro |
| **Product** | Product, ProductDTO | CRUD de productos, inventario |
| **Category** | Category, CategoryDTO | Catálogo de categorías |
| **Shared Kernel** | SecurityConfig, Validators | Infraestructura transversal |

---

## 5. Arquitectura

### Visión General

```
┌──────────────────────────────────────────────────────────┐
│                   FRONTEND                                │
│  ┌──────────────────┐    ┌──────────────────────────┐   │
│  │  Thymeleaf (SSR) │    │  SPA (fetch API REST)    │   │
│  └────────┬─────────┘    └────────────┬─────────────┘   │
│           │                           │                  │
├───────────┼───────────────────────────┼──────────────────┤
│           │         BACKEND            │                  │
│           ▼                           ▼                  │
│  ┌─────────────────────────────────────────────┐        │
│  │           API REST (JSON)                    │        │
│  │  GET/POST/PUT/DELETE /api/products          │        │
│  │  GET /api/categories                        │        │
│  └─────────────────────┬───────────────────────┘        │
│                        │                                 │
│  ┌─────────────────────┴───────────────────────┐        │
│  │     ARQUITECTURA MODULAR (DDD)              │        │
│  │                                              │        │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────────┐ │        │
│  │  │  User    │ │ Product  │ │  Category    │ │        │
│  │  │  Module  │ │  Module  │ │   Module     │ │        │
│  │  │          │ │          │ │              │ │        │
│  │  │ Presen.  │ │ Presen.  │ │  Presen.     │ │        │
│  │  │ Aplic.   │ │ Aplic.   │ │  Aplic.      │ │        │
│  │  │ Dominio  │ │ Dominio  │ │  Dominio     │ │        │
│  │  │ Infra.   │ │ Infra.   │ │  Infra.      │ │        │
│  │  └──────────┘ └──────────┘ └──────────────┘ │        │
│  │                                              │        │
│  │  ┌──────────────────────────────────────┐    │        │
│  │  │        Shared Kernel                 │    │        │
│  │  │  Security | Validation | Exceptions  │    │        │
│  │  └──────────────────────────────────────┘    │        │
│  └──────────────────────────────────────────────┘        │
│                        │                                 │
│  ┌─────────────────────┴───────────────────────┐        │
│  │              MySQL Database                  │        │
│  └──────────────────────────────────────────────┘        │
└──────────────────────────────────────────────────────────┘
```

### Diagrama de Paquetes

```
com.batuhaniskr.product/
│
├── Application.java                     (punto de entrada)
│
├── shared/                              (Shared Kernel)
│   ├── config/SecurityConfig.java
│   ├── exception/MyAccessDeniedHandler.java
│   └── validation/
│
├── user/                                (User Bounded Context)
│   ├── presentation/UserController.java
│   ├── application/UserService.java, UserServiceImpl.java
│   ├── domain/User.java, Role.java, UserRegistrationDto.java
│   └── infrastructure/UserRepository.java
│
├── product/                             (Product Bounded Context)
│   ├── presentation/
│   │   ├── MainController.java          (vistas Thymeleaf)
│   │   └── ProductApiController.java    (REST API)
│   ├── application/
│   │   ├── IProductService.java
│   │   └── ProductService.java
│   ├── domain/Product.java, ProductDTO.java
│   └── infrastructure/ProductRepository.java
│
└── category/                            (Category Bounded Context)
    ├── presentation/CategoryController.java
    ├── application/CategoryService.java
    ├── domain/Category.java, CategoryDTO.java
    └── infrastructure/CategoryRepository.java
```

### Tecnologías Utilizadas

| Capa | Tecnología |
|------|------------|
| **Frontend** | Thymeleaf, HTML5, CSS3, Bootstrap 3, jQuery |
| **Backend** | Java 8, Spring Boot 1.5.7, Spring MVC, Spring Security, Spring Data JPA |
| **ORM** | Hibernate / JPA |
| **Base de Datos** | MySQL 5.7 |
| **Migraciones** | Flyway |
| **Mapeo DTO** | ModelMapper |
| **API Docs** | Swagger / OpenAPI 3 (springdoc-openapi) |
| **Pruebas** | JUnit 4/5, Mockito, AssertJ, Selenium, JMeter, OWASP ZAP |
| **CI/CD** | Jenkins (declarative pipeline), SonarQube, Docker, Docker Compose |
| **Control de Versiones** | Git, GitHub |

---

## 6. Módulos y API REST

### Módulo: Productos (`/api/products`)

Propósito: Gestión del ciclo de vida de productos e inventario.

| Método | URL | Descripción | Autenticación |
|--------|-----|-------------|---------------|
| `GET` | `/api/products?page=1&size=5` | Listar productos (paginado) | Basic Auth |
| `GET` | `/api/products/{id}` | Obtener producto por ID | Basic Auth |
| `POST` | `/api/products` | Crear nuevo producto | Basic Auth |
| `DELETE` | `/api/products/{id}` | Eliminar producto | Admin |

**POST /api/products - Body:**
```json
{
  "name": "iPhone 15",
  "price": 999.99,
  "quantity": 50,
  "category": {
    "id": 1,
    "categoryName": "Electrónica"
  }
}
```

### Módulo: Categorías (`/api/categories`)

Propósito: Catálogo de categorías para clasificación de productos.

| Método | URL | Descripción |
|--------|-----|-------------|
| `GET` | `/api/categories` | Listar todas las categorías |

### Documentación Swagger

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

---

## 7. Pipeline CI/CD

### Diagrama del Pipeline

```
┌─────────────────────────────────────────────────────────────────────┐
│                    JENKINS CI/CD PIPELINE                            │
│                                                                      │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐           │
│  │ 1. Build │─>│2. Sonar  │─>│3. Unit   │─>│4. Funct. │           │
│  │  Maven   │  │  Qube    │  │  Tests   │  │  Tests   │           │
│  │  compile │  │  static  │  │  JUnit   │  │  Selenium│           │
│  │  package │  │  analysis│  │  Mockito │  │  Postman │           │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘           │
│       │                                                             │
│       │        ┌──────────┐  ┌──────────┐  ┌──────────┐           │
│       └───────>│5. Perf.  │─>│6. Secur. │─>│7. Issues │           │
│                │  JMeter  │  │  OWASP   │  │  GitHub  │           │
│                │  load    │  │  ZAP     │  │  Project │           │
│                └──────────┘  └──────────┘  └──────────┘           │
│                                               │                     │
│                                               ▼                     │
│                                        ┌──────────┐                │
│                                        │8. Docker │                │
│                                        │  Build + │                │
│                                        │  Deploy  │                │
│                                        └──────────┘                │
└─────────────────────────────────────────────────────────────────────┘
```

### Etapas del Pipeline

| # | Etapa | Herramienta | Descripción |
|---|-------|-------------|-------------|
| 1 | **Construcción Automática** | Maven | `mvnw clean package` - compila, resuelve dependencias, empaqueta .jar |
| 2 | **Análisis Estático** | SonarQube + SonarScanner | Detección de code smells, bugs y vulnerabilidades |
| 3 | **Pruebas Unitarias** | JUnit + Mockito + AssertJ | 21 pruebas unitarias con mocking de dependencias |
| 4 | **Pruebas Funcionales** | Selenium + Postman/Newman | Flujos de usuario y validación de API REST |
| 5 | **Pruebas de Performance** | JMeter | Pruebas de carga con 50 usuarios virtuales |
| 6 | **Pruebas de Seguridad** | OWASP ZAP | Escaneo automatizado de vulnerabilidades web |
| 7 | **Gestión de Issues** | GitHub Issues + Project | Seguimiento de tareas en tablero Kanban (33 issues cerrados) |
| 8 | **Despliegue** | Docker + Docker Compose | Containerización y despliegue automatizado |

### Archivos del Pipeline

| Archivo | Propósito |
|---------|-----------|
| `Jenkinsfile` | Pipeline declarativo con las 8 etapas |
| `Dockerfile` | Imagen multi-stage (build + runtime) con OpenJDK 8 |
| `docker-compose.yml` | Orquestación de app + MySQL con health checks |

---

## 8. Construcción y Ejecución

### Prerrequisitos

- Java 8 JDK
- Maven Wrapper (incluido en el proyecto: `./mvnw`)
- Docker y Docker Compose
- MySQL (opcional si se usa Docker)

### Compilación

```bash
# Clonar el repositorio
git clone https://github.com/fabriciomessa/ProyectoFinal_IS2.git
cd ProyectoFinal_IS2
git checkout development

# Compilar
./mvnw clean compile
```

### Ejecutar Pruebas

```bash
# Ejecutar todas las pruebas
./mvnw test

# Ejecutar pruebas específicas
./mvnw test -Dtest="UserTest,ProductDTOTest"
```

### Ejecutar la Aplicación

```bash
# Desarrollo (con aplicación.properties local)
./mvnw spring-boot:run

# Producción (con Docker)
docker-compose up -d

# Acceder
open http://localhost:8080
```

### Ejecutar CI/CD con Jenkins

```bash
# Instalar Jenkins (macOS)
brew install jenkins-lts
brew services start jenkins-lts

# Acceder a Jenkins
open http://localhost:8081

# Crear un nuevo Pipeline Job:
#   - Pipeline script from SCM
#   - Git: https://github.com/fabriciomessa/ProyectoFinal_IS2.git
#   - Script Path: Jenkinsfile
```

### Credenciales en Jenkins

Configurar las siguientes credenciales en Jenkins:

| ID | Tipo | Descripción |
|----|------|-------------|
| `sonar-token` | Secret text | Token generado en SonarQube (My Account → Security → Tokens) |

---

## Gestión de Tareas (GitHub Project)

**URL del Board:** https://github.com/users/fabriciomessa/projects/1

### Columnas del Kanban

```
TO-DO → CURRENT ITERATION → IN PROGRESS → FIX VALIDATION → DONE
```

### Issues

- **33 issues cerrados** en total
- **18 issues** del Laboratorio 07 (Refactoring y TDD)
- **15 issues** de la Práctica 07 (Rediseño DDD)
- Etiquetas: `bug`, `code smell`, `vulnerability`, `enhancement`, `redesign`

### Releases

Las releases se generan desde GitHub Releases vinculando los issues cerrados y los commits correspondientes.

---

## Referencias

- Spring Boot: https://spring.io/projects/spring-boot
- Spring Security: https://spring.io/projects/spring-security
- Jenkins: https://www.jenkins.io/
- SonarQube: https://www.sonarsource.com/products/sonarqube/
- Docker: https://www.docker.com/
- Domain-Driven Design: https://learn.microsoft.com/en-us/azure/architecture/microservices/model/domain-analysis
- Refactoring Guru: https://refactoring.guru/refactoring
