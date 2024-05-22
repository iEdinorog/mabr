package org.mabr.postservice.dto.user;

public record UserProfile(

        int id,

        String username,

        String firstName,

        String lastName,

        int age,

        int cityId,

        int currencyId
) {
}
