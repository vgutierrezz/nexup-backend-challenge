# Tests - Dominio e Integración

Este directorio contiene **tests unitarios exhaustivos** para todas las entidades del dominio y **tests de integración** para los casos de uso, utilizando **JUnit 5** y **Mockito**.

## 📋 Estructura de Tests

```
src/test/
├── unit/
│   ├── ProductTest.kt
│   ├── OpenHoursTest.kt
│   ├── SaleTest.kt
│   ├── SupermarketProductTest.kt
│   ├── SupermarketTest.kt
│   └── SupermarketChainTest.kt
└── integration/
    ├── SupermarketIntegrationTest.kt
    └── SupermarketUseCasesIntegrationTest.kt
```

---

## 🧪 Tests Unitarios (Unit Tests)

### 1. **ProductTest** (`ProductTest.kt`)
Tests para la entidad `Product`:
- ✅ Creación con datos válidos
- ✅ Validación de nombre (no vacío, no en blanco)
- ✅ Validación de precio (no negativo)
- ✅ Casos especiales: precio cero (válido), múltiples precios

**Casos cubiertos**: 7 tests

---

### 2. **OpenHoursTest** (`OpenHoursTest.kt`)
Tests para la entidad `OpenHours`:
- ✅ Creación con datos válidos
- ✅ Validación de días (no vacío)
- ✅ Validación de rango de horas (openTime < closeTime)
- ✅ Método `isOpen()`: casos válidos e inválidos
- ✅ Casos especiales: todos los días de la semana

**Casos cubiertos**: 10 tests

---

### 3. **SaleTest** (`SaleTest.kt`)
Tests para la entidad `Sale`:
- ✅ Creación con datos válidos
- ✅ Cálculo correcto de `totalPrice`
- ✅ Validación de cantidad (no cero, no negativa)
- ✅ Validación de precio (no negativo)
- ✅ Timestamp por defecto (LocalDate.now())
- ✅ Casos especiales: cantidad 1, cantidad grande, precio cero

**Casos cubiertos**: 10 tests

---

### 4. **SupermarketProductTest** (`SupermarketProductTest.kt`)
Tests para la entidad `SupermarketProduct`:
- ✅ Creación con stock válido
- ✅ Validación de stock inicial (no negativo)
- ✅ Operación `increaseStock()`: suma correcta, validaciones
- ✅ Operación `decreaseStock()`: resta correcta, validaciones
- ✅ Operación `hasStock()`: consulta de disponibilidad
- ✅ Casos especiales: operaciones múltiples, stock límite

**Casos cubiertos**: 14 tests

---

### 5. **SupermarketTest** (`SupermarketTest.kt`)
Tests para la entidad `Supermarket`:
- ✅ Creación con datos válidos
- ✅ Validación de nombre (no vacío, no en blanco)
- ✅ Operación `addProduct()`: agregar productos, detección de duplicados
- ✅ Operación `increaseStock()`: incrementar stock
- ✅ Operación `sellProduct()`: venta completa, cálculo de total
- ✅ Operación `getSoldQuantity()`: cálculo de cantidad vendida
- ✅ Métodos `getProducts()` y `getSales()`
- ✅ Casos especiales: sin horarios, múltiples ventas

**Casos cubiertos**: 14 tests

---

### 6. **SupermarketChainTest** (`SupermarketChainTest.kt`)
Tests para la entidad `SupermarketChain`:
- ✅ Creación con datos válidos
- ✅ Validación de nombre (no vacío, no en blanco)

**Casos cubiertos**: 5 tests

---

## 🔗 Tests de Integración (Integration Tests)

### 1. **SupermarketIntegrationTest** (`SupermarketIntegrationTest.kt`)
Test básico de integración:
- ✅ Ventas e ingresos totales entre múltiples supermercados
- ✅ Cálculo de revenue consolidado

**Casos cubiertos**: 1 test

---

### 2. **SupermarketUseCasesIntegrationTest** (`SupermarketUseCasesIntegrationTest.kt`)
Tests extensivos de todos los casos de uso:

#### **RegisterSaleUserCase** (6 tests)
- ✅ Registrar una venta exitosamente
- ✅ Actualizar el stock después de una venta
- ✅ Lanzar excepción cuando no hay suficiente stock
- ✅ Lanzar excepción cuando el supermercado no existe
- ✅ Lanzar excepción cuando el producto no existe
- ✅ Permitir múltiples ventas del mismo producto

#### **GetProductRevenueUseCase** (5 tests)
- ✅ Calcular correctamente los ingresos de un producto
- ✅ Retornar cero cuando no hay ventas
- ✅ Calcular correctamente con diferentes precios
- ✅ Lanzar excepción cuando el supermercado no existe
- ✅ Calcular ingresos independientes por supermercado

