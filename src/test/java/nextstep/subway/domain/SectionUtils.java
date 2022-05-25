package nextstep.subway.domain;

public final class SectionUtils {

    private SectionUtils() {
    }

    public static Section generateSection(String upStationName, String downStationName, long distance) {
        Station upStation = new Station(upStationName);
        Station downStation = new Station(downStationName);
        return new Section(upStation, downStation, distance);
    }
}
