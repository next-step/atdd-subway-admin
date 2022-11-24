package nextstep.subway.domain;

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
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public boolean insertInsideFromUpStation(Station upStation, Station newStation, long distance) {
        Optional<Section> first = this.sections.stream()
                .filter(section -> section.hasUpStation(upStation))
                .findFirst();

        if (!first.isPresent()) {
            return false;
        }

        Section section = first.get();
        section.splitFromUpStation(newStation, distance);
        return true;
    }


    public boolean insertInsideFromDownStation(Station downStation, Station newStation, long distance) {
        Optional<Section> first = this.sections.stream()
                .filter(section -> section.hasDownStation(downStation))
                .findFirst();

        if (!first.isPresent()) {
            return false;
        }

        Section section = first.get();
        section.splitFromDownStation(newStation, distance);
        return true;
    }

    public boolean insert(Station upStation, Station downStation, Long distance) {
        return insertInsideFromUpStation(upStation, downStation, distance) ||
                insertInsideFromDownStation(downStation, upStation, distance);
    }

    public void add(Section newSection) {
        this.sections.add(newSection);
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

    public boolean hasOnlyOneSection() {
        return this.sections.size() == 1;
    }

    public void delete(Station station) {
        Section upperSection = this.sections.stream()
                .filter(section -> section.hasDownStation(station))
                .findFirst().get();
        Section downSection = this.sections.stream()
                .filter(section -> section.hasUpStation(station))
                .findFirst().get();
        upperSection.merge(downSection);
        this.sections.remove(downSection);
        downSection.setLine(null);
    }

    public Section popUpperStationIs(Station station) {
        Section first = this.sections.stream()
                .filter(section ->
                        section.hasUpStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 역을 상행역으로 가지는 구간이 없습니다"));
        this.sections.remove(first);
        return first;
    }

    public Section popDownStationIs(Station station) {
        Section first = this.sections.stream()
                .filter(section ->
                        section.hasDownStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 역을 하행역으로 가지는 구간이 없습니다"));
        this.sections.remove(first);
        return first;
    }
}
