package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        Line line = lineRequest.toLine();
        line.addSection(
                new Section(
                        lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance()
                )
        );
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine, stationResponseFrom(persistLine.getSections()));
    }

    public LineResponse findLine(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        return toLineResponse(line);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(this::toLineResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        line.modify(lineRequest);
        lineRepository.save(line);
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public LineResponse addSection(Long id, SectionRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        line.addSection(new Section(request));
        return toLineResponse(line);
    }

    private LineResponse toLineResponse(Line line) {
        return LineResponse.of(line, stationResponseFrom(line.getSections()));
    }

    private List<StationResponse> stationResponseFrom(Sections sections) {
        List<Station> stations = new ArrayList<>();
        sections.toLineStationIds()
                .forEach(id -> stations.add(stationRepository.findById(id).orElseThrow(IllegalArgumentException::new)));
        return stations.stream().map(StationResponse::of).collect(Collectors.toList());
    }
}
