package com.demkom58.ids_lab_2.client;

import com.demkom58.ids_lab_2.compute.task.Task;

import java.math.BigDecimal;

public class Pi implements Task {

    /**
     * constants used in pi computation
     */
    private static final BigDecimal ZERO = BigDecimal.valueOf(0);
    private static final BigDecimal ONE = BigDecimal.valueOf(1);
    private static final BigDecimal FOUR = BigDecimal.valueOf(4);

    /**
     * rounding mode to use during pi computation
     */
    private static final int roundingMode = BigDecimal.ROUND_HALF_EVEN;

    /**
     * digits of precision after the decimal point
     */
    private int digits;

    /**
     * Construct a task to calculate pi to the specified
     * precision.
     */
    public Pi(int digits) {
        this.digits = digits;
    }

    /**
     * Calculate pi.
     */
    @Override
    public BigDecimal execute() {
        return computePi(digits);
    }

    /**
     * Compute the value of pi to the specified number of
     * digits after the decimal point.  The value is
     * computed using Machin's formula:
     * <p>
     * pi/4 = 4*arctan  arctan(1/239)
     * <p>
     * and a power series expansion of arctan(x) to
     * sufficient precision.
     */
    public static BigDecimal computePi(int digits) {
        int scale = digits + 5;

        BigDecimal arctan1_5 = arctan(5, scale);
        BigDecimal arctan1_239 = arctan(239, scale);
        BigDecimal pi = arctan1_5.multiply(FOUR).subtract(arctan1_239).multiply(FOUR);

        return pi.setScale(digits, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Compute the value, in radians, of the arctangent of
     * the inverse of the supplied integer to the speficied
     * number of digits after the decimal point.  The value
     * is computed using the power series expansion for the
     * arc tangent:
     * <p>
     * arctan = x /3/5 /7 +
     * (x^9)/9 ...
     */
    public static BigDecimal arctan(int inverseX, int scale) {
        BigDecimal result, numer, term;
        BigDecimal invX = BigDecimal.valueOf(inverseX);
        BigDecimal invX2 = BigDecimal.valueOf((long) inverseX * inverseX);

        numer = ONE.divide(invX, scale, roundingMode);
        result = numer;

        int i = 1;
        do {
            numer = numer.divide(invX2, scale, roundingMode);

            BigDecimal denom = BigDecimal.valueOf(2L * i + 1);
            term = numer.divide(denom, scale, roundingMode);

            result = (i % 2) != 0 ? result.subtract(term) : result.add(term);
            i++;
        } while (term.compareTo(ZERO) != 0);

        return result;
    }
}
