# Supermarket Receipt – Refactoring & Feature Implementation Report

This document describes the work done on top of the original
**SupermarketReceipt Refactoring Kata** codebase, following the required
process:

1. Add tests to gain confidence
2. Refactor to remove code smells
3. Implement new features (Bundles, Coupons, Loyalty program)

---

## 1. Initial State & Test Coverage

### Goal
Before refactoring, the goal was to freeze the existing behavior of the system
by writing tests that describe what the code already does.

### Tests added before refactoring
Tests were written to validate observable behavior without modifying the code:

- Empty cart → total price is 0
- Single product (EACH) → total = price × quantity
- Single product (KILO) → total = price/kg × weight
- Receipt contains expected items and prices
- Existing special offers:
  - THREE_FOR_TWO
  - TEN_PERCENT_DISCOUNT
  - N_FOR_AMOUNT
- Boundary cases (quantities just below / above discount thresholds)

These tests provided enough confidence to safely refactor the pricing logic.

---

## 2. Refactoring Driven by Code Smells

### Identified code smells (from original code)
- **Long Method**: discount logic concentrated in a single large method
- **Feature Envy**: pricing and discount logic living in `ShoppingCart`
- **Complex Conditionals**: branching on offer types
- **Poor Separation of Responsibilities**: checkout flow, pricing, and discount rules mixed

### Refactoring elements

#### 2.1 Extract checkout orchestration
- Introduced a dedicated checkout orchestration class (`CheckoutCounter`). This class replace and complete the old `Teller` class.
- Responsibility:
  - Build the receipt
  - Apply discounts
  - Finalize payment
- Result: `ShoppingCart` is reduced to a data structure holding items

#### 2.2 Extract discount computation
- Moved discount logic into a dedicated component (`DiscountEngine`)
- Discount computation is no longer mixed with checkout orchestration
- Easier to test discount rules in isolation
- Supports future extension (new discount types)

#### 2.3 Reduce long methods
- Large discount methods were split by responsibility:
  - Bundle discounts
  - Regular offers
  - Coupon-based discounts
- Shared state passed via explicit context instead of implicit field access

#### 2.4 Model cleanup
- Simplified data structures (maps instead of complex collections where appropriate)
- Removed unused / legacy abstractions from the original kata
- Harmonized naming and method responsibilities

All refactorings were done with **tests remaining green at each step**.

---

## 3. Feature: Discounted Bundles

### Specification
- A bundle is a set of products
- When **all products of a bundle are purchased**, a **10% discount** is applied
- Only **complete bundles** are discounted
- Extra items outside the bundle are not discounted

Example:
- Bundle = toothbrush + toothpaste
- Buy 1 toothbrush + 1 toothpaste → 10% off both items
- Buy 2 toothbrushes + 1 toothpaste → only 1 bundle discounted

### Implementation
- Introduced a new offer type: `BUNDLE`
- Bundle definition contains a list of required products
- Number of applicable bundles is calculated as: 
  - min(quantity available for each product in the bundle)
- Discount applied per complete bundle

### Tests added
- Complete bundle → discount applied
- Incomplete bundle → no discount
- Multiple bundles → discount applied multiple times
- Extra products outside bundle are not discounted

---

## 4. Feature: Coupon-Based Discounts

### Specification
- Coupons apply to a specific product
- Coupons have:
  - Validity period (start/end date)
  - Quantity constraints
  - One-time usage
  - Example: “Buy 6 orange juices, get 6 more at half price”
  - Coupons cannot be reused once redeemed

### Implementation
- Introduced a `Coupon` concept owned by the `Customer`
  - Coupon properties:
  - Validity dates
  - Required quantity
- Discount rule
  - `used` flag
- During checkout:
  - Coupon validity is checked
  - Discount is computed once
  - Coupon is marked as used
- When both an offer and a coupon apply to the same product:
  - The **best discount** is applied

### Tests added
- Valid coupon applied once
- Expired coupon ignored
- Already-used coupon ignored
- Coupon with insufficient quantity not applied
- Coupon vs offer → best discount selected

---

## 5. Feature: Loyalty Program

### Specification
- Customers earn loyalty points based on money spent
- Points can be used as an alternative or supplementary payment method

Rules implemented:
- **Earning points**:
- `pointsEarned = floor(eurosPaid × 100)`
- Example: 26.23€ → 262 points
- **Spending points**:
- 100 points = 1€
- Example: 1.23€ → 123 points
- Points are earned **only on the cash part actually paid**
- Discounts are applied **before** loyalty points are used

### Implementation
- Added `LoyaltyProgram` responsible only for conversions and rules
- `Customer` stores loyalty points and exposes:
- Add points
- Use points
- Get current balance
- Checkout flow updated:
1. Compute total after discounts
2. Use available loyalty points
3. Pay remaining amount in cash
4. Earn new points from cash paid

### Tests added
- After checkout, customer points increase
- Second checkout uses points and reduces balance
- Points greater than total → fully paid with points, no cash
- Discounts + loyalty → points applied after discounts

---

## 6. Conclusion

The work followed the expected process:
1. **Tests first** to secure behavior
2. **Refactoring** guided by code smells
3. **New features** implemented incrementally with dedicated tests

The resulting codebase:
- Has clearer responsibilities
- Is easier to extend with new pricing rules
- Is protected by behavior-driven tests
- Remains faithful to the original kata intent

