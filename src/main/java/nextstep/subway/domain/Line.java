package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.constant.ErrorCode;
import nextstep.subway.utils.StringUtils;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private String color;
    @ManyToOne
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_line_to_upstation"), nullable = false)
    private Station upStation;
    @ManyToOne
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_line_to_downstation"), nullable = false)
    private Station downStation;
    @Embedded
    private Distance distance;

    protected Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation, Long distance) {
        validateLineName(name);
        validateLineColor(color);
        validateUpStation(upStation);
        validateDownStation(downStation);

        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = Distance.of(distance);
    }

    public void updateLineNameAndColor(String name, String color) {
        if(!StringUtils.isNullOrEmpty(name)) {
            this.name = name;
        }
        if(!StringUtils.isNullOrEmpty(color)) {
            this.color = color;
        }
    }

    private void validateLineName(String name) {
        if(StringUtils.isNullOrEmpty(name)) {
            throw new IllegalArgumentException(ErrorCode.노선명은_비어있을_수_없음.getErrorMessage());
        }
    }

    private void validateLineColor(String color) {
        if(StringUtils.isNullOrEmpty(color)) {
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
