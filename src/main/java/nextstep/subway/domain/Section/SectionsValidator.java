package nextstep.subway.domain.Section;

import static nextstep.subway.message.ErrorMessage.SECTION_ADD_DISTANCE_IS_BIG;
import static nextstep.subway.message.ErrorMessage.SECTION_STATION_IS_NO_SEARCH;
import static nextstep.subway.message.ErrorMessage.SECTION_STATION_NO_DELETE_RESON_ONE_SECTION;
import static nextstep.subway.message.ErrorMessage.SECTION_UP_STATION_AND_DOWN_STATION_EXIST;
import static nextstep.subway.message.ErrorMessage.SECTION_UP_STATION_AND_DOWN_STATION_NO_EXIST;

import java.util.HashSet;
import java.util.Set;
import nextstep.subway.domain.Station;

class SectionsValidator {
    private static final int INIT_SELECTIONS_SIZE = 1;

    static void validDeleteSectionStation(Sections sections, Station station) {
        if (sections.sectionList().size() == INIT_SELECTIONS_SIZE) {
            throw new IllegalStateException(SECTION_STATION_NO_DELETE_RESON_ONE_SECTION.toMessage());
        }

        if (!isContainStation(sections, station)) {
            throw new IllegalStateException(SECTION_STATION_IS_NO_SEARCH.toMessage());
        }
    }

    private static boolean isContainStation(Sections sections, Station station) {
        return sections.sectionList().stream()
                .anyMatch(section -> section.getUpStation().equals(station)
                        || section.getDownStation().equals(station));
    }


    static void validExistUpStationAndDownStation(Sections sections, Section newSection) {
        if (sections.hasUpStation(newSection.getUpStation()) && sections.hasDownStation(newSection.getDownStation())) {
            throw new IllegalArgumentException(SECTION_UP_STATION_AND_DOWN_STATION_EXIST.toMessage());
        }
    }

    static void validNoExistUpStationAndDownStation(Sections sections, Section newSection) {
        Set<Station> distinctSet = new HashSet<>();
        sections.sectionList().forEach((section -> {
            distinctSet.add(section.getDownStation());
            distinctSet.add(section.getUpStation());
        }));

        if (!distinctSet.contains(newSection.getUpStation()) && !distinctSet.contains(newSection.getDownStation())) {
            throw new IllegalStateException(SECTION_UP_STATION_AND_DOWN_STATION_NO_EXIST.toMessage());
        }
    }

    static void validAddSectionDistance(Section newSection, Section beforeSection) {
        if (!newSection.getDistance().isLess(beforeSection.getDistance())) {
            throw new IllegalStateException(SECTION_ADD_DISTANCE_IS_BIG.toMessage());
        }
    }

}
