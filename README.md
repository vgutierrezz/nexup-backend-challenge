# Nexup Backend Challenge - Solución 🚀

Esta es mi resolución para el desafío técnico de Nexup, enfocada en **Clean Architecture** y **DDD**.

## 🏗️ Arquitectura

El proyecto sigue una estructura de capas para mantener el dominio aislado de la infraestructura.

### Estructura de carpetas:
- **`core/domain/models`**: Entidades con lógica propia (`Supermarket`, `Sale`, `SupermarketChain`).
- **`core/domain/repository`**: Interfaces que definen el contrato de persistencia.
- **`core/service`**: Casos de Uso (Use Cases) que orquestan la lógica de negocio.
- **`infraestructure/persistence`**: Implementación en memoria con **Data Seeding** inicial.
- **`infraestructure/delivery`**: Manejo global de excepciones.



## 🛠️ Decisiones Técnicas
- **Kotlin Idiomático**: Uso de `sumOf`, `flatMap`, `groupBy` y `joinToString` para procesamiento de datos eficiente.
- **Single Responsibility**: Cada reporte e interacción tiene su propio Use Case.
- **KDoc**: Documentación técnica integrada directamente en los métodos principales.
- **Data Seeding**: El repositorio inicia con una cadena y locales precargados para facilitar las pruebas.

## 📋 Relaciones de Clases del Dominio

```
SupermarketChain (1) ──┐
                       │
                       ├─→ Supermarket (1..*)
                            ├─→ SupermarketProduct (1..*)
                            │    └─→ Product (referencia por ID)
                            │
                            ├─→ Sale (1..*)
                            │    └─→ Product (referencia por productId)
                            │
                            └─→ OpenHours (0..1) [Value Object]
```

### Descripciones de Clases

- **`Product`**: Catálogo central de productos (id, name, price)
- **`SupermarketProduct`**: Vinculación entre Product y Supermarket con control de stock
- **`Sale`**: Registro inmutable de transacciones (qué se vendió, cuándo, a qué precio)
- **`Supermarket`** (Raíz de Agregado): Gestiona productos y ventas del local
- **`OpenHours`** (Value Object): Encapsula lógica de horarios de atención
- **`SupermarketChain`**: Agrupa múltiples supermercados

### Patrones Aplicados
- **Agregados**: `Supermarket` es raíz de agregado, contiene `SupermarketProduct`, `Sale` y `OpenHours`
- **Value Objects**: `OpenHours` no tiene identidad propia, se embebe en `Supermarket`
- **Referencias por ID**: `Sale` y `SupermarketProduct` referencian `Product` por ID, no por objeto completo

## 📊 Casos de Uso Principales

### Por Supermercado Individual

| Caso de Uso | Descripción | Entrada | Salida |
|-------------|-------------|---------|--------|
| **`RegisterSaleUseCase`** | Registra venta y descuenta stock | `productId: Long`<br>`quantity: Int` | `BigDecimal` (Total venta) |
| **`GetProductRevenueUseCase`** | Ingresos totales de un producto en el local | `productId: Long` | `BigDecimal` |
| **`GetProductSoldQuantityUseCase`** | Cantidad total vendida de un producto | `productId: Long` | `Int` |
| **`GetTotalRevenueUseCase`** | Ingresos totales acumulados del local | _(ninguna)_ | `BigDecimal` |

### Por Cadena de Supermercados

| Caso de Uso | Descripción | Entrada | Salida |
|-------------|-------------|---------|--------|
| **`GetChainTotalRevenueUseCase`** | Ingresos totales de toda la cadena | _(ninguna)_ | `BigDecimal` |
| **`GetSupermarketHighestRevenueUseCase`** | Supermercado con mayores ingresos | _(ninguna)_ | `String` (Formateado) |
| **`GetTop5ProductsUseCase`** | Top 5 productos más vendidos en la cadena | `topN: Int` (opcional) | `String` (Formateado) |

## ⚠️ Excepciones Personalizadas

El proyecto define excepciones específicas del dominio para manejar errores de negocio de manera clara y controlada.

### Excepciones Disponibles

 Excepción | Ubicación | Descripción | Cuándo se lanza |
