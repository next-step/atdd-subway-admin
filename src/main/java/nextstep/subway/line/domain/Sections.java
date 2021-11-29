package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import nextstep.subway.common.exception.LinkableSectionNotFoundException;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addSection(final Section section) {
        if (sections.isEmpty()) {
            this.sections.add(section);
            return;
        }

        linkSection(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public void linkSection(final Section section) {
        validateOneStationNotRegistered(section);

        Section linkTargetSection = sections.stream()
            .filter(s -> s.isLinkable(section))
            .findFirst().orElseThrow(LinkableSectionNotFoundException::new);

        linkTargetSection.link(section);
        this.sections.add(section);
    }

    private void validateOneStationNotRegistered(Section section) {
        boolean upStationRegistered = sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList())
            .contains(section.getUpStation());

        boolean downStationRegistered = sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList())
            .contains(section.getDownStation());

        if (upStationRegistered && downStationRegistered) {
            throw new LinkableSectionNotFoundException();
        }
    }
}
