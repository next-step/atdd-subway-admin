package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Embeddable
public class Sections {
    private static final String MESSAGE_SECTIONS_HAS_NOT_UPPER_STATION = "해당 역을 가지는 구간이 없습니다";

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
        Section upperSection = popSectionBy(section -> section.hasUpStation(station));
        Section downSection = popSectionBy(section -> section.hasDownStation(station));
        this.sections.add(Section.merge(upperSection, downSection));
        makeOrphan(downSection);
        makeOrphan(upperSection);
    }

    private Section popSectionBy(Predicate<Section> sectionFilter) {
        Section first = this.sections.stream()
                .filter(sectionFilter)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(MESSAGE_SECTIONS_HAS_NOT_UPPER_STATION));
        this.sections.remove(first);
        return first;
    }

    private void makeOrphan(Section section) {
        this.sections.remove(section);
        section.setLine(null);
    }

    public Section popUpperStationIs(Station station) {
        return popSectionBy(section -> section.hasUpStation(station));
    }

    public Section popDownStationIs(Station station) {
        return popSectionBy(section -> section.hasDownStation(station));
    }
}
