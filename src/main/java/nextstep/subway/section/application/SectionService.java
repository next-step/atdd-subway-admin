package nextstep.subway.section.application;

import static nextstep.subway.common.ErrorMessage.*;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional
public class SectionService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public SectionService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public LineResponse addSection(Long id, LineRequest lineRequest) {
        Line line = getLine(id);

        Station downStation = getStation(lineRequest.getDownStationId());
        Station upStation = getStation(lineRequest.getUpStationId());

        Section section = line.updateAddSection(upStation, downStation, lineRequest.getDistance());
        sectionRepository.save(section);
        return LineResponse.of(line);
    }

    public void removeSectionByStationId(Long lineId, Long stationId) {
        Line line = getLine(lineId);
        Station station = getStation(stationId);

        line.removeSection(station);
    }

    private Station getStation(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_STATION));
    }

    private Line getLine(Long lineId) {
        return lineRepository.findById(lineId).orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_LINE));
    }
}
