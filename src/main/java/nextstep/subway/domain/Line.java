package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "LINE")
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    protected Line() {
    }

    private Line(Long id, String name, String color, Station upStation, Station downStation) {
        validateName(name);
        validateColor(color);
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public static Line of(String name, String color, Station upStation, Station downStation) {
        return new Line(null, name, color, upStation, downStation);
    }

    private void validateName(String name) {
        if (isBlankString(name)) {
            throw new IllegalArgumentException("라인 생성 시 이름은 필수 값 입니다.");
        }
    }

    private void validateColor(String color) {
        if (isBlankString(color)) {
            throw new IllegalArgumentException("라인 생성 시 이름은 필수 값 입니다.");
        }
    }

    private boolean isBlankString(String text) {
        return text == null || text.trim().isEmpty();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Line)) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
