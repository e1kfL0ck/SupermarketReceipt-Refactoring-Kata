package dojo.supermarket.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoyaltyProgramTest {

    private final LoyaltyProgram loyalty = new LoyaltyProgram();

    @Test
    void testEarnPoints() {
        // 26.04€ -> 26.04 -> 26 points
        assertEquals(26, loyalty.earnPoints(26.04), "Earn should be floor(26.04) = 26");
        assertEquals(9, loyalty.earnPoints(9.99), "floor(9.99) = 9");
        assertEquals(10, loyalty.earnPoints(10.00), "floor(10) = 1");
    }

    @Test
    void testConvertPoints() {
        // Spéc: 1 point = 1€
        assertEquals(0.1, loyalty.pointsToMoney(1), 1e-9);
        assertEquals(1.2, loyalty.pointsToMoney(12), 1e-9);

        //TODO: vérifier fct Math.floor
        assertEquals(1, loyalty.moneyToPoints(1.0));
        assertEquals(12, loyalty.moneyToPoints(12.99), "floor(12.99) = 12");
    }
}
