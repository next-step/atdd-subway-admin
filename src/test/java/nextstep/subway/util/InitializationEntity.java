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

    public static Section section_1;
    public static Section section_2;

    public static Line line_1;

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

    public void initLine() {
        line_1 = lineRepository.save(new Line("1호선", "빨간색"));
    }

    public void initSection() {
        section_1 = sectionRepository.save(new Section(upStation, mediumStation, line_1, 5));
        section_2 = sectionRepository.save(new Section(mediumStation, downStation, line_1, 7));
    }

}
