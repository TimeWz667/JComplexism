package org.twz.cx.element;

import org.twz.dataframe.Pair;
import java.util.LinkedList;
import java.util.stream.Collectors;


/**
 *
 * Created by TimeWz on 10/02/2017.
 */
public class Request implements Comparable<Request> {
    private final LinkedList<String> Where;
    public final String Who;
    public final Event Todo;

    public Request(Event event, String node, String loc) {
        Todo = event;
        Who = node;
        Where = new LinkedList<>();
        Where.add(loc);
    }

    public Request(Event event, String node, LinkedList<String> loc) {
        Todo = event;
        Who = node;
        Where = loc;
    }

    public String getMessage() {
        return Todo.getMessage();
    }

    public double getTime() {
        return Todo.getTime();
    }

    public String getAddress() {
        return Where.stream().collect(Collectors.joining("@"));
    }

    public String getGroup() {
        return Where.getLast();
    }

    public String getSource() {
        return Where.getFirst();
    }

    Request upScale(String adr) {
        LinkedList<String> new_adr = new LinkedList<>(Where);
        new_adr.add(adr);
        return new Request(Todo, Who, new_adr);
    }


    Pair<String, Request> downScale() {
        String gp = getGroup();
        LinkedList<String> new_adr = new LinkedList<>(Where);
        new_adr.pollLast();
        return new Pair<>(gp, new Request(Todo, Who, new_adr));
    }

    boolean reached() {
        return Where.size() == 1;
    }

    public Disclosure disclose() {
        return new Disclosure(getMessage(), Who, new LinkedList<>(Where));
    }

    @Override
    public String toString() {
        return "Request{" +
                "What='" + getMessage() + '\'' +
                ", Who='" + Who + '\'' +
                ", Where=" + getAddress() +
                '}';
    }

    public String toLog() {
        return String.format("Request:  %s want to %s in %s when %.3f", Who, getMessage(), getAddress(), getTime());
    }

    @Override
    public int compareTo(Request o) {
        return Double.compare(this.getTime(), o.getTime());
    }

}
