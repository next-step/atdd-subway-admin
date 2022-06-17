package nextstep.subway.domain.section;

import nextstep.subway.domain.common.Distance;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {

    @OneToMany(
            mappedBy = "line",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public static Sections create() {
        return new Sections();
    }

    public void addSection(Section section) {
        validateNull(section);
        validateDuplicatedSection(section);

        sections.add(section);
    }

    public void addBetweenSection(Section section) {
        validateNull(section);
        validateDuplicatedSection(section);

        Section registeredSection = getContainsStationSection(section);

        sections.remove(registeredSection);

        Section dividedSection = divideSection(section, registeredSection);

        sections.add(dividedSection);
        sections.add(section);
    }

    private Section divideSection(Section section, Section registeredSection) {
        Section dividedSection = createSection(registeredSection, section);
        dividedSection.setLine(section.getLine());
        return dividedSection;
    }

    private Section createSection(Section registeredSection, Section section) {
        Distance distance = registeredSection.getDistance().substract(section.getDistance());

        if (Objects.equals(registeredSection.getUpStation(), section.getUpStation())) {
            return Section.create(section.getDownStation(), registeredSection.getDownStation(), distance);
        }
        return Section.create(registeredSection.getUpStation(), section.getUpStation(), distance);
    }

    private Section getContainsStationSection(Section section) {
        return sections.stream()
                .filter(registeredSection -> isContainTerminusStation(registeredSection, section))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("상행역과 하행역 둘 중 하나라도 포함되어있지 않으면 구간을 추가할 수 없습니다."));
    }

    private boolean isContainTerminusStation(Section registeredSection, Section section) {
        return Objects.equals(registeredSection.getUpStation(), section.getUpStation())
                || Objects.equals(registeredSection.getDownStation(), section.getDownStation());
    }

    private void validateNull(Section section) {
        if (Objects.isNull(section)) {
            throw new IllegalArgumentException("노선 구간 추가 시 에러가 발생하였습니다. section is null");
        }
    }

    private void validateDuplicatedSection(Section section) {
        Set<Station> stations = new HashSet<>();

        for (Section registeredSection : sections) {
            stations.add(registeredSection.getUpStation());
            stations.add(registeredSection.getDownStation());
        }

        if (stations.contains(section.getUpStation()) && stations.contains(section.getDownStation())) {
            throw new IllegalArgumentException("노선에 동일한 구간이 존재합니다.");
        }
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public int getSize() {
        return sections.size();
    }

    public void removeMiddleStation(Line line, Long stationId) {
        Section leftSection = getLeftSection(stationId);
        Section rightSection = getRightSection(stationId);
        Section section = Section.create(
                leftSection.getUpStation(),
                rightSection.getDownStation(),
                line,
                Distance.of(leftSection.getDistance().getValue() + rightSection.getDistance().getValue()));

        remove(leftSection);
        remove(rightSection);
        addSection(section);
    }

    public Section getLeftSection(Long stationId) {
        return sections.stream()
                .filter(section -> Objects.equals(section.getDownStation().getId(), stationId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당역이 포함된 구간을 찾을 수 없습니다."));

    }

    public Section getRightSection(Long stationId) {
        return sections.stream()
                .filter(section -> Objects.equals(section.getUpStation().getId(), stationId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당역이 포함된 구간을 찾을 수 없습니다."));
    }

    public void remove(Section section) {
        if (Objects.isNull(section)) {
            throw new IllegalArgumentException("제거하려는 구간이 존재하지 않습니다.");
        }

        sections.remove(section);
    }
}
