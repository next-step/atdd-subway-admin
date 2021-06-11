package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.application.StationQueryUseCase;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineCommandService implements LineCommandUseCase {
    private final LineRepository lineRepository;
    private final LineQueryUseCase lineQueryUseCase;
    private final StationQueryUseCase stationQueryUseCase;

    public LineCommandService(LineRepository lineRepository, LineQueryUseCase lineQueryUseCase, StationQueryUseCase stationQueryUseCase) {
        this.lineRepository = lineRepository;
        this.lineQueryUseCase = lineQueryUseCase;
        this.stationQueryUseCase = stationQueryUseCase;
    }

    @Override
    public LineResponse saveLine(LineRequest lineRequest) {
        lineQueryUseCase.checkDuplicatedLineName(lineRequest.getName());
        Station upStation = stationQueryUseCase.findById(lineRequest.getUpStationId());
        Station downStation = stationQueryUseCase.findById(lineRequest.getDownStationId());

        Line persistLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.of(persistLine, persistLine.getStations());
    }

    @Override
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineQueryUseCase.findById(id);
        if (line.isDifferentName(lineRequest.getName())) {
            lineQueryUseCase.checkDuplicatedLineName(lineRequest.getName());
        }
        line.update(lineRequest.toLine());
    }

    @Override
    public void deleteLine(Long id) {
        Line line = lineQueryUseCase.findById(id);
        lineRepository.delete(line);
    }
}
