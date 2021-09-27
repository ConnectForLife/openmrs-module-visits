package org.openmrs.module.visits.builder;

import org.openmrs.Location;

public class LocationBuilder extends AbstractBuilder<Location> {

    private Integer id;
    private String name;

    public LocationBuilder() {
        super();
        name = "Some location" + id;
    }

    @Override
    public Location build() {
        Location location = buildAsNew();
        location.setId(id == null ? getAndIncrementNumber() : null);
        return location;
    }

    @Override
    public Location buildAsNew() {
        Location location = new Location(this.id);
        location.setName(this.name);
        return location;
    }

    public LocationBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public LocationBuilder withName(String name) {
        this.name = name;
        return this;
    }
}
