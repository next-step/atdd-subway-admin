package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class SectionRepositoryImpl extends QuerydslRepositorySupport implements SectionRepositoryCustom {

    public SectionRepositoryImpl() {
        super(Section.class);
    }

    @Override
    public Section getStart(Line line) {
        QSection section = QSection.section;
        return from(section)
                .where(section.start.isTrue()
                        .and(section.line
                                .eq(line)))
                .fetchOne();
    }

    @Override
    public Section getByStation(Line line, Station station) {
        QSection section = QSection.section;
        return from(section)
                .where(section.up.eq(station)
                        .and(section.line.eq(line)))
                .fetchOne();
    }

}
