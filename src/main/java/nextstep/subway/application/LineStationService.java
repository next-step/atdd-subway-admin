package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
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
        Line findLine = lineRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("노선 조회 실패")
        );

        // 2. 상행/하행 조회
        Station preStation = stationRepository.findById(sectionRequest.getUpStationId())
                .orElseThrow(() -> new IllegalArgumentException("역 조회 실패"));

        Station station = stationRepository.findById(sectionRequest.getDownStationId())
                .orElseThrow(() -> new IllegalArgumentException("역 조회 실패"));

        // 2. lineStation 생성 및 저장
        findLine.addSection(preStation, station, sectionRequest.getDistance());
    }
}
