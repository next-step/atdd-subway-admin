package nextstep.subway.section.domain;

import nextstep.subway.section.exception.SectionCannotDeleteException;
import nextstep.subway.section.exception.SectionDuplicationException;
import nextstep.subway.section.exception.SectionStationNotFoundException;
import nextstep.subway.station.exception.StationAllNotExistedException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {
    private static final int MIN_SIZE = 2;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        validateDuplication(section);
        validateRegister(section);
        updateUpStation(section);
        updateDownStation(section);
        sections.add(section);
    }

    public void deleteSection(Long stationId) {
        validateStationSize();
        Section targetSection = getTargetSection(stationId);
        cutOffSection(targetSection, stationId);
        this.sections.remove(targetSection);
    }

    private void validateDuplication(Section section) {
        if (sections.stream().anyMatch(section::matchAllStations)) {
            throw new SectionDuplicationException();
        }
    }

    private void validateRegister(Section section) {
        if (!isFirst()) {
            validateExistence(section);
        }
    }

    private void validateExistence(Section section) {
        boolean upStationExist = sections.stream().anyMatch(section::hasUpStations);
        boolean downStationExist = sections.stream().anyMatch(section::hasDownStations);

        if (!upStationExist && !downStationExist) {
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

    private boolean isFirst() {
        return sections.size() == 0;
    }

    private void validateStationSize() {
        if (sections.size() < MIN_SIZE) {
            throw new SectionCannotDeleteException();
        }
    }

    private Section getTargetSection(Long stationId) {
        return sections.stream()
                .filter(section -> section.hasStation(stationId))
                .findAny()
                .orElseThrow(SectionStationNotFoundException::new);
    }

    private void cutOffSection(Section targetSection, Long stationId) {
        cufOffByUpWard(targetSection, stationId);
        cufOffByDownWard(targetSection, stationId);
    }

    private void cufOffByUpWard(Section targetSection, Long stationId) {
        sections.stream().filter(section -> section.hasUpStation(stationId))
                .findFirst()
                .ifPresent(section -> section.changeDownStation(targetSection.getDownStation()));
    }

    private void cufOffByDownWard(Section targetSection, Long stationId) {
        sections.stream().filter(section -> section.hasUpStation(stationId))
                .findFirst()
                .ifPresent(section -> section.changeUpStation(targetSection.getUpStation()));
    }
}
