package dojo.supermarket.model;

public final class LoyaltyProgram {

    // 100 points = 1€  =>  1 point = 0.01€
    private static final int POINTS_WIN_PER_EURO = 10;
    private static final int POINTS_PER_EURO = 100;

    /**
     * Earn points from euros paid. (10% of euros paid, rounded down).
     * Example: 26.23€ -> 262 points
     */
    public int earnPoints(double eurosPaid) {
        return (int) Math.floor(eurosPaid * POINTS_WIN_PER_EURO);
    }

    /**
     * Money value covered by points.
     * Example: 123 points -> 1.23€
     */
    public double pointsToEuros(int points) {
        return points / (double) POINTS_PER_EURO;
    }

    /**
     * Points needed to cover a given amount in euros.
     * Example: 1.23€ -> 123 points
     */
    public int eurosToPoints(double euros) {
        return (int) Math.round(euros * POINTS_PER_EURO);
    }
}
