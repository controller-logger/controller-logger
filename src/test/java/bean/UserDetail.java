package bean;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Data
@AllArgsConstructor
public class UserDetail {
    @Nullable
    private Integer id;

    @Nullable
    private Integer userId;

    @Nonnull
    private String firstName;

    @Nonnull
    private String lastName;

    @Nonnull
    private Gender gender;
}
