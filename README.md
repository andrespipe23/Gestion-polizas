# Sistema de Gestión de Pólizas de Arrendamiento

API REST desarrollada con **Java 21 y Spring Boot** para administrar pólizas de arrendamiento individuales y colectivas, gestionar sus riesgos y controlar operaciones de renovación y cancelación.

## 1. Tecnologías utilizadas

- Java 21
- Spring Boot 4.1.2
- Maven
- Spring Web
- Spring Data JPA
- Hibernate
- H2 Database
- Jakarta Validation
- JUnit 5
- Mockito
- RestClient

## 2. Arquitectura del proyecto

El proyecto utiliza una arquitectura por capas para separar las responsabilidades:

```text
src/
├── main/
│   ├── java/com/prueba/gestion_polizas/
│   │   ├── config/
│   │   │   ├── ApiKeyFilter.java
│   │   │   └── DataInitializer.java
│   │   │   └── RestClientConfig.java
│   │   ├── controller/
│   │   │   ├── PolizaController.java
│   │   │   ├── RiesgoController.java
│   │   │   └── CoreMockController.java
│   │   ├── dto/
│   │   ├── exception/
│   │   ├── model/
│   │   ├── repository/
│   │   └── service/
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/prueba/gestion_polizas/
```

### Responsabilidades

- **Controller:** recibe las solicitudes HTTP y devuelve las respuestas.
- **Service:** contiene las reglas de negocio.
- **Repository:** gestiona el acceso a la base de datos.
- **Model:** representa las entidades del sistema.
- **DTO:** define los objetos de entrada y salida de la API.
- **Config:** contiene configuraciones y mecanismos de seguridad.
- **Exception:** centraliza el manejo de errores.

## 3. Reglas de negocio implementadas

1. Una póliza puede ser individual o colectiva.
2. Una póliza individual puede tener máximo un riesgo.
3. Una póliza colectiva puede tener varios riesgos.
4. La prima se calcula multiplicando el canon mensual por la vigencia inicial en meses.
5. La renovación aplica el porcentaje de IPC sobre el canon mensual.
6. La nueva prima se calcula con el canon actualizado y la vigencia inicial.
7. Una póliza cancelada no puede renovarse.
8. Al cancelar una póliza, todos sus riesgos también se cancelan.
9. No se pueden agregar riesgos a pólizas canceladas.
10. Un riesgo cancelado no puede cancelarse nuevamente.
11. Se validan los datos obligatorios y los valores numéricos mediante Jakarta Validation.

## 4. Configuración e instalación

### Requisitos previos

- JDK 21.
- Maven Wrapper incluido en el proyecto.
- Visual Studio Code o un IDE compatible con Java.

### Ejecutar el proyecto

Clonar o descargar el repositorio y abrir la carpeta del proyecto.

Ejecutar:

```bash
.\mvnw.cmd spring-boot:run
```

La aplicación estará disponible en:

```text
http://localhost:8080
```

### Configuración de base de datos

Se utiliza H2 en memoria para facilitar la ejecución de la prueba técnica.

