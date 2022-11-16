package nextstep.subway.util;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InitializationEntity {

    public static Station station_1;
    public static Station station_2;
    public static Station station_3;
    public static Station station_4;

    public static Line line_1;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    public void initStations() {
        station_1 = stationRepository.save(new Station("우장산역"));
        station_2 = stationRepository.save(new Station("화곡역"));
        station_3 = stationRepository.save(new Station("까치산역"));
        station_4 = stationRepository.save(new Station("마곡나루역"));
    }

    public void initLine() {
        line_1 = lineRepository.save(new Line("1호선", "", 10 ,station_1, station_2));
    }
}
