package nextstep.subway.application;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineStationService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineStationService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public void addSection(Long id, SectionRequest sectionRequest) {
        // 1. line 조회
        Line findLine = findLineById(id);

        // 2. 상행/하행 조회
        Station preStation = findStationById(sectionRequest.getUpStationId());
        Station station = findStationById(sectionRequest.getDownStationId());

        // 3. lineStation 생성 및 저장
        findLine.addSection(preStation, station, sectionRequest.getDistance());
    }

    public void deleteSection(Long lineId, Long stationId) {
        // 1. line 조회
        Line findLine = findLineById(lineId);

        // 2. 구간 제거
        findLine.deleteSectionByStation(findStationById(stationId));
    }

    private Line findLineById(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(
                        () -> new IllegalArgumentException("노선 조회 실패")
                );
    }
    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("역 조회 실패"));
    }
}
