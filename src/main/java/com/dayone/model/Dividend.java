package com.dayone.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor //레디스 관련 오류 때문.
@AllArgsConstructor
public class Dividend {

    //레디스에 캐싱되는 객체가 단순 값이 아니라, '객체'인 데다가 그 객채가 시간 관련 객체를 포함하고 있을 경우,
    //해당 시간 관련 직렬 및 역직렬화 옵션을 추가해줘야 한다.
    //이 방법마저 통하지 않을 경우, ObjectMapper를 사용하는 방법도 있지만 이는 웬만해서는 마주하고 싶지 않은 끔찍한 상황이다.
    //그마저도 안 통하면 차라리 Dividend 객체에 날짜 객채를 넣는 것이 아니라 날짜마자도 아예 스트링으로 바꿔서
    //스트링 객체로 저장하는 마지막 방법도 있다.
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime date;

    private String dividend;

}