|-----------|-----------|-------------|-----------------|
| **`DomainException`** | `domain/exception` | Excepción base del dominio | Base para todas las excepciones de negocio |
| **`InsufficientStockException`** | `domain/exception` | Stock insuficiente para completar la venta | Cuando `quantity > stock disponible` |
| **`InvalidNameException`** | `domain/exception` | Nombre inválido (vacío o nulo) | En la creación de entidades con nombre |
| **`InvalidOpenDaysException`** | `domain/exception` | Días de apertura inválidos | En `OpenHours` con días vacíos |
| **`InvalidOpenTimeRangeException`** | `domain/exception` | Rango de horas inválido | Cuando hora final ≤ hora inicial en `OpenHours` |
| **`InvalidPriceException`** | `domain/exception` | Precio inválido (negativo o cero) | En la creación de `Product` |
| **`InvalidProductNameException`** | `domain/exception` | Nombre de producto inválido | En la creación de `Product` |
| **`InvalidQuantityException`** | `domain/exception` | Cantidad inválida (≤ 0) | En la creación de `Sale` |
| **`ProductAlreadyExistsException`** | `domain/exception` | Producto ya existe en el supermercado | Al intentar agregar un producto duplicado |
| **`ProductNotFoundException`** | `domain/exception` | Producto no encontrado | Cuando se intenta vender un producto inexistente |
| **`SupermarketNotFoundException`** | `infrastructure/exception` | Supermercado no encontrado en la cadena | Cuando se intenta acceder a un supermercado inexistente |




## 🧪 Testing

He dividido la suite de pruebas para asegurar la calidad en distintos niveles:

- **Tests Unitarios**: Validación de lógica de negocio y excepciones (ej: `InsufficientStockException`).
- **Tests de Integración**: Flujos completos desde el Use Case hasta el repositorio en memoria.

### Cómo ejecutar los tests

**Ejecutar todos los tests:**
```bash
mvn test
```

**Ejecutar tests específicos:**
```bash
# Tests unitarios de Product
mvn test -Dtest=ProductTest

# Tests unitarios de Supermarket
mvn test -Dtest=SupermarketTest

# Tests de integración de casos de uso
mvn test -Dtest=SupermarketBusinessRulesIntegrationTest

# Tests de integración de cadena
mvn test -Dtest=SupermarketChainIntegrationTest
```

### Tests Disponibles

#### Tests Unitarios (`src/test/unit/`)

1. **`ProductTest`** - Entidad Product
   - Creación con datos válidos
   - Validación de nombre (no vacío)
   - Validación de precio (no negativo)

2. **`OpenHoursTest`** - Value Object OpenHours
   - Creación con datos válidos
   - Validación de días (no vacío)
   - Validación de rango de horas
   - Método `isOpen()` con casos válidos e inválidos

3. **`SaleTest`** - Entidad Sale
   - Creación con datos válidos
   - Cálculo de `totalPrice` (unitPrice * quantity)
   - Validación de cantidad (> 0)

4. **`SupermarketProductTest`** - Control de Stock
   - Aumento y disminución de stock
   - Validación de stock disponible
   - Excepciones de stock insuficiente

5. **`SupermarketTest`** - Raíz de Agregado
   - Creación con datos válidos
   - Gestión de productos
   - Registro de ventas
   - Consultas de horarios

6. **`SupermarketChainTest`** - Entidad SupermarketChain
   - Creación con datos válidos
   - Validación de nombre

#### Tests de Integración (`src/test/integration/`)

1. **`SupermarketBusinessRulesIntegrationTest`** - Casos de uso por supermercado
   - `ProductManagementTests`: Agregar productos, validaciones
   - `SalesTrackingTests`: Registrar ventas, historial
   - `StockLimitTests`: Control de stock
   - `RevenueCalculationTests`: Cálculo de ingresos
   - `OpenHoursIntegrationTests`: Horarios de atención
   - `ComplexBusinessScenariosTests`: Escenarios complejos

2. **`SupermarketChainIntegrationTest`** - Casos de uso por cadena
   - `GetChainTotalRevenueUseCaseTests`: Ingresos totales de la cadena
   - `GetSupermarketHighestRevenueUseCaseTests`: Supermercado con mayor ingreso
   - `GetTop5ProductsUseCaseTests`: Top 5 productos más vendidos
   - `CrossSupermarketOperationsTests`: Operaciones entre supermercados
   - `ChainAnalyticsTests`: Análisis de cadena
