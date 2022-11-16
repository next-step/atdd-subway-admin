package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    protected Sections(){

    }

    public void add(Station preStation, Station station, Integer distance){
        this.sections.add(new Section(preStation, station, distance));
    }

    public void addSection(Station preStation, Station station, Integer distance){
        validateAllIncludeStation(preStation, station);
        validateNotIncludeStation(preStation, station);

        if(isStationPresent(preStation)){
            this.sections.stream()
                    .filter(section -> preStation.equals(section.getPreStation()))
                    .findFirst()
                    .ifPresent(section -> section.updateSection(station, section.getStation(), distance));
        }

        if(isStationPresent(station)){
            this.sections.stream()
                    .filter(section -> station.equals(section.getStation()))
                    .findFirst()
                    .ifPresent(section -> section.updateSection(section.getPreStation(), preStation, distance));
        }

        add(preStation, station, distance);
    }

    private void validateNotIncludeStation(Station preStation, Station station) {
        if( !isStationPresent(preStation) && !isStationPresent(station)){
            throw new IllegalArgumentException("시작/도착 역이 모두 존재하지 않습니다.");
        }
    }

    private void validateAllIncludeStation(Station preStation, Station station) {
        if( isStationPresent(preStation) && isStationPresent(station)){
            throw new IllegalArgumentException("시작/도착 역이 이미 존재합니다.");
        }
    }

    private boolean isStationPresent(Station station){
        return this.sections.stream()
                .anyMatch(section -> station.equals(section.getStation()));
    }

    public List<Section> getOrderStations(){
        Optional<Section> first = this.sections.stream()
                .filter(section -> section.getPreStation() == null)
                .findAny();

        List<Section> orders = new ArrayList<>();
        while(first.isPresent()){
            Section tmp = first.get();
            orders.add(tmp);
            first = this.sections.stream()
                    .filter(section -> tmp.getStation() == section.getPreStation())
                    .findAny();
        }
        return orders;
    }

    public List<Section> getSections() {
        return sections;
    }
}
