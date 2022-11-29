package nextstep.subway.application;

import nextstep.subway.constants.ErrorCode;
import nextstep.subway.domain.*;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = getStation(lineRequest.getUpStationId());
        Station downStation = getStation(lineRequest.getDownStationId());
        return LineResponse.from(lineRepository.save(lineRequest.toLine(upStation, downStation)));
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Line line = getLine(id);
        return LineResponse.from(line);
    }

    @Transactional
    public LineResponse modifyLine(Long id, LineRequest lineRequest) {
        Line line = getLine(id);
        line.modify(lineRequest.getName(), lineRequest.getColor());
        return LineResponse.from(line);
    }

    @Transactional
    public void deleteLine(Long id) {
        Line line = getLine(id);
        lineRepository.delete(line);
    }

    private Station getStation(long stationId) {
        return stationRepository.getById(stationId);
    }

    private Line getLine(Long id) {
        return lineRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.NO_SUCH_LINE_EXCEPTION.getErrorMessage()));
    }

    public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = getLine(lineId);
        line.addSection(new Section(getStation(sectionRequest.getUpStationId())
                            , getStation(sectionRequest.getDownStationId())
                            , sectionRequest.getDistance()));
        return LineResponse.from(line);
    }
}
