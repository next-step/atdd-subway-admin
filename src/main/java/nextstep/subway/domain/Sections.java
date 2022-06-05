package nextstep.subway.domain;

import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import org.springframework.dao.DataIntegrityViolationException;

@Embeddable
public class Sections {
    public static final String ERROR_EXISTS_SECTION = "이미 존재하는 구간입니다.";
    public static final String ERROR_CAN_NOT_CONNECT_SECTION = "연결되는 구간을 찾을 수 없습니다.";
    @OneToMany
    @JoinColumn(name = "line_id")
    private List<Section> sections = new LinkedList<>();

    protected Sections() {
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        validateForAddSection(section);
    }

    private void validateForAddSection(Section section) {
        validateExists(section);
        validateConnected(section);
    }

    private void validateExists(Section section) {
        if (checkExistsOfUpStationId(section.getUpStationId())
                && checkExistsOfDownStationId(section.getDownStationId())) {
            throw new DataIntegrityViolationException(ERROR_EXISTS_SECTION);
        }
    }

    private void validateConnected(Section section) {
        if (!checkExistsOfUpStationId(section.getUpStationId())
                && !checkExistsOfDownStationId(section.getDownStationId())) {
            throw new DataIntegrityViolationException(ERROR_CAN_NOT_CONNECT_SECTION);
        }
    }

    private boolean checkExistsOfUpStationId(Long upStationId) {
        return sections.stream().anyMatch(x -> x.getUpStationId().equals(upStationId)
                || x.getDownStationId().equals(upStationId));
    }

    private boolean checkExistsOfDownStationId(Long downStationId) {
        return sections.stream().anyMatch(x -> x.getUpStationId().equals(downStationId)
                || x.getDownStationId().equals(downStationId));
    }
}
