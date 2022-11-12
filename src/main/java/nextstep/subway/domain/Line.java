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
    @Embedded
    private Name name;
    @Embedded
    private Color color;
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
        validateUpStation(upStation);
        validateDownStation(downStation);

        this.name = Name.from(name);
        this.color = Color.from(color);
        this.sections = Sections.from(Collections.singletonList(section));
        this.distance = Distance.from(distance);
    }

    public void updateLineNameAndColor(String name, String color) {
        if(!StringUtils.isNullOrEmpty(name)) {
            updateLineName(name);
        }
        if(!StringUtils.isNullOrEmpty(color)) {
            updateLineColor(color);
        }
    }

    private void updateLineName(String name) {
        this.name = Name.from(name);
    }

    private void updateLineColor(String color) {
        this.color = Color.from(color);
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

    public Name getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }
}
