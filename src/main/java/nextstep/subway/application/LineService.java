package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;

@Service
public class LineService {
    private LineRepository lineRepository;

    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Line persistLine = lineRepository.save(lineRequest.toLine(
                stationRepository.findStationById(lineRequest.getUpStationId()),
                stationRepository.findStationById(lineRequest.getDownStationId())
        ));
        return LineResponse.of(persistLine);
    }

}
