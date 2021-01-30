package uk.ac.ox.ndph.mts.init_service.model;

public class Site implements Entity {
    private String name;
    private String alias;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        return String.format("Site{name='%s', alias='%s'}", name, alias);
    }
}
