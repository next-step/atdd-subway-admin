package nextstep.subway.fixtures;

import nextstep.subway.domain.Station;
import nextstep.subway.domain.repository.StationRepository;

public class StationTestFixture {

    public static String 경기광주역ID = "";
    public static String 중앙역ID = "";
    public static String 모란역ID = "";
    public static String 미금역ID = "";

    public static void setStations(StationRepository stationRepository) {
        Station station1 = stationRepository.save(new Station("경기 광주역"));
        Station station2 = stationRepository.save(new Station("중앙역"));
        Station station3 = stationRepository.save(new Station("모란역"));
        Station station4 = stationRepository.save(new Station("미금역"));
        경기광주역ID = String.valueOf(station1.getId());
        중앙역ID = String.valueOf(station2.getId());
        모란역ID = String.valueOf(station3.getId());
        미금역ID = String.valueOf(station4.getId());
    }
}
