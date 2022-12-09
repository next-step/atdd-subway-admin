package nextstep.subway.domain.station;

public enum StationPosition {
    UPSTATION,
    DOWNSTATION,
    NONE;

    public boolean isNotNone() {
        return this != NONE;
    }

    public boolean isUpstation() {
        return this == UPSTATION;
    }

    public boolean isDownStation() {
        return this == DOWNSTATION;
    }
}
