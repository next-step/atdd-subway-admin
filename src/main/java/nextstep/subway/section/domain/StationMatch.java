package nextstep.subway.section.domain;

import nextstep.subway.exception.SectionCreateFailException;
import nextstep.subway.station.domain.Station;

import java.util.Objects;

public enum StationMatch {
    UP_STATION,
    DOWN_STATION,
    NOT_MATCHED;

    public static StationMatch match(Section section, Station station) {
        if (Objects.isNull(section)) {
            return NOT_MATCHED;
        }

        if (compareUpStation(section, station)) {
            return UP_STATION;
        }

        if (compareDownStation(section, station)) {
            return DOWN_STATION;
        }

        throw new SectionCreateFailException();
    }

    private static boolean compareUpStation(Section section, Station station) {
        return (section.isSameUpStation(station)) && (!section.isSameDownStation(station));
    }

    private static boolean compareDownStation(Section section, Station station) {
        return (!section.isSameUpStation(station)) && (section.isSameDownStation(station));
    }
}
