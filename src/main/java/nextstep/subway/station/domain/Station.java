package nextstep.subway.station.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public Station() {
    }

    private Station(Long id,
                    String name,
                    LocalDateTime createdDate,
                    LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    private Station(String name) {
        this.name = name;
    }

    public static Station of(Long id,
                             String name,
                             LocalDateTime createdDate,
                             LocalDateTime modifiedDate) {
        return new Station(id, name, createdDate, modifiedDate);
    }

    public static Station of(String name) {
        return new Station(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean compareName(Station station) {
        return this.name.equals(station.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(id, station.id) && Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public void deleteFrom(List<Section> sections) {
        // sections 를 로드 후 한번은 정렬하거나 하여 정렬되었다는 가정 하에,
        for (int i = 0; i < sections.size(); ++i) {
            Section sectionCurrent = sections.get(i);

            if (sectionCurrent.upStation().equals(this) && i == 0) {
                sections.remove(i);
                break;
            }

            if (sectionCurrent.upStation().equals(this) && i == sections.size() - 1) {
                sections.get(i-1).setDownStation(sections.get(i).downStation());
                sections.remove(i);
                break;
            }

            if (sectionCurrent.upStation().equals(this)) {
                sections.get(i-1).setDownStation(sections.get(i+1).upStation());
                sections.remove(i);
                break;
            }

            if (sectionCurrent.downStation().equals(this) && i == sections.size() - 1) {
                sections.remove(i);
                break;
            }
        }
        throw new IllegalStateException("삭제할 역이 없습니다.");
    }
}
