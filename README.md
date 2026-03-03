# Nexup Backend Challenge - Solución

Esta es mi resolución para el desafío técnico de Nexup.

## 🏗️ Arquitectura
Se aplicaron principios de Clean Architecture y Domain-Driven Design (DDD) para garantizar el desacoplamiento y la escalabilidad del sistema:

* `core/domain/models`: Contiene las entidades de negocio (Supermarket, Product, Sale, OpenHours) y las interfaces de los repositorios.
* `core/service`: Casos de uso y lógica de dominio (por ejemplo `registerSale`).
* `infraestructure/delivery`: DTOs y mappers para la capa de entrega.
* `infraestructure/persistence`: Implementaciones en memoria para pruebas.


## 🛠️ Decisiones Técnicas

* Kotlin Puro: Se decidió no utilizar Spring Boot para demostrar solidez en el lenguaje base y evitar over-engineering.
* IDs de tipo `Long`: Se utilizaron identificadores numéricos de 64 bits para mantener consistencia con estándares de bases de datos relacionales como PostgreSQL.
* Value Objects: La gestión de horarios (`OpenHours`) se encapsuló en un objeto de valor con su propia lógica de validación.
* DTOs: Se implementaron Data Transfer Objects para el retorno de información, evitando exponer las entidades de dominio directamente.


## 📁 Estructura principal del proyecto

- `src/Main.kt` - punto de entrada (ejemplo)
- `src/main/kotlin/core/domain/models/` - modelos: `Product.kt`, `Sale.kt`, `Supermarket.kt`, `SupermarketChain.kt`, `OpenHours.kt`
- `src/main/kotlin/core/service/` - lógica de negocio (p.ej. `registerSale.kt`)
- `src/main/kotlin/infraestructure/delivery/dtos/` - DTOs: `SaleResponseDTO.kt`, `SupermarketReportDTO.kt`
- `src/main/kotlin/infraestructure/delivery/mapper/` - mappers: `MapperExtensions.kt`
- `test/ChallengeTests.kt` - pruebas de ejemplo
- `docs/diagram.puml` - diagrama de clases en PlantUML


## 📊 Documentación Visual

Se incluyó un archivo `docs/diagram.puml` con el diagrama de clases del sistema. Este documento detalla la relación entre las entidades de dominio, sirviendo como guía para la implementación de la arquitectura. Puedes renderizarlo con PlantUML (extensión de VSCode o plantuml.jar).
Se puede visualizar como imagen en [`docs/diagram.png`](docs/diagram.png).

## ✅ Notas finales
- Este proyecto está preparado para ejecutarse y ser probado sin necesidad de una base de datos: usa estructuras en memoria para las pruebas.
- Para expandirlo, se recomienda añadir un `build.gradle.kts`, configuración de CI y tests adicionales que cubran los casos límite (stock insuficiente, ventas concurrentes, formato de reportes).


---
Actualizado para reflejar la estructura del proyecto en `src/main/kotlin` y los paquetes actuales.
