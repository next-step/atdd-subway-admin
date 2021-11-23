package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Embeddable
public class Sections {
    private static final String NOT_NULL_ERROR_MESSAGE = "종점역은 빈값이 될 수 없습니다.";

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "sectionId", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    private Sections(Section section) {
        if (Objects.isNull(section)) {
            throw new IllegalArgumentException(NOT_NULL_ERROR_MESSAGE);
        }
        add(section);
    }

    private void add(Section section) {
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
