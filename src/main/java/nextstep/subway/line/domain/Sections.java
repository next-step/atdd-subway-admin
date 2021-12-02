package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import nextstep.subway.common.exception.IllegalSectionRemoveException;
import nextstep.subway.common.exception.IllegalStationException;
import nextstep.subway.common.exception.LinkableSectionNotFoundException;
import nextstep.subway.common.exception.SectionNotFoundException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    private static final int MINIMUM_SECTIONS_LIMIT = 1;

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
            .filter(targetSection -> targetSection.isLinkable(section))
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

    public void removeSection(Station targetStation) {
        validateMinimumSectionSize();
        validateRemovable(targetStation);

        if (isBothInclude(targetStation)) {
            mergeSection(targetStation);
            return;
        }

        sections.stream()
            .filter(section -> section.hasSameStation(targetStation))
            .findFirst()
            .ifPresent(section -> sections.remove(section));
    }

    public Section findSection(Station upStation, Station downStation) {
        return sections.stream()
            .filter(section -> section.isSameUpStation(upStation)
                && section.isSameDownStation(downStation))
            .findFirst().orElseThrow(SectionNotFoundException::new);
    }

    private void mergeSection(Station targetStation) {
        Section targetSection = sections.stream()
            .filter(section -> section.isSameUpStation(targetStation))
            .findFirst()
            .orElseThrow(SectionNotFoundException::new);

        sections.stream()
            .filter(section -> section.isSameDownStation(targetStation))
            .findFirst()
            .ifPresent(section -> section.merge(targetSection));

        sections.remove(targetSection);
    }

    private boolean isUpStationMatch(Station targetStation) {
        return sections.stream()
            .anyMatch(section -> section.isSameUpStation(targetStation));
    }

    private boolean isDownStationMatch(Station targetStation) {
        return sections.stream()
            .anyMatch(section -> section.isSameUpStation(targetStation));
    }

    private boolean isBothInclude(Station targetStation) {
        return isUpStationMatch(targetStation) && isDownStationMatch(targetStation);
    }

    private void validateRemovable(Station targetStation) {
        List<Section> targetSections = this.sections.stream()
            .filter(section -> section.hasSameStation(targetStation))
            .collect(Collectors.toList());

        if (targetSections.isEmpty()) {
            throw new IllegalStationException("노선에 등록되어 있지 않은 역을 제거할 수 없습니다.");
        }
    }

    private void validateMinimumSectionSize() {
        if (sections.size() == MINIMUM_SECTIONS_LIMIT) {
            throw new IllegalSectionRemoveException(MINIMUM_SECTIONS_LIMIT);
        }
    }
}
