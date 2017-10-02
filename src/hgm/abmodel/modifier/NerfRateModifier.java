package hgm.abmodel.modifier;

import dcore.Transition;

/**
 *
 * Created by timewz on 30/09/17.
 */
public class NerfRateModifier extends AbsModifier {
    private boolean Value;

    public NerfRateModifier(String name, Transition target) {
        super(name, target);
        Value = false;
    }

    @Override
    public double getValue() {
        return Value? 1.0:0.0;
    }

    @Override
    public double modify(double tte) {
        if (Value) {
            return Double.POSITIVE_INFINITY;
        } else {
            return tte;
        }
    }

    @Override
    public boolean update(double value) {
        if (value >0 ^ Value) {
            Value = !Value;
            return true;
        } else {
            return false;
        }
    }

    public NerfRateModifier clone() {
        NerfRateModifier mod = new NerfRateModifier(getName(), getTarget());
        mod.Value = Value;
        return mod;
    }
}
