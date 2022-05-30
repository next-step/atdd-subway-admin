package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineResponses;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.exception.LineNotFoundException;
import nextstep.subway.exception.StationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository,
                       StationRepository stationRepository,
                       SectionRepository sectionRepository,
                       LineStationRepository lineStationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId())
                .orElseThrow(StationNotFoundException::new);
        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
                .orElseThrow(StationNotFoundException::new);
        Line persistLine = lineRepository.save(lineRequest.toLine());
        persistLine.setUpStation(upStation);
        persistLine.setDownStation(downStation);

        sectionRepository.save(new Section(new Distance(lineRequest.getDistance()), upStation, downStation, persistLine));

        return LineResponse.of(persistLine);
    }

    public LineResponse updateLine(Long id, LineUpdateRequest request) {
        Line persistLine = lineRepository.findById(id)
                .orElseThrow(LineNotFoundException::new);
        persistLine.updateLine(request.getName(), request.getColor());
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        Line persisLine = lineRepository.findById(id)
                .orElseThrow(LineNotFoundException::new);
        return LineResponse.of(persisLine);
    }

    @Transactional(readOnly = true)
    public LineResponses findAllLines() {
        List<Line> lines = lineRepository.findAll();
        List<LineResponse> list = lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
        return new LineResponses(list);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
