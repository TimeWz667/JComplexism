package org.twz.cx.mcore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LeafY0 implements IY0 {
    private List<JSONObject> Entries;

    public LeafY0() {
        Entries = new ArrayList<>();
    }

    public LeafY0(JSONObject js) throws JSONException {
        this(js.getJSONArray("Entries"));
    }

    public LeafY0(JSONArray jar) throws JSONException {
        this();
        for (int i = 0; i < jar.length(); i++) {
            Entries.add(jar.getJSONObject(i));
        }
    }

    @Override
    public void matchModelInfo(AbsSimModel model) {

    }

    @Override
    public void append(JSONObject ent) {
        Entries.add(ent);
    }

    @Override
    public void append(String ent) throws JSONException {
        append(new JSONObject(ent));
    }

    @Override
    public Collection<JSONObject> getEntries() {
        return Entries;
    }

    @Override
    public IY0 adaptTo(JSONObject src) {
        try {
            return this.getClass().getConstructor(JSONObject.class).newInstance(src);
        } catch (InstantiationException | IllegalAccessException |InvocationTargetException | NoSuchMethodException  e) {
            e.printStackTrace();
        }
        return new LeafY0();
    }

    @Override
    public IY0 adaptTo(JSONArray src) {
        try {
            return this.getClass().getConstructor(JSONArray.class).newInstance(src);
        } catch (InstantiationException | IllegalAccessException |InvocationTargetException | NoSuchMethodException  e) {
            e.printStackTrace();
        }
        return new LeafY0();
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("Entries", Entries);
        js.put("Type", "Leaf");
        return js;
    }

    public LeafY0 clone() {
        LeafY0 clone = null;
        try {
            clone = (LeafY0) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }
}
