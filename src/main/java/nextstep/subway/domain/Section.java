package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import nextstep.subway.exception.SectionInvalidException;
import org.springframework.util.ObjectUtils;

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

    public void setSectionAfterThis(Section appendSection) {
        validDistance(appendSection);
        appendSection.setBackNextSection(this, nextSection);

        if (ObjectUtils.isEmpty(this.nextSection)) {//line 상향종착지, distance변경해야함.
            this.nextSection = appendSection;
            this.line.changeUpStation(appendSection);
            return;
        }

        this.distance = this.distance - appendSection.getDistance();
        this.nextSection.setBackNextSection(appendSection, null);
        this.nextSection = appendSection;
    }

    public void setBackNextSection(Section backSection, Section nextSection) {
        if (!ObjectUtils.isEmpty(backSection)) {
            this.backSection = backSection;
        }
        if (!ObjectUtils.isEmpty(nextSection)) {
            this.nextSection = nextSection;
        }
    }

    private void validDistance(Section appendSection) {
        if (this.distance <= appendSection.distance) { //현재 섹션보다 신규 섹션의 길이가 같거나 크면 넣지못한다.
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
}
