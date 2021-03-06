package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import com.example.demo.model.User;
import com.example.demo.dao.FakeDataDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import jersey.repackaged.com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserServiceTest {

    @Mock
    private FakeDataDao fakeDataDao;

    @Autowired
    private UserService userService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userService = new UserService(fakeDataDao);
    }

    @Test
    public void shouldGetAllUsers() throws Exception {
        UUID annaUserUid = UUID.randomUUID();
        User anna = new User(annaUserUid, "anna","montana", User.Gender.FEMALE, 30, "anna@gmail.com");

        ImmutableList<User> users = new ImmutableList.Builder<User>()
                .add(anna)
                .build();

        given(fakeDataDao.selectAllUsers()).willReturn(users);

        List<User> allUsers = userService.getAllUsers(Optional.empty());

        assertThat(allUsers).hasSize(1);

        User user = allUsers.get(0);

        assertAnnaFields(user);
    }

    @Test
    public void shouldGetAllUsersByGender() throws Exception {
        UUID annaUserUid = UUID.randomUUID();
        User anna = new User(annaUserUid, "anna","montana", User.Gender.FEMALE, 30, "anna@gmail.com");

        UUID joeUserUid = UUID.randomUUID();
        User joe = new User(joeUserUid, "joe","jones", User.Gender.MALE, 30, "joe.gones@gmail.com");

        ImmutableList<User> users = new ImmutableList.Builder<User>()
                .add(anna)
                .add(joe)
                .build();

        given(fakeDataDao.selectAllUsers()).willReturn(users);

        List<User> filteredUsers = userService.getAllUsers(Optional.of("female"));
        assertThat(filteredUsers).hasSize(1);
        assertAnnaFields(filteredUsers.get(0));

    }

    @Test
    public void shouldThrowExceptionWhenGenderIsInvalid() throws Exception {
     assertThatThrownBy(()->userService.getAllUsers(Optional.of("adfasdfs")))
             .isInstanceOf(IllegalStateException.class)
             .hasMessageContaining("Invalid gender");
    }
    @Test
    public void shouldGetUser() {
        UUID annaUid = UUID.randomUUID();
        User anna = new User(annaUid, "anna","montana", User.Gender.FEMALE, 30, "anna@gmail.com");

        given(fakeDataDao.selectUserByUserUid(annaUid)).willReturn(Optional.of(anna));

        Optional<User> userOptional = userService.getUser(annaUid);

        assertThat(userOptional.isPresent()).isTrue();
        User user = userOptional.get();

        assertAnnaFields(user);
    }

    @Test
    public void shouldUpdateUser() {
        UUID annaUid = UUID.randomUUID();
        User anna = new User(annaUid, "anna","montana", User.Gender.FEMALE, 30, "anna@gmail.com");

        given(fakeDataDao.selectUserByUserUid(annaUid)).willReturn(Optional.of(anna));
        given(fakeDataDao.updateUser(anna)).willReturn(1);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        int updateResult = userService.updateUser(anna);

        verify(fakeDataDao).selectUserByUserUid(annaUid);
        verify(fakeDataDao).updateUser(captor.capture());

        User user = captor.getValue();
        assertAnnaFields(user);

        assertThat(updateResult).isEqualTo(1);
    }

    @Test
    public void shouldRemoveUser() {
        UUID annaUid = UUID.randomUUID();
        User anna = new User(annaUid, "anna","montana", User.Gender.FEMALE, 30, "anna@gmail.com");

        given(fakeDataDao.selectUserByUserUid(annaUid)).willReturn(Optional.of(anna));
        given(fakeDataDao.deleteUserByUserUid(annaUid)).willReturn(1);

        int deleteResult = userService.removeUser(annaUid);

        verify(fakeDataDao).selectUserByUserUid(annaUid);
        verify(fakeDataDao).deleteUserByUserUid(annaUid);

        assertThat(deleteResult).isEqualTo(1);
    }

    @Test
    public void shouldInsertUser() {
        User anna = new User(null, "anna","montana", User.Gender.FEMALE, 30, "anna@gmail.com");

        given(fakeDataDao.insertUser(any(UUID.class), eq(anna))).willReturn(1);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        int insertResult = userService.insertUser(anna);

        verify(fakeDataDao).insertUser(any(UUID.class), captor.capture());
        User user = captor.getValue();
        assertAnnaFields(user);

        assertThat(insertResult).isEqualTo(1);
    }

    private void assertAnnaFields(User user) {
        assertThat(user.getAge()).isEqualTo(30);
        assertThat(user.getFirstName()).isEqualTo("anna");
        assertThat(user.getLastName()).isEqualTo("montana");
        assertThat(user.getEmail()).isEqualTo("anna@gmail.com");
        assertThat(user.getGender()).isEqualTo(User.Gender.FEMALE);
        assertThat(user.getUserUid()).isInstanceOf(UUID.class);
    }
}