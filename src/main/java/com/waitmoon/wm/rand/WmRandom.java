package com.waitmoon.wm.rand;

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

public final class WmRandom {
    private final long l;
    private final long r;
    private long cw;
    private long ccw;
    private long step;
    private ThreadLocalRandom random;
    private long fr;
    private long t;
    private final long s;
    private long ms;
    private boolean d;
    private long ns;

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

    public void reset() {
        synchronized (this) {
            random = ThreadLocalRandom.current();
            this.ns = fr - l;
            this.step = random.nextLong(1L, (ns >> 1) + 1L);
            while (step != 1L && (ns + 1L) % step == 0) {
                this.fr = BigInteger.valueOf(ns + 1L).nextProbablePrime().longValue() + l - 1L;
            }
            if (fr <= l) {
                throw new IllegalArgumentException("fillBound illegal");
            }
            this.cw = random.nextLong(l, fr);
            long i = cw - step;
            this.ccw = i < l ? ns + i + 1L : i;
            this.t = 0;
            this.ms = random.nextLong(l, r);
            this.d = random.nextBoolean();
        }
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
        this.s = r - l + 1L;
        this.l = l;
        this.r = r;
        if (BigInteger.valueOf(s).isProbablePrime(certainty)) {
            this.fr = r;
        } else {
            this.fr = BigInteger.valueOf(s).nextProbablePrime().longValue() + l - 1L;
        }
        random = ThreadLocalRandom.current();
        this.ns = fr - l;
        this.step = random.nextLong(1L, (ns >> 1) + 1L);
        while (step != 1 && (ns + 1L) % step == 0) {
            this.fr = BigInteger.valueOf(ns + 1L).nextProbablePrime().longValue() + l - 1L;
        }
        if (fr <= l) {
            throw new IllegalArgumentException("fillBound illegal");
        }
        this.cw = random.nextLong(l, fr);
        long i = cw - step;
        this.ccw = i < l ? ns + i + 1L : i;
        this.ms = random.nextLong(l, r);
        this.d = random.nextBoolean();
    }

    /**
     * @return next value
     */
    public long next() {
        long v;
        long ms;
        boolean d;
        synchronized (this) {
            if (t >= s) {
                throw new IllegalStateException("exhausted all possible values");
            }
            d = this.d;
            ms = this.ms;
            long i;
            if (random.nextBoolean()) {
                v = cw;
                if (cw > r) {
                    long g = fr - cw;
                    cw = cw + g - g % step;
                    i = cw + step;
                    cw = i > fr ? i - ns - 1L : i;
                    v = cw;
                }
                i = cw + step;
                cw = i > fr ? i - ns - 1L : i;
            } else {
                v = ccw;
                if (ccw > r) {
                    long g = ccw - r - 1L;
                    ccw = ccw - g + g % step;
                    i = ccw - step;
                    ccw = i < l ? ns + i + 1L : i;
                    v = ccw;
                }
                i = ccw - step;
                ccw = i < l ? ns + i + 1L : i;
            }
            t++;
        }
        if (d) {
            if (v <= ms) {
                return v;
            }
            long f = ((r - ms) & 1) == 0 ? 1L : 0L;
            long m = ms + ((r - ms) >> 1) + 1L;
            return m - v - f + m;
        }
        if (v > ms) {
            return v;
        }
        long f = ((ms - l + 1L) & 1) == 0 ? 1L : 0L;
        long m = l + ((ms - l) >> 1) + f;
        return m - v - f + m;
    }
}
