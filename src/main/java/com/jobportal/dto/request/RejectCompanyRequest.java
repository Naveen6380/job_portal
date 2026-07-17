package com.jobportal.dto.request;

import lombok.Data;

@Data
public class RejectCompanyRequest {
    private String reason;   // optional - shown to employer, also mailed via EmailService later
}
