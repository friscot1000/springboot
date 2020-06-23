package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.clientproxy.UserResourceV1;
import com.example.demo.model.User;

import java.util.List;
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
public class UserIT {

    @Autowired
    private UserResourceV1 userResourceV1; // in test only have reference to resource ** so you can test endpoints really work together

    @Test
    public void shouldInsertUser() throws Exception {
        //Given - given this data
        UUID userUid = UUID.randomUUID();
        User user = new User(userUid, "Francisco", "Contreras", User.Gender.MALE, 22, "fcontreras@gofundme.com");

        //When - I insert user
        userResourceV1.insertNewUser(user);

        //Then - I will do some assertions
        User joe = userResourceV1.fetchUser(userUid); // use actual data type user gets back not response
        assertThat(joe).isEqualToComparingFieldByField(user);
    }

    @Test
    public void shouldDeleteUser() throws Exception {
        //Given - given this data
        UUID userUid = UUID.randomUUID();
        User user = new User(userUid, "Francisco", "Contreras", User.Gender.MALE, 22, "fcontreras@gofundme.com");

        //When - I insert user
        userResourceV1.insertNewUser(user);

        //Then - I will do some assertions
        User joe = userResourceV1.fetchUser(userUid);
        assertThat(joe).isEqualToComparingFieldByField(user);

        //When
        userResourceV1.deleteUser(userUid);

        //Then
        assertThatThrownBy(()-> userResourceV1.fetchUser(userUid))
                .isInstanceOf(NotFoundException.class);//need to catch exception becasue enty does not exist.
    }

    @Test
    public void shouldUpdateUser() throws Exception {
        //Given - given this data
        UUID userUid = UUID.randomUUID();
        User user = new User(userUid, "Francisco", "Contreras", User.Gender.MALE, 22, "fcontreras@gofundme.com");

        //When - I insert user
        userResourceV1.insertNewUser(user);

        User updatedUser = new User(userUid, "Francisco", "Contreras", User.Gender.MALE, 55, "fcontreras@gofundme.com");

        userResourceV1.updateUser(updatedUser);

        //then
        user = userResourceV1.fetchUser(userUid);
        assertThat(user).isEqualToComparingFieldByField(user);
    }

    @Test
    public void shouldFetchUsersByGender() {
        //Given - given this data
        UUID userUid = UUID.randomUUID();
        User user = new User(userUid, "Francisco", "Contreras", User.Gender.MALE, 22, "fcontreras@gofundme.com");

        //When - I insert user
        userResourceV1.insertNewUser(user);

        List<User> females = userResourceV1.fetchUsers("female");

        assertThat(females).extracting("userUid").doesNotContain(user.getUserUid());
        assertThat(females).extracting("firstName").doesNotContain(user.getFirstName());
        assertThat(females).extracting("lastName").doesNotContain(user.getLastName());
        assertThat(females).extracting("gender").doesNotContain(user.getGender());
        assertThat(females).extracting("age").doesNotContain(user.getAge());
        assertThat(females).extracting("email").doesNotContain(user.getEmail());

        List<User> males = userResourceV1.fetchUsers(User.Gender.MALE.name());

        assertThat(males).extracting("userUid").contains(user.getUserUid());
        assertThat(males).extracting("firstName").contains(user.getFirstName());
        assertThat(males).extracting("lastName").contains(user.getLastName());
        assertThat(males).extracting("gender").contains(user.getGender());
        assertThat(males).extracting("age").contains(user.getAge());
        assertThat(males).extracting("email").contains(user.getEmail());
    }
}
