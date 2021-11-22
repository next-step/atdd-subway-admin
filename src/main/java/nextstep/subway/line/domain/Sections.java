package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Embeddable
public class Sections {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "sectionId")
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    private Sections(Section section) {
        if (Objects.isNull(section)) {
            throw new IllegalArgumentException("종점역은 빈값이 될 수 없습니다.");
        }
        this.sections.add(section);
    }

    public Sections(List<Section> sections) {
        this.sections.addAll(sections);
    }

    public static Sections of(Section section) {
        return new Sections(section);
    }

    public static Sections of(List<Section> sections) {
        return new Sections(sections);
    }

    public List<Station> getStationsOrderByUptoDown() {
        return SectionsSort.of(this.sections).sortUpToDown();
    }

}
