package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.domain.Line.LineBuilder;
import org.apache.commons.lang3.StringUtils;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    protected Station() {
    }

    private Station(StationBuilder stationBuilder) {
        this.name = stationBuilder.name;
    }

    public static StationBuilder builder(String name) {
        return new StationBuilder(name);
    }

    public static class StationBuilder {
        private Long id;
        private final String name;

        private StationBuilder(String name) {
            validateNameNotNull(name);
            this.name = name;
        }

        public StationBuilder id(Long id) {
            this.id = id;
            return this;
        }

        private void validateNameNotNull(String name) {
            if (StringUtils.isNotEmpty(name)) {
                throw new IllegalArgumentException("이름 정보가 없습니다.");
            }
        }

        public Station build() {
            return new Station(this);
        }
    }
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
