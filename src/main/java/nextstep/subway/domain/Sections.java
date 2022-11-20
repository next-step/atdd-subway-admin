package nextstep.subway.domain;

import static nextstep.subway.constant.Constant.ADD_SECTION_FAIL_CAUSE_DUPLICATE;
import static nextstep.subway.constant.Constant.ADD_SECTION_FAIL_CAUSE_NOT_MATCH;
import static nextstep.subway.constant.Constant.DELETE_FAIL_CAUSE_ONLY_ONE;
import static nextstep.subway.constant.Constant.NOT_FOUND_SECTION;
import static nextstep.subway.constant.Constant.MINIMUM_SIZE_OF_SECTIONS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.exception.DeleteFailException;
import nextstep.subway.exception.NotFoundException;

@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(Section section){
        this.sections = Arrays.asList(section);
    }

    public void addLineStation(Section section) {
        validateDuplicate(section);
        validateNotMatchStation(section);
        updateUpStation(section);
        updateDownStation(section);
        sections.add(section);
    }

    private void updateUpStation(Section section){
        sections.stream()
                .filter(it -> it.getUpStation() == section.getUpStation())
                .findFirst()
                .ifPresent(it -> {
                    it.updateUpStation(section.getDownStation());
                    it.minusDistance(section.getDistance());
                });
    }

    private void updateDownStation(Section section) {
        sections.stream()
                .filter(it -> it.getDownStation() == section.getDownStation())
                .findFirst()
                .ifPresent(it -> {
                    it.updateDownStation(section.getUpStation());
                    it.minusDistance(section.getDistance());
                });
    }

    public void remove(Station removeStation) {
        validateSectionsSize();
        if (findFirstSection().isEqualToUpStation(removeStation)) {
            sections.remove(removeStation);
            return;
        }
        if (findLastSection().isEqualToDownStation(removeStation)) {
            sections.remove(removeStation);
            return;
        }
        Section updateTarget = findSectionByDownStation(removeStation);
        Section removeTarget = findSectionByUpStation(removeStation);
        updateTarget.updateDownStation(removeTarget.getDownStation());
        updateTarget.plusDistance(removeTarget.getDistance());
        sections.remove(removeTarget);
    }

    private Section findSectionByDownStation(Station station) {
        return sections.stream()
                .filter(it -> it.getDownStation() == station)
                .findFirst()
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_SECTION));
    }

    private Section findSectionByUpStation(Station station) {
        return sections.stream()
                .filter(it -> it.getUpStation() == station)
                .findFirst()
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_SECTION));
    }

    public List<StationResponse> getLineStations() {
        List<Station> allStations = getStationsFromSections();
        return allStations.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    private List<Station> getStationsFromSections(){
        List<Station> stations = new ArrayList<>();
        Section foundSection = findFirstSection();
        while(foundSection != null) {
            stations.add(foundSection.getUpStation());
            foundSection = findNextSection(foundSection).orElse(null);
        }
        foundSection = findLastSection();
        stations.add(foundSection.getDownStation());
        return stations;
    }

    private Optional<Section> findNextSection(Section foundSection){
        return sections.stream()
                .filter(section -> section.getUpStation() == foundSection.getDownStation())
                .findFirst();
    }

    private Section findFirstSection(){
        return sections.stream()
                .filter(section -> sections.stream().map(Section::getDownStation).noneMatch(Predicate.isEqual(section.getUpStation())))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_SECTION));
    }

    private Section findLastSection(){
        return sections.stream()
                .filter(section -> sections.stream().map(Section::getUpStation).noneMatch(Predicate.isEqual(section.getDownStation())))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_SECTION));
    }

    private void validateDuplicate(Section section) {
        boolean anyMatch = sections.stream()
                .anyMatch(it -> it.getUpStation() == section.getUpStation()
                        && it.getDownStation() == section.getDownStation());
        if (anyMatch) {
            throw new IllegalArgumentException(ADD_SECTION_FAIL_CAUSE_DUPLICATE);
        }
    }

    private void validateNotMatchStation(Section section) {
        boolean anyMatch = sections.stream()
                .anyMatch(it -> it.getUpStation() == section.getUpStation()
                        || it.getUpStation() == section.getDownStation()
                        || it.getDownStation() == section.getUpStation()
                        || it.getDownStation() == section.getDownStation());
        if (!anyMatch) {
            throw new IllegalArgumentException(ADD_SECTION_FAIL_CAUSE_NOT_MATCH);
        }
    }

    private void validateSectionsSize(){
        if(sections.size() <= MINIMUM_SIZE_OF_SECTIONS){
            throw new DeleteFailException(DELETE_FAIL_CAUSE_ONLY_ONE);
        }
    }
}