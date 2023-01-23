package com.dayone.model.constants;

public enum Authority {

    // ROLE_ 이렇게 시작하는 이유는 스프링 시큐리티에서 제공하는 기능들을 사용할 수 있게 해주기 위해서다.

    ROLE_READ,

    ROLE_WRITE;

}
