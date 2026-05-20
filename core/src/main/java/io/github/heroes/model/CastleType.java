package io.github.heroes.model;

public enum CastleType {
    CASTLE("Castle"),
    INFERNO("Inferno"),
    STRONGHOLD("Stronghold");

    private final String name;

    CastleType(String name) {
        this.name = name;
    }

    public String getName() { return name; }
}
