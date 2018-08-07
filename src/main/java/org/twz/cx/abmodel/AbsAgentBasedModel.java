package org.twz.cx.abmodel;

import org.twz.cx.abmodel.behaviour.AbsBehaviour;
import org.twz.cx.abmodel.behaviour.ActiveBehaviour;
import org.twz.cx.abmodel.network.AbsNetwork;
import org.twz.cx.element.Event;
import org.twz.cx.element.Request;
import org.twz.cx.mcore.AbsObserver;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.IY0;
import org.twz.cx.mcore.LeafModel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by TimeWz on 09/07/2018.
 */
public abstract class AbsAgentBasedModel<T extends AbsAgent> extends LeafModel {

    private Population<T> Population;
    private Map<String, AbsBehaviour> Behaviours;

    public AbsAgentBasedModel(String name, Map<String, Object> env, Population<T> pop, AbsObserver<AbsSimModel> obs, IY0 protoY0) {
        super(name, env, obs, protoY0);
        this.Population = pop;
        Behaviours = new LinkedHashMap<>();
    }

    public void addNetwork(AbsNetwork net) {
        this.Population.addNetwork(net);
    }

    protected void makeAgents(int n, double ti, Map<String, Object> attributes) {
        List<T> ags = this.Population.addAgents(n, attributes);
        for (T ag: ags) {
            for (AbsBehaviour be: Behaviours.values()) {
                be.register(ag, ti);
            }
        }
    }

    public org.twz.cx.abmodel.Population<T> getPopulation() {
        return Population;
    }

    @Override
    public void preset(double ti) {
        Behaviours.values().forEach(be->be.initialise(ti, this));
        this.Population.getAgents().values().forEach(ag -> ag.initialise(ti, this));
        disclose("initialise", "*");
    }


    @Override
    public void reset(double ti) {
        Behaviours.values().forEach(be->be.reset(ti, this));
        this.Population.getAgents().values().forEach(ag -> ag.reset(ti, this));
        disclose("initialise", "*");
    }

    protected List<AbsBehaviour> checkEnter(T ag) {
        return Behaviours.values().stream().filter(be -> be.checkEnterChange(ag)).collect(Collectors.toList());
    }

    protected void imulseEnter(List<AbsBehaviour> bes, T ag, double ti) {
        bes.forEach(be-> be.impulseEnter(this, ag, ti));
    }

    protected List<AbsBehaviour> checkExit(T ag) {
        return Behaviours.values().stream().filter(be -> be.checkExitChange(ag)).collect(Collectors.toList());
    }

    protected void impulseExit(List<AbsBehaviour> bes, T ag, double ti) {
        bes.forEach(be-> be.impulseExit(this, ag, ti));
    }

    protected List<Boolean> checkPreChange(T ag) {
        return Behaviours.values().stream().map(be -> be.checkPreChange(ag)).collect(Collectors.toList());
    }

    protected List<Boolean> checkPostChange(T ag) {
        return Behaviours.values().stream().map(be -> be.checkPostChange(ag)).collect(Collectors.toList());
    }

    protected List<AbsBehaviour> checkChange(List<Boolean> pre, List<Boolean> post) {
        List<AbsBehaviour> be_all = new ArrayList<>(Behaviours.values()), bes = new ArrayList<>();
        AbsBehaviour be;

        for (int i = 0; i < be_all.size(); i++) {
            be = be_all.get(i);
            if (be.checkChange(pre.get(i), post.get(i))) {
                bes.add(be);
            }
        }

        return bes;
    }

    protected void impulseChange(List<AbsBehaviour> bes, T ag, double ti) {
        bes.forEach(be-> be.impulseExit(this, ag, ti));
    }

    public List<T> birth(int n, double ti, Map<String, Object> attributes) {
        List<T> ags = this.Population.addAgents(n, attributes);
        List<AbsBehaviour> bes;
        int nBirth = 0;
        for (T ag : ags) {
            Behaviours.values().forEach(be->be.register(ag, ti));
            bes = checkEnter(ag);
            ag.initialise(ti, this);
            imulseEnter(bes, ag, ti);
            request(ag.getNext(), ag.getName());
            nBirth ++;
        }
        if (nBirth > 0) {
            disclose("add " + nBirth + " agents", "*");
        }
        return ags;
    }

    public void kill(String id, double ti) {
        try {
            T ag = Population.get(id);
            List<AbsBehaviour> bes = checkExit(ag);
            Population.removeAgent(id);
            impulseExit(bes, ag, ti);
            disclose("remove agent " + id, ag.getName());
        } catch (NullPointerException ignored) {

        }
    }

    @Override
    public void findNext() {
        Behaviours.forEach((key, value) -> request(value.getNext(), key));
        Population.getAgents().forEach((key, value) -> request(value.getNext(), key));
    }

    @Override
    public void doRequest(Request req) {
        String nod = req.Who;
        Event todo = req.Todo;
        double time = req.getTime();
        if (Behaviours.containsKey(nod)) {
            ActiveBehaviour be = (ActiveBehaviour) Behaviours.get(nod);
            be.approveEvent(todo);
            be.operate(this);
        } else {
            try {
                T ag = this.Population.get(nod);
                ag.approveEvent(todo);
                List<Boolean> pre = checkPreChange(ag);
                ((ABMObserver) getObserver()).record(ag, todo.getValue(), time);
                ag.executeEvent();
                ag.dropNext();
                List<Boolean> post = checkPostChange(ag);
                List<AbsBehaviour> bes = checkChange(pre, post);
                impulseChange(bes, ag, time);
                ag.updateTo(time);
            } catch (NullPointerException ignored) {

            }
        }
    }

    public long size() {
        return Population.count();
    }
}