package nextstep.subway.util;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.LineStationRepository;
import nextstep.subway.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InitializationEntity {

    public static Station upStation;
    public static Station downStation;
    public static Station station_3;
    public static Station station_4;

    public static Section section_1;
    public static Section section_2;

    public static Line line_1;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineStationRepository lineStationRepository;

    public void initStations() {
        upStation = stationRepository.save(new Station("우장산역"));
        downStation = stationRepository.save(new Station("화곡역"));
        station_3 = stationRepository.save(new Station("까치산역"));
        station_4 = stationRepository.save(new Station("마곡나루역"));
    }

    public void initLineStation() {
        section_1 = lineStationRepository.save(new Section(upStation));
        section_2 = lineStationRepository.save(new Section(upStation, downStation, 10));
    }

    public void initLine() {
        Sections sections = new Sections();
        sections.add(section_1);
        sections.add(section_2);
        line_1 = lineRepository.save(new Line("1호선", "", sections));
    }

}
