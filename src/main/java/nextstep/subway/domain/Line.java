package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.apache.commons.lang3.StringUtils;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    @Column(nullable = false)
    private String color;

    protected Line() {
    }

    private Line(LineBuilder lineBuilder) {
        this.name = lineBuilder.name;
        this.color = lineBuilder.color;
    }

    public static LineBuilder builder(String name, String color) {
        return new LineBuilder(name, color);
    }

    public static class LineBuilder {
        private Long id;
        private final String name;
        private final String color;

        private LineBuilder(String name, String color) {
            validateNameNotNull(name);
            validateColorNotNull(color);
            this.name = name;
            this.color = color;
        }

        public LineBuilder id(Long id) {
            this.id = id;
            return this;
        }

        private void validateNameNotNull(String name) {
            if (StringUtils.isNotEmpty(name)) {
                throw new IllegalArgumentException("이름 정보가 없습니다.");
            }
        }

        private void validateColorNotNull(String color) {
            if (StringUtils.isNotEmpty(color)) {
                throw new IllegalArgumentException("칼라 정보가 없습니다.");
            }
        }

        public Line build() {
            return new Line(this);
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
}
