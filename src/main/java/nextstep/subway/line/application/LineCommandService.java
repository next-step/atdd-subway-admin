package nextstep.subway.line.application;

import nextstep.subway.common.exception.DataNotFoundException;
import nextstep.subway.common.message.ExceptionMessage;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.station.application.StationQueryService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineCommandService {
    private final LineRepository lineRepository;
    private final StationQueryService stationQueryService;

    public LineCommandService(LineRepository lineRepository, StationQueryService stationQueryService) {
        this.lineRepository = lineRepository;
        this.stationQueryService = stationQueryService;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationQueryService.findById(lineRequest.getUpStationId());
        Station downStation = stationQueryService.findById(lineRequest.getDownStationId());

        Line line = Line.of(lineRequest.getName(), lineRequest.getColor(), upStation, downStation);
        lineRepository.save(line);

        return LineResponse.from(line);
    }

    public void updateLine(Long id, LineUpdateRequest lineRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(ExceptionMessage.NOT_FOUND_LINE));

        line.update(lineRequest.getName(), lineRequest.getColor());
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }
}
