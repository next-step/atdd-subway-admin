package nextstep.subway.line.domain;

import nextstep.subway.exception.CannotConnectSectionException;
import nextstep.subway.exception.UpdateExistingSectionException;
import nextstep.subway.line.exception.SectionExceptionCode;
import nextstep.subway.station.domain.Station;
import org.springframework.util.CollectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

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

    public void updateSections(Line line, Section request, List<Section> matchedSections) {
        validateRequestedSection(request, matchedSections);
        SectionConnector.connectAll(line, request);
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

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }
}
