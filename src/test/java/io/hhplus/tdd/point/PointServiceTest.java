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

    /**
     * 1. 포인트 조회 TEST
     */
    //id값이 0이하일 때
    @Test
    @DisplayName("id가 0일 때 INVALID_ID 에러 메시지 반환")
    void 포인트_조회_실패_ID_제로(){
        assertThatThrownBy(() -> pointService.getPoint(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("INVALID_ID");

    }

    //id값이 0미만일 때
    @Test
    @DisplayName("id가 -500일 때 INVALID_ID 에러 메시지 반환")
    void 포인트_조회_실패_id_음수(){
        assertThatThrownBy(() -> pointService.getPoint(-500))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("INVALID_ID");

    }

    //포인트 정보가 없을 때
    @Test
    @DisplayName("포인트 정보가 없을 때 NULL_EXCEPTION 에러 메시지 반환")
    void 포인트_조회_실패_포인트_null(){
        //유저가 존재하지 않는 이유등으로 인해,
        //반환값이 null일 때
        given(userPointTable.selectById(999_999_999L))
                .willReturn(null);

        //when & then
        assertThatThrownBy(() -> pointService.getPoint(999_999_999L))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("NULL_EXCEPTION");
    }


    /**
     * 2. 포인트 충전 TEST
     */
    //충전금액이 0이하일때
    @Test
    @DisplayName("-500원 포인트 충전 시 INVALID_AMOUNT 에러메시지 반환")
    void 포인트_충전_실패_음수() {
        //given
        //UserPoint mockUserPoint = new UserPoint(111L, 100, 100);
        given(userPointTable.selectById(111L))
                .willReturn(new UserPoint(111L,100,System.currentTimeMillis())); // mockUserPoint 반환

        //then
        assertThatThrownBy(() -> pointService.chargePoint(-500, 111L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("INVALID_AMOUNT");

    }
    //충전금액이 최대한도 초과할때
    @Test
    @DisplayName("2_000_000원 포인트 충전 시 INVALID_AMOUNT 에러메시지 반환")
    void 포인트_충전_실패_최대한도_초과(){
        //given
        given(userPointTable.selectById(111L)).willReturn(
                new UserPoint(111L,100,System.currentTimeMillis())
        );
        //then
        assertThatThrownBy(() -> pointService.chargePoint(2_000_000, 111L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("INVALID_AMOUNT");
    }

    //충전이후 금액이 최대한도를 넘을 때
    @Test
    @DisplayName("1_000_000원 포인트 충전 시 잔고초과로 EXCEED_BALANCE 에러메시지 반환")
    void 포인트_충전_실패_잔고_한도초과() {
        //given
        given(userPointTable.selectById(111L)).willReturn(
                new UserPoint(111L,1_000_000,System.currentTimeMillis())
        );

        //then
        assertThatThrownBy(() -> pointService.chargePoint(1_000_000, 111L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("EXCEED_BALANCE");

    }

    /**
     * 3. 포인트 사용 TEST
     */
    //사용금액이 0일때
    @Test
    @DisplayName("0원 포인트 사용 시 INVALID_AMOUNT 에러메시지 반환")
    void 포인트_사용_실패_포인트_0(){
        assertThatThrownBy(() -> pointService.usePoint(111, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("INVALID_AMOUNT");
    }
    //사용금액이 0이하일때
    @Test
    @DisplayName("-500원 포인트 사용시 INVALID_AMOUNT 에러메시지 반환")
    void 포인트_사용_실패_포인트_음수(){
        assertThatThrownBy(() -> pointService.usePoint(111,-500))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("INVALID_AMOUNT");
    }
    //최고 사용금액을 초과할 때
    @Test
    @DisplayName("2_000_000원 포인트 사용 시 INVALID_AMOUNT 에러메시지 반환")
    void 포인트_사용_실패_포인트_초과(){
        assertThatThrownBy(() -> pointService.usePoint(111, 2_000_000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("INVALID_AMOUNT");
    }
    //잔고금액보다 많이 사용할 때
    @Test
    @DisplayName("잔고금액이 1_000원일 때 2_000원 사용 시 INSUFFICIENT_BALANCE 에러메시지 반환")
    void 포인트_사용_실패_잔고금액_초과(){
        //given
        given(userPointTable.selectById(111L))
                .willReturn(new UserPoint(111L,1_000,System.currentTimeMillis()));

        //when&Then
        assertThatThrownBy(() -> pointService.usePoint(111, 2_000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("INSUFFICIENT_BALANCE");
    }

    /**
     * 4. 포인트 내역 조회 TEST
     */
    //id가 0일 때
    @Test
    @DisplayName("id가 0일 때 INVALID_ID 에러메시지 반환")
    void 포인트_내역조회_실패_ID_제로(){
        assertThatThrownBy(() -> pointService.getPoint(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("INVALID_ID");
    }

    //id가 -999일때
    @Test
    @DisplayName("id가 -999일 때 INVALID_ID 에러메시지 반환")
    void 포인트_내역조회_실패_ID_음수(){
        assertThatThrownBy(() -> pointService.getPoint(-999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("INVALID_ID");
    }

    //포인트 내역이 null일 때
    @Test
    @DisplayName("포인트 내역이 null일 때 NULL_EXCEPTION 에러메시지 반환")
    void 포인트_내역조회_실패_NULL(){
        //given
        given(userPointTable.selectById(999_999_999L)).willReturn(null);
        //when & then
        assertThatThrownBy(() -> pointService.getPoint(999_999_999L))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("NULL_EXCEPTION");
    }
}
