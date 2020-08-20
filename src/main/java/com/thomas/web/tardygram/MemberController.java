package com.thomas.web.tardygram;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
 

import javax.transaction.Transactional; 
import com.tardygram.web.entities.Room;

import com.tardygram.web.entities.Member;
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
@RequestMapping("/member")
@Transactional
public class MemberController {
    @Autowired
    MemberRepository memberrepo;
    @Autowired
    RoomRepository roomrepo;
 
    private static String UPLOADED_FOLDER = "C:\\Users\\user\\Desktop\\tardygram\\frontend\\public\\image\\member\\";
    // private static String UPLOADED_FOLDER = "../../components/Upload/ProfileImage/";

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<Member> insertMember(@RequestBody Member joinFd) {
        joinFd.setProfileimage("/image/member/user.jpg");
        joinFd.setTardystate("-");
        return new ResponseEntity<Member>(memberrepo.save(joinFd), HttpStatus.OK);
    }

    // 회원탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteMember() {
        // Member m = new Member();
        // m.setMemberid("memberid");
        memberrepo.deleteById("c");
        return new ResponseEntity<String>("되어주세요", HttpStatus.OK);
    }

    // 로그인
    @PostMapping("/select")
    public ResponseEntity<HashMap<String,String>> selectone(@RequestBody Member loginFd) {
        HashMap<String,String> map =new HashMap<>();
        try{
            Member repoData = memberrepo.findById(loginFd.getMemberid()).get();
            memberrepo.findById(loginFd.getMemberid());
             System.out.println("아이디 통과");
            if (loginFd.getPwd().equals(repoData.getPwd())) {
                System.out.println("비번 통과");
                map.put("status","sucess");
                map.put("dataid",loginFd.getMemberid());
                return new ResponseEntity<HashMap<String,String>>(map, HttpStatus.OK);
            } else {
                System.out.println("비번 실패");
                map.put("status","fail");
                map.put("msg","비밀번호가 틀렸습니다");
                return new ResponseEntity<HashMap<String,String>>(map, HttpStatus.OK);
            }
        } catch(Exception e) {
            System.out.println("실패");
            map.put("status","fail");
            map.put("msg","아이디 또는 비밀번호가 틀렸습니다");
            return new ResponseEntity<HashMap<String,String>>(map, HttpStatus.OK);
        }

    }

    // 마이페이지 정보 보여주기
    @GetMapping("/mypage/{id}")
    public ResponseEntity<HashMap<String, Object>> mypage(@PathVariable String id) {
        System.out.println("mypage 컨트롤러");
        System.out.println("프론트에서 오는 id : " + id);
        HashMap<String, Object> map = new HashMap<>();
        // c가왔을때
        // 1. 그냥회원내용
        System.out.println("1번 hostProgressEx: " + memberrepo.findById(id).get());
        Member m1 = memberrepo.findById(id).get();
        map.put("uInfo", m1);

        // 2. 방장, 진행O
        List<Room> m2 = roomrepo.hostProgressEx(id);
        map.put("hostProgressEx", m2);
        System.out.println("2번 hostProgressEx: " + roomrepo.hostProgressEx(id));

        // 3. 방장, 진행X
        //System.out.println("3번 hostNotProgressEx: " + roomrepo.hostNotProgressEx(id));
        //List<Room> m3 = roomrepo.hostNotProgressEx(id);
        //map.put("hostNotProgressEx", m3);

        // 4. 방원, 진행O
        System.out.println("4번 MemberProgressEx: " + roomrepo.MemberProgressEx(id));
        List<Room> m4 = roomrepo.MemberProgressEx(id);
        map.put("MemberProgressEx", m4);

        // //5. 방원, 진행X
        //ystem.out.println("5번 MemberNotProgressEx: " + roomrepo.MemberNotProgressEx(id));
        //List<Room> m5 = roomrepo.MemberNotProgressEx(id);
        //map.put("MemberNotProgressEx", m5);

        int hostCount = roomrepo.HostCount(id);
        int memberCount = roomrepo.MemberCount(id);
        map.put("hostCount", hostCount);
        map.put("memberCount", memberCount);

        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);

    }


    //프로필 이미지 파일업로드
    @PostMapping(path="/upload/{id}",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String imgUpload(@RequestParam("file") MultipartFile file, @PathVariable String id){
        
        System.out.println("파일업로드 컨트롤러");
        System.out.println("건너온 data : " + file);
        System.out.println("로그인한id : " + id);
        System.out.println("파일이름 : " + file.getOriginalFilename());

        try{
            String DbPath = "/image/member/" + file.getOriginalFilename();
            System.out.println("Dbpath : " + DbPath);
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);
            System.out.println("path : " + path);
            System.out.println(id);
            memberrepo.profileUpdate(DbPath, id);
            return DbPath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return "No Img";                     
    }


    //메인페이지에 남은시간  
    @GetMapping("/mainchk/{id}")
    public ResponseEntity<String> mainchk(@PathVariable String id) {
        System.out.println("mainchk 컨트롤러");
        System.out.println("프론트에서 오는 id : " + id);
       
        String roomdate = memberrepo.mainchk(id);
     
        return new ResponseEntity<String>(roomdate, HttpStatus.OK);

    }

    

}