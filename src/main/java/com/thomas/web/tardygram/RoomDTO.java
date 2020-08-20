package com.thomas.web.tardygram;
import lombok.Data;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
@Data @Component @Lazy
public class RoomDTO {

    private String roomcategory, roomtitle,
    roomdate, roomplace, roomdetail, roomphoto;
    private int roomcharge, roomprogress;
    



    
}
