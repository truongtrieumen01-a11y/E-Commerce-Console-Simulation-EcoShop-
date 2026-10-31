package model;

/**
 * Interface Discountable — Abstraction layer for discount calculation.
 * Demonstrates: Abstraction + Polymorphism (OOP Pillar).
 * 
 * RegularCustomer and VIPCustomer implement this differently,
 * fulfilling the polymorphism requirement from the assignment.
 */
public interface Discountable {
    /**
     * Calculates the discount amount for a given total.
     * @param totalAmount the pre-discount total price
     * @return the discount amount (not percentage)
     */
    double calculateDiscount(double totalAmount);
}
