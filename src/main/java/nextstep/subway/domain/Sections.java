package nextstep.subway.domain;

import nextstep.subway.exception.SectionsException;
import nextstep.subway.exception.SectionsExceptionMessage;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.exception.SectionsExceptionMessage.LONGER_THAN_OHTER;

public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {

    }

    public void addSection(Section section) {
        validationDistance(section);
        sections.add(section);
    }

    private void validationDistance(Section newSection) {
        boolean isNotAllowDistance = sections.stream().anyMatch(section -> section.isShortDistance(newSection));
        if(isNotAllowDistance){
            throw new SectionsException(LONGER_THAN_OHTER.getMessage());
        }
    }

    public List<Station> getStations() {
        return sections.stream()
                .flatMap(section -> section.getStations().stream())
                .distinct()
                .collect(Collectors.toList());
    }
}
