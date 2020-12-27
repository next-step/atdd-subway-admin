package nextstep.subway.line.domain.sections;

public interface SectionCalculator {
    Section calculate(Section originalSection, Section addSection);
}
