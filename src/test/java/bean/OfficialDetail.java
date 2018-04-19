package bean;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Data
@AllArgsConstructor
public class OfficialDetail extends AddressDetail {

    @Nonnull
    private String employeeId;

    @Nonnull
    private String companyName;

    @Nullable
    private String companyContactEmail;
}
