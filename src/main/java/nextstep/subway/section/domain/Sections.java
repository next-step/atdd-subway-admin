package nextstep.subway.section.domain;

import nextstep.subway.exception.NotFoundException;
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
<<<<<<< Updated upstream
    private static final String NOT_FOUND_START_SECTION = "시작 구간을 찾을 수 없습니다.";
=======
    private static final String NOT_EXISTS_SECTION = "기존에 존재하지 않는 구간 입니다.";
    private static final String ALREADY_EXISTS_SECTION = "이미 존재하는 구간 입니다.";
    private static final String SECTION_LENGTH_TO_SHORT = "구간이 하나인 노선이라 삭제가 불가능 합니다.";
    private static final int MINIMUM_STATION_LENGTH = 2;
>>>>>>> Stashed changes

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "line")
    private final List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        this.sections.add(section);
    }

    public List<Station> stations() {
        List<Station> stations = new ArrayList<>();

        Section section = sections.stream()
                .filter(startSection -> startSection.beforeSection() == null)
                .findFirst()
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_START_SECTION));

        stations.add(section.upStation());
        stations.add(section.downStation());

        while (section.nextSection() != null) {
            section = section.nextSection();
            stations.add(section.downStation());
        }

        return Collections.unmodifiableList(stations);
    }

    public void toSection(Section section) {
        validateStationNoneMatch(section);

        upStationBeforeAddIfExists(section);
        upStationAfterAddIfExists(section);
        downStationBeforeAddIfExists(section);
        downStationAfterAddIfExists(section);

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
                .orElseThrow(() -> new SectionCreateFailException("기존에 존재하지 않는 구간 입니다."));
    }

    private void upStationBeforeAddIfExists(Section newSection) {
        sections.stream()
                .filter(oriSection -> oriSection.upStation().equals(newSection.downStation()))
                .findFirst()
                .ifPresent(oriSection -> oriSection.upStationBeforeAdd(newSection));
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

<<<<<<< Updated upstream
    private void downStationAfterAddIfExists(Section newSection) {
        sections.stream()
                .filter(oriSection -> oriSection.downStation().equals(newSection.upStation()))
                .findFirst()
                .ifPresent(beforeSection -> beforeSection.downStationAfterAdd(newSection));
=======
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
>>>>>>> Stashed changes
    }
}
