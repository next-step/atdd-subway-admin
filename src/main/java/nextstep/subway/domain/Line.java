package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.constant.ErrorCode;

@Entity
public class Line extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String color;
    @ManyToOne
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_line_to_upstation"), nullable = false)
    private Station upStation;      // 상행종점역
    @ManyToOne
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_line_to_downstation"), nullable = false)
    private Station downStation;    // 하행종점역

    protected Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation) {
        validateLineName(name);
        validateLineColor(color);
        validateUpStation(upStation);
        validateDownStation(downStation);

        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    private void validateLineName(String name) {
        if(name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(ErrorCode.노선명은_비어있을_수_없음.getErrorMessage());
        }
    }

    private void validateLineColor(String color) {
        if(color == null || color.trim().isEmpty()) {
            throw new IllegalArgumentException(ErrorCode.노선색상은_비어있을_수_없음.getErrorMessage());
        }
    }

    private void validateUpStation(Station upstation) {
        if(upstation == null) {
            throw new IllegalArgumentException(ErrorCode.상행종착역은_비어있을_수_없음.getErrorMessage());
        }
    }

    private void validateDownStation(Station downStation) {
        if(downStation == null) {
            throw new IllegalArgumentException(ErrorCode.하행종착역은_비어있을_수_없음.getErrorMessage());
        }
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

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }
}
