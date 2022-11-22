package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_of_line"))
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public List<Section> getOrderedSections() {
        Optional<Section> sectionOptional = sections.stream()
                .filter(it -> it.getPreStation() == null)
                .findFirst();

        List<Section> orderedSections = new ArrayList<>();

        while (sectionOptional.isPresent()) {
            Section section = sectionOptional.get();
            orderedSections.add(section);
            sectionOptional = sections.stream()
                    .filter(it -> section.getStation().isSame(it.getPreStation()))
                    .findFirst();
        }

        return Collections.unmodifiableList(orderedSections);
    }

    public void add(Section section) {
        validateSection(section);
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        updateWhenAddablePre(section);
        updateWhenAddablePost(section);
        checkContinuable(section);

        sections.add(section);
    }

    public List<Section> values() {
        return Collections.unmodifiableList(sections);
    }

    private void updateWhenAddablePre(Section section) {
        sections.stream()
                .filter(it -> section.getStation().isSame(it.getStation()))
                .findFirst()
                .ifPresent(it -> it.updatePreSection(section));
    }

    private void updateWhenAddablePost(Section section) {
        sections.stream()
                .filter(it -> section.getPreStation().isSame(it.getPreStation()))
                .findFirst()
                .ifPresent(it -> it.updateSection(section));
    }

    private void checkContinuable(Section section) {
        sections.stream()
                .filter(it -> section.getPreStation().isSame(it.getStation()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private void validateSection(Section section) {
        if (section.getStation() == null) {
            throw new IllegalArgumentException("유효하지 않은 역입니다.");
        }
        if (sections.contains(section)) {
            throw new IllegalArgumentException("이미 등록되어 있는 구간입니다.");
        }
    }
}
