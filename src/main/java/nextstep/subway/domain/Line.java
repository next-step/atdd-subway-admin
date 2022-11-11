package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.apache.commons.lang3.StringUtils;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String color;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_line_up_station"))
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_line_down_station"))
    private Station downStation;
    private Integer distance;

    public static Line of(String name, String color, Station upStation, Station downStation, Integer distance) {
        return new Line(name, color, upStation, downStation, distance);
    }

    protected Line() {

    }

    private Line(String name, String color, Station upStation, Station downStation, Integer distance) {

        validName(name);
        validColor(color);

        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private static void validColor(String color) {
        if (StringUtils.isBlank(color)) {
            throw new IllegalArgumentException("노선의 색을 입력하세요.");
        }
    }

    private static void validName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("노선의 이름을 입력하세요.");
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

    public Integer getDistance() {
        return distance;
    }

    public void changeNameAndColor(String newName, String newColor) {
        this.name = newName;
        this.color = newColor;
    }
}
