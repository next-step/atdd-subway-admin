package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    @Column(name = "sections")
    private final List<Section> values;

    private Sections(List<Section> values) {
        this.values = values;
    }

    protected Sections() {
        throw new IllegalStateException();
    }

    public List<Section> getValues() {
        return values;
    }

    public static Sections of(List<Section> sections) {
        return new Sections(sections);
    }

    public void add(Section targetSection, Line line) {
        if (this.values.isEmpty()) {
            values.add(targetSection);
            return;
        }

        List<Section> temp = new ArrayList<>();
        final Section s = values.get(0);
        final Station upStation = targetSection.getUpStation();
        final Station downStation = targetSection.getDownStation();
        final int distance = targetSection.getDistance();

        if (s.getUpStation().equals(upStation) && distance < 0 && s.getDistance() > distance) {
            final Section section1 = new Section(s.getUpStation(), downStation, Math.abs(distance));
            section1.toLine(line);
            final Section section2 = new Section(downStation, s.getDownStation(), s.getDistance() + distance);
            section2.toLine(line);

            temp.add(section1);
            temp.add(section2);
        } else if (s.getDownStation().equals(downStation) && distance > 0 && s.getDistance() > distance){

            final Section section1 = new Section(s.getUpStation(), upStation, s.getDistance() - distance);
            section1.toLine(line);
            final Section section2 = new Section(upStation, s.getDownStation(), Math.abs(distance));
            section2.toLine(line);

            temp.add(section1);
            temp.add(section2);

        } else if (s.getUpStation().equals(downStation) && distance > 0) { //TODO distance 의 조건 제약 필요함

            final Section section1 = new Section(upStation, downStation, Math.abs(distance));
            section1.toLine(line);
            final Section section2 = new Section(s.getUpStation(), s.getDownStation(), s.getDistance());
            section2.toLine(line);

            temp.add(section1);
            temp.add(section2);
        } else if (s.getDownStation().equals(upStation) && distance < 0) {

            final int abs = Math.abs(distance);
            final Section section1 = new Section(s.getUpStation(), s.getDownStation(), s.getDistance());
            section1.toLine(line);
            final Section section2 = new Section(s.getDownStation(), downStation, abs);
            section2.toLine(line);

            temp.add(section1);
            temp.add(section2);

        } else {
            temp.add(targetSection);
        }

        values.clear();
        values.addAll(temp);
    }
}
