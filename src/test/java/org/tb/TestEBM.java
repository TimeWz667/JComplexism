package org.tb;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import org.twz.cx.Director;
import org.twz.cx.ebmodel.EBMY0;
import org.twz.cx.mcore.IY0;
import org.twz.dag.Parameters;
import org.twz.dataframe.TimeSeries;
import org.twz.dataframe.demographics.SexDemography;
import org.twz.fit.ABC;
import org.twz.fit.GeneticAlgorithm;


import java.util.Map;

public class TestEBM {

    private ReducedTB BM;
    private int StartYear = 1990;

    @Before
    public void setUp() throws JSONException {
        Director da = new Director();
        da.loadBayesNet("src/test/resources/tb/tb.txt");
        SexDemography demoSex = SexDemography.readCSV("src/test/resources/SimFM.csv", "Year",
                "PopF", "PopM", "DeathF", "DeathM",
                "BirthF", "BirthM", "MigrationF", "MigrationM");

        ReducedTB.setUpModel(da, demoSex, StartYear, "s");

        TimeSeries Noti = TimeSeries.readCSV("src/test/resources/tb/NotiYear.csv", "Year");
        BM = new ReducedTB(da, demoSex, StartYear, Noti);

    }

    @Test
    public void fit() {
        ABC alg = new ABC(500);
        alg.onLog();
        BM.fit(alg);
        BM.saveMementosByVariable("tb/S" + StartYear, "Posterior","Post_", ".csv");
        BM.generatePrior(500);
        BM.saveMementosByVariable("tb/S" + StartYear, "Prior", "Prior_", ".csv");
    }

    @Test
    public void fitMLE() {
        GeneticAlgorithm alg = new GeneticAlgorithm(BM.getMovableNodes());
        alg.setOption("Target", "MLE");
        alg.onLog();
        BM.fit(alg);
        BM.saveMementosBySimulation("tb/S" + StartYear,
                "MLE","MLE_", ".csv");

        alg.setOption("Target", "MAP");
        alg.onLog();
        BM.fit(alg);
        BM.saveMementosBySimulation("tb/S" + StartYear,
                "MAP","MAP_", ".csv");
    }

    @Test
    public void simulationDaBN() throws NullPointerException {
        Parameters pc = (Parameters) BM.samplePrior();
        System.out.println(pc);
        IY0 y0 = BM.sampleY0(pc);

        for(Map.Entry<String, Double> ent : ((EBMY0) y0).toMap().entrySet()) {
            System.out.println(ent.getKey() + ": \t"+ ent.getValue());
        }
        System.out.println();

        y0 = BM.warmUp(pc);

        for(Map.Entry<String, Double> ent : ((EBMY0) y0).toMap().entrySet()) {
            System.out.println(ent.getKey() + ": \t"+ ent.getValue());
        }
        System.out.println();

        TimeSeries ts = BM.simulate(pc, y0);
        ts.print();
        ts.toCSV("tb/Ts.csv");
        System.out.println(pc.getDeepLogPrior());

        System.out.println(BM.calculateLogLikelihood(pc, ts));
    }

}
