# 🔗 Shorten-BackEnd: Microservicio Acortador de URLs 🚀

Este proyecto implementa un backend para un servicio acortador de URLs utilizando una arquitectura de microservicios con Spring Boot y Spring Cloud.

## 📑 Índice

* [🚀 Resumen General](#1-resumen-general)
* [🛠️ Tecnologías Utilizadas](#-tecnologías-utilizadas)
* [🏛️ Arquitectura de Microservicios](#-arquitectura-de-microservicios)
* [➡️ Flujo de Datos Típico](#4-flujo-de-datos-típico)
* [🧩 Componentes Detallados](#5-🧩-componentes-detallados)
    * [🐳 docker-compose.yml](#51-docker-composeyml)
    * [⚙️ configuration (Config Server)](#52-configuration-config-server)
    * [🗺️ eureka-api (Eureka Server)](#53-eureka-api-eureka-server)
    * [🚪 gateway-api (API Gateway)](#54-gateway-api-api-gateway)
    * [✍️ write-api](#55-write-api)
    * [📖 read-api](#56-read-api)
    * [💾 db (MySQL)](#57-db-mysql)
    * [⚡ cache (Redis)](#58-cache-redis)
    * [📄 mysql-init/schema.sql](#59-mysql-initschemasql)
* [🔧 Configuración y Ejecución](#6-🔧-configuración-y-ejecución)


---
## 1. 🚀 Resumen General

`Shorten-BackEnd` es un sistema de microservicios diseñado para actuar como un acortador de URLs. Permite a los usuarios enviar una URL larga y recibir una URL corta única 🔗. Posteriormente, al acceder a la URL corta, el sistema devuelve la URL original.

---
## 2. 🛠️ Tecnologías Utilizadas

* ☕ **Lenguaje**: Java 21
* 🌱 **Frameworks**:
    * Spring Boot 3.4.4
    * Spring Cloud 2024.0.1 (Config Server, Eureka, Gateway)
    * Spring Data JPA
    * Spring Data Redis
    * Spring Boot Actuator
* 💾 **Base de Datos**: MySQL 8.0
* ⚡ **Caché / Limitación de Tasa**: Redis
* 🐳 **Contenerización**: Docker, Docker Compose
* 🏗️ **Construcción**: Apache Maven
* 📝 **Otros**: Lombok, Jasypt (para encriptación de propiedades 🔒)

---
## 3. 🏛️ Arquitectura de Microservicios

El sistema sigue una arquitectura de microservicios, separando las responsabilidades en componentes independientes:

1.  **Config Server (`⚙️ configuration`)**: Proporciona configuración centralizada a todos los demás servicios usando Spring Cloud Config Server. Lee archivos `.yml` del classpath y maneja propiedades encriptadas con Jasypt 🔒.
2.  **Eureka Server (`🗺️ eureka-api`)**: Servidor de descubrimiento (Spring Cloud Eureka). Permite que los servicios se registren y encuentren dinámicamente 📍.
3.  **API Gateway (`🚪 gateway-api`)**: Punto de entrada único (Spring Cloud Gateway). Enruta solicitudes, aplica limitación de tasa (Rate Limiting 🚦) con Redis, y gestiona CORS.
4.  **Write API (`✍️ write-api`)**: Responsable de crear nuevos enlaces cortos. Genera IDs, los persiste en MySQL vía JPA.
5.  **Read API (`📖 read-api`)**: Responsable de resolver enlaces cortos. Consulta Redis (caché ⚡) primero, y si no, MySQL (JPA).
6.  **Database (`💾 db`)**: Contenedor MySQL que almacena los mapeos de URL.
7.  **Cache (`⚡ cache`)**: Contenedor Redis usado para caché de lectura y limitación de tasa.

---
## 4. ➡️ Flujo de Datos Típico

* **Acortar una URL: ✍️**
    1.  👤 Cliente envía `POST` a `🚪 gateway-api` (`/write/shorten` con `longUrl`).
    2.  `🚪 gateway-api` aplica Rate Limiting 🚦 y enruta a `✍️ write-api`.
    3.  `✍️ write-api` genera un `shortId`, lo guarda en `💾 db` (MySQL).
    4.  `✍️ write-api` devuelve `shortId` al gateway y este al cliente.
* **Resolver una URL corta: 📖**
    1.  👤 Cliente envía `GET` a `🚪 gateway-api` (`/read/{shortId}`).
    2.  `🚪 gateway-api` aplica Rate Limiting 🚦 y enruta a `📖 read-api`.
    3.  `📖 read-api` busca `shortId` en `⚡ cache` (Redis).
    4.  **Si está en caché:** Devuelve `longUrl` ✅.
    5.  **Si no está en caché:** Consulta `💾 db` (MySQL) ❓.
    6.  Si la encuentra, la guarda en `⚡ cache` y devuelve `longUrl` ✅.
    7.  Si no la encuentra, devuelve error 404 ❌.
    8.  `📖 read-api` devuelve la respuesta al gateway y este al cliente.
---
## 5. 🧩 Componentes Detallados

#### 5.1. 🐳 `docker-compose.yml`

* Orquesta el inicio y la red (`shorten-net` 🕸️) de todos los servicios.
* Usa `depends_on` y `healthcheck` para un orden de inicio correcto 👍.
* Monta `mysql-init/schema.sql` para inicializar la base de datos 🚀.
* Persiste datos de MySQL en el volumen `mysql_data` 💾.
* Expone el puerto `8083` del Gateway 🔌.
* Define límites de recursos (CPU/Memoria) ⚖️.
* Requiere un archivo `.env` para variables sensibles (ej. `MYSQL_PASSWORD` 🔑).

#### 5.2. ⚙️ `configuration` (Config Server)

* **Puerto**: 8888 🔌
* Sirve configuraciones desde `src/main/resources/configurations/` 📄.
* Usa perfil `native` y Jasypt 🔒.

#### 5.3. 🗺️ `eureka-api` (Eureka Server)

* **Puerto**: 8080 (interno, no expuesto por defecto en docker-compose) 🔌
* Servidor de descubrimiento 📍. No se registra a sí mismo.

#### 5.4. 🚪 `gateway-api` (API Gateway)

* **Puerto**: 8083 (expuesto) 🔌
* Enruta `/read/**` a `📖 read-api` y `/write/**` a `✍️ write-api` 🛣️.
* Implementa Rate Limiting 🚦 (basado en IP, usando Redis ⚡) con diferentes límites por ruta.
* Configura CORS para permitir orígenes específicos (ej. `http://localhost:3000`).

#### 5.5. ✍️ `write-api`

* **Puerto**: 8082 🔌
* **Endpoint**: `POST /write/shorten` 📍
* Genera IDs cortos (Base62, 5 chars) con `SecureRandom`, valida URLs ✅, y persiste en MySQL 💾.
* Manejo de excepciones customizado (`GlobalExceptionHandler`) ⚠️.

#### 5.6. 📖 `read-api`

* **Puerto**: 8081 🔌
* **Endpoint**: `GET /read/{shortId}` 📍
* Implementa caché de lectura con Redis (`@Cacheable`) ⚡. Consulta MySQL si falla la caché 💾.
* Manejo de excepciones customizado (`GlobalExceptionHandler`) ⚠️.

#### 5.7. 💾 `db` (MySQL)

* **Imagen**: `mysql:8.0` 📦
* Almacena la tabla `url` en la base de datos `shortener`.
* Inicializada por `mysql-init/schema.sql` 🚀.

#### 5.8. ⚡ `cache` (Redis)

* **Imagen**: `redis:7.4.2` 📦
* Usado por `📖 read-api` (caché) y `🚪 gateway-api` (Rate Limiting 🚦).

#### 5.9. 📄 `mysql-init/schema.sql`

* Script SQL para crear la tabla `url` con las columnas:
    * `short_id` VARCHAR(10) PRIMARY KEY
    * `long_url` TEXT NOT NULL
    * `created_date` DATE NOT NULL
---
## 6. 🔧 Configuración y Ejecución

1.  **✅ Prerrequisitos**:
    * Docker & Docker Compose 🐳
    * JDK 21 ☕
    * Apache Maven 🏗️
    * Git

2.  **📥 Clonar el Repositorio**:
    ```bash
    git clone <url-del-repositorio>
    cd Shorten-BackEnd
    ```

3.  **🔑 Archivo `.env`**:
    Crea un archivo llamado `.env` en la raíz del proyecto (`Shorten-BackEnd/`) con el siguiente contenido, reemplazando `tu_contraseña_segura` por una contraseña robusta:
    ```dotenv
    MYSQL_PASSWORD=tu_contraseña_segura
    # Puedes añadir aquí la contraseña para Jasypt si la configuras 🔒
    # JASYPT_ENCRYPTOR_PASSWORD=tu_otra_contraseña
    ```
    *(Nota: La configuración de Jasypt requiere definir la contraseña maestra, ya sea vía variable de entorno, argumento de JVM, etc. Si no se define, buscará `JASYPT_ENCRYPTOR_PASSWORD`)*.

4.  **🚀 Construir y Ejecutar con Docker Compose**:
    Desde la raíz del proyecto (`Shorten-BackEnd/`), ejecuta:
    ```bash
    docker-compose up --build -d
    ```
    * `--build`: Construye (o reconstruye) las imágenes Docker si es necesario 🏗️.
    * `-d`: Ejecuta los contenedores en segundo plano (detached mode) 💨.

5.  **🌐 Acceso al Servicio**:
    * El **API Gateway** 🚪 estará disponible en: `http://localhost:8083`

6.  **💻 Uso de la API**:
    * **Acortar una URL** ✍️:
        ```bash
        curl -X POST "http://localhost:8083/write/shorten?longUrl=[https://www.ejemplo.com/una/url/muy/larga](https://www.ejemplo.com/una/url/muy/larga)"
        ```
      Esto devolverá el `shortId` generado (ej. `aBcDe`).
    * **Resolver una URL corta** 📖:
        ```bash
        curl -L "http://localhost:8083/read/{shortId}"
        ```
      Reemplaza `{shortId}` con el ID obtenido. El comando `curl -L` seguirá la redirección si el servicio la implementara, o en este caso, mostrará la URL larga devuelta. *(Nota: Este backend actualmente devuelve la URL larga en el cuerpo de la respuesta, no una redirección HTTP 3xx)*.

7.  **🛑 Detener los Servicios**:
    ```bash
    docker-compose down
    ```
    Para eliminar también los volúmenes (¡⚠️ CUIDADO, borra los datos de MySQL!):
    ```bash
    docker-compose down -v
    ```
    
z
