package bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class AddressDetail {
    @Nullable
    private Integer id;

    @Nullable
    private Integer userId;

    @Nonnull
    private String address;

    @Nonnull
    private String state;

    @Nonnull
    private String city;

    @Nonnull
    private String country;
}
