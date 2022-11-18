package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import nextstep.subway.dto.StationResponse;

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
        if(isSameUpStation(section)){
            updateUpStation(section);
        }
        if(isSameDownStation(section)){
            updateDownStation(section);
        }
        sections.add(section);
    }

    private void validateNotMatchedStation(Section section){
        boolean anyMatch = sections.stream()
                .anyMatch(it ->
                       it.getUpStation() == section.getUpStation()
                    || it.getUpStation() == section.getDownStation()
                    || it.getDownStation() == section.getUpStation()
                    || it.getDownStation() == section.getDownStation()
                );
        if(!anyMatch){
            throw new IllegalArgumentException();
        }
    }

    private void validateDuplicated(Section section){
        boolean anyMatch = sections.stream()
                .anyMatch(it ->
                       it.getUpStation() == section.getUpStation()
                    && it.getDownStation() == section.getDownStation()
                );
        if(anyMatch){
            throw new IllegalArgumentException();
        }
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

    private boolean isSameUpStation(Section section){
        return sections.stream()
                .anyMatch(it -> it.getUpStation().equals(section.getUpStation()));
    }

    private boolean isSameDownStation(Section section){
        return sections.stream()
                .anyMatch(it -> it.getDownStation().equals(section.getDownStation()));
    }

    public List<StationResponse> getLineStations() {
        List<Station> allStations = new ArrayList<>();
        sections.forEach(section -> {
            allStations.add(section.getUpStation());
            allStations.add(section.getDownStation());
        });
        return allStations.stream()
                .distinct()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }
}