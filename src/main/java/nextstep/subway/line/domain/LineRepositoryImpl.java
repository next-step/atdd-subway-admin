package nextstep.subway.line.domain;

import nextstep.subway.section.domain.QSection;
import nextstep.subway.station.domain.QStation;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class LineRepositoryImpl extends QuerydslRepositorySupport implements LineRepositoryCustom {

    public LineRepositoryImpl() {
        super(Line.class);
    }

    @Override
    public Line getWithStations(Long id) {
        QLine line = QLine.line;
        QSection section = QSection.section;
        QStation upStation = new QStation("upStation");
        QStation downStation = new QStation("downStation");
        return from(line)
                .join(line.sections, section)
                .fetchJoin()
                .join(section.up, upStation)
                .fetchJoin()
                .join(section.down, downStation)
                .fetchJoin()
                .where(line.id.eq(id))
                .fetchOne();
    }
}
