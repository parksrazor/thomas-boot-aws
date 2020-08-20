package com.thomas.web.tardygram;

import java.util.List;

import com.tardygram.web.entities.Member;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * MemberRepository
 */
@Repository
public interface EnterRepository extends CrudRepository<Member, Integer>{

    @Query(
        value = "insert into tbl_members_rooms (members_memberid, rooms_roomno) values(:memberid, :roomno)",
        nativeQuery = true
    )
    public void enter(Member memberid, int roomno);

    
}