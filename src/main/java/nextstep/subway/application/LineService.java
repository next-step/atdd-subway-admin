package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineChange;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.exception.AlreadyDeletedException;
import nextstep.subway.exception.NoStationException;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.exception.SameStationException;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        if (lineRequest.isSameStations()) {
            throw new SameStationException();
        }

        validateStation(lineRequest.getUpStationId());
        validateStation(lineRequest.getDownStationId());

        Line line = lineRepository.save(Line.of(lineRequest,
            stationService.findStation(lineRequest.getUpStationId()),
            stationService.findStation(lineRequest.getDownStationId())
        ));

        return LineResponse.of(line);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAllLines().stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = lineRepository.findLine(id).orElseThrow(NotFoundException::new);
        return LineResponse.of(line);
    }

    @Transactional
    public void changeLine(Long id, LineChange lineChange) {
        Line line = findLineById(id, new NotFoundException());
        line.update(lineChange);
    }

    @Transactional
    public void removeLine(Long id) {
        Line line = findLineById(id, new AlreadyDeletedException());
        line.removeStations();
        lineRepository.delete(line);
    }

    @Transactional
    public void registerSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findLine(lineId).orElseThrow(NotFoundException::new);
        validateStation(sectionRequest.getUpStationId());
        validateStation(sectionRequest.getDownStationId());
        line.addLineStation(
            new Section(new Station(sectionRequest.getDownStationId()), sectionRequest.getUpStationId(),
                sectionRequest.getDistance()));
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = findLineById(lineId, new NotFoundException());
        line.deleteSection(stationId);
    }

    private Line findLineById(Long id, RuntimeException exception) {
        return lineRepository.findById(id).orElseThrow(() -> exception);
    }

    private void validateStation(Long stationId) {
        if (stationService.notExistsById(stationId)) {
            throw new NoStationException(stationId);
        }
    }
}
