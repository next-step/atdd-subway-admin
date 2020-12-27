package nextstep.subway.line.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import java.util.LinkedList;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    @OrderColumn
    private final List<Section> sections = new LinkedList<>();

    public List<Section> getSections() {
        return sections;
    }

    public void create(Section section) {
        checkValidation(section);
        this.sections.add(section);
    }

    public void add(Section targetSection) {
        checkValidation(targetSection);
        changeUpStation(targetSection);
        this.sections.add(findAscendIndex(targetSection), targetSection);
    }

    public void checkValidation(Section targetSection) {
        if (this.sections.contains(targetSection) || targetSection.isZeroDistance()) {
            throw new IllegalArgumentException();
        }
    }

    public void update(Section section) {
        if (isChanged(section)) {
            this.sections.stream()
                    .findFirst()
                    .ifPresent(s -> s.update(section));
        }
    }

    private boolean isChanged(Section section) {
        return !this.sections.stream()
                .allMatch(s -> s.equals(section));
    }

    public void changeUpStation(Section targetSection) {
        this.sections.stream()
                .filter(base -> base.isSameUpStation(targetSection.getUpStation()))
                .findFirst()
                .ifPresent(base -> {
                    base.changeUpStation(targetSection.getDownStation());
                    base.changeDistance(targetSection.getDistance());
                });
    }

    private Integer findAscendIndex(Section targetSection) {
        return this.sections.stream()
                .filter(base -> base.isSameUpStation(targetSection.getDownStation()))
                .findFirst()
                .map(this.sections::indexOf)
                .orElseGet(() -> findDescendIndex(targetSection));
    }

    private Integer findDescendIndex(Section targetSection) {
        return this.sections.stream()
                .filter(base -> base.isSameDownStation(targetSection.getUpStation()))
                .findFirst()
                .map(base -> this.sections.indexOf(base) + 1)
                .orElseThrow(IllegalArgumentException::new);
    }
}
