package nextstep.subway.domain;

import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "name", unique = true, nullable = false))
    private Name name;

    @Embedded
    @AttributeOverride(name = "color", column = @Column(name = "color", nullable = false, unique = true))
    private Color color;

    @Column(name = "up_station_id")
    private Long upStationId;

    @Column(name = "down_station_id")
    private Long downStationId;

    @Embedded
    @AttributeOverride(name = "distance", column = @Column(name = "distance"))
    private Distance distance;

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY)
    private Set<Station> stations = new HashSet<>();

    public Line(Name name, Color color, Long upStationId, Long downStationId, Distance distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    protected Line() {
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Line updateName(Name name) {
        this.name = name;
        return this;
    }

    public Line updateColor(Color color) {
        this.color = color;
        return this;
    }

    public void addStations(List<Station> stationList) {
        stationList.stream().filter(station -> !stations.contains(station))
                .forEach(station -> stations.add(station));
    }

    public LineResponse toLineResponse() {
        return new LineResponse(id, name, color, stations.stream()
                .map(StationResponse::toLineResponse)
                .collect(Collectors.toList()));
    }
}
