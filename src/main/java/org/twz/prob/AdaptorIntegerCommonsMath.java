package org.twz.prob;


import org.apache.commons.math3.distribution.AbstractIntegerDistribution;


/**
 * An adaptor for integer distribution in Apache commons-math
 * Created by TimeWz on 2017/7/15.
 */
public class AdaptorIntegerCommonsMath implements IWalkable {
    private AbstractIntegerDistribution D;
    protected String Name;

    public AdaptorIntegerCommonsMath(String name, AbstractIntegerDistribution d) {
        Name = name;
        D = d;
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public double sample() {
        return (double) D.sample();
    }

    @Override
    public double[] sample(int n) {
        double[] res = new double[n];
        int i = 0;
        for (int s: D.sample(n)) {
            res[i] = s;
            i ++;
        }
        return res;
    }

    @Override
    public double logProb(double rv) {
        return D.logProbability((int) rv);
    }

    @Override
    public String getDataType() {
        return "Integer";
    }

    @Override
    public double getUpper() {
        return D.getSupportUpperBound();
    }

    @Override
    public double getLower() {
        return D.getSupportLowerBound();
    }

    @Override
    public double getMean() {
        return D.getNumericalMean();
    }

    @Override
    public double getStd() {
        return Math.sqrt(D.getNumericalVariance());
    }

    public String toString() {
        return Name;
    }
}
