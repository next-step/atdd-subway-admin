package nextstep.subway.domain;

import nextstep.subway.exception.ValueOutOfBoundsException;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    @Transient
    private static final int DISTANCE_MIN = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int distance;

    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_line"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_down_station"))
    @OneToOne(fetch = FetchType.LAZY)
    private Station downStation;

    @OneToOne(mappedBy = "downSection", cascade = CascadeType.ALL)
    private Station upStation;

    /**
     * 생성자
     */
    protected Section() {}

    private Section(int distance) {
        this.distance = distance;
    }

    //테스트용 생성자
    public Section(Long id, int distance) {
        this.id = id;
        this.distance = distance;
    }

    /**
     * 생성 메소드
     */
    public static Section create(int distance) {
        validate(distance);
        return new Section(distance);
    }

    private static void validate(int distance) {
        if (distance < DISTANCE_MIN) {
            throw new ValueOutOfBoundsException("구간의 길이가 " + DISTANCE_MIN + " 이상 이어야합니다.",
                    "distance", String.valueOf(distance), null);
        }
    }


    /**
     * 연관관계 메소드
     */
    public void registerDownStation(Station downStation) {
        this.downStation = downStation;
        downStation.setUpSection(this);
    }

    public void registerLine(Line line) {
        if (!isRegisterAlreadyWithSameLine(line)) {
            this.line = line;
            line.addSections(this);
        }
    }

    private boolean isRegisterAlreadyWithSameLine(Line line) {
        return line.contains(this);
    }

    /**
     * 비즈니스 메소드
     */
    public void resetDistance(int distance) {
        if (this.distance <= distance) {
            throw new IllegalStateException("새로 등록될 구간의 길이는 기존에 존재하는 구간의 길이보다 작아야합니다.");
        }
        this.distance -= distance;
    }

    /**
     * 그 밖의 메소드
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id(), section.id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id());
    }

    public Long id() {
        return id;
    }

    public Station downStation() {
        return downStation;
    }

    public int distance() {
        return distance;
    }

    public Line line() {
        return line;
    }
}
