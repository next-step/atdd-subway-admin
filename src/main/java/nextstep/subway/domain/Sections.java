package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    public static final int ZERO_NUM = 0;
    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public Distance distance() {
        return Distance.valueOf(sections.stream()
                .mapToInt(section -> section.distance().distance())
                .reduce(0, Integer::sum));
    }

    public Station upStation() {
        validateSections();
        return sections.get(0).upStation();
    }

    public Station downStation() {
        validateSections();
        return sections.get(sections.size() - 1).downStation();
    }

    private void validateSections() {
        if (isSectionsEmpty()) {
            throw new IllegalStateException("지하철 구간이 비어있습니다.");
        }
    }

    private boolean isSectionsEmpty() {
        return sections.size() == ZERO_NUM;
    }
}