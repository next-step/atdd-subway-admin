package nextstep.subway.line.application;

import nextstep.subway.enums.SectionAddType;
import nextstep.subway.exception.DuplicateValueException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.domain.wrappers.LineStations;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    public static final String DUPLICATE_VALUE_ERROR_MESSAGE = "%s 라인은 이미 등록된 라인 합니다.";

    private final LineRepository lineRepository;
    private final StationService stationService;


    public LineService(LineRepository lineRepository,
                       StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        try {
            Station upStation = stationService.findStationById(request.getUpStationId());
            Station downStation = stationService.findStationById(request.getDownStationId());
            LineStations lineStations = request.toLineStations(upStation, downStation);
            Line line = request.toLine(lineStations);
            return LineResponse.of(lineRepository.save(line));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateValueException(String.format(DUPLICATE_VALUE_ERROR_MESSAGE, request.getName()));
        }
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream().map(LineResponse::of).collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Optional<Line> findLine = lineRepository.findById(id);
        Line line = Line.getNotNullLine(findLine);
        return LineResponse.of(line);
    }

    public void updateLineById(Long id, LineRequest lineRequest) {
        Optional<Line> findLine = lineRepository.findById(id);
        Line line = Line.getNotNullLine(findLine);
        line.update(lineRequest.toLine());
    }

    public void deleteLineById(Long id) {
        Optional<Line> findLine = lineRepository.findById(id);
        Line line = Line.getNotNullLine(findLine);
        lineRepository.delete(line);
    }

    public void saveSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findStationById(sectionRequest.getUpStationId());
        Station downStation = stationService.findStationById(sectionRequest.getDownStationId());
        Line line = Line.getNotNullLine(lineRepository.findById(lineId));
        LineStation lineStation = sectionRequest.toLineStation(upStation, downStation);
        line.checkValidLineStation(lineStation);
        line.updateLineStation(lineStation);
        line.addLineStation(lineStation);
    }

    public void removeSectionByStationId(Long lineId, Long stationId) {
        Station deleteTargetStation = stationService.findStationById(stationId);
        Line line = Line.getNotNullLine(lineRepository.findById(lineId));
        line.checkValidSingleSection();
        LineStation lineStation = line.findLineStationByStation(deleteTargetStation);
    }
}
