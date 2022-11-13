package nextstep.subway.line.domain;

import nextstep.subway.exception.CannotConnectSectionException;
import nextstep.subway.exception.UpdateExistingSectionException;
import nextstep.subway.line.exception.SectionExceptionCode;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addSection(Line line, Section section) {
        if(!hasSection(section)) {
            this.sections.add(section);
            section.updateLine(line);
        }
    }

    public boolean hasSection(Section section) {
        return this.sections.contains(section);
    }

    public void updateSections(Line line, Section request) {
        checkUpdatable(request);

        Optional<Section> startSection = getStartSection(request.getUpStation());
        if(updateFirstSectionOrLastSection(line, startSection, request)) {
            return;
        }

        if(updateMiddleSection(line, startSection, request)) {
            return;
        }

        throw new UpdateExistingSectionException(SectionExceptionCode.ALREADY_EXISTS_SECTION.getMessage());
    }

    private void checkUpdatable(Section request) {
        if(!sections.stream().anyMatch(section -> section.hasAnyStation(request))) {
            throw new CannotConnectSectionException(
                    SectionExceptionCode.CANNOT_CONNECT_SECTION.getMessage());
        }
    }

    private Optional<Section> getStartSection(Station upStation) {
        return sections.stream()
                .filter(section -> section.equalsUpStation(upStation))
                .findFirst();
    }

    private boolean updateFirstSectionOrLastSection(Line line, Optional<Section> startSection, Section request) {
        if (isFirstSectionOrLastSection(startSection)) {
            addSection(line, request);
            return true;
        }

        return false;
    }

    private boolean isFirstSectionOrLastSection(Optional<Section> startSection) {
        return !startSection.isPresent();
    }

    private boolean updateMiddleSection(Line line, Optional<Section> startSection, Section request) {
        List<Section> downSections = findConnectedDownSectionsWithUpStation(startSection);

        if(isSameSectionNotCotains(downSections, request)) {
            Section target = startSection.get();
            target.update(request);
            addSection(line, request);
            return true;
        }

        return false;
    }

    private List<Section> findConnectedDownSectionsWithUpStation(Optional<Section> optCurrent) {
        List<Section> sections = new ArrayList<>();

        while(optCurrent.isPresent()) {
            Section current = optCurrent.get();
            sections.add(current);
            optCurrent = getNextSection(current);
        }

        return sections;
    }

    private Optional<Section> getNextSection(Section current) {
        return sections.stream()
                .filter(section -> section.equalsUpStation(current.getDownStation()))
                .findFirst();
    }

    private boolean isSameSectionNotCotains(List<Section> downSections, Section request) {
        return !downSections.stream()
                .anyMatch(section -> section.equalsDownStation(request.getDownStation()));
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }
}
