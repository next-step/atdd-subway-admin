package nextstep.subway.domain;

import nextstep.subway.dto.StationResponse;

import javax.persistence.*;

@Entity
public class Station extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    protected Station() {}

    public Station(String name) {
        this.name = name;
    }

    public StationResponse toStationResponse() {
        return new StationResponse(id, name, super.getCreatedDate(), super.getModifiedDate());
    }

    public Section toSection(Station preStation, int distance) {
        return new Section(preStation, this, distance);
    }

    public boolean equalsById(Station station) {
        return id.equals(station.getId());
    }

    public Long getId() {
        return id;
    }

}
