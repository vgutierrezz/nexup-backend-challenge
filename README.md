# Nexup Backend Challenge - Solución

Esta es mi resolución para el desafío técnico de Nexup.

## 🏗️ Arquitectura

El proyecto está organizado alrededor del **dominio** y una implementación de **persistencia en memoria**. No hay una capa HTTP externa expuesta en esta versión.

### Estructura de carpetas:

- **`core/domain/models`**: Entidades de negocio (`Supermarket`, `Product`, `Sale`, `OpenHours`, `SupermarketChain`, `SupermarketProduct`)
- **`core/domain/exception`**: Excepciones de dominio para validar invariantes y reglas de negocio
- **`core/domain/repository`**: Contratos de repositorios (`SupermarketRepository`, `ProductRepository`)
- **`core/service`**: Casos de uso/Use cases (cada uno específico para un supermercado)
- **`infraestructure/persistence`**: Implementaciones en memoria de los repositorios
- **`infraestructure/exception`**: Excepciones específicas de infraestructura (`SupermarketNotFoundException`)
- **`infraestructure/delivery`**: `GlobalExceptionHandler` para manejo de excepciones (presente pero no activa en versión actual)

## 🛠️ Decisiones Técnicas

- **Kotlin puro** para enfocarse en el dominio sin sobrecarga de frameworks
- **IDs `Long`** para mantener consistencia en entidades y referencias
- **Validaciones dentro del dominio** (invariantes en constructores y métodos)
- **Persistencia en memoria** adecuada para el alcance del challenge
- **Sin DTOs ni mappers** - Los modelos de dominio se usan directamente
- **Casos de uso parametrizados por `supermarketId`** para operaciones específicas de cada local

## 📁 Estructura principal del proyecto

```
src/
├── main/
│   └── kotlin/
│       └── core/
│           ├── domain/
│           │   ├── models/: Entidades de negocio
│           │   ├── exception/: Excepciones de dominio
│           │   └── repository/: Interfaces de persistencia
│           ├── service/supermarket/: Casos de uso por supermercado
│           └── service/supermarketChain/: Casos de uso por cadena
└── infraestructure/
    ├── persistence/: Implementaciones en memoria
    ├── exception/: Excepciones de infraestructura
    └── delivery/: Manejo global de excepciones
```

## 📊 Casos de Uso (Use Cases)

Cada caso de uso está orientado a operaciones específicas de un **supermercado individual** (reciben `supermarketId` en la construcción):

| Caso de Uso | Descripción | Entrada | Salida | Lógica / Validaciones | Excepciones |
|-------------|-------------|---------|--------|----------------------|-------------|
| **`RegisterSaleUserCase`** | Registrar una venta de un producto en el supermercado | `productId: Long`<br>`quantity: Int` | `BigDecimal`<br>(precio total de la venta) | • Verifica que el supermercado exista<br>• Verifica que el producto exista en el local<br>• Valida stock disponible<br>• Actualiza el stock<br>• Registra la venta en el historial<br>• Retorna el monto total | `SupermarketNotFoundException`<br>`ProductNotFoundException`<br>`InsufficientStockException` |
| **`GetProductRevenueUseCase`** | Obtener ingresos totales por ventas de un producto específico | `productId: Long` | `BigDecimal`<br>(ingresos totales del producto) | • Obtiene el supermercado<br>• Suma el `totalPrice` de todas las ventas del producto | `SupermarketNotFoundException` |
| **`GetProductSoldQuantityUseCase`** | Obtener la cantidad total vendida de un producto | `productId: Long` | `Int`<br>(unidades vendidas) | • Obtiene el supermercado<br>• Suma las cantidades de todas las ventas del producto | `SupermarketNotFoundException` |
| **`GetTotalRevenueUseCase`** | Obtener ingresos totales acumulados del supermercado | _(ninguna)_ | `BigDecimal`<br>(ingresos totales) | • Suma el `totalPrice` de todas las ventas en el historial del local | `SupermarketNotFoundException` |

