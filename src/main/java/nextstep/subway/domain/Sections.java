package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections valueOf(List<Section> sections) {
        return new Sections(sections);
    }

    public void addSection(Section section) {
        if (isSectionsEmpty()) {
            sections.add(section);
        }
        if (!isSectionsEmpty()) {
            registerSection(section);
        }
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

    public List<Section> sections() {
        return sections;
    }

    private void registerSection(Section newSection) {
        for (int i = 0; i < sections.size(); i++) {
            registerSectionIfEqualsUpStation(newSection, i);
        }
    }

    private void registerSectionIfEqualsUpStation(Section newSection, int index) {
        if (isEqualsUpStation(sections.get(index), newSection)) {
            sections.get(index).changeUpSection(newSection);
            sections.add(index, newSection);
        }
    }

    private boolean isEqualsUpStation(Section targetSection, Section newSection) {
        return targetSection.upStation().equals(newSection.upStation());
    }

    private void validateSections() {
        if (isSectionsEmpty()) {
            throw new IllegalStateException("지하철 구간이 비어있습니다.");
        }
    }

    private boolean isSectionsEmpty() {
        return sections.isEmpty();
    }
}