package com.opt.tsp.data;

public class IntNode {
    private int value;

    public IntNode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "IntNode{" +
                "value=" + value +
                '}';
    }
}
