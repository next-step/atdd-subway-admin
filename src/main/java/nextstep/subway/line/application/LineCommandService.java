package nextstep.subway.line.application;

import nextstep.subway.common.exception.DataNotFoundException;
import nextstep.subway.common.message.ExceptionMessage;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.station.application.StationCommandService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineCommandService {
    private final LineRepository lineRepository;
    private final StationCommandService stationCommandService;

    public LineCommandService(LineRepository lineRepository, StationCommandService stationCommandService) {
        this.lineRepository = lineRepository;
        this.stationCommandService = stationCommandService;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Line line = lineRepository.save(lineRequest.toLine());

        Station upStation = stationCommandService.addToLine(lineRequest.getUpStationId(), line);
        Station downStation = stationCommandService.addToLine(lineRequest.getDownStationId(), line);

        return LineResponse.of(line, upStation, downStation);
    }

    public void updateLine(Long id, LineUpdateRequest lineRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(ExceptionMessage.NOT_FOUND_LINE));

        line.update(lineRequest.getName(), line.getColor());
    }

    public void deleteLine(Long id) {
        stationCommandService.removeFromLine(id);
        lineRepository.deleteById(id);
    }
}
