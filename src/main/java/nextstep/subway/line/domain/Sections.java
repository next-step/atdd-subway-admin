package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public static Sections init(final Section section) {
        List<Section> sections = new ArrayList<>();
        sections.add(section);
        return new Sections(sections);
    }

    public void add(final Section section) {
        if (sections.contains(section)) {
            throw new IllegalArgumentException();
        }
        sections.add(section);
    }
}
