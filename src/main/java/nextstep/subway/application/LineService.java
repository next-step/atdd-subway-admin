package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.exception.NoSuchElementFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

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
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId()).orElseThrow(() -> new NoSuchElementFoundException("해당 역을 찾을 수 없습니다."));
        Station downStation = stationRepository.findById(lineRequest.getDownStationId()).orElseThrow(() -> new NoSuchElementFoundException("해당 역을 찾을 수 없습니다."));

        Line persistLine = lineRepository.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        persistLine.addFirstSection(new Section(upStation, downStation, lineRequest.getDistance()));

        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new NoSuchElementFoundException("해당 노선을 찾을 수 없습니다."));
        return LineResponse.of(line);
    }

    @Transactional
    public void updateLine(LineUpdateRequest updateRequest) {
        Line line = lineRepository.findById(updateRequest.getId()).orElseThrow(() -> new NoSuchElementFoundException("해당 노선을 찾을 수 없습니다."));
        line.updateLine(updateRequest.getName(), updateRequest.getColor());
    }

    @Transactional
    public void deleteLineById(@PathVariable Long id) {
        if (!lineRepository.existsById(id)) {
            throw new NoSuchElementFoundException("해당 노선을 찾을 수 없습니다.");
        }
        lineRepository.deleteById(id);
    }

    private Station findStationById(Long id) {
        return stationRepository.findById(id).orElseThrow(() -> new NoSuchElementFoundException("해당 역을 찾을 수 없습니다."));
    }

    @Transactional
    public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new NoSuchElementFoundException("라인을 찾을 수 없습니다."));

        Station upStation = findStationById(sectionRequest.getUpStationId());
        Station downStation = findStationById(sectionRequest.getDownStationId());

        line.addSection(new Section(upStation, downStation, sectionRequest.getDistance()));

        return LineResponse.of(line);
    }
}
