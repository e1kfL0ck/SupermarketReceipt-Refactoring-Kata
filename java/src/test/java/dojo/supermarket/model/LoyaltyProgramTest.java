package dojo.supermarket.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoyaltyProgramTest {

    private final LoyaltyProgram loyalty = new LoyaltyProgram();

    @Test
    void testEarnPoints() {
        // 26.04€ -> 26.04 -> 26 points
        assertEquals(260, loyalty.earnPoints(26.04), "Earn should be floor(26.04) = 26");
        assertEquals(99, loyalty.earnPoints(9.99), "floor(9.99) = 9");
        assertEquals(100, loyalty.earnPoints(10.00), "floor(10) = 1");
        assertEquals(15, loyalty.earnPoints(1.56), "floor(10) = 1");

    }

    @Test
    void testConvertPoints() {
        // Spec: 100 point = 1€
        assertEquals(0.01, loyalty.pointsToEuros(1), 1e-9);
        assertEquals(0.12, loyalty.pointsToEuros(12), 1e-9);
        assertEquals(5.61, loyalty.pointsToEuros(561), 1e-9);

        assertEquals(23, loyalty.eurosToPoints(0.23));
        assertEquals(1000, loyalty.eurosToPoints(10));
        assertEquals(1299, loyalty.eurosToPoints(12.99), "floor(12.99) = 12");
    }
}
