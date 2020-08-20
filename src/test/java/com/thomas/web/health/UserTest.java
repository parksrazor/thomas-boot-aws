package com.thomas.web.health;

import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void getId() {
        final User u = User.builder().userId("").username("").build();
        
    }
}