package org.twz.cx.abmodel.statespace;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.twz.cx.Director;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.ParameterCore;
import org.twz.statespace.AbsStateSpace;

import java.util.HashMap;
import java.util.Map;

public class StSpABMBlueprintTest {
    private Director Ctrl;
    private StSpABMBlueprint Bp;
    private StSpY0 Y0;

    @Before
    public void setUp() throws JSONException {
        Ctrl = new Director();
        Ctrl.loadBayesNet("src/test/resources/script/pCloseSIR.txt");
        Ctrl.loadStateSpace("src/test/resources/script/CloseSIR.txt");

        Bp = new StSpABMBlueprint("SIR");
        Bp.setAgent("Ag", "agent", "CloseSIR");

        Map<String, Object> args = new HashMap<>();

        args.put("s_src", "Inf");
        args.put("t_tar", "Infect");
        Bp.addBehaviour("FOI", "FDShock", args);
        Bp.setObservations(new String[]{"Sus", "Inf", "Rec"}, new String[]{"Infect"}, new String[]{"FOI"});

        Y0 = new StSpY0();
        Y0.append(950, "Sus");
        Y0.append(50, "Inf");
    }

    @Test
    public void simulationPcDc() throws JSONException {
        Map<String, Object> args = new HashMap<>();

        ParameterCore PC = Ctrl.getBayesNet("pCloseSIR")
                .toSimulationCore(Bp.getParameterHierarchy(Ctrl), true)
                .generate("Test");
        AbsStateSpace DC = Ctrl.generateDCore("CloseSIR", PC.genPrototype("agent"));

        args.put("pc", PC);
        args.put("dc", DC);

        run(args);
    }

    @Test
    public void simulationDaBN() throws JSONException {
        Map<String, Object> args = new HashMap<>();

        args.put("bn", "pCloseSIR");
        args.put("da", Ctrl);

        run(args);
    }

    public void run(Map<String, Object> args) throws JSONException {
        StSpABModel Model = Bp.generate("Test", args);

        Simulator Simu = new Simulator(Model);
        //Simu.addLogPath("log/FDShock.txt");

        Simu.simulate(Y0, 0, 10, 1);
        Model.getObserver().getObservations().print();
    }
}