package nextstep.subway.domain;

import org.springframework.util.CollectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    protected Sections(){
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
}
