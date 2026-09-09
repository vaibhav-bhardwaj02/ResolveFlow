package com.resolveflow.dto.complaint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDTO {

    private Long id;

    private String fileName;

    private String fileType;

    private String fileUrl;

    private Long fileSize;
}