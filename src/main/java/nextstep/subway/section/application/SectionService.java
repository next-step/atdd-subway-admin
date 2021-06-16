package nextstep.subway.section.application;

import static nextstep.subway.common.ErrorMessage.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
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
        Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_LINE));

        Station downStation = stationRepository.findById(lineRequest.getDownStationId()).orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_STATION));
        Station upStation = stationRepository.findById(lineRequest.getUpStationId()).orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_STATION));

        line.addSection(upStation, downStation, lineRequest.getDistance());

        // 구간에 대한 유효성 검사
        return LineResponse.of(line);
    }
}
