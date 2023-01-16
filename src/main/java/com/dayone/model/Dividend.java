package com.dayone.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Dividend {

    private LocalDateTime date;
    private String dividend;

}
