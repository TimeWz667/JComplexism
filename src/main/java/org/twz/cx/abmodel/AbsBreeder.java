package org.twz.cx.abmodel;

import org.json.JSONException;
import org.twz.dag.Chromosome;
import org.twz.dag.Parameters;
import org.twz.util.NameGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbsBreeder<T extends AbsAgent> {
    private final String Name, Group;
    private final NameGenerator GenName;
    private final Parameters GenPars;
    private final Map<String, Double> Exo;

    public AbsBreeder(String name, String group, Parameters genPars, Map<String, Double> exo) {
        Name = name;
        Group = group;
        GenName = new NameGenerator(name);
        GenPars = genPars.genPrototype(group);
        Exo = exo;
    }

    public String getName() {
        return Name;
    }

    public List<T> breed(int n, Map<String, Object> attributes) throws JSONException {
        String name;
        Chromosome pars;
        List<T> ags = new ArrayList<>();
        while (n > 0) {
            name = GenName.getNext();
            pars = GenPars.breed(name, Group, Exo);

            T ag = newAgent(name, pars, attributes);
            ag.updateAttributes(attributes);
            ags.add(ag);
            n --;
        }
        return ags;
    }

    protected abstract T newAgent(String name, Chromosome pars, Map<String, Object> attributes) throws JSONException;
}
