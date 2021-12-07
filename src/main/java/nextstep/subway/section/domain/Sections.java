package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(final Section... sections) {
        this.sections.addAll(Arrays.asList(sections));
    }

    public void addSection(final Section section) {
        this.validateSection(section);
        this.adjustDistance(section);
        this.sections.add(section);
    }

    private void validateSection(final Section addSection) {
        boolean noConnectable = this.sections
                .stream()
                .noneMatch(section -> section.connectable(addSection));

        if (noConnectable) {
            throw new IllegalArgumentException("연결할 수 없는 역이 포함되어 있습니다.");
        }
    }

    private void adjustDistance(final Section addSection) {
        this.sections.forEach(section -> section.deductDistance(addSection));
    }

    public void updateSection(final Station upStation, final Station downStation, final Distance distance) {
        this.sections
                .stream()
                .filter(section -> section.getUpStation().equals(upStation) && section.getDownStation().equals(downStation))
                .forEach(section -> section.update(upStation, downStation, distance));
    }

    public List<Section> getSections() {
        return sections;
    }

}
