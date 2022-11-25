package nextstep.subway.fixtures;

import nextstep.subway.domain.Station;
import nextstep.subway.domain.repository.StationRepository;

public class StationTestFixture {

    public static Station 경기광주역 = null;
    public static Station 중앙역 = null;
    public static Station 모란역 = null;
    public static Station 미금역 = null;
    public static String 경기광주역ID = null;
    public static String 중앙역ID = null;
    public static String 모란역ID = null;
    public static String 미금역ID = null;

    public static void setStations(StationRepository stationRepository) {
        경기광주역 = stationRepository.save(new Station("경기 광주역"));
        중앙역 = stationRepository.save(new Station("중앙역"));
        모란역 = stationRepository.save(new Station("모란역"));
        미금역 = stationRepository.save(new Station("미금역"));
        경기광주역ID = String.valueOf(경기광주역.getId());
        중앙역ID = String.valueOf(중앙역.getId());
        모란역ID = String.valueOf(모란역.getId());
        미금역ID = String.valueOf(미금역.getId());
    }
}
