package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineCreateRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.SectionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static nextstep.subway.constants.ErrorMessage.NO_SUCH_ELEMENT_EXCEPTION_MSG;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineCreateRequest lineRequest) {
        Station upStation = findByStationId(lineRequest.getUpStationId());
        Station downStation = findByStationId(lineRequest.getDownStationId());
        Line persistLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.from(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse.from(line))
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) throws IllegalArgumentException {
        Line line = lineRepository.findById(id).orElseThrow(() -> new NoSuchElementException(NO_SUCH_ELEMENT_EXCEPTION_MSG));
        return LineResponse.from(line);
    }

    public SectionResponse findLineSection(Long id) throws IllegalArgumentException {
        Line line = lineRepository.findById(id).orElseThrow(() -> new NoSuchElementException(NO_SUCH_ELEMENT_EXCEPTION_MSG));
        return SectionResponse.from(line);
    }

    @Transactional
    public void updateLine(Long id, LineUpdateRequest lineUpdateRequest) throws IllegalArgumentException {
        Line line = lineRepository.findById(id).orElseThrow(() -> new NoSuchElementException(NO_SUCH_ELEMENT_EXCEPTION_MSG));
        line.update(lineUpdateRequest.getName(), lineUpdateRequest.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    private Station findByStationId(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(() -> new NoSuchElementException(NO_SUCH_ELEMENT_EXCEPTION_MSG));
    }
}
