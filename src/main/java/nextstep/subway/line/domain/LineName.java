package nextstep.subway.line.domain;

import nextstep.subway.consts.ErrorMessage;
import nextstep.subway.station.domain.StationName;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class LineName {
    @Column
    String name;

    protected LineName() {
    }

    private LineName(String name) {
        validateLineName(name);
        this.name = name;
    }

    private void validateLineName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.ERROR_LINENAME_EMPTY);
        }
    }

    public static LineName from(String name) {
        return new LineName(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof LineName)) {
            return false;
        }
        return ((LineName)obj).getName() == name;
    }
}
