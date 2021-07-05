package nextstep.subway.line.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

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

    private Long distance;

    public Station getStation() {
        return station;
    }

    public static Section of(Station station) {
        return of(station, 0L);
    }
    public static Section of(Station station, String distance) {
        return of(station, Long.parseLong(distance));
    }

    public static Section of(Station station, Long distance) {
        return Section.builder()
                .station(station)
                .distance(distance)
                .build();
    }

    public Long getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(station, section.station);
    }

    @Override
    public int hashCode() {
        return Objects.hash(station);
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public void calculateDistanceForSection(Section upSection, Section downSection) {
        Long nextDistance = getDistance() - upSection.getDistance();
        setDistance(upSection.getDistance());
        downSection.setDistance(nextDistance);
    }

    public void verifyDistance(Section upSection) {
        if (upSection.getDistance() >= getDistance()) {
            throw new IllegalArgumentException("new Section should be smaller than exist Section (new:{} exist:{})"
                    .replaceFirst("\\{}",""+ upSection.getDistance())
                    .replaceFirst("\\{}",""+ getDistance()));
        }
    }
}
