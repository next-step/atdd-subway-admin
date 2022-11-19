package nextstep.subway.domain;

import java.util.List;

public class SectionsValidator {
    private static final String ALREADY_CONTAIONS_ERROR_MESSAGE = "상행역과 하행역이 이미 노선에 모두 등록되어 있어 추가할 수 없습니다.";
    private static final String NOT_CONTAIONS_ANY_ERROR_MESSAGE = "상행역과 하행역 둘 중 하나도 포함되어있지 않아 추가할 수 없습니다.";
    private static final String NOT_CONTAIONS_STATION_ERROR_MESSAGE = "노선에 등록되어있지 않은 역을 제거할 수 없습니다.";
    public static final String ONLY_SECTION_ERROR_MESSAGE = "구간이 하나인 노선의 역을 제거할 수 없습니다.";

    private SectionsValidator() {
    }

    public static void validateAlreadyContainsAll(Stations stations, Section newSection) {
        if (stations.containsAll(newSection.getStations())) {
            throw new IllegalArgumentException(ALREADY_CONTAIONS_ERROR_MESSAGE);
        }
    }

    public static void validateNotContainsAny(Stations stations, Section newSection) {
        if (stations.isEmpty()) {
            return;
        }
        if (newSection.nonMatch(stations)) {
            throw new IllegalArgumentException(NOT_CONTAIONS_ANY_ERROR_MESSAGE);
        }
    }

    public static void validateNotContainsStation(Stations stations, Station station) {
        if (stations.notContains(station)) {
            throw new IllegalArgumentException(NOT_CONTAIONS_STATION_ERROR_MESSAGE);
        }
    }

    public static void validateOnlySection(List<Section> sections) {
        if (sections.size() == 1) {
            throw new IllegalArgumentException(ONLY_SECTION_ERROR_MESSAGE);
        }
    }
}
