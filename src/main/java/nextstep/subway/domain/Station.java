package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.dto.StationDTO;
import org.apache.commons.lang3.StringUtils;

@Entity
public class Station extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    public void update(Station station) {
        if(StringUtils.isNotEmpty(station.getName())){
            this.name = station.getName();
        }
    }

    public StationDTO toStationDTO() {
        return new StationDTO(this.id, this.name);
    }

    protected Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
