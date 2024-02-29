package com.waitmoon.wm.rand;

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

public final class WmRandom {
    private final long l;  // left inclusive
    private final long r;  // right inclusive
    private long cw;  // clockwise current value
    private long ccw; // counter clockwise current value
    private long step;
    private ThreadLocalRandom random;
    private long fr; // fill right to prime
    private long t; // total
    private final long s; // size
    private long ms; //mobius strip position
    private boolean d; //direction

    /**
     * @param r [0,r]
     */
    public WmRandom(long r) {
        this(0, r, 200);
    }

    /**
     * @param l left inclusive
     * @param r right inclusive
     */
    public WmRandom(long l, long r) {
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
        this.ms = random.nextLong(l, r);
        this.d = random.nextBoolean();
    }

    /**
     * @param l         left inclusive
     * @param r         right inclusive
     * @param certainty certainty of prime
     */
    public WmRandom(long l, long r, int certainty) {
        if (l >= r) {
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
        this.ms = random.nextLong(l, r);
        this.d = random.nextBoolean();
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
        if (d) {
            if (v <= ms) {
                return v;
            }
            long m = ms + (r - ms) / 2 + 1;
            int f = (r - ms) % 2 == 0 ? 1 : 0;
            if (v > m) {
                return m - (v - m) - f;
            }
            return m + (m - v) - f;
        }
        if (v > ms) {
            return v;
        }
        int f = (ms - l + 1) % 2 == 0 ? 1 : 0;
        long m = l + (ms - l) / 2 + f;
        if (v < m) {
            return m - (v - m) - f;
        }
        return m + (m - v) - f;
    }
}
