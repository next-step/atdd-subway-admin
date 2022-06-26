package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_to_up_station"))
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_to_down_station"))
    private Station downStation;

    @Column(name = "distance")
    private int distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;

    public Section() {
    }

    public Section(Station upStation, Station downStation, int distance, Line line) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.line = line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void arrangeFirstSection(Section newSection) {
        this.downStation = newSection.upStation;
    }

    public void arrangeEndSection(Section newSection) {
        this.upStation = newSection.getDownStation();
    }

    public void arrangeInterSection(Section newSection) {
        this.upStation = newSection.downStation;
        this.distance -= newSection.distance;
    }

    public boolean canAddFirstSection(Section newSection) {
        if (isNotValidNewSection(newSection)) {
            return false;
        }

        if (!isFirstSection()) {
            return false;
        }

        return newSection.downStation.getId().equals(this.downStation.getId());
    }

    public boolean canAddEndSection(Section newSection) {
        if (isNotValidNewSection(newSection)) {
            return false;
        }

        if (!isEndSection()) {
            return false;
        }

        return newSection.upStation.getId().equals(this.upStation.getId());
    }

    public boolean canAddInterSection(Section section) {
        if (!isInterSection()) {
            return false;
        }

        // upStation이 같아야 inter section의 후보가 될 수 있다.
        return this.upStation.getId().equals(section.upStation.getId());
    }

    private boolean isFirstSection() {
        return this.upStation == null && this.downStation != null;
    }

    private boolean isEndSection() {
        return this.upStation != null && this.downStation == null;
    }

    private boolean isInterSection() {
        return !isFirstSection() && !isEndSection();
    }

    public boolean isSameSection(Section newSection) {
        return isInterSection()
                && this.upStation.getId().equals(newSection.getUpStation().getId())
                && this.downStation.getId().equals(newSection.getDownStation().getId());
    }

    private boolean isNotValidNewSection(Section newSection) {
        // newSection의 up, down station은 모두 null이 아니어야 한다.
        return newSection.getUpStation() == null || newSection.getDownStation() == null;
    }

    public boolean isShorterThen(Section newSection) {
        return this.distance < newSection.distance;
    }
}
