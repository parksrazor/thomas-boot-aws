package com.thomas.web.health;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
interface CustomedUserRepository{

}
@Repository
public class UserRepositoryImpl implements CustomedUserRepository{
}
