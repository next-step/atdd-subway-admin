package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.exception.ConflictException;
import nextstep.subway.exception.NotExistLineException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        validDuplicate(request);
        Line line = request.toLine();
        line.addSection(createSection(request));
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    private Section createSection(LineRequest request) {
        Station upStation = findStationBy(request.getUpStationId());
        Station downStation = findStationBy(request.getDownStationId());
        return new Section(upStation, downStation, request.getDistance());
    }

    private Station findStationBy(Long id) {
        return stationRepository.findById(id).orElseThrow(
            NotExistLineException::new
        );
    }

    private void validDuplicate(LineRequest request) {
        lineRepository.findByName(request.getName()).ifPresent(
            line -> {
                throw new ConflictException();
            }
        );
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getList() {
        return lineRepository.findAll().stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse getLine(Long id) {
        return lineRepository.findById(id).orElseThrow(
            NotExistLineException::new
        ).toResponse();
    }

    @Transactional
    public void updateLine(Long id, LineRequest updateRequest) {
        Line line = lineRepository.findById(id).orElseThrow(
            NotExistLineException::new
        );
        line.update(updateRequest);
    }

    @Transactional
    public void delete(Long id) {
        lineRepository.findById(id).orElseThrow(
            NotExistLineException::new
        );
        lineRepository.deleteById(id);
    }
}
