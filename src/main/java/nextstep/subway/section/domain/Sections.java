package nextstep.subway.section.domain;

import nextstep.subway.section.exception.SectionDuplicationException;
import nextstep.subway.station.exception.StationAllNotExistedException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        validateDuplication(section);
        validateExistence(section);
        updateUpStation(section);
        updateDownStation(section);
        sections.add(section);
    }

    private void validateDuplication(Section section) {
        if (sections.stream().anyMatch(section::matchAllStations)) {
            throw new SectionDuplicationException();
        }
    }

    private void validateExistence(Section section) {
        if (sections.stream().anyMatch(section::matchAllNoneStations)) {
            throw new StationAllNotExistedException();
        }
    }

    private void updateUpStation(Section section) {
        sections.stream()
                .filter(section::isSameUpStation)
                .findFirst()
                .ifPresent(s -> {
                    s.updateUpStation(section);
                    s.updateDistance(section);
                });
    }

    private void updateDownStation(Section section) {
        sections.stream()
                .filter(section::isSameDownStation)
                .findFirst()
                .ifPresent(s -> {
                    s.updateDownStation(section);
                    s.updateDistance(section);
                });
    }
}
