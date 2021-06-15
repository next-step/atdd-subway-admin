package nextstep.subway.section.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional
public class SectionService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public SectionService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse addSection(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("라인이 없습니다."));

        Station downStation = stationRepository.findById(lineRequest.getDownStationId()).orElseThrow(() -> new IllegalArgumentException("역이 없습니다."));
        Station upStation = stationRepository.findById(lineRequest.getUpStationId()).orElseThrow(() -> new IllegalArgumentException("역이 없습니다."));

        line.addSection(upStation, downStation, lineRequest.getDistance());
        line.addSection(Section.getInstance(line, upStation, downStation, lineRequest.getDistance()));

        // 구간에 대한 유효성 검사
        return LineResponse.of(line);
    }
}
