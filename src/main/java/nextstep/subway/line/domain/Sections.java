package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        if (sections.size() == 0) {
            sections.add(section);
            return;
        }

        if (section.isDownStationEquals(findStartStation()) && isNewUpStation(section)) {
            sections.add(section);
            return;
        }

        Optional<Section> sectionUpStationMatchedOptional = findByUpStation(section);
        Optional<Section> sectionDownStationMatchedOptional = findByDownStation(section);

        if (sectionUpStationMatchedOptional.isPresent() && sectionDownStationMatchedOptional.isPresent()) {
            throw new SectionAddFailedException("상행역과 하행역이 노선에 포함되어 있는 구간은 등록할 수 없습니다.");
        }
        if (!sectionUpStationMatchedOptional.isPresent() && !sectionDownStationMatchedOptional.isPresent()) {
            throw new SectionAddFailedException("상행역과 하행역중 1개는 노선에 포함되어야 합니다.");
        }

        if (sectionUpStationMatchedOptional.isPresent()) {
            Section sectionUpStationMatched = sectionUpStationMatchedOptional.get();
            if (sectionUpStationMatched.isLessThanOrEquals(section)) {
                throw new SectionAddFailedException("역 사이에 구간을 등록 할 경우 기존 역 사이 거리보다 작아야 합니다.");
            }
            sections.add(section);
            sections.add(new Section(
                    section.getDownStation(),
                    sectionUpStationMatched.getDownStation(),
                    sectionUpStationMatched.getRemainDistance(section))
            );
            sections.remove(sectionUpStationMatched);
        }

        if (sectionDownStationMatchedOptional.isPresent()) {
            Section sectionDownStationMatched = sectionDownStationMatchedOptional.get();
            if (sectionDownStationMatched.isLessThanOrEquals(section)) {
                throw new SectionAddFailedException("역 사이에 구간을 등록 할 경우 기존 역 사이 거리보다 작아야 합니다.");
            }
            sections.add(new Section(
                    sectionDownStationMatched.getUpStation(),
                    section.getUpStation(),
                    sectionDownStationMatched.getRemainDistance(section))
            );
            sections.add(section);
            sections.remove(sectionDownStationMatched);
        }
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

    private boolean isNewUpStation(Section section) {
        return !sections.stream()
                .allMatch(section::isUpStationEqualsWithDownStation);
    }

    public List<Station> toStations() {
        if (sections.size() == 0) {
            return new ArrayList<>();
        }
        return makeStations();
    }

    private List<Station> makeStations() {
        List<Station> stations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        stations.add(getLastStation());
        return stations;
    }

    private Station getLastStation() {
        return sections.get(sections.size() - 1).getDownStation();
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
