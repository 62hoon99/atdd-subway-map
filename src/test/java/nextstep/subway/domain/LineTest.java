package nextstep.subway.domain;

import nextstep.subway.acceptance.util.GivenUtils;
import nextstep.subway.exception.DuplicatedStationException;
import nextstep.subway.exception.SectionRegistrationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.acceptance.util.GivenUtils.FIVE;
import static nextstep.subway.acceptance.util.GivenUtils.RED;
import static nextstep.subway.acceptance.util.GivenUtils.TEN;
import static nextstep.subway.acceptance.util.GivenUtils.강남역_이름;
import static nextstep.subway.acceptance.util.GivenUtils.신분당선_이름;
import static nextstep.subway.acceptance.util.GivenUtils.역삼역_이름;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineTest {

    @Test
    @DisplayName("line 이름 및 컬러 수정")
    void updateNameAndColor() {
        // given
        Line line = GivenUtils.분당선();

        // when
        Line newLine = line.updateNameAndColor(신분당선_이름, RED);

        // then
        assertThat(newLine.getName()).isEqualTo(신분당선_이름);
        assertThat(newLine.getColor()).isEqualTo(RED);
    }

    @Test
    @DisplayName("section 추가")
    void addSection() {
        // given
        int expectedSize = 2;
        Line line = GivenUtils.이호선();
        Station 강남역 = GivenUtils.강남역();
        Station 역삼역 = GivenUtils.역삼역();

        // when
        line.addSection(강남역, 역삼역, TEN);

        // then
        assertThat(convertToStationNames(line)).hasSize(expectedSize)
                .containsExactly(강남역_이름, 역삼역_이름);
    }

    @Test
    @DisplayName("section 추가 - 노선의 하행 종점역과 다른 upStationId")
    void addSectionWithInvalidUpStationId() {
        // given
        Line line = GivenUtils.이호선();
        line.addSection(GivenUtils.강남역(), GivenUtils.역삼역(), FIVE);

        // when
        Executable executable = () -> line.addSection(GivenUtils.강남역(), GivenUtils.양재역(), FIVE);

        // then
        assertThrows(SectionRegistrationException.class, executable);
    }

    @Test
    @DisplayName("section 추가 - 노선에 이미 존재하는 downStationId")
    void addSectionWithInvalidDownStationId() {
        // given
        Line line = GivenUtils.이호선();
        line.addSection(GivenUtils.강남역(), GivenUtils.역삼역(), FIVE);

        // when
        Executable executable = () -> line.addSection(GivenUtils.역삼역(), GivenUtils.강남역(), FIVE);

        // then
        assertThrows(DuplicatedStationException.class, executable);
    }

    private List<String> convertToStationNames(Line line) {
        return line.getStations()
                .stream()
                .map(Station::getName)
                .collect(Collectors.toList());
    }

}