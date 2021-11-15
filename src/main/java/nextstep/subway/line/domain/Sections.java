package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        if (isAddFirst() || isAddPreviousStartStation(section) || isAddNextEndStation(section)) {
            sections.add(section);
            return;
        }

        validate(section);

        if (addSectionsUpStationMatched(section)) {
            return;
        }
        addSectionsDownStationMatched(section);
    }

    public List<Station> toStations() {
        if (sections.size() == 0) {
            return new ArrayList<>();
        }
        return makeStations();
    }

    private boolean isAddFirst() {
        return sections.size() == 0;
    }

    private boolean isAddNextEndStation(Section section) {
        return section.isUpStationEquals(findEndStation()) && isNewDownStation(section);
    }

    private boolean isAddPreviousStartStation(Section section) {
        return section.isDownStationEquals(findStartStation()) && isNewUpStation(section);
    }

    private boolean isNewUpStation(Section section) {
        return !sections.stream()
                .allMatch(section::isUpStationEqualsWithDownStation);
    }

    private boolean isNewDownStation(Section section) {
        return !sections.stream()
                .allMatch(section::isDownStationEqualsWithUpStation);
    }

    private void validate(Section section) {
        Optional<Section> sectionUpStationMatchedOptional = findByUpStation(section);
        Optional<Section> sectionDownStationMatchedOptional = findByDownStation(section);

        if (sectionUpStationMatchedOptional.isPresent() && sectionDownStationMatchedOptional.isPresent()) {
            throw new SectionAddFailedException("상행역과 하행역이 노선에 포함되어 있는 구간은 등록할 수 없습니다.");
        }
        if (!sectionUpStationMatchedOptional.isPresent() && !sectionDownStationMatchedOptional.isPresent()) {
            throw new SectionAddFailedException("상행역과 하행역중 1개는 노선에 포함되어야 합니다.");
        }
        if (sectionUpStationMatchedOptional.isPresent() && sectionUpStationMatchedOptional.get().isLessThanOrEquals(section)) {
            throw new SectionAddFailedException("역 사이에 구간을 등록 할 경우 기존 역 사이 거리보다 작아야 합니다.");
        }
        if (sectionDownStationMatchedOptional.isPresent() && sectionDownStationMatchedOptional.get().isLessThanOrEquals(section)) {
            throw new SectionAddFailedException("역 사이에 구간을 등록 할 경우 기존 역 사이 거리보다 작아야 합니다.");
        }
    }

    private boolean addSectionsUpStationMatched(Section section) {
        Optional<Section> sectionUpStationMatchedOptional = findByUpStation(section);
        if (sectionUpStationMatchedOptional.isPresent()) {
            Section sectionUpStationMatched = sectionUpStationMatchedOptional.get();
            addSplitSections(section, sectionUpStationMatched, section.getDownStation(), sectionUpStationMatched.getDownStation());
            return true;
        }
        return false;
    }

    private void addSectionsDownStationMatched(Section section) {
        Optional<Section> sectionDownStationMatchedOptional = findByDownStation(section);
        if (sectionDownStationMatchedOptional.isPresent()) {
            Section sectionDownStationMatched = sectionDownStationMatchedOptional.get();
            addSplitSections(section, sectionDownStationMatched, sectionDownStationMatched.getUpStation(), section.getUpStation());
        }
    }

    private Optional<Section> findByUpStation(Section section) {
        return sections.stream()
                .filter(section::isUpStationEquals)
                .findFirst();
    }

    private Optional<Section> findByDownStation(Section section) {
        return sections.stream()
                .filter(section::isDownStationEquals)
                .findFirst();
    }

    private void addSplitSections(Section section, Section matchedSection, Station newUpStation, Station newDownStation) {
        sections.add(section);
        sections.add(new Section(
                newUpStation,
                newDownStation,
                matchedSection.getRemainDistance(section))
        );
        sections.remove(matchedSection);
    }

    private Station findStartStation() {
        Section section = sections.get(0);
        Optional<Section> sectionOptional = Optional.of(section);

        while (sectionOptional.isPresent()) {
            section = sectionOptional.get();
            sectionOptional = sections.stream()
                    .filter(section::isUpStationEqualsWithDownStation)
                    .findFirst();
        }
        return section.getUpStation();
    }

    private Station findEndStation() {
        Section section = sections.get(0);
        Optional<Section> sectionOptional = Optional.of(section);

        while (sectionOptional.isPresent()) {
            section = sectionOptional.get();
            sectionOptional = sections.stream()
                    .filter(section::isDownStationEqualsWithUpStation)
                    .findFirst();
        }
        return section.getDownStation();
    }

    private List<Station> makeStations() {
        List<Station> stations = new ArrayList<>();
        Optional<Section> sectionOptional = findFirstSection();

        while (sectionOptional.isPresent()) {
            Section section = sectionOptional.get();
            stations.add(section.getUpStation());
            sectionOptional = sections.stream()
                    .filter(it -> it.isUpStationEqualsWithDownStation(section))
                    .findFirst();
        }

        stations.add(findEndStation());
        return stations;
    }

    private Optional<Section> findFirstSection() {
        return sections.stream()
                .filter(section -> section.isUpStationEquals(findStartStation()))
                .findFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
