package org.twz.graph;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 07/08/2018.
 */
public class DiGraph<T> implements Cloneable {
    protected Map<String, T> Nodes;
    protected Map<String, List<String>> Successor, Predecessor;

    public DiGraph() {
        Nodes = new HashMap<>();
        Successor = new HashMap<>();
        Predecessor = new HashMap<>();
    }

    public T getNode(String node) {
        return Nodes.get(node);
    }

    public boolean has(String node) {
        return Nodes.containsKey(node);
    }

    public List<String> getParents(String node) {
        return Predecessor.get(node);
    }

    public List<T> getParentNodes(String node) {
        return Predecessor.get(node).stream().map(e->Nodes.get(e)).collect(Collectors.toList());
    }

    public boolean hasParent(String node) {
        return !Predecessor.get(node).isEmpty();
    }

    public List<String> getChildren(String node) {
        return Successor.get(node);
    }

    public List<T> getChildNodes(String node) {
        return Successor.get(node).stream().map(e->Nodes.get(e)).collect(Collectors.toList());
    }

    public boolean hasChild(String node) {
        return !Successor.get(node).isEmpty();
    }

    public List<String> getAncestors(String node) {
        Set<String> anc = new HashSet<>(), temp;
        List<String> querying = getParents(node);

        while(!querying.isEmpty()) {
            anc.addAll(querying);
            temp = new HashSet<>();

            querying.stream().map(this::getParents).forEach(temp::addAll);
            querying = temp.stream().filter(e->!anc.contains(e)).collect(Collectors.toList());
        }
        return new ArrayList<>(anc);
    }

    public List<T> getAncestorNodes(String node) {
        return getAncestors(node).stream().map(e->Nodes.get(e)).collect(Collectors.toList());
    }

    public List<String> getDescendants(String node) {
        Set<String> des = new HashSet<>(), temp;
        List<String> querying = getChildren(node);

        while(!querying.isEmpty()) {
            des.addAll(querying);
            temp = new HashSet<>();

            querying.stream().map(this::getChildren).forEach(temp::addAll);
            querying = temp.stream().filter(e->!des.contains(e)).collect(Collectors.toList());
        }
        return new ArrayList<>(des);
    }

    public List<T> getDescendantNodes(String node) {
        return getDescendants(node).stream().map(e->Nodes.get(e)).collect(Collectors.toList());
    }

    public DiGraph<T> getMinimalDAG(Collection<String> nodes) {
        DiGraph<T> sub = this.clone();
        sub.removeUpstream(nodes);
        sub.removeDownstream(nodes);
        return sub;

    }

    public List<String> getMediators(String node, Collection<String> source) {
        List<String> anc = getAncestors(node);
        DiGraph<T> sub = getMinimalDAG(anc);
        anc.retainAll(source);
        sub.removeUpstream(anc);
        List<String> res = new ArrayList<>(sub.Nodes.keySet());
        res.removeAll(source);
        return res;
    }

    public List<String> getMinimalRequirement(String node, Collection<String> given) {
        List<String> anc = getAncestors(node);
        for (String s : given) {
            if (anc.contains(s)) {
                anc.removeAll(getAncestors(s));
            }
        }
        return sort(anc);
    }

    public List<String> getMinimalRequirement(Collection<String> nodes, Collection<String> given) {
        Set<String> req = new HashSet<>();
        for (String node : nodes) {
            req.addAll(getMinimalRequirement(node, given));
        }
        return sort(req);
    }

    public List<String> getLeaves() {
        return Successor.entrySet().stream()
                .filter(ent -> ent.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<String> getRoots() {
        return Predecessor.entrySet().stream()
                .filter(ent -> ent.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void addNode(String name, T node) {
        if (!Nodes.containsKey(name)) {
            Nodes.put(name, node);
            Successor.putIfAbsent(name, new ArrayList<>());
            Predecessor.putIfAbsent(name, new ArrayList<>());
        } else if (node != null){
            Nodes.replace(name, node);
        }
    }

    public void addNode(String name) {
        addNode(name, null);
    }

    public void removeNode(String node) {
        Nodes.remove(node);
        removeInEdges(node);
        removeOutEdge(node);
        Successor.remove(node);
        Predecessor.remove(node);
    }

    public void removeUpstream(Collection<String> nodes) {
        List<String> ord = getOrder();
        for (String s : ord) {
            if (nodes.contains(s)) continue;
            if (getAncestors(s).stream().noneMatch(nodes::contains)) removeNode(s);
        }
    }

    public void removeDownstream(Collection<String> nodes) {
        List<String> ord = getOrder();
        for (String s : ord) {
            if (nodes.contains(s)) continue;
            if (getDescendants(s).stream().noneMatch(nodes::contains)) removeNode(s);
        }
    }

    public void addEdge(String source, String target) {
        addNode(source);
        addNode(target);
        Successor.get(source).add(target);
        Predecessor.get(target).add(source);
    }

    public void removeEdge(String source, String target) {
        Successor.get(source).remove(target);
        Predecessor.get(target).remove(source);
    }

    public void removeInEdges(String node) {
        List<String> pa = Predecessor.get(node);
        for (String s : pa) {
            Successor.get(s).remove(node);
        }
        pa.clear();
    }

    public void removeOutEdge(String node) {
        List<String> pa = Successor.get(node);
        for (String s : pa) {
            Predecessor.get(s).remove(node);
        }
        pa.clear();
    }

    public int getOutDegree(String nod) {
        return Successor.get(nod).size();
    }

    public int getInDegree(String nod) {
        return Predecessor.get(nod).size();
    }

    public int getDegree(String nod) {
        return getInDegree(nod) + getOutDegree(nod);
    }

    public double getAvgOutDegree() {
        OptionalDouble v = Successor.values().stream().mapToInt(List::size).average();
        if (v.isPresent()) {
            return v.getAsDouble();
        } else {
            return 0;
        }
    }

    public double getAvgInDegree() {
        OptionalDouble v = Predecessor.values().stream().mapToInt(List::size).average();
        if (v.isPresent()) {
            return v.getAsDouble();
        } else {
            return 0;
        }
    }

    public double getAvgDegree() {
        return getAvgInDegree() + getAvgOutDegree();
    }

    public boolean isAcyclic() {
        DiGraph<T> di = clone();
        List<String> lvs;
        while(!di.isEmpty()) {
            lvs = di.getLeaves();
            if (lvs.isEmpty()) {
                return false;
            }
            lvs.forEach(di::removeNode);
        }
        return true;
    }

    public List<String> getOrder() {
        List<String> order = new ArrayList<>();

        Set<String> querying, waiting = new HashSet<>(Nodes.keySet());

        while(!waiting.isEmpty()) {
            querying = waiting.stream()
                    .filter(s -> order.containsAll(getParents(s)))
                    .collect(Collectors.toSet());

            if (querying.isEmpty()) {
                break;
            }
            order.addAll(querying);

            waiting.removeAll(querying);
        }
        return order;
    }

    public List<String> sort(Collection<String> nodes) {
        List<String> res = getOrder();
        res.retainAll(nodes);
        return res;
    }

    public int size() {
        return Nodes.size();
    }

    public boolean isEmpty() {
        return Nodes.isEmpty();
    }

    public DiGraph<T> clone() {
        DiGraph<T> di = new DiGraph<>();
        Nodes.forEach(di::addNode);

        di.Nodes.putAll(this.Nodes);
        for (Map.Entry<String, List<String>> entry : Successor.entrySet()) {
            entry.getValue().forEach(e->di.addEdge(entry.getKey(), e));
        }

        return di;
    }
}
