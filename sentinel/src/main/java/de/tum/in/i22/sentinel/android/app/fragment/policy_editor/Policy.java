package de.tum.in.i22.sentinel.android.app.fragment.policy_editor;

/**
 * Created by laurentmeyer on 23/12/15.
 */
public class Policy extends XMLElement {

    boolean deployed;
    String name;
    PreventiveMechanism mechanism;
    boolean standalone;

    Policy() {
        isContainer = true;
        elementXMLName = "policy";
    }

    public boolean isDeployed() {
        return deployed;
    }

    public void setDeployed(boolean deployed) {
        this.deployed = deployed;
    }

    public PreventiveMechanism getMechanism() {
        return mechanism;
    }

    public void setMechanism(PreventiveMechanism mechanism) {
        this.mechanism = mechanism;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStandalone() {
        return standalone;
    }

    public void setStandalone(boolean standalone) {
        this.standalone = standalone;
    }


    @Override
    public String toString() {
        return "<?xml version='1.0' standalone='" + (standalone ? "yes" : "no") + "'?>\n" + super.toString();
    }

    @Override
    String createAttributeString() {
        return null;
    }

    @Override
    String createValueString() {
        return null;
    }
}