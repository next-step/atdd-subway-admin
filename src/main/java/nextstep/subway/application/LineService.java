package nextstep.subway.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateNameAndColorRequest;
import nextstep.subway.dto.StationResponse;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public LineResponse saveLine(LineRequest lineRequest) throws NoSuchElementException {

        Station upStation = stationRepository.findById(lineRequest.getUpStationId())
                .orElseThrow(NoSuchElementException::new);
        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
                .orElseThrow(NoSuchElementException::new);

        Line persistLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) throws NotFoundException {
        return LineResponse.of(lineRepository.findById(id).orElseThrow(NotFoundException::new));
    }

    @Transactional
    public void updateNameAndColor(Long id, LineUpdateNameAndColorRequest lineRequest)
            throws NoSuchElementException {

        Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);

        line.updateNameAndColor(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