#### **GetProductSoldQuantityUseCase** (4 tests)
- ✅ Calcular correctamente la cantidad vendida de un producto
- ✅ Retornar cero cuando no hay ventas
- ✅ Lanzar excepción cuando el supermercado no existe
- ✅ Calcular cantidades independientes por supermercado

#### **GetTotalRevenueUseCase** (5 tests)
- ✅ Calcular correctamente los ingresos totales del supermercado
- ✅ Retornar cero cuando no hay ventas
- ✅ Calcular ingresos totales independientes por supermercado
- ✅ Lanzar excepción cuando el supermercado no existe
- ✅ Acumular ingresos de múltiples ventas

#### **StockManagementIntegrationTests** (3 tests)
- ✅ Prevenir venta cuando el stock se agota
- ✅ Permitir venta exacta del stock disponible
- ✅ Mantener independencia de stock entre supermercados

#### **MultiProductIntegrationTests** (2 tests)
- ✅ Manejar ventas de múltiples productos simultáneamente
- ✅ Rastrear correctamente el historial de ventas de múltiples productos

**Casos cubiertos**: 25 tests de integración

---

## 📊 Resumen General

| Tipo de Test | Cantidad | Total |
|---|---|---|
| Unit Tests | 60 | 60 |
| Integration Tests | 26 | 26 |
| **TOTAL** | | **86** |

### Cobertura por Entidad

| Entidad | Unit Tests | Casos |
|---------|---|---|
| Product | ✅ | 7 |
| OpenHours | ✅ | 10 |
| Sale | ✅ | 10 |
| SupermarketProduct | ✅ | 14 |
| Supermarket | ✅ | 14 |
| SupermarketChain | ✅ | 5 |

### Cobertura por Use Case

| Caso de Uso | Tests de Integración |
|---|---|
| RegisterSaleUserCase | 6 |
| GetProductRevenueUseCase | 5 |
| GetProductSoldQuantityUseCase | 4 |
| GetTotalRevenueUseCase | 5 |
| Gestión de Stock | 3 |
| Múltiples Productos | 2 |

---

## ▶️ Cómo Ejecutar los Tests

### Ejecutar todos los tests
```bash
mvn test
```

### Ejecutar solo tests unitarios
```bash
mvn test -Dtest=**/*Test.kt
```

### Ejecutar solo tests de integración
```bash
mvn test -Dtest=SupermarketUseCasesIntegrationTest
```

### Ejecutar un test específico
```bash
mvn test -Dtest=ProductTest
mvn test -Dtest=SupermarketTest
mvn test -Dtest=SupermarketUseCasesIntegrationTest
```

---

## 📦 Dependencias Utilizadas

El proyecto incluye todas las dependencias necesarias en `pom.xml`:

```xml
<!-- JUnit 5 Framework -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.9.2</version>
    <scope>test</scope>
</dependency>

<!-- Mockito para mocking -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.2.0</version>
    <scope>test</scope>
</dependency>

<!-- Mockito Kotlin DSL -->
<dependency>
    <groupId>org.mockito.kotlin</groupId>
    <artifactId>mockito-kotlin</artifactId>
    <version>5.1.0</version>
    <scope>test</scope>
</dependency>
```

---

## 🎯 Estrategia de Testing

### Objetivo de cobertura:
- ✅ **Unit Tests**: Validar comportamiento de entidades en aislamiento
- ✅ **Integration Tests**: Validar flujos completos de casos de uso
- ✅ **Exception Testing**: Verificar que las excepciones se lancen correctamente
- ✅ **Edge Cases**: Probar límites y casos especiales

### Tipos de tests realizados:

| Tipo | Descripción | Cantidad |
|---|---|---|
| Happy Path | Escenarios normales de éxito | 45 |
| Error Handling | Lanzamiento de excepciones esperadas | 25 |
| Edge Cases | Límites y casos especiales | 16 |

---

## ✅ Estado de los Tests

```
BUILD SUCCESS
Tests run: 86, Failures: 0, Errors: 0, Skipped: 0
```

---

## 📝 Notas Finales

- Los tests están organizados por **unit tests** (validación de entidades) y **integration tests** (validación de casos de uso)
- Se utiliza **JUnit 5** como framework principal
- **Mockito** está configurado pero los tests actuales son mayormente unit tests puros sin mocking externo
- Todos los tests son **independientes** y pueden ejecutarse en cualquier orden
- Hay **cobertura completa de excepciones** de dominio


