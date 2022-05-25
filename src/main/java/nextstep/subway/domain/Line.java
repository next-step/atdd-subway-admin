package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.dto.LineUpdateRequest;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String color;

    private int distance;

    @ManyToOne
    @JoinColumn(name = "upstation_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_line_up_station"))
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "downstation_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_line_down_station"))
    private Station downStation;

    protected Line() {
    }

    public Line(Long id, String name, String color, int distance) {
        this(id, name, color, distance, null, null);
    }

    public Line(String name, String color, int distance) {
        this(null, name, color, distance, null, null);
    }

    public Line(String name, String color, int distance, Station upStation, Station downStation) {
        this(null, name, color, distance, upStation, downStation);
    }

    public Line(Long id, String name, String color, int distance, Station upStation, Station downStation) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public void updateLine(LineUpdateRequest lineUpdateRequest) {
        this.name = lineUpdateRequest.getName();
        this.color = lineUpdateRequest.getColor();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getDistance() {
        return distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public boolean hasUpStation() {
        return upStation != null;
    }

    public boolean hasDownStation() {
        return downStation != null;
    }

    public void setUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public void setDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public void clearStations() {
        upStation = null;
        downStation = null;
    }
}
