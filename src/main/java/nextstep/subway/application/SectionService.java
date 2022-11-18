package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public SectionService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest request) {
        Line line = lineRepository.findById(lineId)
            .orElseThrow(() -> new IllegalArgumentException(String.format("노선을 찾을 수 없습니다 id = %d", lineId)));
        Station upStation = stationRepository.findById(request.getUpStationId())
            .orElseThrow(() -> new IllegalArgumentException(String.format("역을 찾을 수 없습니다 id = %d", request.getUpStationId())));
        Station downStation = stationRepository.findById(request.getDownStationId())
            .orElseThrow(() -> new IllegalArgumentException(String.format("역을 찾을 수 없습니다 id = %d", request.getDownStationId())));
        line.addSection(new Section(upStation, downStation, request.getDistance()));
    }
}
