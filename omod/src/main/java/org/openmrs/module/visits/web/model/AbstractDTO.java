package org.openmrs.module.visits.web.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public abstract class AbstractDTO implements Serializable {

    private static final long serialVersionUID = 1533338555501675215L;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
