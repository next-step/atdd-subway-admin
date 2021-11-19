package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(indexes = @Index(name = "line_name_index", columnList = "name"))
public class Line extends BaseEntity {
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

    private void validate(String name, String color) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("노선의 이름이 빈값일 수 없습니다.");
        }
        if (!StringUtils.hasText(color)) {
            throw new IllegalArgumentException("노선의 색상값이 빈값일 수 없습니다.");
        }
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
}