## 📦 Entidades del Dominio

### Product
- `id: Long` - Identificador único
- `name: String` - Nombre del producto (no puede estar vacío)
- `price: BigDecimal` - Precio unitario (debe ser >= 0)

### Sale
- `id: Long` - Identificador único de la venta
- `productId: Long` - Referencia al producto
- `quantity: Int` - Cantidad vendida (debe ser > 0)
- `supermarketId: Long` - ID del supermercado donde se realiza la venta
- `unitPrice: BigDecimal` - Precio unitario al momento de la venta
- `timestamp: LocalDate` - Fecha de la venta
- `totalPrice: BigDecimal` - Propiedad derivada (unitPrice * quantity)

### SupermarketProduct
- `productId: Long` - Referencia al producto
- `stock: Int` - Stock disponible (privado, acceso controlado)
- Métodos: `increaseStock()`, `decreaseStock()`, `hasStock()`, `currentStock()`

### Supermarket
- `id: Long` - Identificador único
- `name: String` - Nombre del supermercado (no puede estar vacío)
- `chainId: Long` - ID de la cadena a la que pertenece
- `products: MutableList<SupermarketProduct>` - Productos disponibles (privado)
- `sales: MutableList<Sale>` - Historial de ventas (privado)
- `hours: OpenHours?` - Horario de atención (opcional)

### OpenHours
- `daysOpen: Set<DayOfWeek>` - Días de operación
- `openTime: LocalTime` - Hora de apertura
- `closeTime: LocalTime` - Hora de cierre
- Método: `isOpen(day, time): Boolean`

### SupermarketChain
- `id: Long` - Identificador único
- `name: String` - Nombre de la cadena

## ✅ Alcance funcional actual

La lógica principal se centra en:

- Gestión de supermercados y cadenas
- Gestión de productos por supermercado con control de stock (`SupermarketProduct`)
- Registro de ventas (`Sale`) con validaciones de cantidad y precio
- Consultas de ingresos y cantidades vendidas por producto
- Consultas de ingresos totales por supermercado
- Validaciones de horarios de apertura (`OpenHours`)
- Manejo de errores mediante excepciones de dominio específicas

## 🧪 Testing

El proyecto incluye:

- **Tests unitarios** para cada entidad del dominio (usando JUnit 5)
- **Tests de integración** para los casos de uso (cobertura de escenarios completos)
- Cobertura de casos de éxito y excepciones esperadas
- Validación de independencia entre supermercados

### Ejecutar tests:

```bash
mvn test
```

### Ejecutar tests específicos:

```bash
# Tests unitarios
mvn test -Dtest=SupermarketTest
mvn test -Dtest=ProductTest
mvn test -Dtest=SaleTest

# Tests de integración
mvn test -Dtest=SupermarketUseCasesIntegrationTest
```

## 📋 Excepciones de Dominio

- `InvalidNameException` - Nombre vacío en entidades
- `InvalidProductNameException` - Nombre vacío de producto
- `InvalidPriceException` - Precio negativo
- `InvalidQuantityException` - Cantidad <= 0 o stock negativo
- `InsufficientStockException` - Intento de venta con stock insuficiente
- `ProductAlreadyExistsException` - Intento de añadir producto duplicado
- `ProductNotFoundException` - Producto no existe en el supermercado
- `InvalidOpenTimeRangeException` - Horario de cierre anterior a apertura
- `InvalidOpenDaysException` - Configuración inválida de días de operación

## ✅ Notas finales

- No se utilizan DTOs ni mappers en la implementación actual porque no hay una capa externa
- La persistencia es en memoria y está optimizada para el challenge
- Los tests cubren tanto casos de éxito como de error
- El código sigue principios de Clean Code y Domain-Driven Design

## 📚 Documentación visual

- `docs/diagram.puml`: Diagrama de clases del dominio con relaciones y validaciones
- `docs/classDiagram.png`: Versión en imagen del diagrama

