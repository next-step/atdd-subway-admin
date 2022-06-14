package nextstep.subway.domain.section;

import nextstep.subway.domain.common.Distance;
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
        validateDistance(registeredSection.getDistance(), section.getDistance());

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

    private void validateDistance(Distance distance, Distance newDistance) {
        if (distance.getValue() <= newDistance.getValue()) {
            throw new IllegalArgumentException("기존 역 사이 길이보다 크거나 같습니다.");
        }
    }

    private Section getContainsStationSection(Section section) {
        return sections.stream()
                .filter(registeredSection ->
                        Objects.equals(registeredSection.getUpStation(), section.getUpStation())
                                || Objects.equals(registeredSection.getDownStation(), section.getDownStation()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("상행역과 하행역 둘 중 하나라도 포함되어있지 않으면 구간을 추가할 수 없습니다."));
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
}
