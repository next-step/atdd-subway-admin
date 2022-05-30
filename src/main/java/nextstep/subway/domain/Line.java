package nextstep.subway.domain;

import nextstep.subway.dto.LineRequest;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String color;

    private Long distance;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    protected Line() {}

    public Line(String name, String color, Long distance, Station upStation, Station downStation) {
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Line modify(LineRequest.Modification modify) {
        this.name = modify.getName();
        this.color = modify.getColor();

        return this;
    }

    public void addSection(LineRequest.Section lineSectionRequest, Station changeUpStation, Station changeDownStation) {
        checkPossibleAddSection(lineSectionRequest);

        if (this.upStation.isSameId(lineSectionRequest.getUpStationId())) {
            this.upStation = changeDownStation;
        }
        if (this.downStation.isSameId(lineSectionRequest.getDownStationId())) {
            this.downStation = changeUpStation;
        }
        this.distance -= lineSectionRequest.getDistance();
    }

    public void checkPossibleAddSection(LineRequest.Section lineSectionRequest) {
        boolean isSameUpStation = this.upStation.getId().equals(lineSectionRequest.getUpStationId());
        boolean isSameDownStation = this.downStation.getId().equals(lineSectionRequest.getDownStationId());

        if (this.distance <= lineSectionRequest.getDistance()) {
            throw new IllegalArgumentException("기존 노선의 길이와 같거나 긴 노선을 추가할 수 없습니다.");
        }
        if (isSameUpStation && isSameDownStation) {
            throw new IllegalArgumentException("같은 상/하행역을 등록할 수 없습니다.");
        }
    }

    public Line copyAndChangeBy(Long distance, Station upStation, Station downStation) {
        return new Line(this.name, this.color, distance, upStation, downStation);
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
