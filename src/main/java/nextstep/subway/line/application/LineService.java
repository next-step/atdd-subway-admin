package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStationSortAble;
import nextstep.subway.line.domain.LineStationUpToDownSortAble;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineCreateResponse;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.dto.UpdateLineResponseDto;
import nextstep.subway.line.exception.DuplicateLineNameException;
import nextstep.subway.line.exception.NotFoundLineByIdException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.exception.NotFoundStationByIdException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private static final String NOT_FOUND_UP_STATION_ERROR_MESSAGE = "상행역의 정보를 찾지 못하였습니다.";
    private static final String NOT_FOUND_DOWN_STATION_ERROR_MESSAGE = "하행역의 정보를 찾지 못하였습니다.";

    private final LineRepository lineRepository;
    private final StationService stationService;
    private final LineStationSortAble lineStationSort;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
        this.lineStationSort = LineStationUpToDownSortAble.of();
    }

    @Transactional
    public LineCreateResponse saveLine(LineRequest lineRequest) {
        validateExistsByName(lineRequest.getName());
        final Section section = getSectionOrElseThrow(lineRequest.getSectionRequest());
        final Line persistLine = lineRepository.save(lineRequest.toLine(section));
        return LineCreateResponse.of(persistLine);
    }

    @Transactional
    public UpdateLineResponseDto updateLine(long id, LineRequest lineRequest) {
        validateExistsByName(lineRequest.getName());
        final Line line = getLineByIdOrElseThrow(id);
        final Section section = getSectionOrElseThrow(lineRequest.getSectionRequest());
        line.update(lineRequest.toLine(section));
        lineRepository.flush();
        return UpdateLineResponseDto.of(line);
    }

    private void validateExistsByName(String name) {
        if (lineRepository.existsByName(name)) {
            throw new DuplicateLineNameException();
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(long id) {
        final Line line = getLineByIdOrElseThrow(id);
        final List<StationResponse> stationResponses = getStationResponses(line);
        return LineResponse.of(line, stationResponses);
    }

    private List<StationResponse> getStationResponses(Line line) {
        final List<Station> stations = line.getStationsBySort(lineStationSort);
        return stationService.convertStationsResponse(stations);
    }

    private Line getLineByIdOrElseThrow(long id) {
        return lineRepository.findById(id)
                .orElseThrow(NotFoundLineByIdException::new);
    }

    @Transactional
    public void deleteLine(long id) {
        try {
            lineRepository.deleteById(id);
        } catch (EmptyResultDataAccessException exception) {
            throw new NotFoundLineByIdException();
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    protected Section getSectionOrElseThrow(SectionRequest sectionRequest) {
        final int distance = sectionRequest.getDistance();
        final Station upStation = getStationByIdWithErrorMessage(sectionRequest.getUpStationId(), NOT_FOUND_UP_STATION_ERROR_MESSAGE);
        final Station downStation = getStationByIdWithErrorMessage(sectionRequest.getDownStationId(), NOT_FOUND_DOWN_STATION_ERROR_MESSAGE);
        return Section.of(distance, upStation, downStation);
    }

    private Station getStationByIdWithErrorMessage(Long stationId, String errorMessage) {
        try {
            return stationService.getStation(stationId);
        } catch (NotFoundStationByIdException exception) {
            throw new NotFoundStationByIdException(errorMessage);
        }
    }
}
