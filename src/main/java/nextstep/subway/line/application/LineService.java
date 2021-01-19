package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.dto.SectionResponse;
import nextstep.subway.line.exception.ResourceNotFoundException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;
    private final SectionRepository sectionRepository;

    public LineService(final LineRepository lineRepository, final StationService stationService,
        final SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
        this.sectionRepository = sectionRepository;
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        return lineRepository.findAll().stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findOne(final Long id) {
        return LineResponse.of(findById(id));
    }

    public LineResponse saveLine(LineRequest request) {
        if (request.hasUpAndDownStation()) {
            Station upStation = stationService.findById(request.getUpStationId());
            Station downStation = stationService.findById(request.getDownStationId());

            return LineResponse.of(lineRepository.save(request.toLine(upStation, downStation)));
        }

        return LineResponse.of(lineRepository.save(request.toLine()));
    }

    public LineResponse updateLine(final Long id, final LineRequest lineRequest) {
        Line line = findById(id);

        line.update(lineRequest.toLine());

        return LineResponse.of(line);
    }

    public void deleteLine(final Long id) {
        lineRepository.deleteById(id);
    }

    private Line findById(final Long id) {
        return lineRepository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException(String.format("[id=%d] 요청한 지하철 노선 정보가 없습니다.", id)));
    }

    public SectionResponse saveSection(final Long id, final SectionRequest sectionRequest) {
        Line line = findById(id);
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        line.addSection(upStation, downStation, sectionRequest.getDistance());

        return SectionResponse.of(sectionRepository.findByLineAndUpAndDown(line, upStation, downStation)
            .orElseThrow(() -> new ResourceNotFoundException("구간 정보가 없습니다.")));
    }

}
