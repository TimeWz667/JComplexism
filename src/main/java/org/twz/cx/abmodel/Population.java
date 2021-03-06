package org.twz.cx.abmodel;

import org.json.JSONException;
import org.twz.cx.abmodel.network.AbsNetwork;
import org.twz.cx.abmodel.network.NetworkSet;
import org.twz.io.AdapterJSONObject;
import org.json.JSONArray;
import org.json.JSONObject;


import java.util.*;


/**
 *
 * Created by TimeWz on 2017/6/16.
 */
public class Population<T extends AbsAgent> implements AdapterJSONObject {
    private AbsBreeder<T> Eva;
    private Map<String, T> Agents;
    private NetworkSet Networks;


    public Population(AbsBreeder<T> eva) {
        Eva = eva;
        Agents = new HashMap<>();
        Networks = new NetworkSet();
    }

    public AbsBreeder<T> getEva() {
        return Eva;
    }

    public void addNetwork(AbsNetwork net) {
        Networks.append(net);
    }

    public T get(String name) {
        return Agents.get(name);
    }

    public Map<String, T> getAgents() {
        return Agents;
    }

    public List<T> addAgents(int n, Map<String, Object> info) throws JSONException {
        List<T> ags = Eva.breed(n, info);
        for (T ag: ags) {
            Agents.put(ag.getName(), ag);
            Networks.addAgent(ag);
        }
        return ags;
    }

    public boolean hasAgent(String id) {
        return Agents.containsKey(id);
    }

    public AbsAgent removeAgent(String id) {
        AbsAgent ag;
        try {
            ag = Agents.remove(id);
            Networks.removeAgent(ag);
            return ag;
        } catch (NullPointerException ex) {
            return null;
        }
    }

    public void reformNetworks(String net) {
        Networks.reform(net);
    }

    public long count(String key, Object value) {
        long count = 0L;
        for (T ag : Agents.values()) {
            if (ag.isa(key, value)) {
                count++;
            }
        }
        return count;
    }

    public long count(Map<String, Object> kvs) {
        return Agents.values().stream().filter(ag -> ag.isa(kvs)).count();
    }


    public long count() {
        return Agents.size();
    }

    public double averageParameter(String key) {
        return Agents.values().stream().mapToDouble(ag->ag.getParameter(key)).average().orElse(0);
    }

    @Override
    public JSONObject toJSON() {
        // todo
        return null;
    }

    public JSONArray getSnapshot() throws JSONException {
        JSONArray js = new JSONArray();
        for (T ag: Agents.values()) {
            js.put(ag.toData());
        }
        return js;
    }
}
