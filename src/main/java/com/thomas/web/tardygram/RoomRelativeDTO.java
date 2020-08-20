 
package com.thomas.web.tardygram;
import lombok.Data;
import lombok.ToString;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Data @Component @Lazy @ToString
public class RoomRelativeDTO {

    private String members_memberid, roomcategory, roomdate, roomdetail, roomhostid, roomphoto, roomplace, roompwd, roomtitle;
    private int roompenaltyall, roomcharge, roomprogress;
    private Long rooms_roomno, roomno;
    private Double roomlatitude, roomlongitude;

            
}

            
			
			
            
            
			
            


