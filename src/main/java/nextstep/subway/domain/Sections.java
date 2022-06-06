package nextstep.subway.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import org.springframework.dao.DataIntegrityViolationException;

@Embeddable
public class Sections {
    public static final String ERROR_EXISTS_SECTION = "이미 존재하는 구간입니다.";
    public static final String ERROR_CAN_NOT_CONNECT_SECTION = "연결되는 구간을 찾을 수 없습니다.";

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id")
    @OrderBy("order ASC")
    private final List<Section> sections = new LinkedList<>();

    protected Sections() {
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        validateForAddSection(section);
        addSectionInternal(section);
    }

    private void addSectionInternal(Section section) {
        if (isFirstSection(section)) {
            addSectionToList(section, 0);
            return;
        }

        if (isLastSection(section)) {
            addSectionToList(section, sections.size());
            return;
        }

        addSectionWithUpdateMatchedSection(section);
    }

    private boolean isFirstSection(Section section) {
        return sections.get(0).getUpStation().equals(section.getDownStation());
    }

    private boolean isLastSection(Section section) {
        return sections.get(sections.size() - 1).getDownStation().equals(section.getUpStation());
    }

    private void addSectionWithUpdateMatchedSection(Section section) {
        int index = getIndexOfMatchedSection(section);
        Section matchedSection = sections.get(index);

        if(matchedSection.getUpStation().equals(section.getUpStation())) {
            matchedSection.updateUpStation(section.getDownStation());
            matchedSection.updateDistance(matchedSection.getDistance() - section.getDistance());
            addSectionToList(section, index);
            return;
        }

        if(matchedSection.getDownStation().equals(section.getDownStation())) {
            matchedSection.updateDownStation(section.getUpStation());
            matchedSection.updateDistance(matchedSection.getDistance() - section.getDistance());
            addSectionToList(section, index + 1);
        }
    }

    private Integer getIndexOfMatchedSection(Section section) {
        return IntStream
                .range(0, sections.size())
                .filter(index -> sections.get(index).match(section)).findFirst()
                .orElseThrow(() -> new DataIntegrityViolationException(ERROR_CAN_NOT_CONNECT_SECTION));
    }

    private void addSectionToList(Section section, int index) {
        section.updateOrder(index);
        sections.add(index, section);
        increaseOrderOfNextSections(index + 1);
    }

    private void increaseOrderOfNextSections(int index) {
        for (int i = index; i < sections.size(); i++) {
            sections.get(i).increaseOrder();
        }
    }

    private void validateForAddSection(Section section) {
        validateExists(section);
        validateConnected(section);
    }

    private void validateExists(Section section) {
        if (checkExistsStation(section.getUpStation()) && checkExistsStation(section.getDownStation())) {
            throw new DataIntegrityViolationException(ERROR_EXISTS_SECTION);
        }
    }

    private void validateConnected(Section section) {
        if (!checkExistsStation(section.getUpStation()) && !checkExistsStation(section.getDownStation())) {
            throw new DataIntegrityViolationException(ERROR_CAN_NOT_CONNECT_SECTION);
        }
    }

    private boolean checkExistsStation(Station station) {
        return sections.stream().anyMatch(x -> x.getUpStation().equals(station)
                || x.getDownStation().equals(station));
    }
}
