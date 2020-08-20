package com.thomas.web.tardygram;
import lombok.Data;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
@Data @Component @Lazy
public class MemberDTO {

    private String memberid, pwd,
    name,phone,email,birthday,gender,profileimage;
    private int money;
}
