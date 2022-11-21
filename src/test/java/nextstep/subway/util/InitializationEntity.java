package nextstep.subway.util;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.SectionRepository;
import nextstep.subway.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InitializationEntity {

    public static Station upStation;
    public static Station downStation;
    public static Station mediumStation;
    public static Station newStation;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private SectionRepository sectionRepository;

    public void initStations() {
        upStation = stationRepository.save(new Station("우장산역"));
        downStation = stationRepository.save(new Station("화곡역"));
        mediumStation = stationRepository.save(new Station("까치산역"));
        newStation = stationRepository.save(new Station("마곡나루역"));
    }

    public Line createLine(String name) {
        return lineRepository.save(new Line(name, "빨간색"));
    }

    public Section addedSections(Line line, Station upStation, Station downStation, int distance) {
        return sectionRepository.save(new Section(upStation, downStation, line, distance));
    }

}
