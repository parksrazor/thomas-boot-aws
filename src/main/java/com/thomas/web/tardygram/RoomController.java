package com.thomas.web.tardygram;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.tardygram.web.entities.Room;
import com.tardygram.web.entities.Member;
import com.tardygram.web.repositories.EnterRepository;
import com.tardygram.web.repositories.RoomRepository;
import com.tardygram.web.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; 
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
 
/**
* MemberController
*/
@CrossOrigin("*")
@RestController
@RequestMapping("/room")
public class RoomController {
   @Autowired MemberRepository memberrepo;
   @Autowired RoomRepository roomrepo;
   @Autowired EnterRepository enterrepo;


   private static String UPLOADED_FOLDER = "C:\\Users\\user\\Desktop\\tardygram\\frontend\\public\\image\\room\\";

//    @GetMapping("/sucess")
//    public String add() {
//         System.out.println("성공시 컨트롤러"); 
//         return "localhost:3000";       
//     }
    

     
    //모임방 이미지 업로드
    @PostMapping(path="/upload/{roomno}",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String roomUpload(@RequestParam("file") MultipartFile file, @PathVariable String roomno){
        System.out.println("모임방 이미지 업로드 컨트롤러 ");
        // System.out.println("파일업로드 컨트롤러");
        System.out.println("건너온 data : " + file);
        System.out.println("roomno : " + roomno);
        System.out.println("파일이름 : " + file.getOriginalFilename());

        try{
            String DbPath = "/image/room/" + file.getOriginalFilename();
            System.out.println("Dbpath : " + DbPath);
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);
            System.out.println("path : " + path);
            roomrepo.roomphotoUpdate(DbPath, roomno);
            return DbPath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return "No Img";                 
    }



   //방장이 모임방 개설
   @PostMapping(path="/create")
   public ResponseEntity<HashMap<String, Object>> insertRoom(@RequestBody Room data) {
        System.out.println("컨트롤러 도착");
        System.out.println("room : " + data);

 
        data.setRoomprogress(1);
        data.setRoomphoto("/image/room/kakaofriends.jpg");
        Member member = memberrepo.findById(data.getRoomhostid()).get();
        System.out.println("member : " + member);
        member.setTardystate("-"); 
        HashMap<String,Object> map =new HashMap<>();
      

        if(member.getMoney() >= data.getRoomcharge()){
            int tardycashe = member.getMoney()-data.getRoomcharge();
            member.addRoom(data);
         
            Long roomno = roomrepo.save(data).getRoomno(); 
            memberrepo.roomTardy(data.getRoomhostid(), tardycashe);
            roomrepo.insertPenaltyall(data.getRoomno(), data.getRoomcharge());
            map.put("status", "00");
            map.put("msg", "방이 생성되었습니다.");
            map.put("roomno",roomno);
        } else{
            map.put("status", "11");
            map.put("msg", "tardy캐시를 충전하고 다시 방을 만들어주세요.");
        }


        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
   }

//    List<Object []> selectuser = roomrepo.selectuser(roomno);
   //모임방에 방원이 될 사람이 참여하기 버튼클릭시  
   @PostMapping("/enter/{id}/{roomno}/{roomcharge}")
   public ResponseEntity<HashMap<String, Object>> enter(@PathVariable String id, @PathVariable int roomno, @PathVariable int roomcharge){
       System.out.println("enter컨트롤러 도착");
       System.out.println("id : " + id);
       System.out.println("roomno : " + roomno);
       System.out.println("roomcharge : " + roomcharge);
       HashMap<String,Object> map =new HashMap<>();

       int tardycashe = memberrepo.tardyCash(id);
       if(tardycashe >= roomcharge){
            Member m = new Member();
            m.setMemberid(id);
            m.setTardystate("-");  //waiting
            enterrepo.enter(m, roomno);
            int money = tardycashe - roomcharge;
            memberrepo.roomTardy(id, money);
            roomrepo.insertPenaltyall(Long.parseLong(Integer.toString(roomno)), roomcharge);
            //return "방에 참여하셨습니다.";
            Member m2 = memberrepo.findById(id).get();
            System.out.println("m2 : " + m2);
            map.put("status", "00");
            map.put("msg", "성공했습니다");
            map.put("m2", m2);
            return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
       }else {
           //return "tardy캐시를 확인하세요";
           map.put("status", "11");
           map.put("msg", "타디캐시가 없습니다");
           return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
       }
   }


    //연관테이블 레코드 삭제후 room테이블 레코드 삭제, 각인원들에게 돈다시 줘야함
    // @DeleteMapping("/delete")
    // public void deleteroom(){      
    //         roomrepo.deleteRoom((long)1);
    //         // roomrepo.deleteById((long)3);      
    // }


    //모임방 전체출력
    @GetMapping("/selectall")
    public  ResponseEntity<HashMap<String, Object>> selectall(){
        System.out.println("모임방전체출력 컨트롤러 ");
        List<Room> mList = roomrepo.selectall();
        System.out.println("mList : " + mList);
        HashMap <String, Object> map = new HashMap<>();
        map.put("mList", mList);

        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }


//모임방 디테일
@GetMapping("/selectone/{roomno}")
public ResponseEntity<HashMap<String, Object>> selectone(@PathVariable Long roomno){
// public void selectone(@PathVariable int roomno){
    System.out.println("selectone 컨트롤러 도착");
    System.out.println("roomno : " + roomno);
    Room selecthost = roomrepo.selecthost(roomno);
    List<Object []> selectuser = roomrepo.selectuser(roomno);
    List memberList = new ArrayList<>();
 
    selectuser.forEach(arr -> {
        HashMap<String,Object> memlist =new HashMap<>();
        String memberid = arr[0].toString();
        String tardystate = arr[2].toString();
       
        memlist.put("memberid", memberid);
     //    System.out.println(arr[0].toString()); ID
     //    System.out.println(arr[1].toString()); Image
     //    System.out.println(arr[2].toString()); tardyState
   
        try{
            String profileimage = arr[1].toString();
            memlist.put("profileimage", profileimage);             
            memlist.put("tardystate", tardystate);             
                
        }catch(Exception e){
            memlist.put("profileimage", "null");
            memlist.put("tardyState", "null");
        }
        memberList.add(memlist);
    });

    System.out.println("selectuser : " + selectuser);
    HashMap<String,Object> map =new HashMap<>();
    map.put("selecthost", selecthost);
    map.put("selectuser", memberList);
    // System.out.println(map);
     return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
}


   //룸디테일에서 확인버튼 누르면 tardystate변경
   @PutMapping("/checkroom/{memberid}")
   public ResponseEntity<String> checkroom(@PathVariable String memberid){
        System.out.println("체크룸컨트롤러도착");
        System.out.println("memberid : " + memberid);
        roomrepo.changeState(memberid);
        return new ResponseEntity<String>("arrived", HttpStatus.OK);
   }


   @DeleteMapping("/closeroom/{roomno}/{roompenaltyall}")
   public ResponseEntity<String> closeroom(@PathVariable String roomno, @PathVariable String roompenaltyall){
        System.out.println("클로즈룸컨트롤러도착");
        System.out.println("roomno :" + roomno);
        System.out.println("roompenaltyall : " + roompenaltyall);
        int arrivedcount = roomrepo.arrivedcount(roomno);
        //System.out.println("총벌금/도착인원 : " + Integer.parseInt(roompenaltyall)/arrivedcount);
        try{
            int deviceCharge =  Integer.parseInt(roompenaltyall)/arrivedcount;
            String[] arrivedMember =roomrepo.arrivedMember(roomno);
            System.out.println("arrivedMember[0] : " + arrivedMember[0]);
            for(int i=0; i<arrivedMember.length; i++){
            System.out.println(arrivedMember[i]);
            roomrepo.turnTardycashe(deviceCharge, arrivedMember[i]);
            
            String[] memberidArray = roomrepo.memberidArray(roomno);
            for(int j=0; j<memberidArray.length; j++){
                roomrepo.tardystateChange(memberidArray[j]);
            }
        }
        }catch(ArithmeticException e){

        }
           
        roomrepo.deleteRoom(roomno);
        roomrepo.deleteFinalRoom(roomno);

        return new ResponseEntity<String>("성공", HttpStatus.OK);
   }


   //차트데이터 넣기
   @GetMapping("/chart")
   public ResponseEntity<HashMap<String,Object>> chartData(){
    System.out.println("차트 컨트롤러");
    int totalMember = memberrepo.totalMember();
    int totalRoom = roomrepo.totalRoom();

    HashMap <String,Object> map = new HashMap<>();
    map.put("totalMember", totalMember);
    map.put("totalRoom", totalRoom);

    int roomTotalCharge = 0;
    try{
        int[] roomChage = roomrepo.roomCharge();
        for(int i=0; i<roomChage.length; i++){
            roomTotalCharge = roomTotalCharge + roomChage[i];
        }
        System.out.println("roomTotalCharge : " + roomTotalCharge);
        double avg = roomTotalCharge/totalRoom;
        System.out.println("avg : " + avg);
        map.put("avg", avg);
    }catch(ArithmeticException e){

    }


    return new ResponseEntity<HashMap<String,Object>>(map, HttpStatus.OK);
   }






}