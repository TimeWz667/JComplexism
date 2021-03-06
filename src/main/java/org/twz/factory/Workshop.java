package org.twz.factory;

import org.json.JSONException;
import org.junit.internal.runners.InitializationError;
import org.twz.factory.arguments.AbsArgument;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Workshop is a builder object which can build various object with (name, args)
 * Created by TimeWz on 2017/11/3.
 */
public class Workshop<T> {
    private Map<String, Object> Resources;
    private Map<String, Creator<? extends T>> Creators;

    public Workshop() {
        Resources = new HashMap<>();
        Creators = new HashMap<>();
    }

    public Object getResource(String name) {
        return Resources.get(name);
    }

    public boolean hasResource(String name) {
        return Resources.containsKey(name);
    }

    public void clearResource() {
        Resources.clear();
    }

    public void appendResource(String name, Object resource) {
        Resources.put(name, resource);
    }

    public void register(String tp, Class<? extends T> cls, AbsArgument[] args) {
        Creator<? extends T> cr = new Creator<>(tp, cls, args);
        Creators.put(tp, cr);
    }

    public T create(JSONObject js) throws InstantiationError {

        try {
            String name = (js.has("Name"))? js.getString("Name"): js.getString("Type");
            String type = js.getString("Type");
            JSONObject args = js.getJSONObject("Args");
            return create(name, type, args);
        } catch (JSONException e) {
            throw new InstantiationError(e.getMessage());
        }


    }

    public T create(String name, String type, JSONObject args) throws InstantiationError {
        Creator<? extends T> cr = Creators.get(type);
        try {
            return cr.create(name, args, this);
        } catch (JSONException e) {
            throw new InstantiationError(e.toString());
        }
    }

    public T create(String name, String type, String[] args) throws InstantiationError, NullPointerException {
        Creator<? extends T> cr = Creators.get(type);
        return cr.create(name, args, this);
    }

    public Set<String> getCreators() {
        return Creators.keySet();
    }

    public boolean hasCreator(String name) {
        return Creators.containsKey(name);
    }

    public void listCreators() {
        Creators.keySet().forEach(System.out::println);
    }

}
