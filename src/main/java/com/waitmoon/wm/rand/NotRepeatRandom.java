package com.waitmoon.wm.rand;


import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

public final class NotRepeatRandom {
    // left inclusive
    private final long l;
    // right inclusive
    private final long r;
    // clockwise current value
    private long cw;
    // counter clockwise current value
    private long ccw;
    // step
    private long step;
    private ThreadLocalRandom random;
    // fill right to prime
    private long fr;
    // total
    private long t;
    // size
    private final long s;

    /**
     * @param r [0,r]
     */
    public NotRepeatRandom(long r) {
        this(0, r, 200);
    }

    /**
     * @param l left inclusive
     * @param r right inclusive
     */
    public NotRepeatRandom(long l, long r) {
        this(l, r, 200);
    }

    public void refresh() {
        random = ThreadLocalRandom.current();
        this.step = random.nextLong(1, ((fr - l) / 2 + 1));
        while (step != 1 && (fr - l + 1) % step == 0) {
            this.fr = BigInteger.valueOf(fr - l + 1).nextProbablePrime().longValue() + l - 1;
        }
        if (fr <= l) {
            throw new IllegalArgumentException("fillBound illegal");
        }
        this.cw = random.nextLong(l, fr);
        this.ccw = (cw - step) < l ? fr + cw - step - l + 1 : cw - step;
        this.t = 0;
    }

    /**
     * @param l         left inclusive
     * @param r         right inclusive
     * @param certainty certainty of prime
     */
    public NotRepeatRandom(long l, long r, int certainty) {
        if (l < 0 || r <= 0 || l >= r) {
            throw new IllegalArgumentException("origin must be non-negative and bound must be positive and origin must be less than bound");
        }
        this.s = r - l + 1;
        this.l = l;
        this.r = r;
        if (BigInteger.valueOf(s).isProbablePrime(certainty)) {
            this.fr = r;
        } else {
            this.fr = BigInteger.valueOf(s).nextProbablePrime().longValue() + l - 1;
        }
        random = ThreadLocalRandom.current();
        this.step = random.nextLong(1, ((fr - l) / 2 + 1));
        while (step != 1 && (fr - l + 1) % step == 0) {
            this.fr = BigInteger.valueOf(fr - l + 1).nextProbablePrime().longValue() + l - 1;
        }
        if (fr <= l) {
            throw new IllegalArgumentException("fillBound illegal");
        }
        this.cw = random.nextLong(l, fr);
        this.ccw = (cw - step) < l ? fr + cw - step - l + 1 : cw - step;
    }

    /**
     * @return next value
     */
    public long next() {
        if (t >= s) {
            throw new IllegalStateException("exhausted all possible values");
        }
        long v;
        if (random.nextBoolean()) {
            v = cw;
            if (cw > r) {
                cw = cw + (((fr - cw) / step) * step);
                cw = cw + step > fr ? cw + step + l - fr - 1 : cw + step;
                v = cw;
            }
            cw = cw + step > fr ? cw + step + l - fr - 1 : cw + step;
        } else {
            v = ccw;
            if (ccw > r) {
                ccw = ccw - (((ccw - r - 1) / step) * step);
                ccw = ccw - step < l ? fr + ccw - step - l + 1 : ccw - step;
                v = ccw;
            }
            ccw = ccw - step < l ? fr + ccw - step - l + 1 : ccw - step;
        }
        t++;
        return v;
    }
}
