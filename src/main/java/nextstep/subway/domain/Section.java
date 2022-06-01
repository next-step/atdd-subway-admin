package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import nextstep.subway.dto.response.SectionResponse;
import org.apache.commons.lang3.ObjectUtils;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UP_STATION_ID", foreignKey = @ForeignKey(name = "fk_section_up_station"))
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOWN_STATION_ID", foreignKey = @ForeignKey(name = "fk_section_down_station"))
    private Station downStation;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "LINE_ID", foreignKey = @ForeignKey(name = "fk_section_line"))
    private Line line;

    @Embedded
    private Distance distance;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Section nextSection;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Section backSection;

    protected Section() {
    }

    protected Section(Station upStation, Station downStation, Line line, Distance distance,
        Section nextSection, Section backSection) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.line = line;
        this.distance = distance;
        this.nextSection = nextSection;
        this.backSection = backSection;
    }

    public static Section of(Station upStation, Station downStation, Line line, Distance distance) {
        return new Section(upStation, downStation, line, distance, null, null);
    }

    public SectionResponse toResponse() {
        Long nextSectionId = nextSection != null ? nextSection.getId() : -1;
        Long backSectionId = backSection != null ? backSection.getId() : -1;

        return new SectionResponse(this.id, this.upStation.toStationDTO(),
            this.downStation.toStationDTO(),
            line.getName(), this.distance, nextSectionId, backSectionId);
    }

    public boolean insert(Section insertSection) {
        boolean insertFail = true;
        boolean insertSuccess = false;
        if (this.downStation == insertSection.getDownStation()) {
            insertFrontOfSection(insertSection);
            return insertSuccess;
        } else if (this.upStation == insertSection.getUpStation()) {
            insertBackOfSection(insertSection);
            return insertSuccess;
        }
        return insertFail;
    }

    private void insertBackOfSection(Section insertSection) {
        insertSection.setBackSection(this);
        insertSection.setNextSection(this.nextSection);

        this.distance.minus(insertSection.getDistance());

        this.upStation = insertSection.getDownStation();
        this.nextSection.setBackSection(insertSection);
        this.nextSection = insertSection;
    }

    private void insertFrontOfSection(Section insertSection) {
        insertSection.setBackSection(this.backSection);
        insertSection.setNextSection(this);

        this.distance.minus(insertSection.getDistance());
        this.downStation = insertSection.getUpStation();
        this.backSection.setNextSection(insertSection);
        this.backSection = insertSection;
    }

    public void appendAfterSection(Section appendSection) {
        this.nextSection = appendSection;
        appendSection.setBackSection(this);
        return;
    }

    public void appendBeforeSection(Section appendSection) {
        this.backSection = appendSection;
        appendSection.setNextSection(this);
        return;
    }

    public void setNextSection(Section nextSection) {
        this.nextSection = nextSection;
    }

    public void setBackSection(Section backSection) {
        this.backSection = backSection;
    }

    public boolean isFirstSection(){
        return ObjectUtils.isEmpty(downStation);
    }
    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Line getLine() {
        return line;
    }

    public Distance getDistance() {
        return distance;
    }

    public Section getNextSection() {
        return nextSection;
    }

    public Section getBackSection() {
        return backSection;
    }

    public Long getId() {
        return id;
    }
}
