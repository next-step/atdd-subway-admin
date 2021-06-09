package nextstep.subway.line.application;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.DuplicateDataException;
import nextstep.subway.exception.NoSuchDataException;
import nextstep.subway.domain.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesSubResponse;
import nextstep.subway.station.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());
        Section section = Section.create(request.getDistance());

        Line line = Line.createWithSectionAndStation(request.getName(), request.getColor(), section, upStation, downStation);
        Line savedLine = lineRepository.save(line);
        return LineResponse.of(savedLine);
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new NoSuchDataException("역이 존재하지 않습니다.", "upStationId",
                        String.valueOf(stationId), null));
    }

    public void changeLine(Long lineId, LineRequest lineRequest) {
        Line line = findById(lineId);
        line.change(lineRequest.getName(), lineRequest.getColor());
    }

    public void removeLine(Long lineId) {
        lineRepository.deleteById(lineId);
    }

    @Transactional(readOnly = true)
    public void validateDuplicatedName(LineRequest lineRequest) throws NoSuchFieldException {
        if (lineRepository.existsByName(lineRequest.getName())) {
            throw new DuplicateDataException("이미 존재하는 노선 이름입니다.",
                    lineRequest.getClass().getDeclaredField("name").getName(),
                    lineRequest.getName(), lineRequest.getClass().getName());
        }
    }

    @Transactional(readOnly = true)
    public LinesSubResponse readLine(Long lineId) {
        Line line = findById(lineId);
        return LinesSubResponse.of(line);
    }

    @Transactional(readOnly = true)
    public List<LinesSubResponse> readLineAll() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LinesSubResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    private Line findById(Long lineId) {
        return lineRepository.findById(lineId).orElseThrow(() -> new NoSuchDataException("존재하지 않는 노선입니다.",
                "lineId", String.valueOf(lineId), null));
    }
}
