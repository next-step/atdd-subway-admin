package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {

    protected Line() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String color;

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
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

    // TODO: 노선 생성 시 상행종점역과 하행종점역을 등록함
    // TODO: 지하철 노선에 역을 맵핑하는 기능은 없지만 노선 조회시 포함된 역 목록이 함께 응답
}