Configuración en `application.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:polizasdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

### Consola H2

Acceder desde el navegador:

```text
http://localhost:8080/h2-console
```

Datos de conexión:

| Parámetro | Valor |
|---|---|
| JDBC URL | jdbc:h2:mem:polizasdb |
| User Name | sa |
| Password | Vacío |

**Nota:** al utilizar una base de datos en memoria, los datos se pierden al detener la aplicación.

## 5. Seguridad

La API utiliza autenticación mediante API Key.

Todas las solicitudes deben incluir el siguiente encabezado HTTP:

```http
x-api-key: 123456
```

Las solicitudes sin una clave válida reciben:

```http
401 Unauthorized
```

## 6. Endpoints disponibles

Base URL:

```text
http://localhost:8080
```

Todos los endpoints requieren el encabezado `x-api-key`.

### 6.1. Consultar pólizas

**GET /polizas**

Permite consultar las pólizas registradas y aplicar filtros opcionales.

Parámetros:

| Parámetro | Descripción |
|---|---|
| tipo | INDIVIDUAL o COLECTIVA |
| estado | ACTIVA, RENOVADA o CANCELADA |

Ejemplo:

```http
GET /polizas?tipo=INDIVIDUAL&estado=ACTIVA
```

### 6.2. Consultar riesgos de una póliza

**GET /polizas/{id}/riesgos**

Ejemplo:

```http
GET /polizas/1/riesgos
```

Devuelve los riesgos asociados a la póliza indicada.

### 6.3. Crear una póliza

**POST /polizas**

Body:

```json
{
  "tipo": "INDIVIDUAL",
  "fechaInicio": "2026-11-12",
  "fechaFin": "2027-11-11",
  "vigenciaInicialMeses": 12,
  "canonMensual": 1500000
}
```

La aplicación calcula automáticamente la prima y establece el estado inicial de la póliza como ACTIVA.

### 6.4. Renovar una póliza

**POST /polizas/{id}/renovar**

Body:

```json
{
  "ipcPorcentaje": 5
}
```

La operación actualiza:

- Canon mensual.
- Prima.
- Fecha de inicio.
- Fecha de finalización.
- Porcentaje de IPC aplicado.
- Estado de la póliza.

No se permite renovar pólizas canceladas.

### 6.5. Cancelar una póliza

**POST /polizas/{id}/cancelar**

Cancela la póliza y todos los riesgos asociados.

### 6.6. Agregar un riesgo

**POST /polizas/{id}/riesgos**

Body:

```json
{
  "inmueble": "Apartamento 408",
  "arrendatario": "Carlos Perez",
  "arrendador": "Maria Gonzalez"
}
```

La operación valida que la póliza exista, que no esté cancelada y que se cumplan las restricciones según su tipo.

### 6.7. Cancelar un riesgo

**POST /riesgos/{id}/cancelar**

Cancela individualmente un riesgo asociado a una póliza.

## 7. Integración simulada con CORE

Se implementó un endpoint para simular la recepción de eventos por parte de un sistema CORE externo.

**POST /core-mock/evento**

Body:

```json
{
  "evento": "ACTUALIZACION",
  "polizaId": 555
}
```

La aplicación registra en los logs la recepción del evento y devuelve una respuesta de confirmación.

La comunicación se estructura mediante:

- `CoreMockController`: recibe el evento.
- `CoreIntegrationService`: centraliza el envío de solicitudes HTTP.
- `RestClientConfig`: configura el cliente HTTP.

Esta implementación representa una simulación de integración. No existe conexión real con WebLogic ni con un CORE externo.

## 8. Manejo de errores

Se utiliza un manejador global de excepciones para devolver respuestas HTTP controladas.

Principales códigos:

| Código | Descripción |
|---|---|
| 200 | Operación exitosa |
| 201 | Recurso creado |
| 400 | Error de validación o regla de negocio |
| 401 | API Key inválida o ausente |
| 404 | Recurso no encontrado |
| 500 | Error interno del servidor |

## 9. Ejecutar pruebas automatizadas

El proyecto utiliza JUnit 5 y Mockito para validar la lógica de negocio.

Ejecutar todas las pruebas:

```bash
.\mvnw.cmd clean test
```

Ejecutar una prueba específica:

```bash
.\mvnw.cmd -Dtest=PolizaServiceTest test
```

Las pruebas unitarias permiten verificar las reglas del servicio sin depender de una conexión real a la base de datos.

## 10. Consideraciones

- La base de datos H2 está configurada en memoria y se utiliza para facilitar las pruebas.
- La API Key `123456` es un valor de demostración y debe reemplazarse por una configuración segura en ambientes productivos.
- La integración con CORE es simulada y utiliza HTTP mediante RestClient.
- La comunicación real con WebLogic requeriría la configuración del servicio externo correspondiente.

## 11. Ejecución rápida

```bash
# Ejecutar pruebas
.\mvnw.cmd clean test

# Iniciar aplicación
.\mvnw.cmd spring-boot:run
```

---

**Proyecto:** Sistema de Gestión de Pólizas de Arrendamiento  
**Lenguaje:** Java 21  
**Framework:** Spring Boot  
**Base de datos:** H2