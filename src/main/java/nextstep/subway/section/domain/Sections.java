package nextstep.subway.section.domain;

import nextstep.subway.exception.SectionCreateFailException;
import nextstep.subway.exception.SectionDeleteFailException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {
    private static final String NOT_EXISTS_SECTION = "기존에 존재하지 않는 구간 입니다.";
    private static final String ALREADY_EXISTS_SECTION = "이미 존재하는 구간 입니다.";
    private static final String SECTION_LENGTH_TO_SHORT = "구간이 하나인 노선이라 삭제가 불가능 합니다.";
    private static final int MINIMUM_STATION_LENGTH = 2;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "line")
    private final List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        this.sections.add(section);
    }

    public List<Station> stations() {
        List<Station> stations = new ArrayList<>();

        Section section = this.sections.get(0);

        section = findFirstSection(section);
        stations.add(section.upStation());
        stations.add(section.downStation());

        stations(stations, section.downStation());

        return Collections.unmodifiableList(stations);
    }

    private void stations(List<Station> stations, Station downStation) {
        sections.stream()
                .filter(findSection -> findSection.upStation().equals(downStation))
                .findFirst()
                .ifPresent(section -> {
                    Station nextStation = section.downStation();
                    stations.add(nextStation);
                    stations(stations, nextStation);
                });
    }

    private Section findFirstSection(Section section) {
        Section beforeSection = this.sections.stream()
                .filter(findSection -> findSection.downStation().equals(section.upStation()))
                .findFirst()
                .orElse(section);

        if (beforeSection.equals(section)) {
            return section;
        }

        return findFirstSection(beforeSection);
    }

    public void toSection(Section section) {
        validateStationNoneMatch(section);
        validateStationAllMatch(section);
        upStationAfterAddIfExists(section);
        downStationBeforeAddIfExists(section);

        this.sections.add(section);
    }

    public void deleteSection(Station deleteStation) {
        validateStationNoneMatch(deleteStation);
        validateStationsLength();
        sectionDelete(deleteStation);
    }

    private void validateStationNoneMatch(Section section) {
        this.stations().stream()
                .filter(station -> station.equals(section.upStation()) || station.equals(section.downStation()))
                .findFirst()
                .orElseThrow(() -> new SectionCreateFailException(NOT_EXISTS_SECTION));
    }

    private void validateStationAllMatch(Section section) {
        boolean matchedUpStation = this.stations().stream()
                .anyMatch(station -> station.equals(section.upStation()));

        boolean matchedDownStation = this.stations().stream()
                .anyMatch(station -> station.equals(section.downStation()));

        if (matchedUpStation && matchedDownStation) {
            throw new SectionCreateFailException(ALREADY_EXISTS_SECTION);
        }
    }

    private void upStationAfterAddIfExists(Section newSection) {
        sections.stream()
                .filter(oriSection -> oriSection.upStation().equals(newSection.upStation()))
                .findFirst()
                .ifPresent(oriSection -> oriSection.upStationAfterAdd(newSection));
    }

    private void downStationBeforeAddIfExists(Section newSection) {
        sections.stream()
                .filter(oriSection -> oriSection.downStation().equals(newSection.downStation()))
                .findFirst()
                .ifPresent(oriSection -> oriSection.downStationBeforeAdd(newSection));
    }

    private void validateStationNoneMatch(Station deleteStation) {
        this.stations().stream()
                .filter(station -> station.equals(deleteStation))
                .findFirst()
                .orElseThrow(() -> new SectionCreateFailException(NOT_EXISTS_SECTION));
    }

    private void validateStationsLength() {
        if (this.stations().size() <= MINIMUM_STATION_LENGTH) {
            throw new SectionDeleteFailException(SECTION_LENGTH_TO_SHORT);
        }
    }

    private void sectionDelete(Station deleteStation) {
        Optional<Section> sameUpStationSection = this.sections.stream()
                .filter(section -> section.upStation().equals(deleteStation))
                .findFirst();

        Optional<Section> sameDownStationSection = this.sections.stream()
                .filter(section -> section.downStation().equals(deleteStation))
                .findFirst();

        deleteIfFirstSection(sameUpStationSection, sameDownStationSection);
        deleteIfBetweenSection(sameUpStationSection, sameDownStationSection);
        deleteIfLastSection(sameUpStationSection, sameDownStationSection);
        System.out.println(sections);
    }

    private void deleteIfFirstSection(Optional<Section> sameUpStationSection, Optional<Section> sameDownStationSection) {
        if ((sameUpStationSection.isPresent()) && (!sameDownStationSection.isPresent())) {
            sections.remove(sameUpStationSection.get());
        }
    }

    private void deleteIfBetweenSection(Optional<Section> sameUpStationSection, Optional<Section> sameDownStationSection) {
        if (sameUpStationSection.isPresent() && sameDownStationSection.isPresent()) {
            Section changeSection = sameUpStationSection.get();
            Section deleteSection = sameDownStationSection.get();
            changeSection.upStationAfterDelete(deleteSection);
            sections.remove(deleteSection);
        }
    }

    private void deleteIfLastSection(Optional<Section> sameUpStationSection, Optional<Section> sameDownStationSection) {
        if ((!sameUpStationSection.isPresent()) && (sameDownStationSection.isPresent())) {
            sections.remove(sameDownStationSection.get());
        }
    }
}
