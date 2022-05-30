package nextstep.subway.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.LineStationRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.request.SectionRequest;
import nextstep.subway.dto.response.SectionResponse;
import nextstep.subway.exception.DupSectionException;
import nextstep.subway.exception.LineNotFoundException;
import nextstep.subway.exception.StationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final StationRepository stationRepository;
    private final LineStationRepository lineStationRepository;
    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;

    @Autowired
    public SectionService(StationRepository stationRepository,
        LineStationRepository lineStationRepository, LineRepository lineRepository,
        SectionRepository sectionRepository) {
        this.stationRepository = stationRepository;
        this.lineStationRepository = lineStationRepository;
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public void createSection(long lineId, SectionRequest sectionRequest) {
        Line line = getLineOrThrow(lineId);
        Station upStation = getStationOrThrow(sectionRequest.getUpStationId());
        Station downStation = getStationOrThrow(sectionRequest.getDownStationId());

        if (sectionRepository.findAllByUpStationAndDownStationAndLine(upStation, downStation, line)
            .isPresent()) {
            throw new DupSectionException();
        }
        Section appendSection = new Section(upStation, downStation, line,
            sectionRequest.getDistance(),
            null, null);

        Optional<Section> section = null;
        if ((section = sectionRepository.findAllByDownStationAndLine(downStation,
            line)).isPresent()) {
            section.get().insertFrontOfSection(appendSection);
        } else if ((section = sectionRepository.findAllByUpStationAndLine(upStation,
            line)).isPresent()) {
            section.get().insertBackOfSection(appendSection);
        } else if ((section = sectionRepository.findAllByDownStationAndLine(upStation,
            line)).isPresent()) { //기준이 Up인데 down에있다?
            section.get().appendBeforeSection(appendSection);
        } else if ((section = sectionRepository.findAllByUpStationAndLine(downStation,
            line)).isPresent()) {
            section.get().appendAfterSection(appendSection);
        }

    }

    private Station getStationOrThrow(long stationId) {
        return stationRepository.findById(stationId)
            .orElseThrow(StationNotFoundException::new);
    }

    private Line getLineOrThrow(long lineId) {
        return lineRepository.findById(lineId)
            .orElseThrow(LineNotFoundException::new);
    }


    public List<SectionResponse> findAllByLine(Long lineId) {
        Line line = lineRepository.findById(lineId).orElseThrow(LineNotFoundException::new);

        return sectionRepository.findAllByLine(line)
            .stream()
            .map(Section::toResponse)
            .collect(Collectors.toList());
    }
}
