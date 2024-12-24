package com.project.ecommerceapp.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ApiResponse {
    private String Message;
    private Object Data;
}
