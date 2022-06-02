package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.exception.NotFoundException;
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
        this.id = stationBuilder.id;
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
            if (StringUtils.isEmpty(name)) {
                throw new NotFoundException("이름 정보가 없습니다.");
            }
        }

        public Station build() {
            return new Station(this);
        }
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Station station = (Station) o;
        return Objects.equals(id, station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
