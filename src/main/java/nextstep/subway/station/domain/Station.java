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

    public void deleteFrom(List<Section> sections) {
        DeletePosition deletePosition = DeletePosition.None();
        while (deletePosition.isNone() && deletePosition.index() < sections.size()) {
            deletePosition = checkDeletePosition(sections, deletePosition);
            deletePosition.nextIndex();
        }
        deletePosition.subtractIndex();

        if (deletePosition.isNone()) {
            throw new IllegalStateException("삭제하려는 역이 등록되어있지 않습니다.");
        }

        handleDeletion(sections, deletePosition);
    }

    private DeletePosition checkDeletePosition(List<Section> sections, DeletePosition position) {
        int index = position.index();
        Section currentSection = sections.get(index);
        if (currentSection.upStation().equals(this) && index == 0) {
            return position.typeUpInHead();
        }
        if (currentSection.upStation().equals(this) && index == sections.size() - 1) {
            return position.typeUpInTail();
        }
        if (currentSection.upStation().equals(this)) {
            return position.typeUpInMiddles();
        }
        if (currentSection.downStation().equals(this) && index == sections.size() - 1) {
            return position.typeDownInTail();
        }
        return position;
    }

    private void handleDeletion(List<Section> sections, DeletePosition deletePosition) {
        int index = deletePosition.index();
        Section prevSection;
        Section nextSection;
        if (deletePosition.isUpInHead()) {
            sections.remove(index);
        }
        if (deletePosition.isUpInTail()) {
            prevSection = sections.get(index-1);
            prevSection.setDownStation(sections.get(index).downStation());
            sections.remove(index);
        }
        if (deletePosition.isUpInMiddles()) {
            prevSection = sections.get(index-1);
            nextSection = sections.get(index+1);
            prevSection.handleAttributesToConnectInFrontOf(nextSection);
            sections.remove(index);
        }
        if (deletePosition.isDownInTail()) {
            sections.remove(index);
        }
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

}
