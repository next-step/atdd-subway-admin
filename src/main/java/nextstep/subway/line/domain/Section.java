package nextstep.subway.line.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name="station_id")
    private Station station;

    private Integer distance;

    public Station getStation() {
        return station;
    }

    public static Section of(Station station, String distance) {
        return Section.builder()
                .station(station)
                .distance(Integer.parseInt(distance))
                .build();
    }
}
