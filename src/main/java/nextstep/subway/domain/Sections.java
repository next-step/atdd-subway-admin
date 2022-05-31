package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections valueOf(List<Section> sections) {
        return new Sections(sections);
    }

    public void addSection(Section section) {
        if (!isSectionsEmpty()) {
            validNotAddedSection(section);
            validContainsUpStationOrDownStation(section);
            update(section);
        }
        sections.add(section);
    }

    public Distance distance() {
        return Distance.valueOf(sections.stream()
                .mapToInt(section -> section.distance().distance())
                .reduce(0, Integer::sum));
    }

    public List<Station> stations() {
        return sections.stream()
                .map(Section::stations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private void update(Section newSection) {
        sections.forEach(section -> section.update(newSection));
    }

    private void validNotAddedSection(Section section) {
        if (containsUpStationAndDownStation(section)) {
            throw new IllegalArgumentException("이미 등록된 구간 요청입니다.");
        }
    }

    private void validContainsUpStationOrDownStation(Section section) {
        if (containsNoneOfUpStationAndDownStation(section)) {
            throw new IllegalArgumentException("등록을 위해 필요한 상행역과 하행역이 모두 등록되어 있지 않습니다.");
        }
    }

    private boolean containsUpStationAndDownStation(Section section) {
        return this.stations().contains(section.upStation()) && this.stations().contains(section.downStation());
    }

    private boolean containsNoneOfUpStationAndDownStation(Section section) {
        return !this.stations().contains(section.upStation()) && !this.stations().contains(section.downStation());
    }

    private boolean isSectionsEmpty() {
        return sections.isEmpty();
    }
}