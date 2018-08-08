package org.twz.dag.actor;

import org.twz.dag.Gene;
import org.twz.dag.loci.DistributionLoci;
import org.twz.dag.loci.Loci;
import org.twz.prob.IDistribution;

import java.util.Map;

/**
 *
 * Created by TimeWz on 08/08/2018.
 */
public class FrozenSingleActor extends SimulationActor {
    private IDistribution Distribution;
    private DistributionLoci Loci;

    public FrozenSingleActor(String field, Loci di, Map<String, Double> pas) {
        super(field);
        Loci = (DistributionLoci) di;
        Distribution = Loci.findDistribution(pas);
    }

    public void update(Map<String, Double> pas) {
        Distribution = Loci.findDistribution(pas);
    }

    @Override
    public double sample(Gene pas) {
        return Distribution.sample();
    }

    @Override
    public String toString() {
        return Field + " (" + Distribution.getName() + ")";
    }
}
