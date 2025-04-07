# Hexagonal Users

Este proyecto es una implementación de una arquitectura hexagonal (también conocida como "Ports and Adapters") para un sistema de gestión de usuarios. Utiliza Java con Spring Boot, JPA, Lombok, y otros componentes para implementar microservicios que gestionan usuarios y sus teléfonos asociados.

## Descripción

El objetivo del proyecto es proporcionar una API RESTful que permita realizar operaciones CRUD sobre entidades de `User` y `Phone` utilizando una arquitectura orientada al dominio (DDD). La solución está diseñada para ser fácilmente extensible y mantiene una separación clara entre la lógica de negocio y los detalles de infraestructura.

## Tecnologías

- **Java 17**
- **Spring Boot**
- **Spring Data JPA**
- **Lombok**
- **Swagger** (para la documentación de la API)
- **JUnit y Mockito** (para pruebas unitarias)

## Estructura del Proyecto

El proyecto está estructurado de la siguiente manera:

- **src/main/java**
  - **com.example.hexagonalusers**
    - **adapters**: Contiene las implementaciones de los adaptadores (como repositorios y controladores).
    - **domain**: Contiene las entidades y los casos de uso relacionados con la lógica de negocio.
    - **application**: Lógica que coordina los adaptadores y los casos de uso.
    - **config**: Configuraciones de Spring y otras configuraciones necesarias para el funcionamiento del sistema.

## Requisitos

- **Java 17** o superior
- **Maven** o **Gradle** para la gestión de dependencias (el proyecto usa Maven por defecto).
- **Base de datos**: Utiliza H2 como base de datos en memoria por defecto, pero se puede configurar para usar otras bases de datos como MySQL o PostgreSQL.

## Instalación

### 1. Clonar el repositorio

   ```bash
   git clone https://github.com/tuttodev27/hexagonal-users.git

### 2. Accede al directorio donde se encuentra el proyecto

   cd hexagonal-users

### 3. Instalar Dependencias

    mvn clean install
    


