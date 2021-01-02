package nextstep.subway.section.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Sections {
    public static final String SECTION_ALREADY_EXIST_ERROR_MESSAGE = "노선에 이미 구간이 등록되어 있습니다.";
    public static final String NOT_MATCH_STATION_ERROR_MESSAGE = "노선에 선택한 상행역과 하행역 둘다 포함되어 있지 않습니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections(Section upSection, Section downSection) {
        this.sections = Arrays.asList(upSection, downSection);
    }

    public List<Station> getStations() {
        return this.sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    public void addSection(Section newSection) {
        boolean isUpStationInSection = findStationInSection(newSection.getUpStation());
        boolean isDownStationInSection = findStationInSection(newSection.getDownStation());
        validateSection(isUpStationInSection, isDownStationInSection);

        if (isUpStationInSection) {
            this.sections.stream()
                    .filter(oldSection -> oldSection.isUpStationInSection(newSection.getUpStation()))
                    .findFirst()
                    .ifPresent(section -> section.updateUpStationToDownStation(newSection.getDownStation(), newSection.getDistance()));
        }

        if (isDownStationInSection) {
            this.sections.stream()
                    .filter(oldSection -> oldSection.isDownStationInSection(newSection.getDownStation()))
                    .findFirst()
                    .ifPresent(section -> section.updateDownStationToUpStation(newSection.getUpStation(), newSection.getDistance()));
        }

        this.sections.add(newSection);
    }

    private void validateSection(boolean isUpStationInSection, boolean isDownStationInSection) {
        if (isUpStationInSection && isDownStationInSection) {
            throw new IllegalArgumentException(SECTION_ALREADY_EXIST_ERROR_MESSAGE);
        }

        if (!isUpStationInSection && !isDownStationInSection) {
            throw new IllegalArgumentException(NOT_MATCH_STATION_ERROR_MESSAGE);
        }
    }

    private boolean findStationInSection(Station station) {
        return this.sections.stream()
                .anyMatch(section -> section.isDownStationInSection(station));
    }
}
