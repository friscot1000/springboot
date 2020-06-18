package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.clientproxy.UserResourceV1;
import com.example.demo.model.User;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import javax.ws.rs.NotFoundException;

@RunWith(SpringRunner.class)//will run with spring runner
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class DemoApplicationTests {

    @Autowired
    private UserResourceV1 userResourceV1;

    @Test
    public void shouldInsertUser() throws Exception {
        //Given - given this data
        UUID userUid = UUID.randomUUID();
        User user = new User(userUid, "Francisco", "Contreras", User.Gender.MALE, 22, "fcontreras@gofundme.com");

        //When - I insert user
        userResourceV1.insertNewUser(user);

        //Then - I will do some assertions
        User joe = userResourceV1.fetchUser(userUid);
        assertThat(joe).isEqualToComparingFieldByField(user);
    }

//    @Test
//    public void shouldDeleteUser() throws Exception {
//        //Given - given this data
//        UUID userUid = UUID.randomUUID();
//        User user = new User(userUid, "Francisco", "Contreras", User.Gender.MALE, 22, "fcontreras@gofundme.com");
//
//        //When - I insert user
//        userResourceV1.insertNewUser(user);
//
//        //Then - I will do some assertions
//        User joe = userResourceV1.fetchUser(userUid);
//        assertThat(joe).isEqualToComparingFieldByField(user);
//
//        //When
//        userResourceV1.deleteUser(userUid);
//
//        //Then
//        assertThatThrownBy(() ->userResourceV1.fetchUser(userUid))
//                .isInstanceOf(NotFoundException.class);
//    }
}
