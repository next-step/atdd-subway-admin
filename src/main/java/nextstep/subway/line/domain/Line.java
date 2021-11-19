package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {
    private static final String ERROR_MESSAGE = "빈 값을 입력하였습니다.";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    public Line() {
    }

    public Line(String name, String color) {
        validate(name, color);
        this.name = name;
        this.color = color;
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
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

    private void validate(String name, String color) {
        if(isBlank(name) || isBlank(color)) {
            throw new IllegalArgumentException(ERROR_MESSAGE);
        }
    }

    private static boolean isBlank(String text) {
        return text == null || text.isEmpty();
    }
}
