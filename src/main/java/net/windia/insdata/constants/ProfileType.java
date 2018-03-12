package net.windia.insdata.constants;

public enum ProfileType {

    INSTAGRAM("instagram");

    private String value;

    ProfileType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
