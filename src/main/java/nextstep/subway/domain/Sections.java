package nextstep.subway.domain;

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
        validateDuplicated(section);
        validateNotMatchedStation(section);
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
                .orElseThrow(NotFoundException::new);
    }

    private Section findLastSection(){
        return sections.stream()
                .filter(section -> sections.stream().map(Section::getUpStation).noneMatch(Predicate.isEqual(section.getDownStation())))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }

    private void validateDuplicated(Section section) {
        boolean anyMatch = sections.stream()
                .anyMatch(it -> it.getUpStation() == section.getUpStation()
                        && it.getDownStation() == section.getDownStation());
        if (anyMatch) {
            throw new IllegalArgumentException();
        }
    }

    private void validateNotMatchedStation(Section section) {
        boolean anyMatch = sections.stream()
                .anyMatch(it -> it.getUpStation() == section.getUpStation()
                        || it.getUpStation() == section.getDownStation()
                        || it.getDownStation() == section.getUpStation()
                        || it.getDownStation() == section.getDownStation());
        if (!anyMatch) {
            throw new IllegalArgumentException();
        }
    }
}