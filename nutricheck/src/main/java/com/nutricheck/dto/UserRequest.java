package com.nutricheck.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private Long id;
    private String name;
    private String email;
//    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
//    private HealthProfile healthProfile;

}
