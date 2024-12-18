package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@ExtendWith(SpringExtension.class )
public class PointServiceTest {

    @Mock //mock객체 생성
    private UserPointTable userPointTable;

    @InjectMocks //pointService에 userPointTable을 자동으로 주입
    private PointService pointService;

    //충전금액이 0이하일때
    @Test
    @DisplayName("-500원 포인트 충전 시 BadRequestException 반환")
    void 포인트_충전_실패_음수() {
        //given
        UserPoint mockUserPoint = new UserPoint(111L, 100, 100);
        given(userPointTable.selectById(111L)).willReturn(mockUserPoint); // mockUserPoint 반환

        //then
        assertThatThrownBy(() -> pointService.chargePoints(-500, 111L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("INVALID_AMOUNT");

    }
    //충전금액이 최대한도 초과할때
    @Test
    @DisplayName("2_000_000원 포인트 충전 시 BadRequestException 반환")
    void 포인트_충전_실패_최대한도_초과(){
        //given
        given(pointService.getBalance(111L)).willReturn(
                new UserPoint(111L,100,100)
        );
        //then
        assertThatThrownBy(() -> pointService.chargePoints(2_000_000, 111L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("INVALID_AMOUNT");
    }

    //충전이후 금액이 최대한도를 넘을 때
    @Test
    @DisplayName("1_000_000원 포인트 충전 시 잔고초과로 BadRequestException 반환")
    void 포인트_충전_실패_잔고_한도초과() {
        //given
        given(pointService.getBalance(111L)).willReturn(
                new UserPoint(111L,1_000_000,100)
        );

        //then
        assertThatThrownBy(() -> pointService.chargePoints(1_000_000, 111L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("EXCEED_BALANCE");

    }
}
