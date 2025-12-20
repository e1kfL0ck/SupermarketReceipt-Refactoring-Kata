package dojo.supermarket.model;

public final class LoyaltyProgram {
    private static final int POINTS_PER_UNIT = 10;

    public int earnPoints(double amountPaid) {
        return (int) Math.floor(amountPaid / POINTS_PER_UNIT);
    }

    public double pointsToMoney(int points) {
        return points * POINTS_PER_UNIT;
    }

    public int moneyToPoints(double money) {
        return (int) Math.floor(money / POINTS_PER_UNIT);
    }
}
