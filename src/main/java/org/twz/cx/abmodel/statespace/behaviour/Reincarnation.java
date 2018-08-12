package org.twz.cx.abmodel.statespace.behaviour;

import org.twz.cx.abmodel.AbsAgent;
import org.twz.cx.abmodel.AbsAgentBasedModel;
import org.twz.cx.abmodel.behaviour.AbsBehaviour;
import org.twz.cx.abmodel.behaviour.PassiveBehaviour;
import org.twz.cx.abmodel.statespace.behaviour.trigger.StateEnterTrigger;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.statespace.State;

import java.util.HashMap;
import java.util.Map;

public class Reincarnation extends PassiveBehaviour {
    private final State S_death, S_birth;
    private double BirthN;

    public Reincarnation(String name, State s_death, State s_birth) {
        super(name, new StateEnterTrigger(s_death));
        S_death = s_death;
        S_birth = s_birth;
        BirthN = 0;
    }

    @Override
    public void register(AbsAgent ag, double ti) {

    }

    @Override
    public void impulseChange(AbsSimModel model, AbsAgent ag, double ti) {
        AbsAgentBasedModel m = (AbsAgentBasedModel) model;
        m.kill(ag.getName(), ti);
        Map<String, Object> atr = new HashMap<>();
        atr.put("st", S_birth);
        m.birth(1, ti, atr);
        BirthN ++;
    }

    @Override
    public void impulseEnter(AbsSimModel model, AbsAgent ag, double ti) {

    }

    @Override
    public void impulseExit(AbsSimModel model, AbsAgent ag, double ti) {

    }

    @Override
    public void fillData(Map<String, Double> obs, AbsSimModel model, double ti) {
        obs.put(getName(), BirthN);
    }

    @Override
    public void match(AbsBehaviour be_src, Map<String, AbsAgent> ags_src, Map<String, AbsAgent> ags_new, double ti) {
        BirthN = ((Reincarnation) be_src).BirthN;
    }

    @Override
    public void initialise(double ti, AbsSimModel model) {

    }

    @Override
    public void reset(double ti, AbsSimModel model) {

    }

    @Override
    public void shock(double ti, Object source, String target, Object value) {

    }

    @Override
    public String toString() {
        return String.format("Reincarnation(%s, Death:%s, Birth:%s, NBir:%s)", getName(), S_death.getName(), S_birth.getName(), BirthN);
    }
}