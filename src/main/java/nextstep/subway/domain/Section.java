package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import nextstep.subway.dto.response.SectionResponse;
import nextstep.subway.exception.SectionInvalidException;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Line line;

    private Integer distance;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Section nextSection;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Section backSection;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, Line line, Integer distance,
        Section nextSection, Section backSection) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.line = line;
        this.distance = distance;
        this.nextSection = nextSection;
        this.backSection = backSection;
    }

    public SectionResponse toResponse() {
        Long nextSectionId = nextSection != null ? nextSection.getId() : -1;
        Long backSectionId = backSection != null ? backSection.getId() : -1;

        return new SectionResponse(this.id, this.upStation.toStationDTO(),
            this.downStation.toStationDTO(),
            line.getName(), this.distance, nextSectionId, backSectionId);
    }

    public void insertBackOfSection(Section insertSection) {
        validDistance(this, insertSection);

        insertSection.setBackSection(this);
        insertSection.setNextSection(this.nextSection);

        this.distance = distance - insertSection.getDistance();
        this.upStation = insertSection.getDownStation();
        this.nextSection.setBackSection(insertSection);
        this.nextSection = insertSection;
    }

    public void insertFrontOfSection(Section insertSection) {
        validDistance(this, insertSection);

        insertSection.setBackSection(this.backSection);
        insertSection.setNextSection(this);

        this.distance = distance - insertSection.getDistance();
        this.downStation = insertSection.getUpStation();
        this.backSection.setNextSection(insertSection);
        this.backSection = insertSection;
    }

    public void appendAfterSection(Section appendSection) {
        this.nextSection = appendSection;
        appendSection.setBackSection(this);
        this.line.changeUpStation(appendSection);
        return;
    }

    public void appendBeforeSection(Section appendSection) {
        this.backSection = appendSection;
        appendSection.setNextSection(this);
        this.line.changeDownStation(appendSection);
        return;
    }

    public void setNextSection(Section nextSection) {
        this.nextSection = nextSection;
    }

    public void setBackSection(Section backSection) {
        this.backSection = backSection;
    }

    private void validDistance(Section sourceSection, Section appendSection) {
        if (sourceSection.getDistance()
            <= appendSection.getDistance()) { //현재 섹션보다 신규 섹션의 길이가 같거나 크면 넣지못한다.
            throw new SectionInvalidException();
        }
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

    public Integer getDistance() {
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
