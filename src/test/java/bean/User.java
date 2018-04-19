package bean;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


@Data
@AllArgsConstructor
public class User {
    @Nullable
    private Integer id;

    @Nonnull
    private String email;

    @Nonnull
    private String password;

    @Nonnull
    private UserDetail userDetails;

    @Nullable
    private ResidentialDetail residentialDetail;

    @Nullable
    private OfficialDetail officialDetail;
}
