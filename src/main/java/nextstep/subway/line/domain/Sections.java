package nextstep.subway.line.domain;

import nextstep.subway.exception.CannotConnectSectionException;
import nextstep.subway.exception.CannotDeleteSectionException;
import nextstep.subway.exception.UpdateExistingSectionException;
import nextstep.subway.line.exception.SectionExceptionCode;
import nextstep.subway.station.domain.Station;
import org.springframework.util.CollectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Embeddable
public class Sections {

    private static final int LAST_ONE_SECTION = 1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
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

    public void updateSections(Line line, Section request, List<Section> matchedSections) {
        validateRequestedSection(request, matchedSections);
        SectionConnectManager.connectAll(line, request);
    }

    private void validateRequestedSection(Section request, List<Section> matchedSections) {
        if (CollectionUtils.isEmpty(matchedSections)) {
            throw new CannotConnectSectionException(
                    SectionExceptionCode.CANNOT_CONNECT_SECTION.getMessage());
        }

        if(isContainedSameSection(request, matchedSections)) {
            throw new UpdateExistingSectionException(
                    SectionExceptionCode.CANNOT_UPDATE_SAME_SECTION.getMessage());
        }
    }

    private boolean isContainedSameSection(Section request, List<Section> matchedSections) {
        List<Station> stations = matchedSections.stream()
                .map(Section::getStations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(toList());

        return stations.containsAll(request.getStations());
    }

    public void deleteSectionContainsStation(Line line, Optional<Section> sectionOfUpStation, Optional<Section> sectionOfDownStation) {
        checkSectionDeletable(sectionOfUpStation, sectionOfDownStation);
        new SectionDeleteManager(line, sectionOfUpStation, sectionOfDownStation).delete();
    }

    private void checkSectionDeletable(Optional<Section> sectionOfUpStation, Optional<Section> sectionOfDownStation) {
        if(!sectionOfUpStation.isPresent() && !sectionOfDownStation.isPresent()) {
            throw new CannotDeleteSectionException(SectionExceptionCode.CANNOT_DELETE_SECTION.getMessage());
        }

        if(sections.size() == LAST_ONE_SECTION) {
            throw new CannotDeleteSectionException(SectionExceptionCode.CANNOT_DELETE_LAST_ONE_SECTION.getMessage());
        }
    }

    public void deleteSection(Section request) {
        sections.remove(request);
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }
}
