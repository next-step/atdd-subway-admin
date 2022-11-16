package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public SectionService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new RuntimeException("지하철 노선이 존재하지 않습니다."));
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId()).get(); // TODO: optional 사용 어떻게 하는게 좋은 코드인지?
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId()).get();

        line.addSection(sectionRequest.toSection(upStation, downStation));
        lineRepository.save(line);
        return LineResponse.of(line);
    }
}
