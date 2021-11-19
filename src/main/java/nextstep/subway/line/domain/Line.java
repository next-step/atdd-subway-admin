package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.exception.NotEmptyLineColorException;
import nextstep.subway.line.exception.NotEmptyLineNameException;
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
            throw new NotEmptyLineNameException();
        }
        if (!StringUtils.hasText(color)) {
            throw new NotEmptyLineColorException();
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
