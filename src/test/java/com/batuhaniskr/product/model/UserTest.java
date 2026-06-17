package com.batuhaniskr.product.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {

    @Test
    public void toString_ShouldNotExposePassword() {
        User user = new User("testuser", "test@email.com", "secret123");
        String result = user.toString();

        assertThat(result).doesNotContain("secret123");
    }

    @Test
    public void toString_ShouldNotContainPasswordField() {
        User user = new User("testuser", "test@email.com", "secret123");
        String result = user.toString();

        assertThat(result).doesNotContain("password");
    }

    @Test
    public void toString_ShouldStillContainOtherFields() {
        User user = new User("testuser", "test@email.com", "secret123");
        String result = user.toString();

        assertThat(result).contains("testuser");
        assertThat(result).contains("test@email.com");
    }
}
