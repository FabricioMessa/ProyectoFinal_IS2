package com.batuhaniskr.product.user.domain;

import org.junit.Test;

import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToMany;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Test
    public void roles_ShouldNotCascadeDelete() throws NoSuchFieldException {
        Field rolesField = User.class.getDeclaredField("roles");
        ManyToMany annotation = rolesField.getAnnotation(ManyToMany.class);
        Set<CascadeType> cascadeTypes = Arrays.stream(annotation.cascade())
                .collect(Collectors.toSet());

        assertThat(cascadeTypes).doesNotContain(CascadeType.REMOVE);
        assertThat(cascadeTypes).doesNotContain(CascadeType.ALL);
    }

    @Test
    public void idGenerationStrategy_ShouldBeIdentity() throws NoSuchFieldException {
        Field idField = User.class.getDeclaredField("id");
        GeneratedValue annotation = idField.getAnnotation(GeneratedValue.class);

        assertThat(annotation.strategy()).isEqualTo(GenerationType.IDENTITY);
    }
}
