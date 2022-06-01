package nextstep.subway.domain.section;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Embeddable
public class Sections {

    @OneToMany(
            mappedBy = "line",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public static Sections create() {
        return new Sections();
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public void add(Section section) {
        validateNull(section);
        validateDuplicatedSection(section);

        sections.add(section);
    }

    private void validateNull(Section section) {
        if (Objects.isNull(section)) {
            throw new IllegalArgumentException("노선 구간 추가 시 에러가 발생하였습니다. section is null");
        }
    }

    private void validateDuplicatedSection(Section section) {
        boolean isExistSameSection = sections.stream()
                .anyMatch(registeredSection -> Objects.equals(registeredSection, section));

        if (isExistSameSection) {
            throw new IllegalArgumentException("노선에 동일한 구간이 존재합니다.");
        }
    }
}
