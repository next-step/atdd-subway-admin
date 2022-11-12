package nextstep.subway.domain;

import nextstep.subway.dto.StationResponse;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Line extends BaseEntity {

    @Id
    @Column(name = "line_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column
    private String color;
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private final List<Section> sections = new ArrayList<>();

    protected Line() {
    }

    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        this.sections.add(section);
        section.setLine(this);
    }

    public static Line of(final String name, final String color, final Section section) {
        return new Line(name, color, section);
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
        return sections;
    }

    public void changeName(final String name) {
        this.name = name;
    }

    public void changeColor(final String color) {
        this.color = color;
    }

    public void addSection(final Section newSection) {
        Section section = validate(newSection);
        reArrange(section, newSection);
    }

    private void reArrange(final Section section, final Section newSection) {
        changeSection(section, newSection);
        this.sections.add(newSection);
        newSection.setLine(this);
    }

    private void changeSection(final Section section, final Section newSection) {
        if (isSameUpStation(section, newSection)) {
            section.changeUpStation(newSection.getDownStation());
            reArrangeDistance(section, newSection);
        } else if (isSameDownStation(section, newSection)) {
            section.changeDownStation(newSection.getUpStation());
            reArrangeDistance(section, newSection);
        }
    }

    private void reArrangeDistance(final Section section, final Section newSection) {
        section.changeDistance(section.getDistance() - newSection.getDistance());
    }

    private boolean isSameDownStation(final Section section, final Section newSection) {
        return section.getDownStation().equals(newSection.getDownStation());
    }

    private boolean isSameUpStation(final Section section, final Section newSection) {
        return section.getUpStation().equals(newSection.getUpStation());
    }

    private Section validate(final Section newSection) {
        Section section = isPossibleToConnect(newSection);
        if (isIncludedBetweenStations(newSection, section)) {
            validateDistance(newSection, section);
        }
        return section;
    }

    private boolean isIncludedBetweenStations(final Section newSection, final Section section) {
        return isSameUpStation(section, newSection) || isSameDownStation(section, newSection);
    }

    private void validateDistance(final Section newSection, final Section section) {
        if (newSection.getDistance() >= section.getDistance()) {
            throw new IllegalArgumentException("상행역 또는 하행역이 동일한 경우, 기존 구간의 거리보다 짧아야 합니다.");
        }
    }

    private Section isPossibleToConnect(final Section newSection) {
        return sections.stream()
                .filter(s -> isInsertable(s, newSection))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("새 구간을 연결할 수 있는 구간이 없습니다."));
    }

    private boolean isInsertable(final Section section, final Section newSection) {
        Set<Station> copy = new HashSet<>(section.getStations());
        copy.addAll(newSection.getStations());
        return copy.size() == 3;
    }

    public List<StationResponse> stationsToResponse() {
        return sections.stream()
                .flatMap(o -> o.getStations().stream())
                .map(o -> new StationResponse(o.getId(), o.getName(), o.getCreatedDate(), o.getModifiedDate()))
                .distinct()
                .collect(Collectors.toList());
    }
}
