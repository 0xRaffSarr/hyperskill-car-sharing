package carsharing.model;

import carsharing.Executable;

public class Action {
    private String label;
    private Executable callback;

    public Action(String label, Executable callback) {
        this.label = label;
        this.callback = callback;
    }

    public String getLabel() {
        return this.label;
    }

    public Executable getCallback() {
        return this.callback;
    }
}
