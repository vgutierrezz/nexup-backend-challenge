# Nexup Backend Challenge - Solución

Esta es mi resolución para el desafío técnico de Nexup.

## 🏗️ Arquitectura

El proyecto está organizado alrededor del dominio y una implementación de persistencia en memoria.  
Por el momento no hay una API HTTP expuesta.

- `core/domain/models`: entidades y lógica de negocio (`Supermarket`, `Product`, `Sale`, `OpenHours`, `SupermarketChain`, `SupermarketProduct`).
- `core/domain/exception`: excepciones de dominio para validar invariantes y reglas.
- `core/domain/repository`: contrato de repositorio (`SupermarketRepository`).
- `infraestructure/persistence`: implementación en memoria (`SuperMarketRepositoryImp`).
- `infraestructure/exception`: excepción de infraestructura (`SupermarketNotFoundException`).
- `infraestructure/delivery`: `GlobalExceptionHandler` (presente, sin capa externa activa en esta versión).
- `src/Main.kt`: punto de entrada con ejemplo de ejecución.

## 🛠️ Decisiones Técnicas

- **Kotlin puro** para enfocarse en dominio sin sobrecarga de framework.
- **IDs `Long`** para mantener consistencia en entidades y referencias.
- **Validaciones dentro del dominio** (invariantes en constructores y métodos).
- **Persistencia en memoria** adecuada para el alcance del challenge.

## 📁 Estructura principal del proyecto

- `src/Main.kt`
- `src/main/kotlin/core/domain/models/`: Contiene las entidades de negocio (Supermarket, Product, Sale, OpenHours) y las interfaces de los repositorios.
- `src/main/kotlin/core/domain/exception/`: Excepciones de dominio para validar reglas de negocio (ej, `InvalidSaleException`, `InvalidStockException`).
- `src/main/kotlin/core/domain/repository/`: Contiene la interfaz `SupermarketRepository` que define las operaciones de persistencia necesarias para el dominio.
- `src/main/kotlin/core/service/`: Casos de uso y lógica de dominio
- `src/main/kotlin/infraestructure/persistence/`: Implementaciones en memoria para pruebas.
- `src/main/kotlin/infraestructure/exception/`: Excepciones específicas de infraestructura (SupermarketNotFoundException).
- `src/main/kotlin/infraestructure/delivery/`: Manejo de excepciones global (presente, sin capa externa activa).
- `test/ChallengeTests.kt`
- `docs`: Documentación visual y diagramas de clases.

## 📊 Documentación visual

- `docs/diagram.puml`: diagrama de clases del dominio actualizado.
- `docs/classDiagram.png`: versión en imagen del diagrama.

## Alcance funcional actual

La lógica principal se centra en:

- gestión de supermercados y cadenas;
- gestión de productos por supermercado con control de stock (`SupermarketProduct`);
- registro de ventas (`Sale`) con validaciones de cantidad y precio;
- validaciones de horarios de apertura (`OpenHours`);
- manejo de errores mediante excepciones de dominio específicas.

## ✅ Notas finales

- No se usan DTOs ni mappers en la implementación actual, porque no hay una capa externa.
- La persistencia es en memoria y está orientada al challenge.
