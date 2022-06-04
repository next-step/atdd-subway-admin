package nextstep.subway.domain;

import static nextstep.subway.domain.ErrorMessage.ALREADY_CREATED_SECTION;
import static nextstep.subway.domain.ErrorMessage.CANNOT_DELETE_LAST_SECTION;
import static nextstep.subway.domain.ErrorMessage.NOT_EXISTED_STATION;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    private static final int LAST_SECTION_SIZE = 2;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public static Sections of(Station upStation, Station downStation, Distance distance) {
        List<Section> sections = new ArrayList<>();
        sections.add(new Section(null, upStation, null));
        sections.add(new Section(upStation, downStation, distance));
        return new Sections(sections);
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public void add(Section newSection) {

        if (addFirstSection(newSection)) {
            return;
        }

        if (addMiddleSection(newSection)) {
            return;
        }

        if (addLastSection(newSection)) {
            return;
        }

        throw new IllegalArgumentException(NOT_EXISTED_STATION.toString());
    }

    public void remove(Station station) {
        validateLastSectionRemove();

        Section downMatchedSection = findSectionFromDownStation(station);
        Optional<Section> upMatchedSection = findSectionFromUpStation(station);

        if (downMatchedSection.isFirstSection()) {
            downMatchedSection.setDownStation(upMatchedSection.get().getDownStation());
            return;
        }

        if (!upMatchedSection.isPresent()) {
            sections.remove(downMatchedSection);
            return;
        }

        sections.remove(downMatchedSection);
        sections.remove(upMatchedSection.get());

        add(new Section(
                downMatchedSection.getUpStation(),
                upMatchedSection.get().getDownStation(),
                downMatchedSection.getDistance().add(upMatchedSection.get().getDistance()))
        );
    }

    private Section findSectionFromDownStation(Station downStation) {
        Section preSection = getFirstSection();
        Optional<Section> nextStation = getNextStation(preSection);
        while (isLastSection(nextStation)) {
            if (isMatchedDownStation(downStation, preSection)) {
                return preSection;
            }
            preSection = nextStation.get();
            nextStation = getNextStation(preSection);
        }

        if (isMatchedDownStation(downStation, preSection)) {
            return preSection;
        }
        throw new IllegalArgumentException(NOT_EXISTED_STATION.toString());
    }

    private Optional<Section> findSectionFromUpStation(Station upStation) {
        Section preSection = getFirstSection();
        Optional<Section> nextStation = getNextStation(preSection);
        while (isLastSection(nextStation)) {
            if (isMatchedUpStation(upStation, preSection)) {
                return Optional.of(preSection);
            }
            preSection = nextStation.get();
            nextStation = getNextStation(preSection);
        }
        if (isMatchedUpStation(upStation, preSection)) {
            return Optional.of(preSection);
        }

        return Optional.ofNullable(null);
    }

    private Section findSection(Station upStation, Station downStation) {
        Section preSection = getFirstSection();
        Optional<Section> nextStation = getNextStation(preSection);
        while (isLastSection(nextStation)) {
            if (isMatchedUpStation(upStation, preSection) && isMatchedDownStation(downStation, preSection)) {
                return preSection;
            }
            preSection = nextStation.get();
            nextStation = getNextStation(preSection);
        }
        throw new IllegalArgumentException(NOT_EXISTED_STATION.toString());
    }

    private boolean addLastSection(Section newSection) {
        Section lastSection = getLastSection();
        if (isMatchedDownStation(newSection.getUpStation(), lastSection)) {
            sections.add(newSection);
            return true;
        }
        return false;
    }

    private boolean addFirstSection(Section newSection) {
        Section firstSection = getFirstSection();
        if (isMatchedDownStation(newSection.getDownStation(), firstSection)) {
            addSectionOfFirstSectionMatched(newSection, firstSection);
            return true;
        }
        return false;
    }

    private boolean addMiddleSection(Section newSection) {
        Optional<Section> upMatchedSection = matchUpStation(newSection);
        Optional<Section> downMatchedSection = matchDownStation(newSection);

        validateAlreadySection(upMatchedSection, downMatchedSection);

        if (upMatchedSection.isPresent()) {
            addSectionOfUpMatchedCase(newSection, upMatchedSection);
            return true;
        }
        if (downMatchedSection.isPresent()) {
            addSectionOfDownMatchedCase(newSection, downMatchedSection);
            return true;
        }
        return false;
    }

    private void addSectionOfFirstSectionMatched(Section newSection, Section firstSection) {
        firstSection.setDownStation(newSection.getUpStation());
        sections.add(newSection);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        Section preSection = getFirstSection();
        stations.add(preSection.getDownStation());
        Optional<Section> nextStation = getNextStation(preSection);

        while (isLastSection(nextStation)) {
            stations.add(nextStation.get().getDownStation());
            preSection = nextStation.get();

            nextStation = getNextStation(preSection);
        }

        return stations;
    }

    private Section getFirstSection() {
        return sections.stream()
                .filter(Section::isFirstSection)
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private Section getLastSection() {

        Section preSection = getFirstSection();
        Optional<Section> nextStation = getNextStation(preSection);
        while (isLastSection(nextStation)) {
            preSection = nextStation.get();
            nextStation = getNextStation(preSection);
        }
        return preSection;
    }

    private boolean isLastSection(Optional<Section> section) {
        if (section.isPresent()) {
            return true;
        }
        return false;
    }

    private Optional<Section> matchDownStation(Section newSection) {
        return sections.stream()
                .filter(section -> isMatchedDownStation(section.getDownStation(), newSection))
                .findFirst();
    }

    private void addSectionOfDownMatchedCase(Section newSection, Optional<Section> downMatchedSection) {

        Section oldSection = downMatchedSection.get();
        sections.add(
                new Section(
                        newSection.getUpStation(),
                        oldSection.getDownStation(),
                        oldSection.getDistance()
                )
        );
        oldSection.setDownStation(newSection.getUpStation());
        oldSection.setDistance(oldSection.getDistance().minus(newSection.getDistance()));
    }

    private Optional<Section> matchUpStation(Section newSection) {
        Optional<Section> upMatchedSection = sections.stream()
                .filter(section -> !section.isFirstSection())
                .filter(section -> isMatchedUpStation(section.getUpStation(), newSection))
                .findFirst();
        return upMatchedSection;
    }

    private void addSectionOfUpMatchedCase(Section newSection, Optional<Section> upMatchedSection) {
        Section oldSection = upMatchedSection.get();
        sections.add(
                new Section(
                        newSection.getDownStation(),
                        oldSection.getDownStation(),
                        oldSection.getDistance().minus(newSection.getDistance())
                )
        );
        oldSection.setDownStation(newSection.getDownStation());
        oldSection.setDistance(newSection.getDistance());
    }

    private Optional<Section> getNextStation(Section finalPreSection) {
        return sections.stream()
                .filter(section -> !section.isFirstSection())
                .filter(section -> isMatchedDownStation(section.getUpStation(), finalPreSection))
                .findFirst();
    }

    private boolean isMatchedDownStation(Station station, Section section) {
        return station.getId().equals(section.getDownStation().getId());
    }

    private boolean isMatchedUpStation(Station station, Section section) {
        if (section.isFirstSection()) {
            return false;
        }
        return station.getId().equals(section.getUpStation().getId());
    }

    private void validateAlreadySection(Optional<Section> upMatchedSection, Optional<Section> downMatchedSection) {
        if (upMatchedSection.isPresent() && downMatchedSection.isPresent()) {
            throw new IllegalArgumentException(ALREADY_CREATED_SECTION.toString());
        }
    }

    private void validateLastSectionRemove() {
        if (this.sections.size() == LAST_SECTION_SIZE) {
            throw new IllegalArgumentException(CANNOT_DELETE_LAST_SECTION.toString());
        }
    }
}
