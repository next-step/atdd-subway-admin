package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.exception.BadRequestException;
import nextstep.subway.station.domain.Station;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    protected Line() {
    }

    public Line(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public void update(final Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public void addSection(final Section section) {
        if (!sections.isEmpty()) {
            validate(section);

            sections.stream()
                .filter(it -> it.isOverlapped(section))
                .findAny()
                .ifPresent(it -> it.divideBy(section));
        }

        sections.add(section);
    }

    private void validate(final Section section) {
        final Set<Station> allStations = extractAllStations();
        final List<Station> sectionStations = section.getStations();

        final long matchCount = sectionStations.stream()
            .filter(allStations::contains)
            .count();

        if (matchCount == 0) {
            throw new BadRequestException("추가되는 구간은 기존의 구간과 연결 가능하여야 합니다.");
        }
        if (matchCount == 2) {
            throw new BadRequestException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }
    }

    public List<Station> computeSortedStations() {
        final List<Station> sortedStations = new ArrayList<>();
        final Section upEdgeSection = computeUpEdgeSection();
        sortedStations.add(upEdgeSection.getUpStation());
        sortedStations.add(upEdgeSection.getDownStation());

        final List<Station> nextStations = computeNextSections(upEdgeSection).stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());

        sortedStations.addAll(nextStations);

        return sortedStations;
    }

    private Section computeUpEdgeSection() {
        final Set<Station> downStations = extractDownStations();

        return sections.stream()
            .filter(section -> !downStations.contains(section.getUpStation()))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("지하철 노선에 상행 종점 구간이 존재하지 않습니다."));
    }

    private List<Section> computeNextSections(final Section previousSection) {
        final List<Section> nextSections = new ArrayList<>();

        final Optional<Section> nextSection = sections.stream()
            .filter(section -> section.isNextSection(previousSection))
            .findAny();

        if (nextSection.isPresent()) {
            nextSections.add(nextSection.get());
            nextSections.addAll(computeNextSections(nextSection.get()));
        }

        return nextSections;
    }

    private Set<Station> extractAllStations() {
        return sections.stream()
            .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
            .collect(Collectors.toSet());
    }

    private Set<Station> extractDownStations() {
        return sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toSet());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }
}
