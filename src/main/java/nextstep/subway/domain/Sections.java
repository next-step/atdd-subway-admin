package nextstep.subway.domain;

import nextstep.subway.constants.ErrorCode;
import org.springframework.data.annotation.ReadOnlyProperty;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    @ReadOnlyProperty
    private final List<Section> sections;

    public Sections() {
        sections = new ArrayList<>();
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        checkValidation(section);
        sections.stream()
                .filter(it -> it.isSameUpStationId(section))
                .findFirst()
                .ifPresent(it -> it.updateAndCreateTwiceSection(it, section));
        sections.add(section);
    }

    private void checkValidation(Section section) {
        if (sections.stream().anyMatch(s -> s.equals(section))) {
            throw new IllegalArgumentException(ErrorCode.NO_SAME_SECTION_EXCEPTION.getErrorMessage());
        }
    }

    public List<Section> asList() {
        return Collections.unmodifiableList(sections);
    }
}
