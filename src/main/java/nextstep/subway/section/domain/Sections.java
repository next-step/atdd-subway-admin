package nextstep.subway.section.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.distance.Distance;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sections {
    private static final String ERR_TEXT_INVALID_SECTION = "유효하지 않은 구간데이터입니다.";
    private static final String ERR_TEXT_NOT_EXIST_DATA = "해당 데이터가 존재하지 않습니다.";
    private static final String ERR_TEXT_EXIST_ONLY_ONE_SECTION = "구간이 하나밖에 존재하지 않는 경우 구간을 삭제할 수 없습니다.";
    private static final int EXIST_ONLY_ONE = 1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public static Sections newInstance() {
        return new Sections();
    }

    public List<Station> getStations() {
        final ArrayList<Station> stations = new ArrayList<>();
        Section currentSection = findFirstSection();
        stations.add(currentSection.getUpStation());

        while (currentSection != null) {
            final Station currDownStation = currentSection.getDownStation();
            stations.add(currDownStation);

            currentSection = findSection(section -> Objects.equals(section.getUpStation(), currDownStation)).orElse(null);
        }

        return stations;
    }

    private Section findFirstSection() {
        final List<Station> downStations = this.sections.stream()
            .map(Section::getDownStation)
            .collect(toList());

        return findSection(section -> !downStations.contains(section.getUpStation()))
            .orElseThrow(NotFoundException::new);
    }

    private Optional<Section> findSection(final Predicate<Section> predicate) {
        return this.sections.stream()
            .filter(predicate)
            .findFirst();
    }

    public void add(final Section newSection) {
        if (this.sections.isEmpty()) {
            this.sections.add(newSection);
            return;
        }

        validateNewSection(newSection);

        addSectionUpToUp(newSection);
        addSectionDownToDown(newSection);

        this.sections.add(newSection);
    }

    private void validateNewSection(final Section newSection) {
        final List<Station> allStations = getStations();
        if (isNotExistStation(newSection, allStations)) {
            throw new IllegalArgumentException(ERR_TEXT_INVALID_SECTION);
        }

        final List<Station> stationsInNewSections = Arrays.asList(newSection.getUpStation(), newSection.getDownStation());
        if (allStations.containsAll(stationsInNewSections)) {
            throw new IllegalArgumentException(ERR_TEXT_INVALID_SECTION);
        }
    }

    private boolean isNotExistStation(final Section newSection, final List<Station> allStations) {
        return !allStations.contains(newSection.getUpStation()) && !allStations.contains(newSection.getDownStation());
    }

    private void addSectionUpToUp(final Section newSection) {
        findSection(section -> section.isSameUpStation(newSection))
            .ifPresent(section -> replaceSectionWithDownStation(section, newSection));
    }

    private void replaceSectionWithDownStation(final Section originSection, final Section newSection) {
        if (originSection.distanceIsLessThan(newSection)) {
            throw new IllegalArgumentException(ERR_TEXT_INVALID_SECTION);
        }

        sections.add(new Section(
            originSection.getLine(),
            newSection.getDownStation(),
            originSection.getDownStation(),
            getMinusDistance(originSection, newSection)));
        this.sections.remove(originSection);
    }

    private void addSectionDownToDown(final Section newSection) {
        findSection(section -> section.isSameDownStation(newSection))
            .ifPresent(section -> replaceSectionWithUpStation(section, newSection));
    }

    private void replaceSectionWithUpStation(final Section originSection, final Section newSection) {
        if (originSection.distanceIsLessThan(newSection)) {
            throw new IllegalArgumentException(ERR_TEXT_INVALID_SECTION);
        }

        sections.add(new Section(
            originSection.getLine(),
            originSection.getUpStation(),
            newSection.getUpStation(),
            getMinusDistance(originSection, newSection)));
        this.sections.remove(originSection);
    }

    private Distance getMinusDistance(final Section originSection, final Section newSection) {
        return originSection.getDistance().minus(newSection.getDistance());
    }

    public void deleteByStation(final Station targetStation) {
        makeSureThatCanDeleteSection(targetStation);

        delete(targetStation);
    }

    private void makeSureThatCanDeleteSection(final Station targetStation) {
        if (this.sections.isEmpty()) {
            throw new IllegalArgumentException(ERR_TEXT_NOT_EXIST_DATA);
        }

        if (this.sections.size() == EXIST_ONLY_ONE) {
            throw new IllegalArgumentException(ERR_TEXT_EXIST_ONLY_ONE_SECTION);
        }

        final List<Station> stations = getStations();
        if (!stations.contains(targetStation)) {
            throw new IllegalArgumentException(ERR_TEXT_NOT_EXIST_DATA);
        }
    }

    private void delete(final Station targetStation) {
        final Section containByDownStation = findSection(sec -> sec.getDownStation() == targetStation).orElse(null);
        final Section containByUpStation = findSection(sec -> sec.getUpStation() == targetStation).orElse(null);

        this.sections.remove(containByDownStation);
        this.sections.remove(containByUpStation);

        addNewSectionWhenDeleteStationsIsMiddleStation(containByDownStation, containByUpStation);
    }

    private void addNewSectionWhenDeleteStationsIsMiddleStation(final Section containByDownStation, final Section containByUpStation) {
        if (containByDownStation != null && containByUpStation != null) {
            this.sections.add(new Section(
                containByDownStation.getLine(),
                containByDownStation.getUpStation(),
                containByUpStation.getDownStation(),
                containByUpStation.plusDistance(containByDownStation)));
        }
    }
}
