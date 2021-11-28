package nextstep.subway.line.domain;

import nextstep.subway.line.exception.AlreadyRegisteredSectionException;
import nextstep.subway.line.exception.LongDistanceException;
import nextstep.subway.line.exception.NotFoundUpAndDownStation;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "position")
    private List<Section> sections = new ArrayList<>();

    // section 순서 필요?


    protected Sections() {
    }

    public Sections(Section section) {
        sections.add(section);
    }

    public List<StationResponse> getStations() {
        return sections.stream()
                .flatMap(section -> section.getStations().stream())
                .map(station -> StationResponse.of(station))
                .distinct()
                .collect(Collectors.toList());
    }

    public void addSection(Section inputSection) {
        if (isNotExistsUpAndDownStation(inputSection)) {
            throw new NotFoundUpAndDownStation();
        }

        if (isRegisteredSection(inputSection)) {
            throw new AlreadyRegisteredSectionException();
        }

        createInnerSection(inputSection);
        createOuterSection(inputSection);
    }

    private void createInnerSection(Section inputSection) {
        this.sections.stream()
                .filter(section -> isCreateInnerSection(section, inputSection))
                .findAny()
                .ifPresent(savedSection -> {
                    if (inputSection.isLongDistance(savedSection)) {
                        throw new LongDistanceException();
                    }
                    List<Section> sections = savedSection.createInnerSection(inputSection);
                    int index = this.sections.indexOf(savedSection);
                    this.sections.remove(savedSection);
                    this.sections.addAll(index, sections);
                });

    }

    private void createOuterSection(Section inputSection) {
        this.sections.stream()
                .filter(section -> isCreateOuterSection(section, inputSection))
                .findAny()
                .ifPresent(savedSection -> {
                    Section newSection = savedSection.createOuterSection(inputSection);
                    int index = this.sections.indexOf(savedSection);
                    if (savedSection.isCreateUpSection(inputSection)) {
                        this.sections.add(index, newSection);
                        return;
                    }
                    this.sections.add(index + 1, newSection);
                });
    }

    private boolean isCreateInnerSection(Section savedSection, Section inputSection) {
        return savedSection.equalsUpStation(inputSection) || savedSection.equalsDownStation(inputSection);
    }

    private boolean isCreateOuterSection(Section savedSection, Section inputSection) {
        return savedSection.isCreateUpSection(inputSection) || savedSection.isCreateDownSection(inputSection);
    }

    private boolean isNotExistsUpAndDownStation(Section section) {
        return sections.stream()
                .allMatch(savedSection -> savedSection.isNotContainUnAndDownStation(section));
    }

    private boolean isRegisteredSection(Section inputSection) {
        return sections.stream()
                .anyMatch(savedSection -> savedSection.equalsUpAndDownStations(inputSection));
    }

}
