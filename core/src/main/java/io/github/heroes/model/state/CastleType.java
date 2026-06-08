package io.github.heroes.model.state;

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
