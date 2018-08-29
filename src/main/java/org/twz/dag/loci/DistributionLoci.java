package org.twz.dag.loci;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mariuszgromada.math.mxparser.Expression;
import org.twz.dag.Gene;
import org.twz.prob.DistributionManager;
import org.twz.prob.IDistribution;
import java.util.*;


/**
 *
 *
 * Created by TimeWz on 2017/4/17.
 */
public class DistributionLoci extends Loci {
    private final List<String> Parents;
    private final String Distribution;
    private final Expression DE;

    public DistributionLoci(String name, String dist) {
        super(name);
        Distribution = dist;
        DE = new Expression(Distribution);
        Parents = Arrays.asList(DE.getMissingUserDefinedArguments());
    }

    public DistributionLoci(String name, String dist, Collection<String> parents) {
        super(name);
        Distribution = dist;
        DE = new Expression(Distribution);
        Parents = new ArrayList<>(parents);
    }

    @Override
    public List<String> getParents() {
        return Parents;
    }

    @Override
    public double evaluate(Map<String, Double> pas) {
        return findDistribution(pas).logpdf(pas.get(getName()));
    }

    @Override
    public double evaluate(Gene gene) {
        return findDistribution(gene).logpdf(gene.get(getName()));
    }

    @Override
    public double sample(Map<String, Double> pas) {
        return findDistribution(pas).sample();
    }

    @Override
    public double sample(Gene gene) {
        return findDistribution(gene).sample();
    }

    @Override
    public void fill(Gene gene) {
        IDistribution dist = findDistribution(gene);
        double v = dist.sample();
        gene.put(getName(), v);
        gene.addLogPriorProb(dist.logpdf(v));
    }

    public IDistribution findDistribution(Map<String, Double> pas) {
        String f = Distribution;
        for (String par : Parents) {
            f = f.replaceAll("\\b" + par + "\\b", pas.get(par).toString());
        }

        f = f.replaceAll("\\s+", "");

        return DistributionManager.parseDistribution(f);
    }

    public IDistribution findDistribution(Gene pas) {
        String f = Distribution;
        for (String par : Parents) {
            f = f.replaceAll("\\b" + par + "\\b","" + pas.get(par));
        }

        f = f.replaceAll("\\s+", "");

        return DistributionManager.parseDistribution(f);
    }

    @Override
    public String getDefinition() {
        return getName() + "~" + Distribution;
    }

    @Override
    public String toString() {
        return getName() + ": " + Distribution;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = super.toJSON();
        js.put("Type", "Distribution");
        js.put("Parents", new JSONArray(getParents()));
        return js;
    }
}
