package com.dayone.model;

import lombok.*;

@Data
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    private String ticker;
    private String name;
}
