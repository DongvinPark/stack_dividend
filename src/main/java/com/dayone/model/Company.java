package com.dayone.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor //레디스 관련 오류 때문.
@AllArgsConstructor
public class Company {

    private String ticker;
    private String name;

}
